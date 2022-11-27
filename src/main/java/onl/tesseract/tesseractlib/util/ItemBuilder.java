package onl.tesseract.tesseractlib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Builder class to build item, specifying its name, material, lore, ...
 *
 * @see ItemLoreBuilder
 */
public class ItemBuilder {
    protected ItemStack base;
    protected Material material;
    protected Component name;
    protected List<Component> lore;
    protected boolean enchanted;
    protected int quantity;
    protected int lineWidth;
    protected ItemFlag[] flags = ItemFlag.values();
    protected int customModelData = -1;
    protected Color color;

    /**
     * Default constructor. Instantiate a new builder by initializing all parameters.
     *
     * @param material Base material
     * @param name Display name
     * @param lore Lore
     * @param enchanted If true, add a enchanted effect to the item, and add the flag {@link ItemFlag#HIDE_ENCHANTS}
     * @param quantity Amount in the stack
     * @param lineWidth Maximum line width in the lore
     * @param flags List of flags
     */
    public ItemBuilder(@NotNull final Material material, final Component name, final List<Component> lore, final boolean enchanted,
                       final int quantity,
                       final int lineWidth, ItemFlag... flags)
    {
        this.material = material;
        this.name = name;
        if (name != null && this.name.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET)
            this.name = this.name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        this.lore = lore;
        this.enchanted = enchanted;
        this.quantity = quantity;
        this.lineWidth = lineWidth;
        this.flags = flags;
    }

    /**
     * Instantiate a new builder. The built item will have all values of flags in {@link ItemFlag}.
     * Equivalent to {@code new ItemBuilder(material, name, lore, enchanted, quantity, lineWith, ItemFlags.values());}
     *
     * @param material Base material
     * @param name Display name
     * @param lore Lore
     * @param enchanted If true, add a enchanted effect to the item, and add the flag {@link ItemFlag#HIDE_ENCHANTS}
     * @param quantity Amount in the stack
     * @param lineWidth Maximum line width in the lore
     */
    public ItemBuilder(@NotNull final Material material, final Component name, final List<Component> lore, final boolean enchanted,
                       final int quantity,
                       final int lineWidth)
    {
        this(material, name, lore, enchanted, quantity, lineWidth, ItemFlag.values());
    }

    /**
     * Instantiate a new builder.
     * Equivalent to {@code new ItemBuilder(material, name, lore, enchanted, 1, 35, ItemFlags.values());}
     *
     * @param material Base material
     * @param name Display name
     * @param lore Lore
     * @param enchanted If true, add a enchanted effect to the item, and add the flag {@link ItemFlag#HIDE_ENCHANTS}
     */
    public ItemBuilder(@NotNull final Material material, final Component name, final List<Component> lore, final boolean enchanted)
    {
        this(material, name, lore, enchanted, 1, 35);
    }

    /**
     * Instantiate a new builder.
     * Equivalent to {@code new ItemBuilder(material, name, lore, false, 1, 35, ItemFlags.values());}
     *
     * @param material Base material
     * @param name Display name
     * @param lore Lore
     */
    public ItemBuilder(@NotNull final Material material, final Component name, final List<Component> lore)
    {
        this(material, name, lore, false, 1, 35);
    }

    /**
     * Instantiate a new builder.
     * Equivalent to {@code new ItemBuilder(material, name, null, false, 1, 35, ItemFlags.values());}
     *
     * @param material Base material
     * @param name Display name
     */
    public ItemBuilder(@NotNull final Material material, final Component name)
    {
        this(material, name, null, false, 1, 35);
    }

    public ItemBuilder(@NotNull final Material material)
    {
        this(material, null, null, false, 1, 35);
    }

    /**
     * Instantiate a new builder, based on existing item stack
     *
     * @param base Base item stack
     */
    public ItemBuilder(final ItemStack base)
    {
        this.base = base;
        this.material = null;
    }

