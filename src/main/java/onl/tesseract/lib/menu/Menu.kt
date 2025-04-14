package onl.tesseract.lib.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.lib.logger.LoggerFactory
import onl.tesseract.lib.profile.PlayerProfileService
import onl.tesseract.lib.service.PluginService
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.lib.task.TaskScheduler
import onl.tesseract.lib.util.plus
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.slf4j.Logger
import java.util.function.Consumer

private val logger: Logger = LoggerFactory.getLogger(Menu::class.java)

/**
 * Create in-game menus using immutable inventories. Buttons are symbolized by items in the top inventory, and trigger
 * a callback when clicked.
 */
open class Menu @JvmOverloads constructor(
    /**
     * Size of the menu, between 1 and 6 rows
     */
    val size: MenuSize,
    /**
     * Menu title
     */
    val title: Component,
    /**
     * Menu to open when clicking on the 'back' button. The back button will not show if there is no previous menu.
     */
    val previous: Menu? = null,
    /**
     * If true, the bottom inventory will be frozen. If false, the player can interact with its inventory. The top
     * inventory (being the menu itself) will be frozen in any case.
     */
    val freezeBottom: Boolean = true,
    val type: InventoryType? = null,
) : Listener {
    private val buttons: MutableMap<Int, AButton> = mutableMapOf()

    var viewer: Player? = null
    /**
     * Current view of the inventory, if the menu is currently open
     */
    var view: InventoryView? = null

    @JvmOverloads
    constructor(
        size: MenuSize,
        title: String,
        color: NamedTextColor? = null,
        previous: Menu? = null,
        freezeBottom: Boolean = true
    ) : this(size, Component.text(title, color), previous, freezeBottom)

    open fun open(viewer: Player) {
        val inventory = if (type != null)
            ServiceContainer[PluginService::class.java].createInventory(type, title)
        else
            ServiceContainer[PluginService::class.java].createInventory(size.size, title)

        this.view = viewer.openInventory(inventory)
        this.viewer = viewer
        ServiceContainer[PluginService::class.java].registerEventListener(this)
        buttons.forEach { index, button -> button.draw(this, index) }
        placeButtons(viewer)
    }

    /**
     * Add a normal button to the menu.
     * @param index Index of the button in the inventory.
     * @param item Icon of the button. Will be placed at the given index.
     * @param function Optional callback to trigger when the button is clicked
     */
    @JvmOverloads
    fun addButton(index: Int, item: ItemStack, function: Consumer<InventoryClickEvent>? = null) {
        addButton(index, Button(item = item, function = function))
    }

    /**
     * Add an asynchronous button to the menu. The rendering of the item is deferred.
     * @param index Index of the button in the inventory.
     * @param async Asynchronous supplier for the button icon. Will be placed at the given index.
     * @param function Optional callback to trigger when the button is clicked
     */
    fun addButton(index: Int, plugin: Plugin, async: () -> ItemStack, function: Consumer<InventoryClickEvent>? = null) {
        addButton(index, AsyncButton(itemSupplier = async, function = function, plugin = plugin))
    }

    fun addButton(index: Int, button: AButton) {
        buttons[index] = button
        button.draw(this, index)
    }

    /**
     * Add a 'back' button to return to the previous menu. Will not be displayed if the previous menu is null.
     * @see [Menu.previous]
     */
    @JvmOverloads
    fun addBackButton(index: Int = size.size - 9) {
        if (previous == null) return
        addButton(
            index,
            ItemBuilder(getBackButton())
                .name("Retour")
                .color(NamedTextColor.RED)
                .build()
        ) {
            this.viewer?.let { this.previous.open(it) }
        }
    }

    /**
     * Add a close button that will close the menu when clicked.
     */
    @JvmOverloads
    fun addCloseButton(index: Int = size.size - 1) {
        addButton(
            index,
            ItemBuilder(getCloseButton())
                .name("Fermer")
                .color(NamedTextColor.DARK_RED)
                .build()
        ) {
            this.close()
        }
    }

    /**
     * Fill the entire inventory with the given item
     */
    fun fill(item: ItemStack) {
        for (i in 0 until size.size - 1) {
            addButton(i, item)
        }
    }

    /**
     * Fill the given indices with the given item
     */
    fun fill(indices: Array<Int>, item: ItemStack) {
        indices.forEach { addButton(it, item) }
    }

    /**
     * Fill the given indices with the given item without replacing existing buttons
     */
    fun softFill(indices: Array<Int>, item: ItemStack) {
        indices.filter { !buttons.containsKey(it) }
            .forEach { addButton(it, item) }
    }

    open fun placeButtons(viewer: Player) {

    }

    /**
     * Close the inventory view.
     */
    fun close() {
        view?.close()
    }

    /**
     * Clear all buttons, without closing the view.
     */
    open fun clear() {
        buttons.clear()
        view?.topInventory?.clear()
    }

    /**
     * Replace all buttons
     */
    fun refresh(viewer: Player) {
        clear()
        placeButtons(viewer)
    }

    @EventHandler
    open fun onClick(event: InventoryClickEvent) {
        // Check that the click happened in this inventory
        if (event.inventory != this.view?.topInventory)
            return
        // Cancel the event to freeze the items.
        if (freezeBottom && event.clickedInventory == this.view?.bottomInventory)
            event.isCancelled = true

        if (event.clickedInventory == null || event.clickedInventory != this.view?.topInventory)
            return

        if (event.getCurrentItem() == null)
            return
        // If the clicked item is a button
        buttons[event.slot]?.let {
            // Accept the consumer, with a delay of one tick
            ServiceContainer[TaskScheduler::class.java].runLater(1) {
                try {
                    it.onClick(event)
                } catch (e: Exception) {
                    logger.error("Error while clicking a menu button", e)
                    event.whoClicked.sendMessage(NamedTextColor.RED + "Une erreur interne est survenue.")
                    close()
                }
            }
            event.isCancelled = true
        }
    }

    @EventHandler
    open fun onClose(event: InventoryCloseEvent) {
        if (event.inventory == this.view?.topInventory) {
            viewer = null
            view = null
            ServiceContainer[PluginService::class.java].unregisterEventListener(this)
        }
    }

    fun hasViewer(): Boolean = viewer != null

    fun getButtons(): Map<Int, AButton> = buttons

    companion object {
        private lateinit var backButton: ItemStack
        private lateinit var closeButton: ItemStack
        private lateinit var checkMarkButton: ItemStack

        fun getBackButton(): ItemStack {
            if (this::backButton.isInitialized) return backButton
            backButton = ItemBuilder(Material.PLAYER_HEAD)
                .customHead(
                    "eyJ0aW1lc3RhbXAiOjE1MzQ0NTU2Njg3MTgsInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mN2FhY2FkMTkzZTIyMjY5NzFlZDk1MzAyZGJhNDMzNDM4YmU0NjQ0ZmJhYjVlYmY4MTgwNTQwNjE2NjdmYmUyIn19fQ==",
                    "P1pFjz8nr9mcyMiBoisU0ON86W+7MG4K3ieuuLKrAvBwd11KFNrKqY7t0vp3kUVF0TNCaN/1oPEN27Ahl/L7l0yrM6c+tiPBQQkEGQiQpMqHPPM0bSVdT6m9Sv0zW7ZAytJXuRoK/JFr6InxMoAcd/lvhZvuNyL60nW7NRDtKYyac2/Z1X0Hk+aEI6XwuAE1g2SVkxyv7FWTrOWE+KO2Umv/w3GteV9fT6moHYOHhs0PmhqzrXHtqK+jfXB0b/eiVhQSBBiR4e9A8Svj+XJDzvH2csfZu9XeQ2kAUuJMQ09CpxvrBeQ1E8FFBFk8UAxQH/ANLMCcg+SsmJxnrR1SS45PP1BM+arm/VdmVsqzk60VBDyREhQmqtB+h6IDbYLOzIvggZhF3nQyolC/uklYy7SJ4WP5R3XuQtT/wPeS9s6BixtNhvbTVA7Yv02c8XTKMZpI4gN9sX2icbtOuYlIBf7w4aXNLBfi896RONuU4odS7X3mz7HwmNN2Zyu+XOPU8njTcbbIDxBWmTsfK/ROnFol19b4Vd8geyQSbFDZvsvqrLYS83mnBoQXODowHnSH8rRXAdQ0F8o/QkmUylz4tlSk5oi+y4Vv1EOKtut05HGyor38WFbO0niBYDv0EmHSO33m9vLYVJzoE65wXGT6bLhhrxdasBAr+WvkExtcgPg="
                )
                .build(ServiceContainer[PlayerProfileService::class.java])
            return backButton
        }

        fun getCloseButton(): ItemStack {
            if (this::closeButton.isInitialized) return closeButton
            closeButton = ItemBuilder(Material.PLAYER_HEAD)
                .customHead(
                    "eyJ0aW1lc3RhbXAiOjE1ODcxNTc4ODI2NDMsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZkMDU2YzdlZGRhNTc4Y2MyYTEwYzU1NTBjNTY2YTVmNzQxNmEzMzZkZmQwNjIzMTZkOGMwYjI4NTFjZGU5M2MifX19",
                    "bRUXqC6nIpHlgj3rWIW1STtWgK5BlDHcQq8PnZzXmmTjrLI5D9KzP9TKmq3v3PbtPIOvmoB+sIWIOTQp9gsPiqkWmtEh9cdyDe2ak7X5U+z79Got/p649Sd12W+Pg8iR0YVOJ7hOYRjFkYTi/f/2fi0V3YG+kIXDyNPkUXZHfEOdLnTRmUjbsT5EjM747+o3tX9bg7bX9i6Nsxo0/zIi4XPt3g8i0sBuo3ytgHb7WJQ/QxCtCoBg2M5SMr9pnk4bO9xtoLBmY/t2PcJqagMTY1QeWShtnKr4sHG7hXyXXf8hPFAnizv1zyMWsfuyPQCWqcVfHr3GDFj/Osxylov/faLOlWG2oCJCAODKVbZ6D+1nyPCgkO/ouK8WQ+U+Iyzzakx+zwaS7P22/9gw1PM8pfkF356bP6PWy1hHIb2EvYh1tVgkOCbUYIzdgkvGnts+qBAGA39Uqhz0lZ3IgW4uVSygbkcMqASGiwLfaWGsNsZF4Q24phOFIFiyO1oVw4eeV9GOPVYhKdT2RAePm4c4duoAing36vuwvV3oOUbJXUMHjMUPtXeLX5uJqJt2DetZCXiqIUyxqCXgCOLjJujjoyuyHpD/2m0IsPhg2a/dgfOcm/25K1lNaKOIgYhZ773eSyCZbkjN0h5bzZY8S87n3zTrHPqpGGUYzdRptZvvwAc="
                )
                .build(ServiceContainer[PlayerProfileService::class.java])
            return closeButton
        }

        fun getCheckMarkButton(): ItemStack {
            if (this::checkMarkButton.isInitialized) return checkMarkButton
            checkMarkButton = ItemBuilder(Material.PLAYER_HEAD)
                .customHead(
                    "ewogICJ0aW1lc3RhbXAiIDogMTU5MjY5NjU3ODgxMSwKICAicHJvZmlsZUlkIiA6ICI3MzgyZGRmYmU0ODU0NTVjODI1ZjkwMGY4OGZkMzJmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4cWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgyMzUwODA4NDg4YjQ0Y2EwNmVhNGE1NDUzY2QwZDM4N2I2MTg5YjVhZmRlY2EwYjhjNjJlNTVkNjg1MTBhMSIKICAgIH0KICB9Cn0=",
                    "Z98pJmCgeb9xFh9V37708rOQz2j2zrOH4bkj+Yu7xcHPUnX3TObOAxQnp9iJoWoMDaQ2z9T6aJw3d/WXqQK8Ektqk0v59DrOHlv6Ei17uDHKoLrIlNfzT+YYY/x/cyeEd9EktFYp8GsvCHJi8YtJHV2msH7Z7j0d4IhB/MbE/ploYJfrIG3x8haziBWMdTLbZhr/ykGPh+d2uiCVHgjxj5w2HSjvhyaW5a0PDsJ9uiiPI8ZZaOLZHELZXHOPliy0cJrNH+KH4yV22vOR8XAzooSHv384xUMjkkrzYwGFK1HFaLelNXfve9odQC0Rjv0eEqFjnobiVq4wzC0Pl1nOUv0+Q4J/sOLs1GAy4P26usz1IZzfLgFw/Sx7h5xRuvdD6/6P50xr3jja8o3gH35DGID//ImivYniDz1eOTTfreosABjxVn3cTsyXOlDx+F9YHlYgwxmT4gcJXpTxFN52Ef1H9Qw8Moq6t7xbVMaOv4aHlSsMug94/5rHJV1745HQ/pDRcVNSE5qb8FTNtHJjhOsmm6ZLwnyf/5JAjkP2yp7qP+9Be6O/3f38v0Yr7UzWgLwvyJeoat/oKSif/ub6ujOxNTBcIVsPRNONaN64Rr44qm3zXU+F40E0wgLAx98SKLZw/JssVsTvHat8WHFMaZ2vMXmyZeNXS4c+Q+kPjdA="
                )
                .build(ServiceContainer[PlayerProfileService::class.java])
            return checkMarkButton
        }
    }
}

/**
 * Size of a menu in number of rows
 */
enum class MenuSize(val size: Int) {
    One(9),
    Two(18),
    Three(27),
    Four(36),
    Five(45),
    Six(54),
    Hopper(5);
}
