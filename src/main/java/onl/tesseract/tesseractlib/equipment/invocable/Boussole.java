package onl.tesseract.tesseractlib.equipment.invocable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormat;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class Boussole extends Invocable {
    BukkitTask propulsionTask;
    BoussoleMenu menu;

    public Boussole(Equipment equipment)
    {
        super(equipment, EquipmentSlot.HAND, "INVOCABLE_BOUSSOLE", createItem());
        menu = new BoussoleMenu(equipment.getPlayer());
    }

    public Boussole(Equipment equipment, Map<String, Object> yamlMap)
    {
        super(equipment, EquipmentSlot.HAND, "INVOCABLE_BOUSSOLE", createItem(), yamlMap);
        menu = new BoussoleMenu(equipment.getPlayer());
    }

    static ItemStack createItem() {
        String left = ChatColor.DARK_GRAY + "« " + ChatColor.GRAY;
        String right = ChatColor.DARK_GRAY + " »" + Util.NEW_LINE;
        String wave = ChatColor.DARK_GRAY + "~ " + ChatColor.DARK_AQUA;
        return new ItemBuilder(Material.COMPASS)
                .name("Boussole des voeux", NamedTextColor.BLUE)
                .lore(Util.NEW_LINE
                              + left + "Des possibilités incroyables !" + right + wave + "F.I.A (Force d'Intervention Ailée) " + wave + Util.NEW_LINE
                              + Util.NEW_LINE + left + "Un concentré de magie à l'état pur !" + right + wave + "Parangon Transport Inc. "
                              + wave + Util.NEW_LINE + Util.NEW_LINE + left + "À utiliser sans modération !" + right + wave
                              + "Flying Whales Corp. " + wave + Util.NEW_LINE
                              + Util.NEW_LINE + left + "La boussole des voeux me donne tout ce que je veux !" + right + wave
                              + "Anonyme " + wave + Util.NEW_LINE
                              + Util.NEW_LINE + ChatColor.DARK_GREEN + ChatColor.UNDERLINE + "Utilisation en main :" + ChatColor.RESET
                              + ChatColor.GREEN + " Clic gauche pour décoller avec les ailes.")
                .build();
    }

    @Override
    protected void onUninvoke(boolean manualUninvocation)
    {

    }

    @Override
    protected void onInvoke(boolean manualInvocation)
    {

    }

    @Override
    public void use(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.LEFT_CLICK_AIR)
        {
            // If the elytra are invoked
            TPlayer player = TPlayer.get(event.getPlayer());
            if (TPlayer.get(event.getPlayer()).getEquipment().get(EquipmentSlot.CHEST) instanceof Elytra)
            {
                if (event.getPlayer().isGliding() && event.getPlayer().getLocation().getBlock().getType() != Material.WATER
                        || (propulsionTask != null && !propulsionTask.isCancelled())) return;
                Elytra el = (Elytra) player.getEquipment().get(EquipmentSlot.CHEST);
                player.sendMessage(ChatFormats.EQUIPMENT.append(Component.text("Décollage dans 3 secondes... Regardez en l'air !")));
                propulsionTask = new BukkitRunnable() {
                    @Override
                    public void run()
                    {
                        if (! player.getOfflinePlayer().isOnline()) {
                        }
                        else if (! el.isInvoked()) {
                            player.sendMessage(ChatFormats.EQUIPMENT.append(Component.text("Décollage annulé (ailes désinvoquées).")));
                            propulsionTask = null;
                        }
                        else if (! player.getBukkitPlayer().getInventory().getItemInMainHand().equals(item)) {
                            player.sendMessage(ChatFormats.EQUIPMENT.append(Component.text("Décollage annulé (boussole pas en main).")));
                            propulsionTask = null;
                        }
                        else {
                            player.getBukkitPlayer().setVelocity(player.getBukkitPlayer().getLocation().getDirection().multiply(2));
                            new BukkitRunnable() {
                                @Override
                                public void run()
                                {
                                    if (player.isOnline())
                                        player.getBukkitPlayer().setGliding(true);
                                    propulsionTask = null;
                                }
                            }.runTaskLater(TesseractLib.instance, 5);
                        }
                    }
                }.runTaskLater(TesseractLib.instance, 20*3);
            }
            else
                player.sendMessage(ChatFormats.EQUIPMENT.append(Component.text("Vous devez être équipé de vos ailes pour décoller avec la boussole.")));
        }
        else
            menu.open(equipment.getPlayer().getBukkitPlayer());
    }

    @Override
    public void useInInventory(InventoryClickEvent event)
    {

    }

    @Override
    public boolean excludesOther() { return false; }
}
