package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.equipment.invocable.Boussole;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
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
    final TPlayer player;

    /**
     * Opens the equipment menu
     * @param player Player who opens the menu
     */
    public EquipmentMenu(TPlayer player)
    {
        super(54, ChatColor.BLUE + "     Équipements invocables");
        this.player = player;
        //this.previous = new BoussoleMenu(player);
    }

    public EquipmentMenu(TPlayer player, InventoryMenu previous) {
        super(54, ChatColor.BLUE + "     Équipements invocables");
        this.player = player;
        this.previous = previous;
    }

    /**
     * Opens the equipment menu.
     * @param viewer Player who will see the menu. Not necessarily the same as the player used to create the menu.
     */
    @Override
    public void open(Player viewer) {
        if (! this.player.getOfflinePlayer().isOnline())
            return;
        this.fill(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "*");
        this.addQuitButton();
        if (previous != null)
            this.addBackButton();

        addButton(49, new Button(new ItemBuilder(Material.NAME_TAG)
                                         .name("Tout désinvoquer", NamedTextColor.GOLD).build()
                , event -> {
            this.player.getEquipment().uninvokeAll();
            this.open(viewer);
        }));

        ItemStack chestplate;
        if (this.player.getEquipment().chestplate != null)
            chestplate = this.player.getEquipment().chestplate.getItem();
        else
            chestplate = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de plastron", NamedTextColor.DARK_AQUA).build();
        this.addButton(22, chestplate, event -> this.subMenu(this.player.getEquipment().unblockedChestplate, ChatColor.BLUE + "Emplacement de plastron", viewer));

        ItemStack helmet;
        if (this.player.getEquipment().helmet != null)
            helmet = this.player.getEquipment().helmet.getItem();
        else
            helmet = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de casque", NamedTextColor.DARK_AQUA).build();
        this.addButton(13, helmet, event -> this.subMenu(this.player.getEquipment().unblockedHelmet, ChatColor.BLUE + "Emplacement de casque", viewer));

        ItemStack leggings;
        if (this.player.getEquipment().leggings != null)
            leggings = this.player.getEquipment().leggings.getItem();
        else
            leggings = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de jambières", NamedTextColor.DARK_AQUA).build();
        this.addButton(31, leggings, event -> this.subMenu(this.player.getEquipment().unblockedLeggings, ChatColor.BLUE + "Emplacement de jambières", viewer));

        ItemStack boots;
        if (this.player.getEquipment().boots != null)
            boots = this.player.getEquipment().boots.getItem();
        else
            boots = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de bottes", NamedTextColor.DARK_AQUA).build();
        this.addButton(40, boots, event -> this.subMenu(this.player.getEquipment().unblockedBoots, ChatColor.BLUE + "Emplacement de bottes", viewer));

        ItemStack mainHand;
        if (this.player.getEquipment().mainHand != null)
            mainHand = this.player.getEquipment().mainHand.getItem();
        else
            mainHand = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de main principale", NamedTextColor.DARK_AQUA).build();
        this.addButton(21, mainHand, event -> this.subMenu(this.player.getEquipment().unblockedMainHand, ChatColor.BLUE + "Emplacement de main principale", viewer));

        ItemStack offHand;
        if (this.player.getEquipment().offHand != null)
            offHand = this.player.getEquipment().offHand.getItem();
        else
            offHand = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de main secondaire", NamedTextColor.DARK_AQUA).build();
        this.addButton(23, offHand, event -> this.subMenu(this.player.getEquipment().unblockedOffHand, ChatColor.BLUE + "Emplacement de main secondaire", viewer));

        // SECONDARY INVOCATIONS
        Boussole boussole = (Boussole) this.player.getEquipment().getLike(Boussole.class);
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
                finalInvoked.uninvoke();
                this.open(player);
            }
        }));
        subMenu.addBackButton();
        subMenu.addQuitButton();
        subMenu.open(player);
    }

    protected void invokeHandler(final Player player, final Invocable invocable)
    {
        if (invocable.slotType == EquipmentSlot.HAND) {
            this.mainHandInvocationMenu(invocable, player);
            return;
        }
        // Invoke the item
        if (! invocable.isInvoked()) {
            invocable.invoke();
            this.open(player);
        }
    }

    /**
     * Opens the slot selection to invoke the mainHand item
     * @param invocable Item to invoke
     * @param player player that will see the menu
     */
    public void mainHandInvocationMenu(Invocable invocable, Player player) {
        InventoryMenu menu = new InventoryMenu(18, ChatColor.DARK_AQUA + "  Séléction du slot d'invocation", this);

        PlayerInventory inv = this.player.getBukkitPlayer().getInventory();
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
                if (finalI == invocable.slot)
                    invocable.uninvoke();
                else
                    invocable.invoke(finalI);
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