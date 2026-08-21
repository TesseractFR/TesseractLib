package onl.tesseract.lib.itembuilder;

import net.kyori.adventure.text.Component;

/**
 * Builder class to construct an item lore, and to automatically wrap it to fit a given maximum width.
 * The result is a collection of {@link Component} that can be used to set the lore of an {@link
 * org.bukkit.inventory.meta.ItemMeta}
 *
 * @see org.bukkit.inventory.ItemStack
 * @see org.bukkit.inventory.meta.ItemMeta
 * @see Component
 */
public class ItemLoreBuilder extends AItemLoreBuilder<ItemLoreBuilder> {

    public ItemLoreBuilder() {
    }

    public ItemLoreBuilder(int width) {
        super(width);
    }

    @Override
    protected ItemLoreBuilder self() {
        return this;
    }
}
