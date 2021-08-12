package onl.tesseract.tesseractlib.cosmetics;

import onl.tesseract.tesseractlib.bddfacade.CosmeticFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CosmeticManager {

    private static final Map<UUID,CosmeticPlayer> cosmeticPlayer = new HashMap<>();


    public static void loadPlayer(UUID uuid)
    {
        cosmeticPlayer.remove(uuid);
        Map<CosmeticType, Set<Cosmetic>> cosmetics = CosmeticFacade.getAll(uuid);
        cosmeticPlayer.put(uuid,new CosmeticPlayer(cosmetics));
    }

}
