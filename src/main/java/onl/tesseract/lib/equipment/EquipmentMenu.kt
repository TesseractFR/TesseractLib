package onl.tesseract.lib.equipment

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.lib.event.equipment.invocable.Boussole
import onl.tesseract.lib.menu.Button
import onl.tesseract.lib.menu.ItemBuilder
import onl.tesseract.lib.menu.Menu
import onl.tesseract.lib.menu.MenuSize
import onl.tesseract.tesseractlib.util.ItemLoreBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class EquipmentMenu(val player: Player, val service: EquipmentService, previous: Menu? = null) : Menu(
    MenuSize.Six,
    "     Équipements invocables",
    NamedTextColor.BLUE,
    previous,
) {

    override fun placeButtons(viewer: Player) {
        if (!player.isOnline) return

        fill(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("*").color(NamedTextColor.DARK_GRAY).build())
        addCloseButton()
        if (previous != null)
            addBackButton()

        addButton(
            49, Button(ItemBuilder(Material.NAME_TAG)
                .name("Tout désinvoquer", NamedTextColor.GOLD).build(), {
                service.uninvokeAll(this.player)
                this.open(viewer)
            })
        )

        val equipment = service.getEquipment(player.uniqueId)

        placeEquipmentSlotButton(equipment, EquipmentSlot.HEAD, "Emplacement de casque", 13, viewer)
        placeEquipmentSlotButton(equipment, EquipmentSlot.CHEST, "Emplacement de plastron", 22, viewer)
        placeEquipmentSlotButton(equipment, EquipmentSlot.LEGS, "Emplacement de jambières", 31, viewer)
        placeEquipmentSlotButton(equipment, EquipmentSlot.FEET, "Emplacement de bottes", 40, viewer)
        placeEquipmentSlotButton(equipment, EquipmentSlot.HAND, "Emplacement de main principale", 21, viewer)
        placeEquipmentSlotButton(equipment, EquipmentSlot.OFF_HAND, "Emplacement de main secondaire", 23, viewer)

        val boussole = equipment.get(Boussole::class.java)
        if (boussole != null) {
            addButton(38, ItemBuilder(boussole.getItem()).enchanted(boussole.isInvoked).build()) {
                mainHandInvocationMenu(boussole, viewer)
            }
        } else {
            addButton(38, ItemBuilder(Material.BARRIER).name("Emplacement de boussole", NamedTextColor.RED).build())
        }
    }

    private fun placeEquipmentSlotButton(
        equipment: Equipment,
        slotType: EquipmentSlot,
        text: String,
        index: Int,
        viewer: Player,
    ) {
        val item: ItemStack = equipment[slotType]?.getItem()
            ?: ItemBuilder(Material.STRUCTURE_VOID).name(text, NamedTextColor.DARK_AQUA).build()
        this.addButton(index, item) {
            subMenu(equipment.getAll(slotType), text, viewer)
        }
    }

    fun subMenu(invocables: List<Invocable>, title: String, viewer: Player) {
        val subMenu = Menu(MenuSize.Six, Component.text(title, NamedTextColor.BLUE), this)
        subMenu.open(viewer)
        var invoked: Invocable? = null
        var index = 0
        invocables.forEach { invocable ->
            subMenu.addButton(index++, invocable.getItem()) {
                invokeHandler(invocable, viewer)
            }
            if (invocable.isInvoked) invoked = invocable
        }
        while (index < 45) {
            subMenu.addButton(index, ItemBuilder(Material.BARRIER).name("*", NamedTextColor.DARK_GRAY).build())
            index++
        }
        subMenu.fill(
            arrayOf(46, 47, 48, 50, 51, 52), ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(
                "*",
                NamedTextColor.DARK_GRAY
            ).build()
        )

        subMenu.addButton(
            49,
            ItemBuilder(Material.NAME_TAG).name("Désinvoquer cet équipement", NamedTextColor.GOLD).build()
        ) {
            invoked?.let {
                service.uninvoke(player, it)
                open(viewer)
            }
        }
        subMenu.addBackButton()
        subMenu.addCloseButton()
    }

    fun invokeHandler(invocable: Invocable, viewer: Player) {
        if (invocable.slotType == EquipmentSlot.HAND) {
            this.mainHandInvocationMenu(invocable, player)
            return
        }
        // Invoke the item
        if (!invocable.isInvoked) {
            service.invoke(this.player, invocable.javaClass, null, true)
            this.open(viewer)
        }
    }

    fun mainHandInvocationMenu(invocable: Invocable, viewer: Player) {
        val menu = Menu(MenuSize.Two, "  Séléction du slot d'invocation", NamedTextColor.DARK_AQUA, this)
        menu.open(player)

        val inv = this.player.inventory
        for (i in 0 until 9) {
            val item = inv.getItem(i)
                ?: ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .name("Libre", NamedTextColor.GREEN)
                    .lore(
                        ItemLoreBuilder()
                            .append("Cliquez pour invoquer votre équipement ici", NamedTextColor.GRAY)
                            .get()
                    ).build()
            menu.addButton(i, item) {
                if (i == invocable.handSlot)
                    service.uninvoke(this.player, invocable)
                else
                    service.invoke(this.player, invocable.javaClass, i, true)
                mainHandInvocationMenu(invocable, viewer)
            }
        }
        menu.addButton(
            13, ItemBuilder(Material.ACACIA_SIGN)
                .name(" ")
                .lore(
                    ItemLoreBuilder()
                        .append(
                            "Séléctionnez un slot pour invoquer votre équipement. L'invocation déplacera ou désinvoquera l'objet déjà présent "
                                    + "sur le slot.", NamedTextColor.GRAY
                        ).get()
                )
                .build()
        )
        menu.fill(
            arrayOf(10, 11, 12, 14, 15, 16),
            ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).build()
        )
        menu.addBackButton()
        menu.addCloseButton()
    }
}