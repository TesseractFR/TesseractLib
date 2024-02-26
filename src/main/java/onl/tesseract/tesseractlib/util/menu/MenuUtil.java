package onl.tesseract.tesseractlib.util.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import static onl.tesseract.tesseractlib.util.menu.InventoryMenu.NEW_LINE;

public class MenuUtil {


    static public void addBoutiqueButton(InventoryMenu inventoryMenu, int index, TPlayer tPlayer){
        inventoryMenu.addButton(index, Material.RAW_GOLD, ChatColor.GOLD + "Lys d'or",
                NEW_LINE + ChatColor.GRAY + "Vous avez " + ChatColor.DARK_AQUA + tPlayer.getMarketCurrency()
                        + ChatColor.GRAY +
                        " lys d'or." +
                        NEW_LINE + ChatColor.GRAY + "Cliquez ici pour acheter des lys d'or", event -> {
                    tPlayer.sendMessage(Component.text(
                                    ChatColor.GOLD
                                            + "[" + ChatColor.YELLOW + "Cliquez ici pour acheter des lys d'or" + ChatColor.GOLD
                                            +
                                            "]")
                            .clickEvent(
                                    ClickEvent.openUrl("https://tesseract.craftingstore.net/")));
                    inventoryMenu.close();
                });
    }
}
