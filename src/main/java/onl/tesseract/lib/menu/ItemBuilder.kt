package onl.tesseract.lib.menu

import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import onl.tesseract.lib.profile.PlayerProfileService
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.lib.util.AItemLoreBuilder
import onl.tesseract.lib.util.ItemLoreBuilder
import onl.tesseract.lib.util.menu.InventoryHeadIcons
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta

abstract class AItemBuilder<T : AItemBuilder<T>>(material: Material, base: ItemStack? = null, builder: AItemBuilder<*>? = null) {

    private var name: Component? = builder?.name
    private var nameStr: String? = builder?.nameStr
    private var color: TextColor? = builder?.color
    private var decoration: TextDecoration? = builder?.decoration
    private var material: Material = builder?.material ?: material
    private var base: ItemStack? = builder?.base ?: base
    private var enchanted: Boolean = builder?.enchanted == true
    private var lore: List<Component>? = builder?.lore
    private var metaColor: Color? = builder?.metaColor
    private var flags: MutableList<ItemFlag> = builder?.flags ?: mutableListOf()
    private var amount: Int = builder?.amount ?: 1
    private var customModelData: Int? = builder?.customModelData

    abstract fun self(): T

    fun name(name: String): T {
        this.nameStr = name
        return self()
    }

    fun name(name: String, color: TextColor): T {
        this.nameStr = name
        this.color = color
        return self()
    }

    fun name(name: String, color: TextColor, decoration: TextDecoration): T {
        this.nameStr = name
        this.color = color
        this.decoration = decoration
        return self()
    }

    fun name(name: Component): T {
        this.name = name
        return self()
    }

    fun color(color: TextColor): T {
        this.color = color
        return self()
    }

    fun metaColor(color: Color): T {
        this.metaColor = color
        return self()
    }

    fun enchanted(): T {
        this.enchanted = true
        return self()
    }

    fun enchanted(enchanted: Boolean): T {
        this.enchanted = enchanted
        return self()
    }

    fun lore(lore: String): T {
        this.lore = ItemLoreBuilder().append(lore).get()
        return self()
    }

    fun lore(lore: List<Component>): T {
        this.lore = lore
        return self()
    }

    fun customHead(data: String, signature: String?): CustomHeadItemBuilder {
        return CustomHeadItemBuilder(data, signature, this)
    }

    fun lore(): ItemBuilderLoreBuilder<T> = ItemBuilderLoreBuilder<T>()

    fun flags(vararg flags: ItemFlag): T {
        this.flags.addAll(flags)
        return self()
    }

    fun amount(amount: Int): T {
        this.amount = amount
        return self()
    }

    fun customModelData(customModelData: Int): T {
        this.customModelData = customModelData
        return self()
    }

    protected open fun material(material: Material): T {
        this.material = material
        return self()
    }

    protected open fun build(): ItemStack {
        val item = base ?: ItemStack(material)
        item.editMeta { meta ->
            computeName()?.let { meta.displayName(it) }
            if (enchanted) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
            metaColor?.let { color ->
                if (meta is PotionMeta)
                    meta.color = color
                if (meta is LeatherArmorMeta)
                    meta.setColor(color)
            }
            meta.addItemFlags(*this.flags.toTypedArray())
            lore?.let { meta.lore(it) }
            customModelData?.let { meta.setCustomModelData(it) }
        }
        item.amount = amount
        return item
    }

    private fun computeName(): Component? {
        var nameComponent = nameStr?.let { Component.text(it) } ?: name ?: return null
        color?.let { nameComponent = nameComponent.color(it) }
        decoration?.let { nameComponent = nameComponent.decorate(it) }
        return nameComponent
    }

    inner class ItemBuilderLoreBuilder<T : AItemBuilder<T>> : AItemLoreBuilder<ItemBuilderLoreBuilder<T>>() {

        fun buildLore(): T {
            this@AItemBuilder.lore(this.get())
            return this@AItemBuilder.self() as T
        }

        override fun self(): ItemBuilderLoreBuilder<T> {
            return this
        }
    }
}

open class ItemBuilder(material: Material, name: String? = null, base: ItemStack? = null) : AItemBuilder<ItemBuilder>(material, base) {

    constructor(material: Material) : this(material, null, null)

    constructor(base: ItemStack): this(base.type, base = base)

    init {
        name?.let { name(it) }
    }

    override fun self(): ItemBuilder = this

    public override fun build(): ItemStack {
        return super.build()
    }

    public override fun material(material: Material): ItemBuilder {
        return super.material(material)
    }
}

class CustomHeadItemBuilder(private val data: String, private val signature: String?, builder: AItemBuilder<*>?) :
    AItemBuilder<CustomHeadItemBuilder>(Material.PLAYER_HEAD, null, builder) {

    constructor(icon: InventoryHeadIcons) : this(icon.data, icon.signature, null)

    override fun self(): CustomHeadItemBuilder = this

    fun build(profileService: PlayerProfileService = ServiceContainer[PlayerProfileService::class.java]): ItemStack {
        val item = material(Material.PLAYER_HEAD)
            .build()
        item.editMeta {
            val profile = profileService.createProfile()
            profile.setProperty(ProfileProperty("textures", data, signature))
            (it as SkullMeta).playerProfile = profile
        }
        return item
    }
}
