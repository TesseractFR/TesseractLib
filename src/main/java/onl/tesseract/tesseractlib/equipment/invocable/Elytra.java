package onl.tesseract.tesseractlib.equipment.invocable;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.animation.AnimationTarget;
import onl.tesseract.tesseractlib.animation.Circle;
import onl.tesseract.tesseractlib.animation.Concentration;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormat;
import onl.tesseract.tesseractlib.util.Util;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Elytra extends Invocable {
    BukkitTask accelerateTask;
    BukkitTask actionBarTask;
    BukkitTask autoGlideTask;

    boolean autoGlide = true;

    boolean ignoreSpeedLevel = false;
    List<Trail> purchasedTrails = new ArrayList<>();
    Trail trail = Trail.NONE;

    int protectionLevel = 0;
    int speedLevel = 0;

    // static int[] prices = new int[] {2000,4000,8000,14000,19000,25000,30000,35000,40000};
    static int[] prices = new int[] {100,200,300,400,500,600,700,800,900};

    public enum Upgrade {
        PROTECTION, VITESSE
    }

    public enum Trail {
        ENDER(ChatColor.DARK_PURPLE + "Ender", Material.ENDER_PEARL,1, Particle.DRAGON_BREATH),
        FLAME(ChatColor.GOLD + "Flammes", Material.BLAZE_POWDER,2, Particle.FLAME),
        CLOUD(ChatColor.GRAY + "Nuages", Material.PHANTOM_MEMBRANE,3, Particle.CLOUD),
        LOVE(ChatColor.GRAY + "Amour", Material.APPLE,4, Particle.HEART),
        MUSICAL(ChatColor.DARK_AQUA + "Musical", Material.NOTE_BLOCK,5, Particle.NOTE),
        REDSTONE(ChatColor.DARK_RED + "Redstone", Material.REDSTONE,6, Particle.REDSTONE),
        SMOKE(ChatColor.DARK_GRAY + "Fumée noire", Material.CHARCOAL,7, Particle.SMOKE_LARGE),
        GREEN(ChatColor.GREEN + "Verdoyant", Material.LILY_PAD,10, Particle.VILLAGER_HAPPY),
        ANGER(ChatColor.DARK_RED + "Colère", Material.NETHER_WART,11, Particle.VILLAGER_ANGRY),
        INCENDIARY(ChatColor.GOLD + "Incendiaire", Material.FIRE_CHARGE,12, Particle.LAVA),
        NEBULOUS(ChatColor.WHITE + "Nébuleux", Material.FEATHER,13, Particle.END_ROD),
        TOTEM(ChatColor.DARK_GREEN + "Totem", Material.TOTEM_OF_UNDYING,14, Particle.TOTEM),
        POTION(ChatColor.LIGHT_PURPLE + "Potion", Material.DRAGON_BREATH,15, Particle.SPELL_MOB),
        SHINNING(ChatColor.WHITE + "Scintillant", Material.PRISMARINE_CRYSTALS,16, Particle.FIREWORKS_SPARK),
        NONE(ChatColor.GRAY + "Sans sillage", Material.STRUCTURE_VOID, 0, null)
        ;

        String name;
        Material material;
        int index;
        Particle particle;
        Trail(String s, Material m, int i, Particle p)
        {
            name = s;
            material = m;
            index = i;
            particle = p;
        }
        public int getIndex() {return index;}
        public Material getMaterial() {return material;}
        public String getName() {
            return name;
        }
        public Particle getParticle()
        {
            return particle;
        }
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
        setItem();

        // Load trails
        File file = new File(TPlayer.folderPath + equipment.getPlayer().getOfflinePlayer().getUniqueId() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (yaml.contains("elytraTrails"))
        {
            List<String> names = yaml.getStringList("elytraTrails");
            purchasedTrails = names.stream().map(Trail::valueOf).collect(Collectors.toList());
        }
        // Load active trail
        if (yamlMap.containsKey("trail"))
            trail = Trail.valueOf((String) yamlMap.get("trail"));

        equipment.unblockedChestplate.add(this);
    }

    static ItemStack createItem() {
        ItemStack item = Util.buildItem(Material.ELYTRA, ChatColor.GOLD + "Flanc éthéré",
                ChatColor.DARK_PURPLE + "« Des ailes divines imprégnées de clairvoyance. »" + Util.NEW_LINE + Util.NEW_LINE +
                        ChatColor.DARK_AQUA + "Vitesse : " + ChatColor.GOLD + "0" + Util.NEW_LINE +
                        ChatColor.DARK_AQUA + "Protection : " + ChatColor.GOLD + "0", true);
        return item;
    }

    void setItem() {
        this.item = Util.buildItem(Material.ELYTRA, ChatColor.GOLD + "Flanc éthéré",
                ChatColor.DARK_PURPLE + "« Des ailes divines imprégnées de clairvoyance. »" + Util.NEW_LINE + Util.NEW_LINE +
                        ChatColor.DARK_AQUA + "Vitesse : " + ChatColor.GOLD + speedLevel + Util.NEW_LINE +
                        ChatColor.DARK_AQUA + "Protection : " + ChatColor.GOLD + protectionLevel, true);
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
        map.put("trail", this.trail.toString());
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
                    player.sendActionBar(
                            ChatColor.GRAY + "Vitesse: " + ChatColor.AQUA + (int) (player.getVelocity().length() * 20)
                                    + ChatColor.DARK_GRAY + " | "
                                    + ChatColor.GRAY + "Alt: " + ChatColor.GREEN + player.getLocation().getBlockY()
                                    + ChatColor.DARK_GRAY + " | "
                                    + ChatColor.GRAY + "Distance: " + ChatColor.YELLOW + (int) (player.getLocation().distance(player.getCompassTarget()))
                    );
                    if (trail != Trail.NONE) {
                        ParticleBuilder builder = new ParticleBuilder(trail.getParticle());
                        if (trail == Trail.SHINNING)
                            builder.count(1);
                        else
                            builder.count(2);
                        builder.offset(.5, .5, .5);
                        if (trail == Trail.POTION || trail == Trail.MUSICAL)
                            builder.extra(0.2);
                        else
                            builder.extra(0);
                        builder.location(Util.Locations.backward(player.getLocation(), 2));
                        builder.receivers(100);
                        if (trail == Trail.REDSTONE)
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

    public int getSpeedLevel(boolean ignoreSpeedLevel)
    {
        return ignoreSpeedLevel ? 1 : speedLevel;
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
            protectionLevel++;
        else
            speedLevel++;
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

    public List<Trail> getPurchasedTrails()
    {
        return purchasedTrails;
    }

    public Trail getTrail()
    {
        return trail;
    }

    public void setTrail(Trail trail)
    {
        this.trail = trail;
        equipment.getPlayer().sendMessage(ChatFormat.EQUIPMENT + "Le sillage a été activé !");
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

    public static void addTrail(OfflinePlayer player, Trail trail) {
        if (player.isOnline())
        {
            Elytra el = (Elytra) TPlayer.get((Player) player).getEquipment().getLike(Elytra.class);
            el.getPurchasedTrails().add(trail);
            ((Player) player).sendMessage(ChatFormat.EQUIPMENT_SUCCESS + "Le sillage " + trail.getName() + ChatColor.GREEN +
                    " a bien été ajouté à vos ailes ! Activez le dans le menu des ailes.");
        }
        File file = new File(TPlayer.folderPath + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> trails = yaml.contains("elytraTrails") ? yaml.getStringList("elytraTrails") : new ArrayList<>();
        trails.add(trail.toString());
        yaml.set("elytraTrails", trails);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasTrail(OfflinePlayer player, Trail trail) {
        File file = new File(TPlayer.folderPath + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> trails = yaml.contains("elytraTrails") ? yaml.getStringList("elytraTrails") : new ArrayList<>();
        return trails.contains(trail.toString());
    }

    public static void removeTrail(OfflinePlayer player, Trail trail)
    {
        if (player.isOnline()) {
            Elytra el = (Elytra) TPlayer.get((Player) player).getEquipment().getLike(Elytra.class);
            el.getPurchasedTrails().remove(trail);
            if (el.getTrail() == trail)
                el.setTrail(Trail.NONE);
        }
        ((Player) player).sendMessage(ChatFormat.EQUIPMENT_SUCCESS + "Le sillage " + trail.getName() + ChatColor.GREEN +
                " a été retiré de vos ailes !");
        File file = new File(TPlayer.folderPath + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> trails = yaml.contains("elytraTrails") ? yaml.getStringList("elytraTrails") : new ArrayList<>();
        trails.remove(trail.toString());
        yaml.set("elytraTrails", trails);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
