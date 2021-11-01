package onl.tesseract.tesseractlib.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.equipment.invocable.Boussole;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import onl.tesseract.tesseractlib.util.Util;
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
    TPlayer player;

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
     * @param player Player who will see the menu. Not necessarily the same as the player used to create the menu.
     */
    @Override
    public void open(Player player) {
        if (! this.player.getOfflinePlayer().isOnline())
            return;
        this.fill(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "*");
        this.addQuitButton();
        if (previous != null)
            this.addBackButton();

        this.putInvocationPower(player);

        addButton(49, new Button(new ItemBuilder(Material.NAME_TAG)
                                         .name("Tout désinvoquer", NamedTextColor.GOLD).build()
                , event -> {
            this.player.getEquipment().uninvokeAll();
            this.open(player);
        }));

        ItemStack chestplate;
        if (this.player.getEquipment().chestplate != null)
            chestplate = this.player.getEquipment().chestplate.getItem();
        else
            chestplate = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de plastron", NamedTextColor.DARK_AQUA).build();
        this.addButton(22, chestplate, event -> this.subMenu(this.player.getEquipment().unblockedChestplate, ChatColor.BLUE + "Emplacement de plastron", player));

        ItemStack helmet;
        if (this.player.getEquipment().helmet != null)
            helmet = this.player.getEquipment().helmet.getItem();
        else
            helmet = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de casque", NamedTextColor.DARK_AQUA).build();
        this.addButton(13, helmet, event -> this.subMenu(this.player.getEquipment().unblockedHelmet, ChatColor.BLUE + "Emplacement de casque", player));

        ItemStack leggings;
        if (this.player.getEquipment().leggings != null)
            leggings = this.player.getEquipment().leggings.getItem();
        else
            leggings = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de jambières", NamedTextColor.DARK_AQUA).build();
        this.addButton(31, leggings, event -> this.subMenu(this.player.getEquipment().unblockedLeggings, ChatColor.BLUE + "Emplacement de jambières", player));

        ItemStack boots;
        if (this.player.getEquipment().boots != null)
            boots = this.player.getEquipment().boots.getItem();
        else
            boots = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de bottes", NamedTextColor.DARK_AQUA).build();
        this.addButton(40, boots, event -> this.subMenu(this.player.getEquipment().unblockedBoots, ChatColor.BLUE + "Emplacement de bottes", player));

        ItemStack mainHand;
        if (this.player.getEquipment().mainHand != null)
            mainHand = this.player.getEquipment().mainHand.getItem();
        else
            mainHand = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de main principale", NamedTextColor.DARK_AQUA).build();
        this.addButton(21, mainHand, event -> this.subMenu(this.player.getEquipment().unblockedMainHand, ChatColor.BLUE + "Emplacement de main principale", player));

        ItemStack offHand;
        if (this.player.getEquipment().offHand != null)
            offHand = this.player.getEquipment().offHand.getItem();
        else
            offHand = new ItemBuilder(Material.STRUCTURE_VOID)
                    .name("Emplacement de main secondaire", NamedTextColor.DARK_AQUA).build();
        this.addButton(23, offHand, event -> this.subMenu(this.player.getEquipment().unblockedOffHand, ChatColor.BLUE + "Emplacement de main secondaire", player));

        // SECONDARY INVOCATIONS
        Boussole boussole = (Boussole) this.player.getEquipment().getLike(Boussole.class);
        if (boussole != null) {
            this.addButton(38, boussole.getItem(), boussole.isInvoked(), event -> mainHandInvocationMenu(boussole, player));
        }else {
            add(38, Material.BARRIER, Component.text("Emplacement de boussole", NamedTextColor.RED));
        }
        super.open(player);
    }

    void putInvocationPower(Player viewer) {
        PlayerInventory inv = player.getBukkitPlayer().getInventory();
        int goldAvailable = Util.countNonSpecialItems(inv, Material.GOLD_INGOT);
        final int goldCount = Math.min(goldAvailable, (100 - player.getEquipment().getInvocationPower()));
        var lore = new ItemLoreBuilder()
                .newline()
                .append("Puissance disponible ", NamedTextColor.DARK_PURPLE)
                .append(": ", NamedTextColor.DARK_GRAY)
                .append(player.getEquipment().getInvocationPower() + " %", NamedTextColor.LIGHT_PURPLE)
                .newline(2)
                .append("Vous perdez 5% de votre puissance d'invocation en mourrant avec au moins un objet invocable équipé.", NamedTextColor.GRAY)
                .newline(2)
                .append("Cliquez ici pour recharger votre pouvoir d'invocation avec ", NamedTextColor.RED)
                .append(goldCount + "", NamedTextColor.WHITE)
                .append(" lingots d'or dans votre inventaire.", NamedTextColor.RED)
                .newline(2)
                .append("(1 lingot d'or = 1%)", NamedTextColor.GRAY)
                .get();

        addButton(4, new Button(new ItemBuilder(Material.NETHER_STAR)
                                        .name("Puissance d'invocation", NamedTextColor.GOLD)
                                        .lore(lore).build()
                , event -> {
            if (goldCount == 0) return;
            // Remove the gold from the inventory, and get the number of gold removed
            int removed = Util.removeNonSpecialItems(this.player.getBukkitPlayer().getInventory(), Material.GOLD_INGOT, goldCount);
            // Update the invocation power
            this.player.getEquipment().addInvocationPower(removed);
            var comp = ChatFormats.EQUIPMENT.append(Component.text("Votre puissance d'invocation a été rechargée de "))
                    .append(Component.text(removed + "%", NamedTextColor.GRAY))
                    .append(Component.text(". Charge actuelle : "))
                    .append(Component.text(player.getEquipment().getInvocationPower() + "%", NamedTextColor.GOLD));
            this.player.getBukkitPlayer().sendMessage(comp);
            this.open(viewer);
        }));
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
                if (this.player.getEquipment().getInvocationPower() == 0) {
                    if (!invocable.toString().contains("onl.tesseract.item.invocable.Elytra")) {
                        var comp = ChatFormats.EQUIPMENT_ERROR
                                .append(Component.text("Impossible d'invoquer l'équipement, vous n'avez plus de pouvoir d'invocation. Rechargez là dans le menu à l'aide de lingots d'or."));
                        this.player.getBukkitPlayer().sendMessage(comp);
                        return;
                    }
                }
                if (invocable.slotType == EquipmentSlot.HAND) {
                    this.mainHandInvocationMenu(invocable, player);
                    return;
                }
                // Invoke the item
                if (! invocable.isInvoked()) {
                    invocable.invoke();
                    this.open(player);
                }
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