package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.equipment.Equipment;
import onl.tesseract.lib.equipment.EquipmentService;
import onl.tesseract.lib.equipment.Invocable;
import onl.tesseract.lib.service.ServiceContainer;
import onl.tesseract.lib.event.equipment.invocable.Boussole;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.tesseractlib.util.menu.Button;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class EquipmentMenu extends InventoryMenu {
    final Player player;
    private final EquipmentService equipmentService;

    /**
     * Opens the equipment menu
     * @param player Player who opens the menu
     */
    public EquipmentMenu(Player player, EquipmentService equipmentService)
    {
        super(54, ChatColor.BLUE + "     Équipements invocables");
        this.player = player;
        //this.previous = new BoussoleMenu(player);
        this.equipmentService = equipmentService;
    }

    public EquipmentMenu(Player player, EquipmentService equipmentService, InventoryMenu previous) {
        super(54, ChatColor.BLUE + "     Équipements invocables");
        this.player = player;
        this.equipmentService = equipmentService;
        this.previous = previous;
    }

    /**
     * Opens the equipment menu.
     * @param viewer Player who will see the menu. Not necessarily the same as the player used to create the menu.
     */
    @Override
    public void open(Player viewer) {
        if (!this.player.isOnline())
            return;
        this.fill(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "*");
        this.addQuitButton();
        EquipmentService equipmentService = ServiceContainer.get(EquipmentService.class);
        if (previous != null)
            this.addBackButton();

        addButton(49, new Button(new ItemBuilder(Material.NAME_TAG)
                                         .name("Tout désinvoquer", NamedTextColor.GOLD).build()
                , event -> {
            equipmentService.uninvokeAll(this.player);
            this.open(viewer);
        }));

        Equipment equipment = equipmentService.getEquipment(player.getUniqueId());
        ItemStack chestplate;
        if (equipment.get(EquipmentSlot.CHEST) != null)
            chestplate = equipment.get(EquipmentSlot.CHEST).getItem();
        else
            chestplate = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de plastron", NamedTextColor.DARK_AQUA).build();
        this.addButton(22, chestplate, event -> this.subMenu(equipment.getAll(EquipmentSlot.CHEST), ChatColor.BLUE + "Emplacement de plastron", viewer));

        ItemStack helmet;
        if (equipment.get(EquipmentSlot.HEAD) != null)
            helmet = equipment.get(EquipmentSlot.HEAD).getItem();
        else
            helmet = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de casque", NamedTextColor.DARK_AQUA).build();
        this.addButton(13, helmet, event -> this.subMenu(equipment.getAll(EquipmentSlot.HEAD), ChatColor.BLUE + "Emplacement de casque", viewer));

        ItemStack leggings;
        if (equipment.get(EquipmentSlot.LEGS) != null)
            leggings = equipment.get(EquipmentSlot.LEGS).getItem();
        else
            leggings = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de jambières", NamedTextColor.DARK_AQUA).build();
        this.addButton(31, leggings, event -> this.subMenu(equipment.getAll(EquipmentSlot.LEGS), ChatColor.BLUE + "Emplacement de jambières", viewer));

        ItemStack boots;
        if (equipment.get(EquipmentSlot.FEET) != null)
            boots = equipment.get(EquipmentSlot.FEET).getItem();
        else
            boots = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de bottes", NamedTextColor.DARK_AQUA).build();
        this.addButton(40, boots, event -> this.subMenu(equipment.getAll(EquipmentSlot.FEET), ChatColor.BLUE + "Emplacement de bottes", viewer));

        ItemStack mainHand;
        if (equipment.get(EquipmentSlot.HAND) != null)
            mainHand = equipment.get(EquipmentSlot.HAND).getItem();
        else
            mainHand = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de main principale", NamedTextColor.DARK_AQUA).build();
        this.addButton(21, mainHand, event -> this.subMenu(equipment.getAll(EquipmentSlot.HAND), ChatColor.BLUE + "Emplacement de main principale", viewer));

        ItemStack offHand;
        if (equipment.get(EquipmentSlot.OFF_HAND) != null)
            offHand = equipment.get(EquipmentSlot.OFF_HAND).getItem();
        else
            offHand = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de main secondaire", NamedTextColor.DARK_AQUA).build();
        this.addButton(23, offHand, event -> this.subMenu(equipment.getAll(EquipmentSlot.OFF_HAND), ChatColor.BLUE + "Emplacement de main secondaire", viewer));

        // SECONDARY INVOCATIONS
        Boussole boussole = equipment.get(Boussole.class);
        if (boussole != null) {
            this.addButton(38, boussole.getItem(), boussole.isInvoked(), event -> mainHandInvocationMenu(boussole, viewer));
        }else {
            add(38, Material.BARRIER, Component.text("Emplacement de boussole", NamedTextColor.RED));
        }
        super.open(viewer);
    }

    /**
     * Open a subMenu to select an equipment to invoke
     * @param items List of items that can be invoked
     * @param title Title of the menu
     * @param player Player that will see the menu
     */
    public void subMenu(List<Invocable> items, String title, Player player) {
        InventoryMenu subMenu = new InventoryMenu(54, title, this);
        Invocable invoked = null;
        int i = 0;
        // Add the invokables
        for (; i < items.size(); i++) {
            Invocable invocable = items.get(i);
            // If this invokable is invoked, remember it for later
            if (invocable.isInvoked())
                invoked = invocable;
            subMenu.addButton(i, items.get(i).getItem(), event -> {
                invokeHandler(player, invocable);
            });
        }
        for (; i < 45; i++) {
            subMenu.add(i, Material.RED_STAINED_GLASS_PANE, Component.text("*", NamedTextColor.DARK_GRAY));
        }
        subMenu.add(new int[] {46, 47, 48, 50, 51, 52}, Material.RED_STAINED_GLASS_PANE, Component.text("*", NamedTextColor.DARK_GRAY));

        Invocable finalInvoked = invoked;
        subMenu.addButton(49, new Button(new ItemBuilder(Material.NAME_TAG)
                                                 .name("Désinvoquer cet équipement", NamedTextColor.GOLD).build()
                , event -> {
            if (finalInvoked != null) {
                equipmentService.uninvoke(this.player, finalInvoked);
                this.open(player);
            }
        }));
        subMenu.addBackButton();
        subMenu.addQuitButton();
        subMenu.open(player);
    }

    protected void invokeHandler(final Player player, final Invocable invocable)
    {
        if (invocable.getSlotType() == EquipmentSlot.HAND) {
            this.mainHandInvocationMenu(invocable, player);
            return;
        }
        // Invoke the item
        if (! invocable.isInvoked()) {
            equipmentService.invoke(this.player, invocable.getClass(), null, true);
            this.open(player);
        }
    }

    /**
     * Opens the slot selection to invoke the mainHand item
     * @param invocable Item to invoke
     * @param player player that will see the menu
     */
    public void mainHandInvocationMenu(onl.tesseract.lib.equipment.Invocable invocable, Player player) {
        InventoryMenu menu = new InventoryMenu(18, ChatColor.DARK_AQUA + "  Séléction du slot d'invocation", this);

        PlayerInventory inv = this.player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null)
                item = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .name("Libre", NamedTextColor.GREEN)
                        .lore(new ItemLoreBuilder()
                                      .append("Cliquez pour invoquer votre équipement ici", NamedTextColor.GRAY)
                                      .get()).build();
            int finalI = i;
            menu.addButton(i, item, event -> {
                EquipmentService service = ServiceContainer.get(EquipmentService.class);
                if (finalI == invocable.getHandSlot())
                    service.uninvoke(this.player, invocable);
                else
                    service.invoke(this.player, invocable.getClass(), finalI, true);
                mainHandInvocationMenu(invocable, player);
            });
        }
        menu.add(13, new ItemBuilder(Material.ACACIA_SIGN, Component.text(" "))
                 .lore(new ItemLoreBuilder()
                       .append("Séléctionnez un slot pour invoquer votre équipement. L'invocation déplacera ou désinvoquera l'objet déjà présent "
                                       + "sur le slot.", NamedTextColor.GRAY).get())
                 .build());
        menu.add(new int[] {10,11,12,14,15,16}, Material.GRAY_STAINED_GLASS_PANE, Component.text(" "));
        menu.addBackButton();
        menu.addQuitButton();
        menu.open(player);
    }
}