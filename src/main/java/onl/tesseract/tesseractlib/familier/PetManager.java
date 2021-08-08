package onl.tesseract.tesseractlib.familier;

import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PetManager implements Listener {
    static public HashMap<UUID, ArmorStand> invokedPets = new HashMap<>();


    public static void invokePet(Player p, Pet pet, boolean spawn)
    {
        if (!spawn || pet == null)
        {
            if (invokedPets.containsKey(p.getUniqueId()))
            {
                invokedPets.get(p.getUniqueId()).remove();
                invokedPets.remove(p.getUniqueId());
            }
            return;
        }
        TPlayer tPlayer = TPlayer.get(p);

        if (tPlayer.hasPet(pet))
        {
            ArmorStand armorStand;
            armorStand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(+0, +1.5, +0.3),
                                                               EntityType.ARMOR_STAND);
            armorStand.setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
            armorStand.setCustomName("§6" + pet);
            Objects.requireNonNull(armorStand.getEquipment()).setHelmet(pet.getHead());
            armorStand.setCustomNameVisible(false);
            armorStand.setSmall(true);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setCollidable(true);
            armorStand.setMarker(true);
            armorStand.setDisabledSlots(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.FEET,
                                        EquipmentSlot.HAND, EquipmentSlot.LEGS, EquipmentSlot.OFF_HAND);
            p.sendMessage(ChatFormat.PET + ChatColor.DARK_GREEN + "Vous avez invoqué " + pet);
            invokedPets.put(p.getUniqueId(), armorStand);
        }
        else
        {
            p.sendMessage(ChatFormat.PET + ChatColor.RED + "Vous ne possédez pas ce familier");
        }
    }

    public static void supprimerFamilier(Player player, Pet pet, CommandSender sender)
    {
        if (player != null)
        {
            TPlayer tPlayer = TPlayer.get(player);
            if (!tPlayer.hasPet(pet))
            {
                sender.sendMessage(
                        ChatFormat.PET + ChatColor.RED + pet.name() + " n'est pas possedé par " + player
                                .getDisplayName());
                return;
            }
            tPlayer.removePet(pet);
            sender.sendMessage(
                    ChatFormat.PET + ChatColor.GREEN + "Vous avez supprimé " + pet.name() + " de "
                            + player.getDisplayName());

        }
    }


    /***************************************************************************************
     Lister familier
     **************************************************************************************/
    public static void ListerFamilier(@Nonnull Player player, PetCategory category, CommandSender sender)
    {
        TPlayer tPlayer = TPlayer.get(player);
        List<Pet> pets = tPlayer.getPets();
        sender.sendMessage(ChatColor.DARK_GREEN + "---- " + ChatColor.GREEN + " Liste des familiers de "
                                   + player.getDisplayName() + ChatColor.DARK_GREEN + " ----");
        sender.sendMessage(ChatColor.DARK_GREEN + "---- " + ChatColor.DARK_AQUA + category.name()
                                   + ChatColor.DARK_GREEN + " ----");
        for (Pet p : category.getPets())
        {
            if (pets.contains(p))
            {
                sender.sendMessage(ChatColor.GREEN + p.name());
            }
            else
            {
                sender.sendMessage(ChatColor.RED + p.name());
            }
        }
        sender.sendMessage(ChatColor.DARK_GREEN + "-----------------------------------------------");
    }

    public static void ajouterFamilier(@Nonnull Player player, Pet pet, CommandSender sender)
    {
        TPlayer tPlayer = TPlayer.get(player);
        if (tPlayer.hasPet(pet))
        {
            sender.sendMessage(ChatFormat.PET + "Le joueur possède déjà ce familier");
            return;
        }
        tPlayer.addPet(pet);
        sender.sendMessage(
                ChatFormat.PET + ChatColor.GREEN + "Vous avez ajouté " + pet.name() + " à " + player.getName());

    }

    public static boolean hasPetInvocked(Player p)
    {
        return invokedPets.containsKey(p.getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e)
    {
        Player p = e.getPlayer();
        Yaw yaw = Yaw.getYaw(p);
        if (!invokedPets.containsKey(p.getUniqueId()) || invokedPets.get(p.getUniqueId()) == null)
            return;
        switch (yaw)
        {
            case NORTH:
                invokedPets.get(p.getUniqueId()).teleport(p.getLocation().add(0.3, +1.5, +0.3));
                invokedPets.get(p.getUniqueId()).setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
                break;
            case EAST:
                invokedPets.get(p.getUniqueId()).teleport(p.getLocation().add(-0.3, +1.5, +0.3));
                invokedPets.get(p.getUniqueId()).setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
                break;
            case WEST:
                invokedPets.get(p.getUniqueId()).teleport(p.getLocation().add(+0.3, +1.5, -0.3));
                invokedPets.get(p.getUniqueId()).setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
                break;
            case SOUTH:
                invokedPets.get(p.getUniqueId()).teleport(p.getLocation().add(-0.3, +1.5, -0.3));
                invokedPets.get(p.getUniqueId()).setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
                break;
            default:
                break;

        }
    }

    @EventHandler
    public void OnLeave(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        reset(p);
    }

    public void reset(Player p)
    {
        if (invokedPets.get(p.getUniqueId()) != null)
        {
            invokePet(p, null, false);
            invokedPets.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        if (invokedPets.containsKey(player.getUniqueId()))
        {
            invokedPets.get(player.getUniqueId()).teleport(event.getTo());
        }
    }
}
