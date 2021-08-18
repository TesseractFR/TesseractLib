package onl.tesseract.tesseractlib.cosmetics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.bddfacade.CosmeticFacade;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CosmeticManager {

    private static final Map<UUID,CosmeticPlayer> cosmeticPlayer = new HashMap<>();

    private static final Map<String, Set<Cosmetic>> cosmetics = new HashMap<>();

    public static void registerCosmetic(String type, Set<Cosmetic> cosmetic)
    {
       cosmetics.put(type,cosmetic);
    }


    public static void loadPlayer(UUID uuid)
    {
        new BukkitRunnable() {
            @Override
            public void run()
            {
                cosmeticPlayer.remove(uuid);
                Map<String, Set<Cosmetic>> cosmetics = CosmeticFacade.getAll(uuid);
                cosmeticPlayer.put(uuid,new CosmeticPlayer(cosmetics));
            }
        }.runTaskAsynchronously(TesseractLib.instance);
    }

    public static void giveCosmetic(UUID uuid,String type,Cosmetic cosmetic){

        new BukkitRunnable() {
            @Override
            public void run()
            {
                if(!cosmeticPlayer.containsKey(uuid))loadPlayer(uuid);
                if(cosmeticPlayer.get(uuid).addCosmetics(type,cosmetic)){
                    CosmeticFacade.add(uuid,type,cosmetic);
                    if(Bukkit.getOfflinePlayer(uuid).isOnline()){
                        Objects.requireNonNull(Bukkit.getPlayer(uuid))
                               .sendMessage(ChatFormats.COSMETICS.append(cosmetic.getObtainMessage()));
                    }
                }

            }
        }.runTaskAsynchronously(TesseractLib.instance);
    }

    public static void removeCosmetic(UUID uuid,String type,Cosmetic cosmetic){
        new BukkitRunnable() {
            @Override
            public void run()
            {
                if(!cosmeticPlayer.containsKey(uuid))loadPlayer(uuid);
                if(cosmeticPlayer.get(uuid).removeCosmetics(type,cosmetic)){
                    CosmeticFacade.remove(uuid,type,cosmetic);
                }

            }
        }.runTaskAsynchronously(TesseractLib.instance);
    }

    public static boolean hasCosmetic(UUID uuid,String type,Cosmetic cosmetic){
        if(!cosmeticPlayer.containsKey(uuid))loadPlayer(uuid);
        return cosmeticPlayer.get(uuid).hasCosmetics(type,cosmetic);
    }

    public static Cosmetic stringToCosmetic(String type,String cosmetic){
        if(cosmetics.containsKey(type)){
            for(Cosmetic c : cosmetics.get(type)){
                if(c.toString().equals(cosmetic))return c;
            }
        }
        throw new IllegalArgumentException("Unknow cosmetic "+type + " "+cosmetic);
    }

    public static int getTotalPossessed(UUID uuid, String type)
    {
        if(!cosmeticPlayer.containsKey(uuid))return 0;
        return cosmeticPlayer.get(uuid).getTotal(type);
    }
    public static void tryToBuyEvent(Player viewer,InventoryMenu mainMenu,TPlayer player,
                                                          String type,
                                                          Cosmetic cosmetic){
        if (player.getMarketCurrency() >= cosmetic.getPrice())
            InventoryMenu.openConfirmationMenu(viewer,"Être vous sur de vouloir acheter",mainMenu, event2 ->{
                player.buyCosmetic(type,cosmetic,cosmetic.getPrice());
            });
        else{
            player.sendMessage(ChatFormats.COSMETICS_ERROR.append(
                    Component.text("Vous n'avez pas assez de lys d'or, cliquez ici pour en acheter")
                             .hoverEvent(HoverEvent.showText(
                                     Component
                                             .text("Cliquez ici pour accéder à la boutique.", NamedTextColor.GOLD)))
                             .clickEvent(ClickEvent.openUrl("https://tesseract.craftingstore.net/"))));
            mainMenu.close();
        }
    }


    public static Set<String> getTypes(){
        return cosmetics.keySet();
    }

    public static Set<Cosmetic> getCosmetics(String arg)
    {
        return cosmetics.get(arg);
    }

    public static Set<Cosmetic> getPlayerCosmetics(UUID uuid,String cosmeticType){
        if(!cosmeticPlayer.containsKey(uuid))return null;
        return cosmeticPlayer.get(uuid).getCosmetics(cosmeticType);
    }
}
