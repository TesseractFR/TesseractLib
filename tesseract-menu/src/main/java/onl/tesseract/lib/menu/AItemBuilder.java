package onl.tesseract.lib.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import onl.tesseract.lib.util.AItemLoreBuilder;
import onl.tesseract.lib.util.ItemLoreBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Abstract builder for crafting ItemStacks with various metadata.
 */
public abstract class AItemBuilder<T extends AItemBuilder<T>> {

    private Component name;
    private String nameStr;
    private TextColor color;
    private TextDecoration decoration;
    private Material material;
    private ItemStack base;
    private boolean enchanted;
    private List<Component> lore;
    private Color metaColor;
    private final List<ItemFlag> flags;
    private int amount;
    private Integer customModelData;
    private final Map<Enchantment, Integer> enchantments;

    protected AItemBuilder(Material material, ItemStack base, AItemBuilder<?> builder) {
        if (builder != null) {
            this.name = builder.name;
            this.nameStr = builder.nameStr;
            this.color = builder.color;
            this.decoration = builder.decoration;
            this.material = builder.material != null ? builder.material : material;
            this.base = builder.base != null ? builder.base : base;
            this.enchanted = builder.enchanted;
            this.lore = builder.lore;
            this.metaColor = builder.metaColor;
            this.flags = builder.flags != null ? new ArrayList<>(builder.flags) : new ArrayList<>();
            this.amount = builder.amount != 0 ? builder.amount : 1;
            this.customModelData = builder.customModelData;
            this.enchantments = builder.enchantments != null ? new java.util.HashMap<>(builder.enchantments) : new java.util.HashMap<>();
        } else {
            this.material = material;
            this.base = base;
            this.flags = new ArrayList<>();
            this.amount = 1;
            this.enchantments = new java.util.HashMap<>();
        }
    }

    public abstract T self();

    public T name(String name) {
        this.nameStr = name;
        return self();
    }

    public T name(String name, TextColor color) {
        this.nameStr = name;
        this.color = color;
        return self();
    }

    public T name(String name, TextColor color, TextDecoration decoration) {
        this.nameStr = name;
        this.color = color;
        this.decoration = decoration;
        return self();
    }

    public T name(Component name) {
        this.name = name;
        return self();
    }

    public T color(TextColor color) {
        this.color = color;
        return self();
    }

    public T metaColor(Color color) {
        this.metaColor = color;
        return self();
    }

    public T enchanted() {
        this.enchanted = true;
        return self();
    }

    public T enchanted(boolean enchanted) {
        this.enchanted = enchanted;
        return self();
    }

    public T lore(String lore) {
        this.lore = new ItemLoreBuilder().append(lore).get();
        return self();
    }

    public T lore(List<Component> lore) {
        this.lore = lore;
        return self();
    }

    public CustomHeadItemBuilder customHead(String data, String signature) {
        return new CustomHeadItemBuilder(data, signature, this);
    }

    /**
     * Starts a lore builder for chaining.
     */
    public ItemBuilderLoreBuilder<T> lore() {
        return new ItemBuilderLoreBuilder<>(self());
    }

    public T flags(ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return self();
    }

    public T amount(int amount) {
        this.amount = amount;
        return self();
    }

    public T customModelData(int customModelData) {
        this.customModelData = customModelData;
        return self();
    }

    public T addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return self();
    }

    protected T material(Material material) {
        this.material = material;
        return self();
    }

    /**
     * Gets the flags list. Use {@link #flags(ItemFlag...)} to add flags instead.
     */
    public List<ItemFlag> getFlags() {
        return flags;
    }

    public Material getMaterial() {
        return material;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    ItemStack build() {
        ItemStack item = base != null ? base : new ItemStack(material);
        item.editMeta(meta -> {
            Component comp = computeName();
            if (comp != null) {
                meta.displayName(comp);
            }
            if (enchanted && enchantments.isEmpty()) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
            if (metaColor != null) {
                if (meta instanceof PotionMeta potionMeta) {
                    potionMeta.setColor(metaColor);
                }
                if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                    leatherArmorMeta.setColor(metaColor);
                }
            }
            if (!flags.isEmpty()) {
                meta.addItemFlags(flags.toArray(new ItemFlag[0]));
            }
            if (lore != null) {
                meta.lore(lore);
            }
            if (customModelData != null) {
                meta.setCustomModelData(customModelData);
            }
        });
        item.setAmount(amount);
        return item;
    }

    private Component computeName() {
        Component nameComponent;
        if (nameStr != null) {
            nameComponent = Component.text(nameStr);
        } else if (name != null) {
            nameComponent = name;
        } else {
            return null;
        }
        nameComponent = nameComponent.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        if (color != null) {
            nameComponent = nameComponent.color(color);
        }
        if (decoration != null) {
            nameComponent = nameComponent.decorate(decoration);
        }
        return nameComponent;
    }

    /**
     * Inner builder for crafting lore with fluent API.
     */
    public static class ItemBuilderLoreBuilder<T extends AItemBuilder<T>> extends AItemLoreBuilder<ItemBuilderLoreBuilder<T>> {

        private final T parent;

        protected ItemBuilderLoreBuilder(T parent) {
            this.parent = parent;
        }

        public T buildLore() {
            parent.lore(get());
            return parent.self();
        }

        @Override
        public ItemBuilderLoreBuilder<T> self() {
            return this;
        }
    }
}
