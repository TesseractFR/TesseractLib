package onl.tesseract.tesseractlib.equipment.invocable;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.animation.AnimationTarget;
import onl.tesseract.tesseractlib.animation.Circle;
import onl.tesseract.tesseractlib.animation.Concentration;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.Util;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Map;

public class Elytra extends Invocable {
    BukkitTask accelerateTask;
    BukkitTask actionBarTask;
    BukkitTask autoGlideTask;

    boolean autoGlide = true;

    boolean ignoreSpeedLevel = false;

    int protectionLevel = 0;
    int speedLevel = 0;
    int topprotectionLevel = 0;
    int topspeedLevel = 0;

    // static int[] prices = new int[] {2000,4000,8000,14000,19000,25000,30000,35000,40000};
    static int[] prices = new int[] {100,200,300,400,500,600,700,800,900};

    public enum Upgrade {
        PROTECTION, VITESSE
    }



    public Elytra(Equipment equipment)
    {
        super(equipment, EquipmentSlot.CHEST, "INVOCABLE_ELYTRA", createItem());
        setAutoGlide(true);
        setItem();
        equipment.unblockedChestplate.add(this);
    }

    public Elytra(Equipment equipment, Map<String, Object> yamlMap)
    {
        super(equipment, EquipmentSlot.CHEST, "INVOCABLE_ELYTRA", createItem(), yamlMap);
        setAutoGlide((boolean) yamlMap.get("autoGlide"));
        protectionLevel = (int) yamlMap.get("protectionLvl");
        speedLevel = (int)yamlMap.get("speedLvl");
        topprotectionLevel = (int) yamlMap.getOrDefault("topprotectionLvl", 0);
        topspeedLevel = (int)yamlMap.getOrDefault("topspeedLvl", 0);
        if (this.topprotectionLevel == 0)
            this.topprotectionLevel = protectionLevel;
        if (this.topspeedLevel == 0)
            this.topspeedLevel = speedLevel;
        setItem();
        // Load active trail


        equipment.unblockedChestplate.add(this);
    }

    static ItemStack createItem() {
        return new ItemBuilder(Material.ELYTRA)
                .name("Flanc éthéré", NamedTextColor.GOLD)
                .lore(ChatColor.DARK_PURPLE + "« Des ailes divines imprégnées de clairvoyance. »" + Util.NEW_LINE + Util.NEW_LINE +
                              ChatColor.DARK_AQUA + "Vitesse : " + ChatColor.GOLD + "0" + Util.NEW_LINE +
                              ChatColor.DARK_AQUA + "Protection : " + ChatColor.GOLD + "0")
                .enchanted(true)
                .build();
    }

    void setItem() {
        this.item = new ItemBuilder(Material.ELYTRA)
                .name("Flanc éthéré", NamedTextColor.GOLD)
                .lore(ChatColor.DARK_PURPLE + "« Des ailes divines imprégnées de clairvoyance. »" + Util.NEW_LINE + Util.NEW_LINE +
                        ChatColor.DARK_AQUA + "Vitesse : " + ChatColor.GOLD + speedLevel + Util.NEW_LINE +
                        ChatColor.DARK_AQUA + "Protection : " + ChatColor.GOLD + protectionLevel)
                .enchanted(true)
                .build();
        ItemMeta meta = item.getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", protectionLevel, AttributeModifier.Operation.ADD_NUMBER));

        meta.setLocalizedName(localizedName);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        updateItemInInventory();
    }

    @Override
    public Map<String, Object> save() {
        Map<String, Object> map = super.save();
        map.put("autoGlide", this.autoGlide);
        map.put("protectionLvl", this.protectionLevel);
        map.put("speedLvl", this.speedLevel);
        map.put("topprotectionLvl", this.topprotectionLevel);
        map.put("topspeedLvl", this.topspeedLevel);
        return map;
    }

