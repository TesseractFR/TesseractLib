package onl.tesseract.tesseractlib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemBuilder {
    protected ItemStack base;
    protected Material material;
    protected Component name;
    protected List<Component> lore;
    protected boolean enchanted;
    protected int quantity;
    protected int lineWidth;
    protected ItemFlag[] flags;

    public ItemBuilder(@NotNull final Material material, final Component name, final List<Component> lore, final boolean enchanted, final int quantity,
                       final int lineWidth)
    {
        this.material = material;
        this.name = name;
        if (name != null && this.name.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET)
            this.name = this.name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        this.lore = lore;
        this.enchanted = enchanted;
        this.quantity = quantity;
        this.lineWidth = lineWidth;
        this.flags = ItemFlag.values();
    }

    public ItemBuilder(@NotNull final Material material, final Component name, final List<Component> lore, final boolean enchanted)
    {
        this(material, name, lore, enchanted, 1, 35);
    }

    public ItemBuilder(@NotNull final Material material, final Component name, final List<Component> lore)
    {
        this(material, name, lore, false, 1, 35);
    }

    public ItemBuilder(@NotNull final Material material, final Component name)
    {
        this(material, name, null, false, 1, 35);
    }

    public ItemBuilder(@NotNull final Material material)
    {
        this(material, null, null, false, 1, 35);
    }

    public ItemBuilder(final ItemStack base)
    {
        this.base = base;
        this.material = null;
    }

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

    public ItemBuilder flags(final ItemFlag... flags)
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

    public ItemBuilder name(final String name)
    {
        this.name = Component.text(name).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        return this;
    }

    public ItemBuilder name(final String name, TextColor color)
    {
        this.name = Component.text(name, color).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);;
        return this;
    }

    public ItemBuilder name(final String name, TextColor color, TextDecoration decoration)
    {
        this.name = Component.text(name, color)
                             .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                             .decorate(decoration);
        return this;
    }

    public List<Component> lore()
    {
        return lore;
    }

    public ItemBuilder lore(final List<Component> lore)
    {
        this.lore = lore;
        return this;
    }

    public ItemBuilder lore(final Component lore)
    {
        this.lore = new ItemLoreBuilder().append(lore).get();
        return this;
    }

    public ItemBuilder lore(final String content, final TextColor color, final TextDecoration decoration)
    {
        this.lore = new ItemLoreBuilder().append(content, color, decoration).get();
        return this;
    }

    public ItemBuilder lore(final String content, final TextColor color)
    {
        this.lore = new ItemLoreBuilder().append(content, color).get();
        return this;
    }

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
}
