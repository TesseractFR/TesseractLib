package onl.tesseract.tesseractlib.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import onl.tesseract.tesseractlib.player.Gender
import onl.tesseract.tesseractlib.player.TPlayer
import onl.tesseract.tesseractlib.util.ItemBuilder
import onl.tesseract.tesseractlib.util.ItemLoreBuilder
import onl.tesseract.tesseractlib.util.menu.InventoryMenu
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class GenderMenu(private val player: TPlayer, previous: InventoryMenu) :

    InventoryMenu(9, Component.text("Choisissez votre genre", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD), previous) {

    override fun open(viewer: Player) {
        this.fill(Material.GRAY_STAINED_GLASS_PANE, " ")

        addButton(2, createMaleItem()){
            player.sendMessage(Component.text("Vous avez bien changé votre genre en ${Gender.MALE.getName()} !", NamedTextColor.GREEN))
            player.gender = Gender.MALE
            player.save()
            this.close()
        }

        addButton(4, createFemaleItem()) {
            player.sendMessage(Component.text("Vous avez bien changé votre genre en ${Gender.FEMALE.getName()} !", NamedTextColor.GREEN))
            player.gender = Gender.FEMALE
            player.save()
            this.close()
        }

        addButton(6, createOtherItem()) {
            player.sendMessage(Component.text("Vous avez bien changé votre genre en ${Gender.OTHER.getName()} !", NamedTextColor.GREEN))
            player.gender = Gender.OTHER
            player.save()
            this.close()
        }

        this.addBackButton()
        this.addQuitButton()
        super.open(viewer)
    }

    private fun createMaleItem(): ItemStack {
        val teteHomme = getCustomHead("", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2EwOGQwZGFiYzQzNGEwOTNmMDk4YmFmNTA1YjE2NWMxNGNiZTk2NDU3M2VkOGU5ZTYxODUxNTg5MTc5NTcwIn19fQ==", "")
        val ilb = ItemLoreBuilder()
            .newline()
            .append("Cliquez ici pour définir votre genre en ${Gender.MALE.getName()}.", NamedTextColor.GRAY)
        return ItemBuilder(teteHomme)
            .name(Gender.MALE.getName(), NamedTextColor.GOLD, TextDecoration.BOLD)
            .lore(ilb.get())
            .build()
    }

    private fun createFemaleItem(): ItemStack {
        val teteFemme = getCustomHead("", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWEyMjA2ODJhYjdmMjBmYmNlOTEzMDY2MGVjOTgyMjliMzMyMGEyMzlhNDc4MmViMTUzMzg1ZWRhOWJmYmZkOCJ9fX0=", "")
        val ilb = ItemLoreBuilder()
            .newline()
            .append("Cliquez ici pour définir votre genre en ${Gender.FEMALE.getName()}.", NamedTextColor.GRAY)
        return ItemBuilder(teteFemme)
            .name(Gender.FEMALE.getName(), NamedTextColor.GOLD, TextDecoration.BOLD)
            .lore(ilb.get())
            .build()
    }

    private fun createOtherItem(): ItemStack {
        val teteAutre = getCustomHead("", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19", "")
        val ilb = ItemLoreBuilder()
            .newline()
            .append("Cliquez ici pour définir votre genre en ${Gender.OTHER.getName()}.", NamedTextColor.GRAY)
        return ItemBuilder(teteAutre)
            .name(Gender.OTHER.getName(), NamedTextColor.GOLD, TextDecoration.BOLD)
            .lore(ilb.get())
            .build()
    }
}
