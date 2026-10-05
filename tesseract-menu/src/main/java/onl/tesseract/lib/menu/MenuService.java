package onl.tesseract.lib.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.event.EventService;
import onl.tesseract.lib.itembuilder.ItemBuilder;
import onl.tesseract.lib.menu.event.PlayerMenuOpenEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/** Services for opening and configuring menus. */
public class MenuService {

    private final EventService eventService;

    public MenuService(EventService eventService) {
        this.eventService = eventService;
    }

    public void openMenu(Menu menu, Player viewer) {
        if (!eventService.callEvent(new PlayerMenuOpenEvent(menu, viewer))) {
            return;
        }
        menu.open(viewer);
    }

    /**
     * Opens a small menu asking the player to confirm or cancel.
     *
     * @param viewer player to whom the menu will be displayed
     * @param message confirmation message
     * @param back previous menu in case of cancellation, or {@code null}
     * @param onAccept callback to execute in case of confirmation
     */
    public void openConfirmationMenu(Player viewer, Component message, Menu back, Runnable onAccept) {
        Menu menu = new Menu(MenuSize.One, Component.text("Confirmer"), back);
        menu.open(viewer);
        menu.fill(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
        if (back != null) {
            menu.addBackButton();
        } else {
            menu.addCloseButton();
        }

        menu.addButton(
                4,
                new ItemBuilder(Material.LIME_CONCRETE)
                        .name(message)
                        .color(NamedTextColor.GREEN)
                        .build(),
                event -> {
                    menu.close();
                    onAccept.run();
                }
        );
    }
}
