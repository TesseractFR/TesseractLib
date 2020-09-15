package onl.tesseract.tesseractlib.menu;

import onl.tesseract.tesseractlib.equipment.invocable.Boussole;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormat;
import onl.tesseract.tesseractlib.util.InventoryMenu;
import onl.tesseract.tesseractlib.util.Util;
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
        this.fill(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "*", null);
        this.addQuitButton();
        if (previous != null)
            this.addBackButton();

        this.putInvocationPower(player);

        this.addButton(49, Material.NAME_TAG, ChatColor.GOLD + "Désinvoquer tout", null, event -> {
            this.player.getEquipment().uninvokeAll();
            this.open(player);
        });

        ItemStack chestplate;
        if (this.player.getEquipment().chestplate != null)
            chestplate = this.player.getEquipment().chestplate.getItem();
        else
            chestplate = Util.buildItem(Material.STRUCTURE_VOID, ChatColor.DARK_AQUA + "Emplacement de plastron", null);
        this.addButton(22, chestplate, event -> {
            this.subMenu(this.player.getEquipment().unblockedChestplate, ChatColor.BLUE + "Emplacement de plastron", player);
        });

        ItemStack helmet;
        if (this.player.getEquipment().helmet != null)
            helmet = this.player.getEquipment().helmet.getItem();
        else
            helmet = Util.buildItem(Material.STRUCTURE_VOID, ChatColor.DARK_AQUA + "Emplacement de casque", null);
        this.addButton(13, helmet, event -> {
            this.subMenu(this.player.getEquipment().unblockedHelmet, ChatColor.BLUE + "Emplacement de casque", player);
        });

        ItemStack leggings;
        if (this.player.getEquipment().leggings != null)
            leggings = this.player.getEquipment().leggings.getItem();
        else
            leggings = Util.buildItem(Material.STRUCTURE_VOID, ChatColor.DARK_AQUA + "Emplacement de jambières", null);
        this.addButton(31, leggings, event -> {
            this.subMenu(this.player.getEquipment().unblockedLeggings, ChatColor.BLUE + "Emplacement de jambières", player);
        });

        ItemStack boots;
        if (this.player.getEquipment().boots != null)
            boots = this.player.getEquipment().boots.getItem();
        else
            boots = Util.buildItem(Material.STRUCTURE_VOID, ChatColor.DARK_AQUA + "Emplacement de bottes", null);
        this.addButton(40, boots, event -> {
            this.subMenu(this.player.getEquipment().unblockedBoots, ChatColor.BLUE + "Emplacement de bottes", player);
        });

        ItemStack mainHand;
        if (this.player.getEquipment().mainHand != null)
            mainHand = this.player.getEquipment().mainHand.getItem();
        else
            mainHand = Util.buildItem(Material.STRUCTURE_VOID, ChatColor.DARK_AQUA + "Emplacement de main principale", null);
        this.addButton(21, mainHand, event -> {
            this.subMenu(this.player.getEquipment().unblockedMainHand, ChatColor.BLUE + "Emplacement de main principale", player);
        });

        ItemStack offHand;
        if (this.player.getEquipment().offHand != null)
            offHand = this.player.getEquipment().offHand.getItem();
        else
            offHand = Util.buildItem(Material.STRUCTURE_VOID, ChatColor.DARK_AQUA + "Emplacement de main secondaire", null);
        this.addButton(23, offHand, event -> {
            this.subMenu(this.player.getEquipment().unblockedOffHand, ChatColor.BLUE + "Emplacement de main secondaire", player);
        });

        // SECONDARY INVOCATIONS
        Boussole boussole = (Boussole) this.player.getEquipment().get(Boussole.class);
        if (boussole != null) {
            this.addButton(38, boussole.getItem(), boussole.isInvoked(), event -> {
                mainHandInvocationMenu(boussole, player);
            });
        }else {
            this.addInactiveButton(38, Material.BARRIER, ChatColor.RED + "Emplacement de boussole", null);
        }

        super.open(player);
    }

    void putInvocationPower(Player viewer) {
        PlayerInventory inv = player.getBukkitPlayer().getInventory();
        int goldAvailable = Util.countNonSpecialItems(inv, Material.GOLD_INGOT);
        final int goldCount = Math.min(goldAvailable, (100 - player.getEquipment().getInvocationPower()));
        String lore = ChatColor.DARK_PURPLE + "Puissance disponible " + ChatColor.DARK_GRAY + ": " +
                ChatColor.LIGHT_PURPLE + player.getEquipment().getInvocationPower() + " %" + NEW_LINE + NEW_LINE +
                ChatColor.GRAY + "Vous perdez 5% de votre puissance d'invocation en mourrant avec au moins un objet " +
                "invocable équipé." + NEW_LINE + NEW_LINE +
                ChatColor.RED + "Cliquez ici pour recharger votre pouvoir d'invocation avec " + ChatColor.WHITE + goldCount +
                ChatColor.RED + " lingots d'or dans votre inventaire." + NEW_LINE + NEW_LINE +
                ChatColor.GRAY + "(1 lingot d'or = 1%)";

        this.addButton(4, Material.NETHER_STAR, ChatColor.GOLD + "Puissance d'invocation", lore, event -> {
            if (goldCount == 0) return;
            // Remove the gold from the inventory, and get the number of gold removed
            int removed = Util.removeNonSpecialItems(this.player.getBukkitPlayer().getInventory(), Material.GOLD_INGOT, goldCount);
            // Update the invocation power
            this.player.getEquipment().addInvocationPower(removed);
            this.player.getBukkitPlayer().sendMessage(ChatFormat.EQUIPMENT + "Votre puissance d'invocation a été rechargée" +
                    " de " + ChatColor.GOLD + removed + "%" + ChatColor.GRAY + ". Charge actuelle : " + ChatColor.GOLD +
                    this.player.getEquipment().getInvocationPower() + "%");
            this.open(viewer);
        });
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
                    this.player.getBukkitPlayer().sendMessage(ChatFormat.EQUIPMENT_ERROR + "Impossible d'invoquer l'équipement, " +
                            "vous n'avez plus de pouvoir d'invocation. Rechargez là dans le menu à l'aide de lingots d'or.");
                    return;
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
            subMenu.addInactiveButton(i, Material.RED_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "*", null);
        }
        subMenu.addInactiveButtons(new int[] {46, 47, 48, 50, 51, 52}, Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "*", null);

        Invocable finalInvoked = invoked;
        subMenu.addButton(49, Material.NAME_TAG, ChatColor.GOLD + "Désinvoquer cet équipement", null, event -> {
            if (finalInvoked != null) {
                finalInvoked.uninvoke();
                this.open(player);
            }
        });
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
                item = Util.buildItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "Libre", ChatColor.GRAY + "Cliquez pour invoquer votre équipement ici");
            int finalI = i;
            menu.addButton(i, item, event -> {
                if (finalI == invocable.slot)
                    invocable.uninvoke();
                else
                    invocable.invoke(finalI);
                mainHandInvocationMenu(invocable, player);
            });
        }
        menu.addInactiveButton(13, Material.ACACIA_SIGN, " ", ChatColor.GRAY + "Séléctionnez un slot pour invoquer " +
                "votre équipement. L'invocation déplacera ou désinvoquera l'objet déjà présent sur le slot.");
        menu.addInactiveButtons(new int[] {10,11,12,14,15,16}, Material.GRAY_STAINED_GLASS_PANE, " ", null);
        menu.addBackButton();
        menu.addQuitButton();
        menu.open(player);
    }
}