    /**
     * Build the item stack
     *
     * @return Built ItemStack
     */
    @NotNull
    public ItemStack build()
    {
        ItemStack item = base == null
                         ? new ItemStack(material, quantity)
                         : base;
        ItemMeta meta = item.getItemMeta();
        if (name != null)
            meta.displayName(name);
        if (lore != null)
            meta.lore(lore);
        if (customModelData != -1)
            meta.setCustomModelData(customModelData);
        if (color != null && meta instanceof PotionMeta potionMeta)
            potionMeta.setColor(color);
        if (color != null && meta instanceof LeatherArmorMeta armorMeta)
            armorMeta.setColor(color);
        item.setItemMeta(meta);
        if (enchanted)
        {
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.addItemFlags(flags);
        return item;
    }

    public ItemFlag[] flags()
    {
        return flags;
    }

    public ItemBuilder flags(@NotNull final ItemFlag... flags)
    {
        this.flags = flags;
        return this;
    }

    public Material material()
    {
        return material;
    }

    public ItemBuilder material(@NotNull final Material material)
    {
        this.material = material;
        return this;
    }

    public Component name()
    {
        return name;
    }

    public ItemBuilder name(@NotNull final Component name)
    {
        this.name = name;
        if (this.name.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET)
            this.name = this.name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        return this;
    }

    /**
     * To put color or decoration in the name, prefer {@link ItemBuilder#name(Component)} or {@link ItemBuilder#name(String, TextColor,
     * TextDecoration)}
     */
    public ItemBuilder name(final String name)
    {
        if (name == null)
            return this;
        this.name = Component.text(name).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        return this;
    }

    public ItemBuilder name(final String name, TextColor color)
    {
        if (name == null)
            return this;
        this.name = Component.text(name, color).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        return this;
    }

    public ItemBuilder name(final String name, TextColor color, TextDecoration decoration)
    {
        if (name == null)
            return this;
        this.name = Component.text(name, color)
                             .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                             .decorate(decoration);
        return this;
    }

    public List<Component> lore()
    {
        return lore;
    }

    /**
     * @see ItemLoreBuilder
     */
    public ItemBuilder lore(final List<Component> lore)
    {
        this.lore = lore;
        return this;
    }

    /**
     * @see ItemLoreBuilder
     */
    public ItemBuilder lore(final Component lore)
    {
        this.lore = new ItemLoreBuilder().append(lore).get();
        return this;
    }

    /**
     * @see ItemLoreBuilder
     */
    public ItemBuilder lore(final String content, final TextColor color, final TextDecoration decoration)
    {
        this.lore = new ItemLoreBuilder().append(content, color, decoration).get();
        return this;
    }

    /**
     * @see ItemLoreBuilder
     */
    public ItemBuilder lore(final String content, final TextColor color)
    {
        this.lore = new ItemLoreBuilder().append(content, color).get();
        return this;
    }

    /**
     * Note: The string can contained values from {@link org.bukkit.ChatColor}, as they will be converted to components
     *
     * @see ItemLoreBuilder
     */
    public ItemBuilder lore(final String content)
    {
        this.lore = new ItemLoreBuilder().append(content).get();
        return this;
    }

    public boolean enchanted()
    {
        return enchanted;
    }

    public ItemBuilder enchanted(final boolean enchanted)
    {
        this.enchanted = enchanted;
        return this;
    }

    public int quantity()
    {
        return quantity;
    }

    public ItemBuilder quantity(final int quantity)
    {
        this.quantity = quantity;
        return this;
    }

    public int lineWidth()
    {
        return lineWidth;
    }

    public ItemBuilder lineWidth(final int lineWidth)
    {
        this.lineWidth = lineWidth;
        return this;
    }

    public ItemBuilder setCustomModelData(final int modelData)
    {
        this.customModelData = modelData;
        return this;
    }

    public ItemBuilder setColor(final Color color)
    {
        this.color = color;
        return this;
    }
}
