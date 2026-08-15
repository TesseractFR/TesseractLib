package onl.tesseract.lib.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Predicate;

public class ItemSearchPredicateBuilder {
    private Material material;
    private int customModelData = -1;
    private boolean countDamaged;

    public Predicate<ItemStack> build()
    {
        return item -> {
            if (material != null && material != item.getType())
                return false;
            ItemMeta meta = item.getItemMeta();
            if ((customModelData == -1) == meta.hasCustomModelData())
                return false;
            if (meta.hasCustomModelData() && meta.getCustomModelData() != customModelData)
                return false;
            return !countDamaged || !(meta instanceof Damageable damageable) || damageable.hasDamage();
        };
    }

    public ItemSearchPredicateBuilder setMaterial(final Material material)
    {
        this.material = material;
        return this;
    }

    public ItemSearchPredicateBuilder setCustomModelData(final int customModelData)
    {
        this.customModelData = customModelData;
        return this;
    }

    public ItemSearchPredicateBuilder setCountDamaged(final boolean countDamaged)
    {
        this.countDamaged = countDamaged;
        return this;
    }
}
