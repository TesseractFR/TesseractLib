package onl.tesseract.tesseractlib.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import onl.tesseract.tesseractlib.player.Gender
import onl.tesseract.tesseractlib.player.TPlayer
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

        addButton(2, male, Component.text(Gender.MALE.getName(), NamedTextColor.GOLD, TextDecoration.BOLD),
            Component.text("Cliquez ici pour définir votre genre en Masculin.", NamedTextColor.GRAY)) {
            player.sendMessage(Component.text("Vous avez bien changé votre genre en ${Gender.MALE.getName()} !", NamedTextColor.GREEN))
            player.gender = Gender.MALE
            this.close()
        }

        addButton(4, female, Component.text(Gender.FEMALE.getName(), NamedTextColor.GOLD, TextDecoration.BOLD),
            Component.text("Cliquez ici pour définir votre genre en Féminin.", NamedTextColor.GRAY)) {
            player.sendMessage(Component.text("Vous avez bien changé votre genre en ${Gender.FEMALE.getName()}", NamedTextColor.GREEN))
            player.gender = Gender.FEMALE
            this.close()
        }

        addButton(6, other, Component.text(Gender.OTHER.getName(), NamedTextColor.GOLD, TextDecoration.BOLD),
            Component.text("Cliquez ici pour définir votre genre en \"Non renseigné\".", NamedTextColor.GRAY)) {
            player.sendMessage(Component.text("Vous avez bien changé votre genre en ${Gender.OTHER.getName()} !", NamedTextColor.GREEN))
            player.gender = Gender.OTHER
            this.close()
        }

        this.addBackButton()
        this.addQuitButton()
        super.open(viewer)
    }

    companion object {
        val male: ItemStack =  getCustomHead(
            "",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2EwOGQwZGFiYzQzNGEwOTNmMDk4YmFmNTA1YjE2NWMxNGNiZTk2NDU3M2VkOGU5ZTYxODUxNTg5MTc5NTcwIn19fQ==",
            ""
        )
        val female: ItemStack = getCustomHead(
            "",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWEyMjA2ODJhYjdmMjBmYmNlOTEzMDY2MGVjOTgyMjliMzMyMGEyMzlhNDc4MmViMTUzMzg1ZWRhOWJmYmZkOCJ9fX0=",
            ""
        )
        val other = ItemStack(Material.PLAYER_HEAD).apply {
            val skullMeta = this.itemMeta as SkullMeta
            skullMeta.owningPlayer = Bukkit.getOfflinePlayer("MHF_Question")
            this.itemMeta = skullMeta
        }
    }
}
