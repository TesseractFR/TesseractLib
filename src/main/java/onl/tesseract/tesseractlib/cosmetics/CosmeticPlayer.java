package onl.tesseract.tesseractlib.cosmetics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CosmeticPlayer {
    Map<CosmeticType, Set<Cosmetic>> cosmetics = new HashMap<>();

    public CosmeticPlayer(Map<CosmeticType, Set<Cosmetic>> cosmetics)
    {
        this.cosmetics = cosmetics;
    }

    public boolean hasCosmetics(CosmeticType type,Cosmetic cosmetic){
        return cosmetics.containsKey(type) && cosmetics.get(type).contains(cosmetic);
    }
    public void addCosmetics(CosmeticType type,Cosmetic cosmetic){
        if(!cosmetics.containsKey(type))
            cosmetics.put(type,new HashSet<>());
        cosmetics.get(type).add(cosmetic);
    }
    public void removeCosmetics(CosmeticType type,Cosmetic cosmetic){
        if(!cosmetics.containsKey(type))
            return;
        cosmetics.get(type).remove(cosmetic);
    }

}
