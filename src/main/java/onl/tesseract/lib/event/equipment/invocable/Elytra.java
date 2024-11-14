package onl.tesseract.lib.event.equipment.invocable;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.equipment.Invocable;
import onl.tesseract.lib.service.PluginService;
import onl.tesseract.lib.service.ServiceContainer;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.lib.animation.AnimationTarget;
import onl.tesseract.lib.animation.Circle;
import onl.tesseract.lib.animation.Concentration;
import onl.tesseract.lib.util.ItemBuilder;
import onl.tesseract.lib.util.Util;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Elytra extends Invocable implements Listener {
    BukkitTask accelerateTask;
    BukkitTask actionBarTask;
    BukkitTask autoGlideTask;

    boolean autoGlide = true;

    boolean ignoreSpeedLevel = false;

    @Getter @Setter
    int protectionLevel = 0;
    @Getter @Setter
    int speedLevel = 0;
    @Getter @Setter
    int topprotectionLevel = 0;
    @Getter @Setter
    int topspeedLevel = 0;

    // static int[] prices = new int[] {2000,4000,8000,14000,19000,25000,30000,35000,40000};
    static final int[] prices = new int[] {100, 200, 300, 400, 500, 600, 700, 800, 900};

    public enum Upgrade {
        PROTECTION, VITESSE
    }

    public Elytra(@NotNull UUID playerUUID, boolean invoked, int handSlot) {
        super(playerUUID, invoked, handSlot);
    }

    @Override
    public boolean getExcludeOthers() {
        return true;
    }

    @Override
    public @NotNull EquipmentSlot getSlotType() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public @NotNull String getUniqueName() {
        return "ELYTRA";
    }

    @Override
    protected ItemStack createItem()
    {
        final ItemStack item = new ItemBuilder(Material.ELYTRA)
                .name("Flanc éthéré", NamedTextColor.GOLD)
                .lore(ChatColor.DARK_PURPLE + "« Des ailes divines imprégnées de clairvoyance. »" + Util.NEW_LINE + Util.NEW_LINE +
                        ChatColor.DARK_AQUA + "Vitesse : " + ChatColor.GOLD + speedLevel + Util.NEW_LINE +
                        ChatColor.DARK_AQUA + "Protection : " + ChatColor.GOLD + protectionLevel)
                .enchanted(true)
                .build();
        ItemMeta meta = item.getItemMeta();
        NamespacedKey namespacedKey = new NamespacedKey(NamespacedKey.MINECRAFT,"generic.armor");
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(namespacedKey, protectionLevel, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));

        return item;
    }

    @EventHandler
    public void onAccelerate(PlayerToggleSneakEvent event)
    {
        if (! event.getPlayer().getUniqueId().equals(getPlayerUUID())) return;
        // If the player is sneaking in flight
        if (event.isSneaking() && isInvoked() && event.getPlayer().isGliding() && event.getPlayer().getVelocity().length() < (1.20 + (0.10 * (getEffectiveSpeedLevel() + 1)))
                && accelerateTask == null)
        {
            // Start a timer to accelerate every 0.5 seconds while sneaking.
            accelerateTask = new BukkitRunnable() {
                final Player player = event.getPlayer();
                @Override
                public void run()
                {
                    // Cancel speed level if in event world
                    final int finalSpeedLevel = event.getPlayer().getLocation().getWorld().getName().equals("Event") ?
                            0 : getEffectiveSpeedLevel();
                    // Cancel if not sneaking or flying
                    if (!player.isOnline() || !player.isSneaking() || !player.isGliding()) {
                        this.cancel();
                        accelerateTask = null;
                    }
                    else if (player.getVelocity().length() < (1.20 + (0.10 * (finalSpeedLevel + 1)))) {
                        Vector vector = player.getVelocity();
                        vector.add(player.getLocation().getDirection().multiply(0.7));

                        player.setVelocity(vector);
                        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1, 1);
                    }
                }
            }.runTaskTimer(TesseractLib.instance, 0, 10);
        }
    }

    @EventHandler
    public void onFly(EntityToggleGlideEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (event.getEntity().getUniqueId().equals(getPlayerUUID()) && isInvoked())
        {
            if (event.isGliding())
                displayActionBar((Player) event.getEntity());
            else if (autoGlide)
                setAutoGlide(true);
        }
    }

    void displayActionBar(Player player) {
        if (actionBarTask != null && !actionBarTask.isCancelled())
            actionBarTask.cancel();
        actionBarTask = new BukkitRunnable() {
            @Override
            public void run()
            {
                if (!player.isOnline() || !player.isGliding())
                {
                    this.cancel();
                    accelerateTask = null;
                }
                // Show action bar
                else {
                    var comp = Component.text("Vitesse: ", NamedTextColor.GRAY)
                            .append(Component.text((int) (player.getVelocity().length() * 20), NamedTextColor.AQUA))
                            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Alt: "))
                            .append(Component.text(player.getLocation().getBlockY(), NamedTextColor.GREEN))
                            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Distance: "))
                            .append(Component.text((int) (player.getLocation().distance(player.getCompassTarget())), NamedTextColor.YELLOW));
                    player.sendActionBar(comp);
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 2);
    }

    @Override
    public void onUninvoke(@NotNull Player player, boolean manualUninvocation)
    {
        ServiceContainer.get(PluginService.class).unregisterEventListener(this);
        if (manualUninvocation)
            animate(player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onInvoke(@NotNull Player player, boolean manuelInvocation)
    {
        ServiceContainer.get(PluginService.class).registerEventListener(this);
        if (!player.isOnGround() && !player.isGliding())
            player.setGliding(true);
        if (autoGlide)
            setAutoGlide(true);

        if (manuelInvocation)
            animate(player);
    }

    void animate(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 0));
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 20, 1);
        new Circle(Particle.DUST, new AnimationTarget(player))
                .setColor(Color.FUCHSIA)
                .setDelay(0.1f)
                .setRadius(1)
                .setRotationCount(3).draw();
        new BukkitRunnable() {
            @Override
            public void run()
            {
                player.playSound(player.getLocation(),
                        Sound.BLOCK_END_PORTAL_SPAWN, 20, 1);
            }
        }.runTaskLater(TesseractLib.instance, 40);
    }


    /**
     * Propells the player upward then forward.
     */
    public void synergicPropulsion(Player player)
    {
        new Concentration().setParticle(Particle.DUST)
                           .setColor(Color.FUCHSIA)
                           .setTarget(new AnimationTarget(player))
                           .setRadius(2)
                           .setCount(20)
                           .build()
                           .draw();
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 20, 1);
        new BukkitRunnable() {
            @Override
            public void run()
            {
                if (! player.isOnline() || !isInvoked()) return;
                player.setVelocity(new Vector(0, 2, 0));
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 150, 1);
                new BukkitRunnable() {
                    @Override
                    public void run()
                    {
                        if (player.isOnline() && isInvoked()) {
                            player.setVelocity(player.getLocation().getDirection());
                            player.setGliding(true);
                            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 150, 1);
                        }
                    }
                }.runTaskLater(TesseractLib.instance, 20);
            }
        }.runTaskLater(TesseractLib.instance, 30);
    }

    public int getEffectiveSpeedLevel()
    {
        return ignoreSpeedLevel ? 1 : speedLevel;
    }
    public int getEffectiveTopSpeedLevel()
    {
        return ignoreSpeedLevel ? 1 : topspeedLevel;
    }
    public void topLevel(Upgrade type) {
        if (type == Upgrade.PROTECTION)
            topprotectionLevel++;
        else
            topspeedLevel++;
        updateItem(true);
    }
    public int getTopLevel(Upgrade type) {
        return type == Upgrade.PROTECTION ? topprotectionLevel : getEffectiveTopSpeedLevel();
    }
    public void setTopLevel(Upgrade type , int level)
    {
        if (type == Upgrade.PROTECTION)
            this.topprotectionLevel = level;
        else
            this.topspeedLevel = level;
        updateItem(true);
    }
    public int getLevel(Upgrade type) {
        return type == Upgrade.PROTECTION ? protectionLevel : getEffectiveSpeedLevel();
    }
    public void setLevel(Upgrade type , int level)
    {
        if (type == Upgrade.PROTECTION)
            this.protectionLevel = level;
        else
            this.speedLevel = level;
        updateItem(true);
    }

    public void upgradeLevel(Upgrade type) {
        if (type == Upgrade.PROTECTION)
        {
            this.topprotectionLevel ++;
            protectionLevel = this.topprotectionLevel;
        }
        else
        {
            this.topspeedLevel ++;
            speedLevel = this.topspeedLevel;
        }
        updateItem(true);
    }

    @Override
    public void use(PlayerInteractEvent event)
    {

    }

    @Override
    public void useInInventory(InventoryClickEvent event)
    {

    }

    public boolean hasAutoGlide()
    {
        return autoGlide;
    }

    @SuppressWarnings("deprecation")
    public void setAutoGlide(boolean autoGlide)
    {
        this.autoGlide = autoGlide;
        if (!autoGlide) {
            return;
        }
        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player == null) return;
        autoGlideTask = new BukkitRunnable() {
            @Override
            public void run()
            {
                if (! player.isOnline() || !isInvoked()) {
                    this.cancel();
                    autoGlideTask = null;
                }
                else
                {
                    // Don't glide if the player is levitating
                    if (player.hasPotionEffect(PotionEffectType.LEVITATION))
                        return;
                    Block b = player.getLocation().getBlock();
                    if (!player.isOnGround() && b.getRelative(BlockFace.DOWN).getType() == Material.AIR
                            && b.getRelative(BlockFace.DOWN, 2).getType() == Material.AIR
                            && b.getRelative(BlockFace.DOWN, 3).getType() == Material.AIR
                            && b.getRelative(BlockFace.DOWN, 4).getType() == Material.AIR)
                    {
                        this.cancel();
                        autoGlideTask = null;
                        player.setGliding(true);
                        displayActionBar(player);
                    }
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 10);
    }

    public boolean isIgnoreSpeedLevel()
    {
        return ignoreSpeedLevel;
    }

    public void setIgnoreSpeedLevel(boolean ignoreSpeedLevel)
    {
        this.ignoreSpeedLevel = ignoreSpeedLevel;
    }

    /////////// STATIC ////////////

    public static int[] getPrices()
    {
        return prices;
    }



}
