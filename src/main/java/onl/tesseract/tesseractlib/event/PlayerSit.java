package onl.tesseract.tesseractlib.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

/**
 * Makes a player sit down when he right clicks a slab or stairs
 */
public class PlayerSit implements Listener {
    static HashMap<Player, Pig> map = new HashMap<>();

    @EventHandler
    public void onSit(PlayerInteractEvent event)
    {
        if (event.hasBlock() && event.getClickedBlock() != null && !event.hasItem() && event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (event.getClickedBlock().getType().toString().contains("SLAB"))
            {
                if (event.getClickedBlock().getRelative(BlockFace.UP).getType().isSolid())
                    return;
                Slab dir = (Slab)event.getClickedBlock().getBlockData();
                if (dir.getType() == Slab.Type.BOTTOM)
                {
                    Location sitLocation = event.getClickedBlock().getLocation().add(0.5, -.4, 0.5);
                    new PlayerSitEvent(event.getPlayer(), sitLocation, event.getPlayer().getLocation().getYaw())
                            .callEvent();
                }
            }
            else if (event.getClickedBlock().getType().equals(Material.CAMPFIRE))
            {
                Campfire campfire = (Campfire) event.getClickedBlock().getBlockData();
                if (!campfire.isLit())
                {
                    Location sitLocation = event.getClickedBlock().getLocation().add(0.5, -.4, 0.5);
                    new PlayerSitEvent(event.getPlayer(), sitLocation, event.getPlayer().getLocation().getYaw()).callEvent();
                }
            }
            else if (event.getClickedBlock().getType().toString().contains("STAIRS"))
            {
                if (event.getClickedBlock().getRelative(BlockFace.UP).getType().isSolid())
                    return;
                Stairs stairs = (Stairs)event.getClickedBlock().getBlockData();
                if (stairs.getHalf() == Bisected.Half.TOP)
                    return;

                Location sitLocation = event.getClickedBlock().getLocation().add(0.5, -.4, 0.5);
                Directional dir = (Directional) event.getClickedBlock().getBlockData();
                switch (dir.getFacing())
                {
                    case DOWN:
                        break;
                    case NORTH:
                        new PlayerSitEvent(event.getPlayer(), sitLocation, 0).callEvent();
                        break;
                    case SOUTH:
                        new PlayerSitEvent(event.getPlayer(), sitLocation, 180).callEvent();
                        break;
                    case EAST:
                        new PlayerSitEvent(event.getPlayer(), sitLocation, 90).callEvent();
                        break;
                    case WEST:
                        new PlayerSitEvent(event.getPlayer(), sitLocation, -90).callEvent();
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onStandUp(VehicleExitEvent event)
    {
        if (event.getExited().getType() == EntityType.PLAYER && map.containsKey((Player) event.getExited()))
            standUp((Player) event.getExited());
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        if (map.containsKey(event.getPlayer()))
            standUp(event.getPlayer());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onSitHandler(PlayerSitEvent event)
    {
        if (!event.isCancelled())
            sit(event.getPlayer(), event.getLocation(), event.getRotation());
    }

    static public void sit(Player player, Location location, float rotation)
    {
        Pig pig = (Pig) player.getWorld().spawnEntity(location, EntityType.PIG);
        pig.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0, false, false));
        pig.setInvulnerable(true);
        pig.setAI(false);
        pig.setSilent(true);
        pig.setGravity(false);
        pig.setRotation(rotation, 0);
        pig.addPassenger(player);

        map.put(player, pig);
    }

    static public void standUp(Player player)
    {
        map.get(player).remove();
        map.remove(player);
    }
}