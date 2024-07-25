package onl.tesseract.tesseractlib.util.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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

    @SuppressWarnings("SameParameterValue")
    protected List<Component> getPriceLore(int price, boolean containsShopPoint) {
        ItemLoreBuilder itemLore = new ItemLoreBuilder().newline()
                .append("Prix : ", NamedTextColor.GRAY)
                .newline()
                .append(price + " lys d'or" + (containsShopPoint ? " / points boutique" : ""), NamedTextColor.GOLD, TextDecoration.BOLD).newline()
                .newline()
                .append("--- Clic gauche ---", NamedTextColor.LIGHT_PURPLE)
                .newline()
                .append("Acheter en lys d'or", NamedTextColor.AQUA);

        if (containsShopPoint) {
            itemLore.newline()
                    .newline()
                    .append("--- Clic droit ---", NamedTextColor.LIGHT_PURPLE)
                    .newline()
                    .append("Acheter en points boutique", NamedTextColor.AQUA);
        }
        return itemLore.get();
    }

    protected List<Component> getPriceLore(int price) {
        return getPriceLore(price, true);
    }

    protected void askBuyItem(ItemStack itemStack, int price, boolean withShopPoint) {
        TextComponent message = text("Confirmer votre achat de ")
                .append(itemStack.displayName())
                .append(text(" pour " + price + (withShopPoint ? " points boutiques ?" : " lys d'or ?")));
        openConfirmationMenu(tPlayer.getBukkitPlayer(), message, this, event -> buyItem(itemStack, price, withShopPoint));

    }

    private void buyItem(ItemStack itemStack, int price, boolean withShopPoint) {
        boolean hasBuyItem;
        if (withShopPoint) {
            hasBuyItem = buyItemWithShopPoint(itemStack, price);
        } else {
            hasBuyItem = buyItemWithLysDor(itemStack, price);
        }
        if (!hasBuyItem) {
            String moneyType = withShopPoint ? "points boutique" : "lys d'or";
            tPlayer.sendMessage(ChatFormats.CHAT_ERROR.append(
                    Component.text("Vous n'avez pas suffisamment de " + moneyType + ". Nécessite : " + price + ".")));
            close();
            return;
        }
        tPlayer.sendMessage(ChatFormats.CHAT_SUCCESS.append(Component.text("Achat réussi !")));
    }

    private boolean buyItemWithLysDor(ItemStack itemStack, int price) {
        if (tPlayer.getMarketCurrency() < price) {
            return false;
        }
        tPlayer.addMarketCurrency(-price);
        tPlayer.getBukkitPlayer().getInventory().addItem(itemStack);
        return true;
    }

    private boolean buyItemWithShopPoint(ItemStack itemStack, int price) {
        if (tPlayer.getShopPoint() < price) {
            return false;
        }
        tPlayer.addShopPoint(-price);
        tPlayer.getBukkitPlayer().getInventory().addItem(itemStack);
        return true;
    }


}
