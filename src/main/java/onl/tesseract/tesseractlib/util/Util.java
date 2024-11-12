package onl.tesseract.tesseractlib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Util {
    static public final String NEW_LINE = " {nl} ";

    /**
     * Checks if two values modulo a precision are equals
     *
     * @param a         A
     * @param b         B
     * @param precision Precision of equality
     * @return True if equivalents
     */
    static public boolean isNear(double a, double b, double precision) {
        return (a - b) >= -precision && (a - b) <= precision;
    }

    /**
     * Split a string into several strings of size width
     *
     * @param message Original string
     * @param width   Size of substrings
     * @return List of substrings
     * @deprecated In favor of {@link ItemLoreBuilder}
     */
    @Deprecated
    static public List<String> splitByLines(String message, short width) {
        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        StringBuilder currentLine = new StringBuilder();
        char lastColor = 0;
        for (String word : words) {
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
            } else if (currentLine.length() + len > width) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                // Add the word to the next line, with the last used color
                if (lastColor != 0)
                    currentLine.append("§").append(lastColor).append(word).append(" ");
                else
                    currentLine.append(word).append(" ");
            } else
                currentLine.append(word).append(" ");

        }
        if (currentLine.length() > 0)
            lines.add(currentLine.toString());
        return lines;
    }

    /**
     * Returns an item stack built with given parameters
     *
     * @param material Material of the item
     * @param name     display name to apply to the item
     * @param lore     Lore to apply to the item. It will be trimmed by 30
     * @return an itemstack
     * @deprecated In favor of {@link ItemBuilder}
     */
    @Deprecated
    static public ItemStack buildItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        return buildItem(item, name, lore);
    }

    /**
     * @deprecated In favor of {@link ItemBuilder}
     */
    @Deprecated
    static public ItemStack buildItem(Material material, String name, String lore, int lineWidth, boolean enchant) {
        ItemStack item = new ItemStack(material);
        return buildItem(item, name, lore, lineWidth, enchant);
    }

    /**
     * @deprecated In favor of {@link ItemBuilder}
     */
    @Deprecated
    static public ItemStack buildItem(Material material, String name, String lore, boolean enchant) {
        ItemStack item = new ItemStack(material);
        return buildItem(item, name, lore, enchant);
    }

    /**
     * Modifies the name and lore of an ItemStack.
     *
     * @param item ItemStack to modify
     * @param name display name to apply to the item
     * @param lore Lore to apply to the item. It will be trimmed by 30
     * @return returns the same itemstack.
     * @deprecated In favor of {@link ItemBuilder}
     */
    @Deprecated
    static public ItemStack buildItem(ItemStack item, String name, String lore) {
        return buildItem(item, name, lore, false);
    }

    /**
     * @deprecated In favor of {@link ItemBuilder}
     */
    @Deprecated
    static public ItemStack buildItem(ItemStack item, String name, String lore, boolean enchant) {
        return buildItem(item, name, lore, 35, enchant);
    }

    /**
     * @deprecated In favor of {@link ItemBuilder}
     */
    @Deprecated
    static public ItemStack buildItem(ItemStack item, String name, String lore, int lineWidth, boolean enchant) {
        ItemMeta meta = item.getItemMeta();
        if (name != null)
            meta.displayName(Component.text(name));
        if (lore != null)
            meta.lore(new ItemLoreBuilder(lineWidth).append(lore).get());
        item.setItemMeta(meta);
        if (enchant) {
            item.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
            item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return item;
    }

    @Deprecated
    static public String center(String title) {
        int length = title.replaceAll("§.", "").length();
        int spaceLength = (41 - length) / 2;
        String space = " ".repeat(spaceLength);
        return space + title;
    }

    /**
     * Returns the number of items matching this material, excluding items having a localizedName
     *
     * @param inv      Inventory to search
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

            if (item.hasItemMeta() && item.getItemMeta() instanceof Damageable && ((Damageable) item.getItemMeta()).getDamage() > 0)
                continue;

            // Skip if it has a localizedName

            if ((item.hasItemMeta() && item.getItemMeta().hasDisplayName()) || !item.getEnchantments().isEmpty())
                continue;
            count += item.getAmount();
        }

        return count;
    }

    static public int countItems(Inventory inv, Predicate<ItemStack> predicate) {
        int count = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && predicate.test(item))
                count += item.getAmount();
        }

        return count;
    }

    static public int countFreeSlots(Inventory inv) {
        int count = 0;
        for (ItemStack item : inv.getContents()) {
            if (item == null || item.getType() == Material.AIR)
                count++;
        }

        return count;
    }

    static public int countItems(PlayerInventory inv, Predicate<ItemStack> predicate) {
        ItemStack item = inv.getItem(EquipmentSlot.OFF_HAND);
        if (item != null) {
            if (predicate.test(item))
                return item.getAmount() + countItems((Inventory) inv, predicate);
        }
        return countItems((Inventory) inv, predicate);
    }

    /**
     * Returns the number of items matching this material, excluding items having a localizedName
     *
     * @param inv      Inventory to search
     * @param material Material to search
     * @return Number of items, excluding items with a localizedName
     */
    static public int countNonSpecialItems(PlayerInventory inv, Material material) {
        // Get off hand
        ItemStack item = inv.getItem(EquipmentSlot.OFF_HAND);
        if (item != null) {
            if (item.hasItemMeta() && item.getItemMeta() instanceof Damageable && ((Damageable) item.getItemMeta()).getDamage() > 0)
                return countNonSpecialItems((Inventory) inv, material);
            if (item.getType() == material && !(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) && item.getEnchantments().isEmpty())
                return item.getAmount() + countNonSpecialItems((Inventory) inv, material);
        }
        return countNonSpecialItems((Inventory) inv, material);
    }

    /**
     * Removes a given number of item having this material, excluding items having a localizedName
     *
     * @param inv      Inventory to search
     * @param material Material to search
     * @param count    Number of items to remove
     * @return Number of items that have been removed.
     */
    static public int removeNonSpecialItems(Inventory inv, Material material, int count) {
        int start = count;
        HashMap<Integer, ? extends ItemStack> items = inv.all(material);
        for (int index : items.keySet()) {
            ItemStack item = items.get(index);
            if (item.hasItemMeta() && item.getItemMeta() instanceof Damageable && ((Damageable) item.getItemMeta()).getDamage() > 0)
                continue;
            if ((item.hasItemMeta() && item.getItemMeta().hasDisplayName()) || !item.getEnchantments().isEmpty())
                continue;
            if (item.getAmount() >= count) {
                item.setAmount(item.getAmount() - count);
                inv.clear(index);
                inv.setItem(index, item);
                return start;
            } else {
                count -= item.getAmount();
                inv.clear(index);
            }
        }
        return start - count;
    }

    static public int removeItems(Inventory inv, Predicate<ItemStack> predicate, int count) {
        int start = count;
        var content = inv.getContents();
        for (int i = 0; i < content.length; i++) {
            ItemStack item = content[i];
            if (item == null || !predicate.test(item))
                continue;
            if (item.getAmount() >= count) {
                item.setAmount(item.getAmount() - count);
                inv.clear(i);
                inv.setItem(i, item);
                return start;
            } else {
                count -= item.getAmount();
                inv.clear(i);
            }
        }

        return start - count;
    }

    static public int removeItems(PlayerInventory inv, Predicate<ItemStack> predicate, int count) {
        int remaining = count - removeItems((Inventory) inv, predicate, count);
        // Check off hand
        if (remaining > 0) {
            ItemStack item = inv.getItemInOffHand();
            if (predicate.test(item)) {
                int tmp = item.getAmount() - remaining;
                remaining -= item.getAmount();
                item.setAmount(tmp);
                inv.setItem(EquipmentSlot.OFF_HAND, item);
            }

        }
        return count - Math.max(remaining, 0);
    }

    /**
     * Removes a given number of item having this material, excluding items having a localizedName
     *
     * @param inv      Inventory to search
     * @param material Material to search
     * @param count    Number of items to remove
     * @return Number of items that have been removed.
     */
    static public int removeNonSpecialItems(PlayerInventory inv, Material material, int count) {
        int remaining = count - removeNonSpecialItems((Inventory) inv, material, count);
        // Check off hand
        if (remaining > 0) {
            ItemStack item = inv.getItemInOffHand();
            if (item.getType().equals(material) && item.getEnchantments().isEmpty()) {
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
     *
     * @param inv  Inventory to search
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
        if (item.getType().equals(item2.getType()) && item.lore().equals(item2.lore()) && item.getItemFlags().equals(item2.getItemFlags()))
            return -1;
        return -2;
    }

    static public boolean removeExact(Inventory inventory, ItemStack item) {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].equals(item)) {
                inventory.setItem(i, null);
                return true;
            }
        }
        return false;
    }

    /**
     * @param max Exclusive bound
     */
    static public double random(int min, int max) {
        return (Math.random() * (max - min)) + min;
    }

    /**
     * Replaces all non-ending '&' by '§' to apply a color
     *
     * @param message Message to color
     * @return Colored message
     */
    static public String convertColors(String message) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '&' && i + 1 < message.length())
                res.append("§");
            else
                res.append(message.charAt(i));
        }
        return res.toString();
    }

    static public String itemsPossessedOverTotal(Player player, Material material, int total) {
        boolean hasAll = total <= Util.countNonSpecialItems(player.getInventory(), material);
        return (hasAll ? ChatColor.GREEN : ChatColor.RED) + "" + countNonSpecialItems(player.getInventory(), material) +
                ChatColor.GRAY + "/" + ChatColor.GREEN + total;
    }

    static public String itemsPossessedOverTotal(int possessed, int total) {
        if (possessed < total)
            return ChatColor.RED + "" + possessed + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + total;
        else
            return ChatColor.GREEN + "" + possessed + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + total;
    }


    static public void giveItemOrDrop(final Player player, final ItemStack itemStack) {
        if (player.getInventory().firstEmpty() == -1) {
            Item item = player.getWorld().spawn(player.getLocation(), Item.class);
            item.setItemStack(itemStack);
            item.setOwner(player.getUniqueId());
            item.setCanMobPickup(false);
        } else {
            player.getInventory().addItem(itemStack);
        }
    }

    public static TextColor getGreenRedGradient(final int a, final int total) {
        return getGreenRedGradient(((double) a) / total);
    }

    public static TextColor getGreenRedGradient(final double percentage) {
        int green = (int) (percentage * 255);
        int red = 255 - green;
        return TextColor.color(red, green, 40);
    }

    public static String getProgressBar(float value, float maxValue, ChatColor color1, ChatColor color2, int barCount)
    {
        if (value >= maxValue)
            value -= maxValue;
        int progress = (int) ((value / maxValue) * barCount);
        return color1 + "ǀ".repeat(progress) + color2 + "ǀ".repeat(barCount - progress);
    }

    public static Collection<File> getAllYamlFiles(final File rootFolder) {
        final Collection<File> res = new ArrayList<>();
        File[] files = rootFolder.listFiles();
        if (files == null)
            return List.of();
        for (final File file : files) {
            if (file.isDirectory())
                res.addAll(getAllYamlFiles(file));
            else if (file.getName().endsWith(".yml"))
                res.add(file);
        }
        return res;
    }


    public static class Entity {
        static public void push(org.bukkit.entity.Entity entity, Location destination, int duration) {
            Vector v = destination.subtract(entity.getLocation()).toVector().multiply(1.0 / duration);
            new BukkitRunnable() {
                int i = 0;

                @Override
                public void run() {
                    entity.setVelocity(v);

                    if (++i >= duration)
                        this.cancel();
                }
            }.runTaskTimer(TesseractLib.instance, 0, 1);
        }

        /**
         * Gets the angle between the current direction of the entity and the direction to face the location.
         *
         * @param entity   Entity
         * @param location Target
         */
        static public double facing(org.bukkit.entity.Entity entity, Location location) {
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

        static public Location getTargetLocation(LivingEntity entity, int distance) {
            RayTraceResult result = entity.getWorld().rayTraceBlocks(entity.getEyeLocation(), entity.getLocation().getDirection(), distance, FluidCollisionMode.SOURCE_ONLY, true);
            if (result == null)
                return Locations.onGround(entity.getLocation().add(entity.getLocation().getDirection().multiply(distance)));
            else
                return result.getHitPosition().toLocation(entity.getWorld(), entity.getLocation().getYaw(), entity.getLocation().getPitch());
        }
    }

    public static class Locations {
        /**
         * Computes the distance between two points, regardless of the Y axis
         *
         * @param a Point A
         * @param b Point B
         * @return Distance
         */
        static public float flatDistance(org.bukkit.Location a, org.bukkit.Location b) {
            float x = (float) (b.getX() - a.getX());
            float z = (float) (b.getZ() - a.getZ());
            return (float) Math.sqrt(x * x + z * z);
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

        static public org.bukkit.Location backward(org.bukkit.Location origin) {
            return backward(origin, 1);
        }

        static public org.bukkit.Location backward(org.bukkit.Location origin, double dist) {
            double yCos = Math.cos(Math.toRadians(origin.getPitch()));
            return origin.clone().add(
                    Math.sin(Math.toRadians(origin.getYaw())) * yCos * dist,
                    Math.sin(Math.toRadians(origin.getPitch())) * dist,
                    -Math.cos(Math.toRadians(origin.getYaw())) * yCos * dist
            );
        }

        static public org.bukkit.Location forwardFloat(org.bukkit.Location origin, double dist) {
            return origin.clone().add(new Vector(origin.getDirection().getX(), 0, origin.getDirection().getZ()).multiply(dist));
        }

        static public org.bukkit.Location above(org.bukkit.Location origin, double dist) {
            return origin.clone().add(0, dist, 0);
        }

        static public org.bukkit.Location onGround(org.bukkit.Location origin) {
            while (!origin.getBlock().getType().isSolid() && origin.getY() > 0 && origin.getBlock().getType() != Material.WATER)
                origin.subtract(0, 1, 0);
            return origin;
        }

        /**
         * Get the location of a column of non-solid blocks sitting on a solid block
         *
         * @param origin Location where to seek
         * @param upperBoundY World height at which to start seeking
         * @param belowBoundY World height at which to stop seeking
         * @param spaceHeight Height of the column to seek
         *
         * @return Location of the first non-solid block, starting from the bottom. Null if none is found.
         */
        @Nullable
        public static Location onGroundEmptySpace(org.bukkit.Location origin, int upperBoundY, int belowBoundY, int spaceHeight) {
            outer: for (int y = upperBoundY; y >= belowBoundY; y--)
            {
                Block blockAt = origin.getWorld().getBlockAt(origin.getBlockX(), y, origin.getBlockZ());
                if (!blockAt.isSolid())
                {
                    int currentSpace = 1;
                    while (currentSpace++ < spaceHeight)
                    {
                        if (blockAt.getRelative(BlockFace.UP, currentSpace - 1).isSolid())
                            continue outer;
                    }
                    if (blockAt.getRelative(BlockFace.DOWN).isSolid())
                    {
                        return blockAt.getLocation();
                    }
                }
            }
            return null;
        }

        static public List<org.bukkit.Location> getPointsBetween(org.bukkit.Location a, org.bukkit.Location b, double step) {
            Vector v = b.clone().subtract(a).toVector();
            double dist = v.length();
            v.normalize().multiply(step);
            List<org.bukkit.Location> locs = new ArrayList<>();
            org.bukkit.Location current = a.clone();
            for (double i = 0; i < dist; i += step) {
                locs.add(current.add(v).clone());
            }
            return locs;
        }
    }

    public static String getPrintableDuration(final Duration duration) {
        return String.format("%dh%02dm%02ds",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }

    public static TextComponent replace(TextComponent origin, int start, int end, Component replacement) {
        String raw = origin.content();
        TextComponent res = Component.text(raw.substring(0, start))
                .style(origin.style());

        Component next = Component.text(raw.substring(end))
                .style(origin.style());
        for (var child : origin.children())
            next = next.append(child);

        res = res.append(replacement)
                .append(next);

        return res;
    }

    public static String getChainedExceptionCauseMessages(Throwable throwable)
    {
        StringBuilder builder = new StringBuilder("\n\tReason: ");
        builder.append(throwable.getMessage());
        throwable = throwable.getCause();
        while (throwable != null)
        {
            builder.append("\n\tCaused by: ")
                   .append(throwable.getMessage());
            throwable = throwable.getCause();
        }
        return builder.toString();
    }
}
