package onl.tesseract.lib.menu

import onl.tesseract.lib.util.menu.InventoryHeadIcons
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class MenuItems(private val supplier: () -> ItemStack) {
    Barrier({
        ItemBuilder(Material.BARRIER, " ").build()
    }),
    PreviousPage({
        ItemBuilder(Material.PLAYER_HEAD)
                .customHead(InventoryHeadIcons.LEFT_ARROW.data, InventoryHeadIcons.LEFT_ARROW.signature)
                .name("Précédent")
                .build()
    }),
    NextPage({
        ItemBuilder(Material.PLAYER_HEAD)
                .customHead(InventoryHeadIcons.RIGHT_ARROW.data, InventoryHeadIcons.RIGHT_ARROW.signature)
                .name("Suivant")
                .build()
    }),
    ;

    fun get(): ItemStack = supplier()
}