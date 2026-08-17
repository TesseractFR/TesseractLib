package onl.tesseract.lib.menu;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Concrete implementation of {@link AItemBuilder} for standard item building.
 */
public class ItemBuilder extends AItemBuilder<ItemBuilder> {

    public ItemBuilder(Material material) {
        this(material, (String) null, (ItemStack) null);
    }

    public ItemBuilder(Material material, String name) {
        super(material, name == null ? null : new ItemStack(material), null);
        if (name != null) {
            name(name);
        }
    }

    public ItemBuilder(Material material, String name, ItemStack base) {
        super(material, base, null);
        if (name != null) {
            name(name);
        }
    }

    public ItemBuilder(ItemStack base) {
        super(base.getType(), base, null);
    }

    public ItemBuilder(Material material, String name, NamedTextColor color) {
        super(material, name == null ? null : new ItemStack(material), null);
        if (name != null) {
            name(name, color);
        }
    }


    @Override
    public ItemBuilder self() {
        return this;
    }

    @Override
    public ItemStack build() {
        return super.build();
    }

    @Override
    public ItemBuilder material(Material material) {
        return super.material(material);
    }
}
