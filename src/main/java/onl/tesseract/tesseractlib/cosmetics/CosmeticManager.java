package onl.tesseract.tesseractlib.cosmetics;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.bddfacade.CosmeticFacade;
import onl.tesseract.tesseractlib.familier.Pet;
import onl.tesseract.tesseractlib.util.ChatFormats;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CosmeticManager {

    private static final Map<UUID,CosmeticPlayer> cosmeticPlayer = new HashMap<>();


    public static void loadPlayer(UUID uuid)
    {
        new BukkitRunnable() {
            @Override
            public void run()
            {
                cosmeticPlayer.remove(uuid);
                Map<CosmeticType, Set<Cosmetic>> cosmetics = CosmeticFacade.getAll(uuid);
                cosmeticPlayer.put(uuid,new CosmeticPlayer(cosmetics));
            }
        }.runTaskAsynchronously(TesseractLib.instance);
    }

    public static void giveCosmetic(UUID uuid,CosmeticType type,Cosmetic cosmetic){

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

    public static void removeCosmetic(UUID uuid,CosmeticType type,Cosmetic cosmetic){
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

    public static boolean hasCosmetic(UUID uuid,CosmeticType type,Cosmetic cosmetic){
        if(!cosmeticPlayer.containsKey(uuid))loadPlayer(uuid);
        return cosmeticPlayer.get(uuid).hasCosmetics(type,cosmetic);
    }

    public static Cosmetic stringToCosmetic(CosmeticType type,String s) throws IllegalArgumentException{
        switch (type){
            case PET -> {
                return Pet.valueOf(s);
            }
            case ELYTRA_TRAIL -> {
                return null;
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }

}
