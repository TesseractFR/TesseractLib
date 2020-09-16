package onl.tesseract.tesseractlib.event;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
                    sit(event.getPlayer(), event.getClickedBlock().getLocation().add(0.5, -.4, 0.5), event.getPlayer().getLocation().getYaw());
            }
            else if (event.getClickedBlock().getType().toString().contains("STAIRS"))
            {
                if (event.getClickedBlock().getRelative(BlockFace.UP).getType().isSolid())
                    return;
                Stairs stairs = (Stairs)event.getClickedBlock().getBlockData();
                if (stairs.getHalf() == Bisected.Half.TOP)
                    return;

                Directional dir = (Directional) event.getClickedBlock().getBlockData();
                switch (dir.getFacing())
                {
                    case DOWN:
                        break;
                    case NORTH:
                        sit(event.getPlayer(), event.getClickedBlock().getLocation().add(0.5, -.4, 0.5), 0);
                        break;
                    case SOUTH:
                        sit(event.getPlayer(), event.getClickedBlock().getLocation().add(0.5, -.4, 0.5), 180);
                        break;
                    case EAST:
                        sit(event.getPlayer(), event.getClickedBlock().getLocation().add(0.5, -.4, 0.5), 90);
                        break;
                    case WEST:
                        sit(event.getPlayer(), event.getClickedBlock().getLocation().add(0.5, -.4, 0.5), -90);
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