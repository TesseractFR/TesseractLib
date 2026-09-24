package onl.tesseract.lib.event.equipment.invocable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import onl.tesseract.lib.animation.AnimationTarget;
import onl.tesseract.lib.animation.Circle;
import onl.tesseract.lib.animation.Concentration;
import onl.tesseract.lib.chat.ChatFormats;
import onl.tesseract.lib.equipment.EquipmentService;
import onl.tesseract.lib.equipment.Invocable;
import onl.tesseract.lib.itembuilder.ItemBuilder;
import onl.tesseract.lib.service.PluginService;
import onl.tesseract.lib.service.ServiceContainer;
import onl.tesseract.lib.task.TaskScheduler;
import onl.tesseract.lib.util.Util;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Elytra extends Invocable implements Listener {
    private static final int MS_TO_SECONDS = 1000;
    private static final double SPEED_MULTIPLIER = 0.10;
    private static final double PROTECTION_MULTIPLIER = 0.5;
    private static final int PERCENT_CONVERSION = 100;
    private static final int BOOST_BASE = 5;
    private static final int BOOST_LEVEL_MULTIPLIER = 5;
    private static final int MAX_DISPLAYED_BOOSTS = 10;
    private static final int MAX_BOOST_LEVEL = 10;
    private static final long MAX_RECOVERY_TIME = 50000L;
    private static final long RECOVERY_DECREASE_PER_LEVEL = 5000L;
    private static final double BOOST_CONSUMPTION_MULTIPLIER = 0.7;
    private static final float LOW_PITCH = 0.5f;
    private static final double SPEED_THRESHOLD_BASE = 1.20;
    private static final double SPEED_THRESHOLD_STEP = 0.10;
    private static final long ACTIONBAR_INTERVAL_TICKS = 2L;
    private static final double ACTIONBAR_RECHARGE_TICK_MS = 50.0;
    private static final double RECHARGE_FULL_THRESHOLD = 1.0;
    private static final int SPEED_DISPLAY_MULTIPLIER = 20;
    private static final float PARTICLE_DELAY = 0.1f;
    private static final float PARTICLE_RADIUS = 1f;
    private static final float PARTICLE_ROTATION = 3f;
    private static final int LEVITATION_DURATION = 40;
    private static final float SOUND_VOLUME = 20f;
    private static final long ALTITUDE_SOUND_DELAY = 40L;
    private static final int PROPULSION_RADIUS = 2;
    private static final int PROPULSION_COUNT = 20;
    private static final long FIRST_TIMER_DELAY = 30L;
    private static final long SECOND_TIMER_DELAY = 20L;
    private static final float FIREWORK_SOUND_VOLUME = 150f;

    private BukkitTask accelerateTask;
    private BukkitTask actionBarTask;
    private BukkitTask autoGlideTask;
    private boolean ignoreSpeedLevel;
    private boolean autoGlide = true;
    private int protectionLevel;
    private int speedLevel;
    private int boostChargeLevel;
    private int recoveryLevel;
    private int currentCharges;
    private double rechargeProgress;
    private static double lastVelocity;
    private static boolean isManuallyAccelerating;

    public Elytra(@NotNull UUID playerUUID, boolean invoked, int handSlot) {
        this(playerUUID, invoked, handSlot, true, 0, 0, 0, 0, 0, 0.0);
    }

    public Elytra(@NotNull UUID playerUUID, boolean invoked, int handSlot, boolean autoGlide,
                  int protectionLevel, int speedLevel, int boostChargeLevel, int recoveryLevel,
                  int currentCharges, double rechargeProgress) {
        super(playerUUID, invoked, handSlot);
        this.autoGlide = autoGlide;
        this.protectionLevel = protectionLevel;
        this.speedLevel = speedLevel;
        this.boostChargeLevel = boostChargeLevel;
        this.recoveryLevel = recoveryLevel;
        this.currentCharges = currentCharges;
        this.rechargeProgress = rechargeProgress;
    }

    public boolean getAutoGlide() { return autoGlide; }
    public void setAutoGlide(boolean value) { autoGlide = value; }
    public int getProtectionLevel() { return protectionLevel; }
    public void setProtectionLevel(int value) { protectionLevel = value; }
    public int getSpeedLevel() { return speedLevel; }
    public void setSpeedLevel(int value) { speedLevel = value; }
    public int getBoostChargeLevel() { return boostChargeLevel; }
    public void setBoostChargeLevel(int value) { boostChargeLevel = value; }
    public int getRecoveryLevel() { return recoveryLevel; }
    public void setRecoveryLevel(int value) { recoveryLevel = value; }
    public int getCurrentCharges() { return currentCharges; }
    public void setCurrentCharges(int value) { currentCharges = value; }
    public double getRechargeProgress() { return rechargeProgress; }
    public void setRechargeProgress(double value) { rechargeProgress = value; }

    @Override public boolean getExcludeOthers() { return true; }
    @Override @NotNull public EquipmentSlot getSlotType() { return EquipmentSlot.CHEST; }
    @Override @NotNull public String getUniqueName() { return getClass().getSimpleName(); }

    @Override
    protected ItemStack createItem() {
        int boostCount = getBoostCount(boostChargeLevel);
        long recoveryTimeSeconds = getBaseRecoveryTime(recoveryLevel) / MS_TO_SECONDS;
        int speedBonus = (int) (SPEED_MULTIPLIER * (speedLevel + 1) * PERCENT_CONVERSION);
        double protectionBonus = PROTECTION_MULTIPLIER * protectionLevel;
        ItemStack item = new ItemBuilder(Material.ELYTRA)
                .name(Component.text("Flanc éthéré", NamedTextColor.GOLD))
                .lore().append(Component.text("« Des ailes divines imprégnées de clairvoyance. »", NamedTextColor.DARK_PURPLE))
                .newline().newline()
                .append(Component.text("Vitesse : ", NamedTextColor.DARK_AQUA))
                .append(Component.text("+" + speedBonus + "%", NamedTextColor.GOLD)).newline()
                .append(Component.text("Protection : ", NamedTextColor.DARK_AQUA))
                .append(Component.text(protectionBonus + " points", NamedTextColor.GOLD)).newline()
                .append(Component.text("Boosts max : ", NamedTextColor.DARK_AQUA))
                .append(Component.text(Integer.toString(boostCount), NamedTextColor.GOLD)).newline()
                .append(Component.text("Temps recharge : ", NamedTextColor.DARK_AQUA))
                .append(Component.text("1 boost / " + recoveryTimeSeconds + "s", NamedTextColor.GOLD))
                .buildLore().enchanted(true).build();
        var meta = item.getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(
                new NamespacedKey(NamespacedKey.MINECRAFT, "generic.armor"), protectionLevel,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onUninvoke(@NotNull Player player, boolean manuelRemoval) {
        if (manuelRemoval) setInvoked(false);
        ServiceContainer.get(PluginService.class).unregisterEventListener(this);
        cancel(actionBarTask); actionBarTask = null;
        cancel(autoGlideTask); autoGlideTask = null;
        cancel(accelerateTask); accelerateTask = null;
        if (manuelRemoval) animate(player);
    }

    @Override
    public void onInvoke(@NotNull Player player, boolean manuelInvocation) {
        ServiceContainer.get(PluginService.class).registerEventListener(this);
        if (!player.isGliding()) player.setGliding(true);
        if (currentCharges == 0) currentCharges = getBoostCount(boostChargeLevel);
        displayActionBar(player);
        if (autoGlide) toggleAutoGlideEnabled(true);
        if (manuelInvocation) animate(player);
    }

    @EventHandler
    public void onAccelerate(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking() || !isInvoked() || !player.isGliding() || accelerateTask != null) return;
        if (currentCharges <= 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, LOW_PITCH);
            return;
        }
        accelerateTask = ServiceContainer.get(TaskScheduler.class).runTimer(0L, 10L, 0L, task -> {
            isManuallyAccelerating = true;
            int speed = player.getLocation().getWorld().getName().equals("Event") ? 0 : getEffectiveSpeedLevel();
            if (!player.isOnline() || !player.isSneaking() || !player.isGliding()) {
                task.cancel(); accelerateTask = null; isManuallyAccelerating = false; return;
            }
            if (player.getVelocity().length() < getMaxSpeed(speed)) {
                currentCharges--;
                player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(BOOST_CONSUMPTION_MULTIPLIER)));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1f, 1f);
            }
        });
        saveYaml();
    }

    private double getMaxSpeed(int level) { return SPEED_THRESHOLD_BASE + SPEED_THRESHOLD_STEP * (level + 1); }

    @EventHandler
    public void onFly(EntityToggleGlideEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && event.getEntity().getUniqueId().equals(getPlayerUUID()) && isInvoked() && event.isGliding())
            displayActionBar((Player) event.getEntity());
    }

    public void toggleAutoGlideEnabled(boolean enabled) {
        autoGlide = enabled;
        cancel(autoGlideTask); autoGlideTask = null;
        if (!enabled) return;
        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player == null) return;
        autoGlideTask = ServiceContainer.get(TaskScheduler.class).runTimer(0L, 10L, 0L, task -> {
            if (!player.isOnline() || !isInvoked()) return;
            if (player.hasPotionEffect(PotionEffectType.LEVITATION)) return;
            boolean airBelow = true;
            for (int i = 1; i <= 4; i++) if (!player.getLocation().getBlock().getRelative(BlockFace.DOWN, i).isPassable()) airBelow = false;
            if (airBelow && !player.isGliding()) { player.setGliding(true); displayActionBar(player); }
        });
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getPlayerUUID()) || !isInvoked()) return;
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(Elytra.class), () -> {
            Player player = event.getPlayer();
            if (player.getInventory().getChestplate() == null) player.getInventory().setChestplate(getItem());
        }, 1L);
    }

    private void displayActionBar(Player player) {
        if (actionBarTask != null && !actionBarTask.isCancelled()) actionBarTask.cancel();
        actionBarTask = ServiceContainer.get(TaskScheduler.class).runTimer(0L, ACTIONBAR_INTERVAL_TICKS, 0L, task -> {
            if (!player.isOnline()) { task.cancel(); accelerateTask = null; return; }
            long rechargeTime = getRecoveryTime(recoveryLevel, player);
            boolean fastRecharge = rechargeTime < getBaseRecoveryTime(recoveryLevel);
            if (currentCharges < getBoostCount(boostChargeLevel)) {
                rechargeProgress += ACTIONBAR_INTERVAL_TICKS * ACTIONBAR_RECHARGE_TICK_MS / rechargeTime;
                if (rechargeProgress >= RECHARGE_FULL_THRESHOLD) { currentCharges++; rechargeProgress = 0.0; }
                saveYaml();
            }
            var comp = Component.text();
            if (player.isGliding()) {
                comp.append(Component.text("Vitesse: ")).append(Component.text((int) (player.getVelocity().length() * SPEED_DISPLAY_MULTIPLIER), NamedTextColor.AQUA))
                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY)).append(Component.text("Alt: "))
                        .append(Component.text(player.getLocation().getBlockY(), NamedTextColor.GREEN)).append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("Distance: ")).append(Component.text((int) player.getLocation().distance(player.getCompassTarget()), NamedTextColor.YELLOW))
                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY));
            }
            comp.append(Component.text("Boosts : ")).append(boostBar());
            if (currentCharges < getBoostCount(boostChargeLevel) && fastRecharge) comp.append(Component.text(" ⏻", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
            player.sendActionBar(comp.build());
        });
    }

    private Component boostBar() {
        var builder = Component.text();
        int max = getBoostCount(boostChargeLevel), visible = Math.min(MAX_DISPLAYED_BOOSTS, currentCharges);
        boolean recharging = currentCharges < max;
        int overflow = currentCharges - visible;
        if (overflow > 0) builder.append(Component.text("(+" + overflow + ") ", NamedTextColor.GREEN, TextDecoration.BOLD));
        for (int i = 0; i < visible; i++) builder.append(Component.text("⚡", NamedTextColor.GREEN, TextDecoration.BOLD));
        if (recharging) {
            TextColor color = Util.getGreenRedGradient((int) (Math.clamp(rechargeProgress, 0.0, 1.0) * PERCENT_CONVERSION), 100);
            builder.append(Component.text("⚡", color, TextDecoration.BOLD));
        }
        int remaining = Math.min(max, MAX_DISPLAYED_BOOSTS) - (visible + (recharging ? 1 : 0));
        for (int i = 0; i < remaining; i++) builder.append(Component.text("⚡", TextColor.color(255, 0, 40), TextDecoration.BOLD));
        return builder.build();
    }

    public int getLevel(ElytraUpgrade upgrade) {
        return switch (upgrade) { case PROTECTION -> protectionLevel; case SPEED -> speedLevel; case BOOST_NUMBER -> boostChargeLevel; case RECOVERY -> recoveryLevel; };
    }

    public void setLevel(ElytraUpgrade upgrade, int level) {
        switch (upgrade) {
            case PROTECTION -> protectionLevel = level;
            case SPEED -> speedLevel = level;
            case BOOST_NUMBER -> { boostChargeLevel = level; currentCharges = Math.min(currentCharges, getBoostCount(level)); }
            case RECOVERY -> recoveryLevel = level;
        }
        refreshItemInInventory();
    }

    public void upgradeLevel(ElytraUpgrade upgrade) {
        switch (upgrade) { case PROTECTION -> protectionLevel++; case SPEED -> speedLevel++; case BOOST_NUMBER -> boostChargeLevel++; case RECOVERY -> recoveryLevel++; }
        refreshItemInInventory();
    }

    public void enableSpeedUpgrade() { ignoreSpeedLevel = false; }

    private void animate(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, LEVITATION_DURATION, 0));
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, SOUND_VOLUME, 1f);
        TaskScheduler scheduler = ServiceContainer.get(TaskScheduler.class);
        new Circle(Particle.DUST, new AnimationTarget(player), scheduler.getPlugin()).setColor(Color.FUCHSIA).setDelay(PARTICLE_DELAY).setRadius(PARTICLE_RADIUS).setRotationCount(PARTICLE_ROTATION).draw();
        scheduler.runTimer(ALTITUDE_SOUND_DELAY, 0L, 0L, task -> player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SOUND_VOLUME, 1f));
    }

    public void synergicPropulsion(Player player) {
        if (currentCharges <= 0) {
            player.sendMessage(ChatFormats.ELYTRA_ERROR.append(Component.text("Vous n'avez plus de boost disponible, patientez quelques instants.")));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, LOW_PITCH); return;
        }
        player.sendMessage(ChatFormats.ELYTRA_SUCCESS.append(Component.text("Décollage imminent !")));
        TaskScheduler scheduler = ServiceContainer.get(TaskScheduler.class);
        new Concentration(scheduler.getPlugin()).setParticle(Particle.DUST).setColor(Color.FUCHSIA).setTarget(new AnimationTarget(player)).setRadius(PROPULSION_RADIUS).setCount(PROPULSION_COUNT).build().draw();
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, SOUND_VOLUME, 1f);
        scheduler.runTimer(FIRST_TIMER_DELAY, 0L, 0L, task -> {
            if (!player.isOnline() || !isInvoked()) return;
            player.setVelocity(new Vector(0, 2, 0));
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, FIREWORK_SOUND_VOLUME, 1f);
            scheduler.runTimer(SECOND_TIMER_DELAY, 0L, 0L, second -> {
                if (player.isOnline() && isInvoked()) {
                    player.setVelocity(player.getLocation().getDirection()); player.setGliding(true);
                    player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, FIREWORK_SOUND_VOLUME, 1f);
                }
            });
            currentCharges--; saveYaml();
        });
    }

    private int getEffectiveSpeedLevel() { return ignoreSpeedLevel ? 1 : speedLevel; }
    @Override public void use(PlayerInteractEvent event) { }
    @Override public void useInInventory(InventoryClickEvent event) { }
    private void refreshItemInInventory() { updateItem(true); }
    private void saveYaml() { EquipmentService service = ServiceContainer.get(EquipmentService.class); service.saveEquipment(service.getEquipment(getPlayerUUID())); }
    private static void cancel(BukkitTask task) { if (task != null) task.cancel(); }

    public static int getBoostCount(int level) { return level >= 0 && level <= MAX_BOOST_LEVEL ? BOOST_BASE + level * BOOST_LEVEL_MULTIPLIER : BOOST_BASE; }
    public static long getBaseRecoveryTime(int level) { return MAX_RECOVERY_TIME - RECOVERY_DECREASE_PER_LEVEL * level; }
    public static long getRecoveryTime(int level, Player player) {
        long base = getBaseRecoveryTime(level);
        double velocity = player.getVelocity().length();
        boolean passive = player.isGliding() && velocity > lastVelocity && !isManuallyAccelerating;
        lastVelocity = velocity;
        return !player.isGliding() || passive ? base / 2 : base;
    }
}
