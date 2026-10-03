package onl.tesseract.lib.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.Tick;
import onl.tesseract.lib.task.TaskScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Service used to retrieve a player's next chat or command input.
 */
public class ChatEntryService implements Listener {

    private final TaskScheduler scheduler;
    private final Random random = new Random(123456789L);
    private final Map<UUID, ChatMessageCallback> chatCallbacks = new HashMap<>();
    private final Map<UUID, Map<UUID, CommandCallback>> commandCallbacks = new HashMap<>();

    public ChatEntryService(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Get the next chat message of the player.
     * @param player Player to watch.
     * @param message Prompt to show to the player, asking him an input.
     * @param callback Callback taking the player's chat input as argument.
     */
    public void getChatEntry(Player player, Component message, Consumer<String> callback) {
        if (!player.isOnline()) return;
        player.sendMessage(ChatFormats.CHAT.append(message).append(Component.text(" : ", NamedTextColor.GRAY)));

        chatCallbacks.put(player.getUniqueId(), new ChatMessageCallback(callback,
                scheduler.runLater(Tick.ofSeconds(60), () -> chatCallbacks.remove(player.getUniqueId()))));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Component message = event.message();
        ChatMessageCallback callback = chatCallbacks.remove(event.getPlayer().getUniqueId());
        if (callback != null) {
            // Make a sync call
            scheduler.runLater(() -> {
                if (message instanceof TextComponent textComponent) {
                    callback.stringCallback().accept(textComponent.content());
                }
                callback.expirationTask().cancel();
            });
            event.setCancelled(true);
        }
    }

    /**
     * Generate a ClickEvent and capture the corresponding command call. Used to insert a click event in a message,
     * and execute a callback when the message is clicked.
     * @throws IllegalArgumentException If the player is offline
     */
    public ClickEvent clickCommand(Player player, Runnable callback) {
        return clickCommand(player, 0, callback);
    }

    /** @throws IllegalArgumentException If the player is offline */
    private ClickEvent clickCommand(Player player, int group, Runnable callback) {
        if (!player.isOnline()) throw new IllegalArgumentException("Player is offline");
        UUID uuid = UUID.randomUUID();
        CommandCallback commandCallback = new CommandCallback(callback, group);
        commandCallbacks.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>()).put(uuid, commandCallback);
        return ClickEvent.runCommand("/commandCallback " + uuid);
    }

    public void clickCommandGroup(Player player, Consumer<CommandCallbackGroup> builder) {
        int groupID = random.nextInt(1, 99999);
        CommandCallbackGroup group = new CommandCallbackGroup(player, groupID);
        builder.accept(group);
    }

    public CommandCallbackGroup clickCommandGroup(Player player) {
        int groupID = random.nextInt(1, 99999);
        return new CommandCallbackGroup(player, groupID);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/commandCallback")) return;
        String[] parts = event.getMessage().split(" ");
        if (parts.length != 2) return;

        event.setCancelled(true);
        Map<UUID, CommandCallback> callbacks = commandCallbacks.get(event.getPlayer().getUniqueId());
        if (callbacks == null) return;
        try {
            UUID uuid = UUID.fromString(parts[1]);
            CommandCallback callback = callbacks.get(uuid);
            if (callback == null) return;
            callback.runnable().run();
            if (callback.group() > 0) clearGroup(event.getPlayer().getUniqueId(), callback.group());
        } catch (IllegalArgumentException ignored) {
            // Ignore malformed callback identifiers.
        }
    }

    private void clearGroup(UUID playerUUID, int groupID) {
        Map<UUID, CommandCallback> callbacks = commandCallbacks.get(playerUUID);
        if (callbacks != null) callbacks.values().removeIf(callback -> callback.group() == groupID);
    }

    public final class CommandCallbackGroup {
        private final Player player;
        private final int id;

        private CommandCallbackGroup(Player player, int id) {
            this.player = player;
            this.id = id;
        }

        public ClickEvent clickCommand(Runnable callback) {
            return ChatEntryService.this.clickCommand(player, id, callback);
        }
    }

    private record ChatMessageCallback(Consumer<String> stringCallback, BukkitTask expirationTask) { }

    private record CommandCallback(Runnable runnable, int group) { }
}