    @EventHandler
    public void onAccelerate(PlayerToggleSneakEvent event)
    {
        if (! event.getPlayer().equals(equipment.getPlayer().getBukkitPlayer())) return;
        // If the player is sneaking in flight
        if (event.isSneaking() && invoked && event.getPlayer().isGliding() && event.getPlayer().getVelocity().length() < (1.20 + (0.10 * (getSpeedLevel() + 1)))
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
                            0 : getSpeedLevel();
                    // Cancel if not sneaking or flying
                    if (!player.isOnline() || !player.isSneaking() || !player.isGliding()) {
                        this.cancel();
                        accelerateTask = null;
                    }
                    else if (player.getVelocity().length() < (1.20 + (0.10 * (finalSpeedLevel + 1)))) {
                        Vector vector = player.getVelocity();
                        vector.add(player.getLocation().getDirection().multiply(0.7));

                        player.setVelocity(vector);
                        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
                    }
                }
            }.runTaskTimer(TesseractLib.instance, 0, 10);
        }
    }

    @EventHandler
    public void onFly(EntityToggleGlideEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (event.getEntity().equals(equipment.getPlayer().getBukkitPlayer()) && invoked)
        {
            if (event.isGliding())
            {
                displayActionBar((Player) event.getEntity());
            } else if (autoGlide)
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
                    ElytraTrails trail = TPlayer.get(player).getActiveTrail();
                    if (trail != ElytraTrails.NONE) {
                        ParticleBuilder builder = new ParticleBuilder(trail.getParticle());
                        if (trail == ElytraTrails.SHINNING)
                            builder.count(1);
                        else
                            builder.count(2);
                        builder.offset(.5, .5, .5);
                        if (trail == ElytraTrails.POTION || trail == ElytraTrails.MUSICAL)
                            builder.extra(0.2);
                        else
                            builder.extra(0);
                        builder.location(Util.Locations.backward(player.getLocation(), 2));
                        builder.receivers(100);
                        if (trail == ElytraTrails.REDSTONE)
                            builder.color(Color.RED);
                        builder.spawn();
                    }
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 2);
    }

    @Override
    protected void onUninvoke(boolean manualUninvocation)
    {
        if (manualUninvocation)
            animate();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onInvoke(boolean manuelInvocation)
    {
        if (! equipment.getPlayer().getBukkitPlayer().isOnGround() && !equipment.getPlayer().getBukkitPlayer().isGliding())
            equipment.getPlayer().getBukkitPlayer().setGliding(true);
        if (autoGlide)
            setAutoGlide(true);

        if (manuelInvocation)
            animate();
    }

    void animate() {
        equipment.getPlayer().getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 0));
        equipment.getPlayer().getBukkitPlayer().playSound(equipment.getPlayer().getBukkitPlayer().getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 20, 1);
        new Circle(Particle.REDSTONE, new AnimationTarget(equipment.getPlayer().getBukkitPlayer()))
                .setColor(Color.FUCHSIA)
                .setDelay(0.1f)
                .setRadius(1)
                .setRotationCount(3).draw();
        new BukkitRunnable() {
            @Override
            public void run()
            {
                equipment.getPlayer().getBukkitPlayer().playSound(equipment.getPlayer().getBukkitPlayer().getLocation(),
                        Sound.BLOCK_END_PORTAL_SPAWN, 20, 1);
            }
        }.runTaskLater(TesseractLib.instance, 40);
    }


    /**
     * Propells the player upward then forward.
     */
    public void synergicPropulsion()
    {
        Player player = equipment.getPlayer().getBukkitPlayer();
        new Concentration(Particle.REDSTONE, Color.FUCHSIA, new AnimationTarget(player), 2, 20);
        equipment.getPlayer().getBukkitPlayer().playSound(equipment.getPlayer().getBukkitPlayer().getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 20, 1);
        new BukkitRunnable() {
            @Override
            public void run()
            {
                if (! player.isOnline() || !invoked) return;
                player.setVelocity(new Vector(0, 2, 0));
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 150, 1);
                new BukkitRunnable() {
                    @Override
                    public void run()
                    {
                        if (player.isOnline() && invoked) {
                            player.setVelocity(player.getLocation().getDirection());
                            player.setGliding(true);
                            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 150, 1);
                        }
                    }
                }.runTaskLater(TesseractLib.instance, 20);
            }
        }.runTaskLater(TesseractLib.instance, 30);
    }

    public int getProtectionLevel()
    {
        return protectionLevel;
    }

    public int getSpeedLevel()
    {
        return ignoreSpeedLevel ? 1 : speedLevel;
    }
    public int getTopSpeedLevel()
    {
        return ignoreSpeedLevel ? 1 : topspeedLevel;
    }
    public void topLevel(Upgrade type) {
        if (type == Upgrade.PROTECTION)
            topprotectionLevel++;
        else
            topspeedLevel++;
        setItem();
    }
    public int getTopLevel(Upgrade type) {
        return type == Upgrade.PROTECTION ? topprotectionLevel : getTopSpeedLevel();
    }
    public void setTopLevel(Upgrade type , int level)
    {
        if (type == Upgrade.PROTECTION)
            this.topprotectionLevel = level;
        else
            this.topspeedLevel = level;
        setItem();
    }
    public int getLevel(Upgrade type) {
        return type == Upgrade.PROTECTION ? protectionLevel : getSpeedLevel();
    }
    public void setLevel(Upgrade type , int level)
    {
        if (type == Upgrade.PROTECTION)
            this.protectionLevel = level;
        else
            this.speedLevel = level;
        setItem();
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
        setItem();
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
        if (autoGlide)
        {
            autoGlideTask = new BukkitRunnable() {
                final Player player = equipment.getPlayer().getBukkitPlayer();
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
