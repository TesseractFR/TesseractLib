package onl.tesseract.lib.event.equipment.invocable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.equipment.BoussoleMenu;
import onl.tesseract.lib.equipment.Equipment;
import onl.tesseract.lib.equipment.EquipmentService;
import onl.tesseract.lib.equipment.Invocable;
import onl.tesseract.lib.service.ServiceContainer;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.lib.util.ChatFormats;
import onl.tesseract.lib.util.ItemBuilder;
import onl.tesseract.lib.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Boussole extends Invocable {
    BukkitTask propulsionTask;
    final BoussoleMenu menu;

    public Boussole(@NotNull UUID playerUUID, boolean invoked, int handSlot) {
        super(playerUUID, invoked, handSlot);
        menu = new BoussoleMenu();
    }

    @Override
    protected ItemStack createItem()
    {
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
    public void use(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR)
        {
            EquipmentService equipmentService = ServiceContainer.get(EquipmentService.class);
            Equipment equipment = equipmentService.getEquipment(getPlayerUUID());
            // If the elytra are invoked
            Elytra elytra = equipment.get(Elytra.class);
            if (elytra.isInvoked())
            {
                if (player.isGliding() && player.getLocation().getBlock().getType() != Material.WATER
                        || (propulsionTask != null && !propulsionTask.isCancelled())) return;
                player.sendMessage(ChatFormats.EQUIPMENT.append(Component.text("Décollage dans 3 secondes... Regardez en l'air !")));
                propulsionTask = new BukkitRunnable() {
                    @Override
                    public void run()
                    {
                        if (!player.isOnline())
                            return;
                        if (!elytra.isInvoked()) {
                            player.sendMessage(ChatFormats.EQUIPMENT.append(Component.text("Décollage annulé (ailes désinvoquées).")));
                            propulsionTask = null;
                        } else if (!player.getInventory().getItemInMainHand().equals(getItem())) {
                            player.sendMessage(ChatFormats.EQUIPMENT.append(Component.text("Décollage annulé (boussole pas en main).")));
                            propulsionTask = null;
                        } else {
                            player.setVelocity(player.getLocation().getDirection().multiply(2));
                            new BukkitRunnable() {
                                @Override
                                public void run()
                                {
                                    if (player.isOnline())
                                        player.setGliding(true);
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
            menu.open(player);
    }

    @Override
    public void useInInventory(InventoryClickEvent event)
    {

    }

    @Override
    public boolean getExcludeOthers() {
        return false;
    }

    @Override
    public @NotNull EquipmentSlot getSlotType() {
        return EquipmentSlot.HAND;
    }

    @Override
    public @NotNull String getUniqueName() {
        return "BOUSSOLE";
    }

    @Override
    public void onUninvoke(@NotNull Player player, boolean manuelRemoval) {

    }

    @Override
    public void onInvoke(@NotNull Player player, boolean manuelInvocation) {

    }
}
