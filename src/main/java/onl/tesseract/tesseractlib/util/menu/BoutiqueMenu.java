package onl.tesseract.tesseractlib.util.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public abstract class BoutiqueMenu extends InventoryMenu {
    protected TPlayer tPlayer;

    public BoutiqueMenu(TPlayer tPlayer, int size, String title, NamedTextColor color, InventoryMenu previous) {
        super(size, title, color, previous);
        this.tPlayer = tPlayer;
    }


    @Override
    public void open(Player viewer) {
        fillNoReplace(Material.GRAY_STAINED_GLASS_PANE, " ");
        addBackButton();
        addQuitButton();
        addBoutiqueButton();
        super.open(viewer);
    }


    protected void addBoutiqueButton() {
        final ItemLoreBuilder lore = new ItemLoreBuilder()
                .newline()
                .append(text("Vous avez ", GRAY))
                .append(text(tPlayer.getShopPoint(), DARK_AQUA))
                .append(text(" Points boutique.", GRAY))
                .newline()
                .append(text("Vous avez ", GRAY))
                .append(text(tPlayer.getMarketCurrency(), DARK_AQUA))
                .append(text(" lys d'or.", GRAY))
                .newline()
                .append(text("Cliquez ici pour acheter des lys d'or", GRAY));

        Component nameComponent = text("Lys d'or et Points Boutique", GOLD);

        ItemStack itemStack = new ItemBuilder(Material.RAW_GOLD)
                .name(nameComponent)
                .lore(lore.get())
                .build();
        this.addButton(this.inventory.getSize() - 5, itemStack, event -> {
            tPlayer.sendMessage(text("[", GOLD)
                    .append(text(" Cliquez ici pour acheter des lys d'or", YELLOW))
                    .append(text("]", GOLD))
                    .clickEvent(
                            ClickEvent.openUrl("https://tesseract.craftingstore.net/")));
            this.close();
        });
    }
}
