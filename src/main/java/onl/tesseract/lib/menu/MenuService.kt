package onl.tesseract.lib.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.lib.event.EventService
import onl.tesseract.lib.menu.event.PlayerMenuOpenEvent
import onl.tesseract.lib.service.PluginService
import org.bukkit.Material
import org.bukkit.entity.Player

class MenuService(private val pluginService: PluginService, private val eventService: EventService) {

    fun openMenu(menu: Menu, viewer: Player) {
        if (!eventService.callEvent(PlayerMenuOpenEvent(menu, viewer)))
            return
        menu.open(viewer)
    }

    /**
     * Opens a small menu asking the player to confirm or to cancel.
     *
     * @param viewer   Player to whom the menu will be displayed
     * @param message  Confirmation message
     * @param back The previous menu in case of cancellation
     * @param onAccept Callback to execute in case of confirmation
     */
    fun openConfirmationMenu(viewer: Player, message: Component, back: Menu? = null, onAccept: () -> Unit) {
        val menu = Menu(MenuSize.One, Component.text("Confirmer"), back)
        menu.open(viewer)
        menu.fill(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build())
        if (back != null) menu.addBackButton()
        else menu.addCloseButton()

        menu.addButton(
            4,
            ItemBuilder(Material.LIME_CONCRETE).name("Confirmer").color(NamedTextColor.GREEN).build()
        ) {
            menu.close()
            onAccept()
        }
    }
}