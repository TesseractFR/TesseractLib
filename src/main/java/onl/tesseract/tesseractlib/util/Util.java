package onl.tesseract.tesseractlib.util;

import net.kyori.adventure.text.Component;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Util {
    static public final String NEW_LINE = " {nl} ";
    /**
     * Checks if two values modulo a precision are equals
     * @param a A
     * @param b B
     * @param precision Precision of equality
     * @return True if equivalents
     */
    static public boolean isNear(double a, double b, double precision)
    {
        return (a - b) >= -precision && (a - b) <= precision;
    }

    /**
     * Gets a player head by recovering his player profile stored at server start by TPlayer#loadPlayerProfile.
     * If the player profile is not loaded, it will make a request to Mojang to recover the profile.
     * @param tPlayer Owner of the head.
     * @return Head with the skin of the owner.
     */
    @SuppressWarnings("all")
    static public ItemStack getPlayerHead(TPlayer tPlayer)
    {
        if (tPlayer.skinSignature == null || tPlayer.skinValue == null)
            return InventoryMenu.getHead(tPlayer.getOfflinePlayer().getUniqueId());
        else
            return InventoryMenu.getCustomHead(null, tPlayer.skinValue, tPlayer.skinSignature);
    }

    /**
     * Split a string into several strings of size width
     * @param message Original string
     * @param width Size of substrings
     * @return List of substrings
     * @deprecated In favor of {@link ItemLoreBuilder}
     */
    @Deprecated
    static public List<String> splitByLines(String message, short width)
    {
        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        StringBuilder currentLine = new StringBuilder();
        char lastColor = 0;
        for(String word : words) {
            // Get real length
            int len = word.replaceAll("§.", "").length();
            // Get the last used color.
            int colorIndex = word.lastIndexOf('§');
            if (colorIndex != -1 && colorIndex + 1 < word.length())
                lastColor = word.charAt(colorIndex + 1);
            // Check if new line
            boolean isNewLine = word.strip().equals(NEW_LINE.strip());
            if (isNewLine) {
                // Split the line
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                if (lastColor != 0)
                    currentLine.append("§").append(lastColor);
                // Split the line if there is no place to add the word
            }else if (currentLine.length() + len > width) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                // Add the word to the next line, with the last used color
                if (lastColor != 0)
                    currentLine.append("§").append(lastColor).append(word).append(" ");
                else
                    currentLine.append(word).append(" ");
            }else
                currentLine.append(word).append(" ");

        }
        if (currentLine.length() > 0)
            lines.add(currentLine.toString());
        return lines;
    }

    /**
     * Returns an item stack built with given parameters
     * @param material Material of the item
     * @param name display name to apply to the item
     * @param lore Lore to apply to the item. It will be trimmed by 30
     * @return an itemstack
     */
    static public ItemStack buildItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        return buildItem(item, name, lore);
    }

    static public ItemStack buildItem(Material material, String name, String lore, int lineWidth, boolean enchant) {
        ItemStack item = new ItemStack(material);
        return buildItem(item, name, lore, lineWidth, enchant);
    }

    static public ItemStack buildItem(Material material, String name, String lore, boolean enchant) {
        ItemStack item = new ItemStack(material);
        return buildItem(item, name, lore, enchant);
    }

    /**
     * Modifies the name and lore of an ItemStack.
     * @param item ItemStack to modify
     * @param name display name to apply to the item
     * @param lore Lore to apply to the item. It will be trimmed by 30
     * @return returns the same itemstack.
     */
    static public ItemStack buildItem(ItemStack item, String name, String lore) {
        return buildItem(item, name, lore, false);
    }

    static public ItemStack buildItem(ItemStack item, String name, String lore, boolean enchant) {
        return buildItem(item, name, lore, 35, enchant);
    }

    static public ItemStack buildItem(ItemStack item, String name, String lore, int lineWidth, boolean enchant) {
        ItemMeta meta = item.getItemMeta();
        if (name != null)
            meta.displayName(Component.text(name));
        if (lore != null)
            meta.lore(new ItemLoreBuilder(lineWidth).append(lore).get());
        item.setItemMeta(meta);
        if (enchant) {
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return item;
    }

    static public String center(String title) {
        int length = title.replaceAll("§.", "").length();
        int spaceLength = (41 - length) / 2;
        String space = " ".repeat(spaceLength);
        System.out.println(length + " " + spaceLength);
        return space + title;
    }

    /**
     * Returns the number of items matching this material, excluding items having a localizedName
     * @param inv Inventory to search
     * @param material Material to search
     * @return Number of items, excluding items with a localizedName
     */
    static public int countNonSpecialItems(Inventory inv, Material material) {
        // Get all items having this material
        HashMap<Integer, ? extends ItemStack> items = inv.all(material);
        int count = 0;
        // For each item
        for (ItemStack item : items.values()) {
            //Skip if is damaged

            if( item.hasItemMeta() && item.getItemMeta() instanceof Damageable && ((Damageable)item.getItemMeta()).getDamage() >0)
                continue;

            // Skip if it has a localizedName

            if ((item.hasItemMeta() && item.getItemMeta().hasLocalizedName()) || !item.getEnchantments().isEmpty())
                continue;
            count += item.getAmount();
        }

        return count;
    }

    /**
     * Returns the number of items matching this material, excluding items having a localizedName
     * @param inv Inventory to search
     * @param material Material to search
     * @return Number of items, excluding items with a localizedName
     */
    static public int countNonSpecialItems(PlayerInventory inv, Material material) {
        // Get off hand
        ItemStack item = inv.getItem(EquipmentSlot.OFF_HAND);
        if (item != null) {
            if(item.hasItemMeta() && item.getItemMeta() instanceof Damageable && ((Damageable)item.getItemMeta()).getDamage() >0)
                return countNonSpecialItems((Inventory) inv, material);
            if (item.getType() == material && !(item.hasItemMeta() && item.getItemMeta().hasLocalizedName()) && item.getEnchantments().isEmpty())
                return item.getAmount() + countNonSpecialItems((Inventory) inv, material);
        }
        return countNonSpecialItems((Inventory) inv, material);
    }

    /**
     * Removes a given number of item having this material, excluding items having a localizedName
     * @param inv Inventory to search
     * @param material Material to search
     * @param count Number of items to remove
     * @return Number of items that have been removed.
     */
    static public int removeNonSpecialItems(Inventory inv, Material material, int count) {
        int start = count;
        HashMap<Integer, ? extends ItemStack> items = inv.all(material);
        for (int index : items.keySet()) {
            ItemStack item = items.get(index);
            if(item.hasItemMeta() && item.getItemMeta() instanceof Damageable && ((Damageable)item.getItemMeta()).getDamage() >0)
                continue;
            if ((item.hasItemMeta() && item.getItemMeta().hasLocalizedName() )|| !item.getEnchantments().isEmpty())
                continue;
            if (item.getAmount() >= count) {
                item.setAmount(item.getAmount() - count);
                inv.clear(index);
                inv.setItem(index, item);
                return start;
            }else {
                count -= item.getAmount();
                inv.clear(index);
            }
        }
        return start - count;
    }

    /**
     * Removes a given number of item having this material, excluding items having a localizedName
     * @param inv Inventory to search
     * @param material Material to search
     * @param count Number of items to remove
     * @return Number of items that have been removed.
     */
    static public int removeNonSpecialItems(PlayerInventory inv, Material material, int count) {
        int remaining = count - removeNonSpecialItems((Inventory) inv, material, count);
        // Check off hand
        if (remaining > 0) {
            ItemStack item = inv.getItemInOffHand();
            if (item.getType().equals(material) && item.getEnchantments().isEmpty())
            {
                int tmp = item.getAmount() - remaining;
                remaining -= item.getAmount();
                item.setAmount(tmp);
                inv.setItem(EquipmentSlot.OFF_HAND, item);
            }

        }
        return count - Math.max(remaining, 0);
    }

    /**
     * Returns the slot of an item
     * @param inv Inventory to search
     * @param item Item to search
     * @return slot of the item. -1 if off hand. -2 if not found
     */
    static public int getSlot(PlayerInventory inv, ItemStack item) {
        for (int index : inv.all(item).keySet()) {
            return index;
        }
        ItemStack item2 = inv.getItemInOffHand();

        if (item.equals(inv.getItemInOffHand()))
            return -1;
        if(item.getType().equals(item2.getType()) && item.lore().equals(item2.lore()) && item.getItemFlags().equals(item2.getItemFlags()) )
            return -1;
        return -2;
    }

    static public double random(int min, int max) {
        return (Math.random() * (max - min)) + min;
    }

    static public double parseDenizenDouble(String raw)
    {
        if (! raw.contains("."))
            return Integer.parseInt(raw);
        return Double.parseDouble(raw.replaceAll("d@", "").replaceAll("E", "E+").replaceAll("s", ""));
    }

    static public UUID parseDernizenUUID(String raw)
    {
        return UUID.fromString(raw.replaceAll("p@", ""));
    }

    static public Location parseDenizenLocation(String raw)
    {
        raw = raw.replaceAll("l@", "");
        String[] parts = raw.split(",");
        if (parts.length != 4 && parts.length != 6)
            throw new IllegalArgumentException("Wrong location format");

        double x = parseDenizenDouble(parts[0]);
        double y = parseDenizenDouble(parts[1]);
        double z = parseDenizenDouble(parts[2]);
        double yaw = parts.length == 6 ? parseDenizenDouble(parts[3]) : 0;
        double pitch = parts.length == 6 ? parseDenizenDouble(parts[4]) : 0;
        World world = parts.length == 6 ? Bukkit.getWorld(parts[5]) : Bukkit.getWorld(parts[3]);
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    static public List<Location> parseDenizenLocations(String raw)
    {
        List<Location> res = new ArrayList<>();
        String[] parts = raw.split(",");
        World world = Bukkit.getWorld(parts[0]);
        for (int i = 3; i < parts.length; i += 3)
        {
            double x = parseDenizenDouble(parts[i - 2]);
            double y = parseDenizenDouble(parts[i - 1]);
            double z = parseDenizenDouble(parts[i]);
            res.add(new Location(world, x, y, z));
        }
        return res;
    }

    /**
     * Replaces all non-ending '&' by '§' to apply a color
     * @param message Message to color
     * @return Colored message
     */
    static public String convertColors(String message)
    {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < message.length(); i++)
        {
            if (message.charAt(i) == '&' && i+1 < message.length())
                res.append("§");
            else
                res.append(message.charAt(i));
        }
        return res.toString();
    }

    static public String itemsPossessedOverTotal(Player player, Material material, int total)
    {
        boolean hasAll = total <= Util.countNonSpecialItems(player.getInventory(), material);
        return (hasAll ? ChatColor.GREEN : ChatColor.RED) + "" + countNonSpecialItems(player.getInventory(), material) +
                ChatColor.GRAY + "/" + ChatColor.GREEN + total;
    }

    static public String itemsPossessedOverTotal(int possessed, int total)
    {
        if (possessed < total)
            return ChatColor.RED + "" + possessed + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + total;
        else
            return ChatColor.GREEN + "" + possessed + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + total;
    }



    public static class Entity {
        static public void push(org.bukkit.entity.Entity entity, Location destination, int duration)
        {
            Vector v = destination.subtract(entity.getLocation()).toVector().multiply(1.0 / duration);
            new BukkitRunnable() {
                int i =0;
                @Override
                public void run() {
                    entity.setVelocity(v);

                    if (++i >= duration)
                        this.cancel();
                }
            }.runTaskTimer(TesseractLib.instance, 0, 1);
        }

        static public void damageByMagic(Player damager, LivingEntity damaged, double amount)
        {
            damaged.damage(amount);
            damaged.setKiller(damager);
            new EntityDamageByEntityEvent(damager, damaged, EntityDamageEvent.DamageCause.MAGIC, amount).callEvent();
        }

        /**
         * Gets the angle between the current direction of the entity and the direction to face the location.
         * @param entity Entity
         * @param location Target
         */
        static public double facing(LivingEntity entity, Location location)
        {
            Vector v = location.subtract(entity.getLocation()).toVector().setY(0).normalize();
            double z = v.getZ();
            double yaw = Math.acos(z);
            if (v.getX() > 0)
                yaw *= -1;
            double dist = Math.toDegrees(yaw) - entity.getLocation().getYaw();
            if (dist >= 180)
                return 360 - dist;
            else if (dist <= -180)
                return dist + 360;
            return dist;
        }

        static public Location getTargetLocation(LivingEntity entity, int distance)
        {
            RayTraceResult result = entity.getWorld().rayTraceBlocks(entity.getEyeLocation(), entity.getLocation().getDirection(), distance, FluidCollisionMode.SOURCE_ONLY, true);
            if (result == null)
                return Locations.onGround(entity.getLocation().add(entity.getLocation().getDirection().multiply(distance)));
            else return result.getHitPosition().toLocation(entity.getWorld(), entity.getLocation().getYaw(), entity.getLocation().getPitch());
        }
    }

    public static class Locations {
        /**
         * Computes the distance between two points, regardless of the Y axis
         * @param a Point A
         * @param b Point B
         * @return Distance
         */
        static public float flatDistance(org.bukkit.Location a, org.bukkit.Location b)
        {
            float x = (float)(b.getX() - a.getX());
            float z = (float)(b.getZ() - a.getZ());
            return (float)Math.sqrt(x*x + z*z);
        }

        static public String xyz(org.bukkit.Location loc) {
            return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        }

        static public org.bukkit.Location right(org.bukkit.Location origin, double dist) {
            float yaw = origin.getYaw();
            double x = Math.cos(Math.toRadians(yaw)) * dist;
            double z = Math.sin(Math.toRadians(yaw)) * dist;
            return origin.clone().add(x, 0, z);
        }

        static public org.bukkit.Location right(org.bukkit.Location origin) {
            return right(origin, 1);
        }

        static public org.bukkit.Location backward(org.bukkit.Location origin)
        {
            return backward(origin, 1);
        }

        static public org.bukkit.Location backward(org.bukkit.Location origin, double dist)
        {
            double yCos = Math.cos(Math.toRadians(origin.getPitch()));
            return origin.clone().add(
                    Math.sin(Math.toRadians(origin.getYaw())) * yCos * dist,
                    Math.sin(Math.toRadians(origin.getPitch())) * dist,
                    - Math.cos(Math.toRadians(origin.getYaw())) * yCos * dist
            );
        }

        static public org.bukkit.Location forwardFloat(org.bukkit.Location origin, double dist)
        {
            return origin.clone().add(new Vector(origin.getDirection().getX(), 0, origin.getDirection().getZ()).multiply(dist));
        }

        static public org.bukkit.Location above(org.bukkit.Location origin, double dist) {
            return origin.clone().add(0, dist, 0);
        }

        static public org.bukkit.Location onGround(org.bukkit.Location origin)
        {
            while (! origin.getBlock().getType().isSolid() && origin.getY() > 0)
                origin.subtract(0, 1, 0);
            return origin;
        }

        static public List<org.bukkit.Location> getPointsBetween(org.bukkit.Location a, org.bukkit.Location b, double step)
        {
            Vector v = b.clone().subtract(a).toVector();
            double dist = v.length();
            v.normalize().multiply(step);
            List<org.bukkit.Location> locs = new ArrayList<>();
            org.bukkit.Location current = a.clone();
            for (double i = 0; i < dist; i += step)
            {
                locs.add(current.add(v).clone());
            }
            return locs;
        }
    }
}
