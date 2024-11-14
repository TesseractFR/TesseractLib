package onl.tesseract.lib.chat

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.lib.Ticks
import onl.tesseract.lib.task.TaskScheduler
import onl.tesseract.lib.util.ChatFormats
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.random.Random

class ChatEntryService(private val scheduler: TaskScheduler) : Listener {

    private val random = Random(123456789)
    private val chatCallbacks: MutableMap<UUID, ChatMessageCallback> = mutableMapOf()
    private val commandCallbacks: MutableMap<UUID, MutableMap<UUID, CommandCallback>> = mutableMapOf()

    fun getChatEntry(player: Player, message: Component, callback: (String) -> Unit) {
        if (!player.isOnline) return
        player.sendMessage(ChatFormats.CHAT.append(message).append(Component.text(" : ", NamedTextColor.GRAY)))

        chatCallbacks[player.uniqueId] = ChatMessageCallback(callback, scheduler.runLater(Ticks.ofSeconds(60)) {
            chatCallbacks.remove(player.uniqueId)
        })
    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val message = event.message()
        chatCallbacks[event.player.uniqueId]?.let {
            chatCallbacks.remove(event.player.uniqueId)
            // Make a sync call
            scheduler.runLater {
                if (message is TextComponent)
                    it.stringCallback?.invoke(message.content())
                it.expirationTask.cancel()
            }
            event.isCancelled = true
        }
    }

    /**
     * @throws IllegalArgumentException If the player is offline
     */
    fun clickCommand(player: Player, callback: () -> Unit): ClickEvent {
        return clickCommand(player, 0, callback)
    }

    /**
     * @throws IllegalArgumentException If the player is offline
     */
    private fun clickCommand(player: Player, group: Int = 0, callback: () -> Unit): ClickEvent {
        require(!player.isOnline)
        val uuid = UUID.randomUUID()
        val commandCallback = CommandCallback(callback, group)
        commandCallbacks.computeIfAbsent(player.uniqueId) { mutableMapOf() }[uuid] = commandCallback
        return ClickEvent.runCommand("/commandCallback $uuid")
    }

    fun clickCommandGroup(player: Player, builder: CommandCallbackGroup.() -> Unit) {
        val groupID = random.nextInt(1, 99999)
        val group = CommandCallbackGroup(this, player, groupID)
        group.apply(builder)
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (!event.message.startsWith("/commandCallback"))
            return
        val parts = event.message.split(" ")
        if (parts.size != 2) return

        event.isCancelled = true
        val callbacks = commandCallbacks[event.player.uniqueId] ?: return
        try {
            val uuid = UUID.fromString(parts[1])
            val callback = callbacks[uuid] ?: return
            callback.runnable()
            if (callback.group > 0)
                clearGroup(event.player.uniqueId, callback.group)
        } catch (e: IllegalArgumentException) {
            return
        }
    }

    private fun clearGroup(playerUUID: UUID, groupID: Int) {
        commandCallbacks[playerUUID]?.values?.removeIf { it.group == groupID }
    }

    inner class CommandCallbackGroup(
        private val service: ChatEntryService,
        private val player: Player,
        private val id: Int,
    ) {

        fun clickCommand(callback: () -> Unit): ClickEvent {
            return service.clickCommand(player, id, callback)
        }
    }
}

private data class ChatMessageCallback(
    val stringCallback: ((String) -> Unit)?,
    val expirationTask: BukkitTask,
)

private data class CommandCallback(
    val runnable: () -> Unit,
    val group: Int,
)