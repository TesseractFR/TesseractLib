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
    public boolean addCosmetics(CosmeticType type, Cosmetic cosmetic){
        if(!cosmetics.containsKey(type))
            cosmetics.put(type,new HashSet<>());
        if(cosmetics.get(type).contains(cosmetic))return false;
        cosmetics.get(type).add(cosmetic);
        return true;
    }
    public boolean removeCosmetics(CosmeticType type,Cosmetic cosmetic){
        if(!cosmetics.containsKey(type))
            return false;
        if(!cosmetics.get(type).contains(cosmetic))
            return false;
        cosmetics.get(type).remove(cosmetic);
        return true;
    }

    public int getTotal(CosmeticType type)
    {
        if(!cosmetics.containsKey(type))return 0;
        return cosmetics.get(type).size();
    }
}
