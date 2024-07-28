package onl.tesseract.tesseractlib;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.tesseractlib.achievement.AchievementManager;
import onl.tesseract.tesseractlib.afk.AfkManager;
import onl.tesseract.tesseractlib.entity.Title;
import onl.tesseract.tesseractlib.bdd.BDDManager;
import onl.tesseract.tesseractlib.chat.tag.TagEventHandler;
import onl.tesseract.tesseractlib.command.*;
import onl.tesseract.tesseractlib.command.staff.*;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.cosmetics.familier.PetManager;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.event.ColoredChat;
import onl.tesseract.tesseractlib.event.EntityBossBar;
import onl.tesseract.tesseractlib.event.PlayerSit;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceEventHandler;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceManager;
import onl.tesseract.tesseractlib.placeholder.TesseractPlaceHolder;
import onl.tesseract.tesseractlib.player.PlayerContainer;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.player.def.StandalonePlayerContainer;
import onl.tesseract.tesseractlib.util.Util;
import onl.tesseract.tesseractlib.vote.VoteManager;
import onl.tesseract.tesseractlib.vote.goal.VoteGoalManager;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TesseractLib extends JavaPlugin implements Listener {
    public static JavaPlugin instance;

    private Config config;
    private static BDDManager bddManager;
    static public int port;

    private static PlayerContainer<? extends TPlayer, ? extends Equipment> playerContainer;

    public static BDDManager getBddManager() {
        if(bddManager == null){
            Config config = Config.getInstance();
            bddManager= new BDDManager(config.getDb_host(),config.getDb_port(),config.getDb_username(),config.getDb_password(),config.getDb_database());
        }
        return bddManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        config = Config.getInstance();
        setPlayerContainer(new StandalonePlayerContainer());
        bddManager= new BDDManager(config.getDb_host(),config.getDb_port(),config.getDb_username(),config.getDb_password(),config.getDb_database());
        registerEvents();
        registerCommands();
        new TesseractPlaceHolder(this).register();
        logger().info("Loading title...");
        Title.loadAll();
        logger().info("Loading achievement...");
        AchievementManager.getInstance().loadAll();

        try
        {
            InventoryInstanceManager.loadConfigurations();
        }
        catch (IOException e)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to load inventories configurations", e);
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        InventoryInstanceManager.loadPlayers();

        VoteManager.getInstance().init();
        VoteGoalManager.startLoops();
    }

    public static void setPlayerContainer(final PlayerContainer<?, ?> playerContainer)
    {
        TesseractLib.playerContainer = playerContainer;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        bddManager.close();
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreJoin(AsyncPlayerPreLoginEvent event)
    {
        if (!playerContainer.exists(event.getPlayerProfile().getId()))
            return;
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED)
        {
            UUID uuid = event.getPlayerProfile().getId();
            if (uuid == null)
                return;
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            var instant = Instant.ofEpochMilli(player.getLastLogin());
            if (Duration.between(instant, Instant.now()).toDays() > 0)
                return;
            var dateTime = instant.atZone(ZoneId.systemDefault());
            boolean hasPlayedToday = dateTime.getDayOfYear() == Instant.now().atZone(ZoneId.systemDefault()).getDayOfYear();
            playerContainer.get(uuid).setPlayedToday(hasPlayedToday);
        }
    }

    void registerCommands()
    {
        Objects.requireNonNull(instance.getCommand("animation")).setExecutor(new Animation());
        Objects.requireNonNull(instance.getCommand("animation")).setTabCompleter(new Animation());
        Objects.requireNonNull(instance.getCommand("equipment")).setExecutor(new EquipmentCommand());
        Objects.requireNonNull(instance.getCommand("socialspy")).setExecutor(new SocialSpy());
        Objects.requireNonNull(instance.getCommand("msg")).setExecutor(new MsgCommand());
        Objects.requireNonNull(instance.getCommand("reply")).setExecutor(new ReplyToMsg());
        Objects.requireNonNull(instance.getCommand("familier")).setExecutor(new FamilierCommand());
        Objects.requireNonNull(instance.getCommand("cosmetic")).setExecutor(new CosmeticCommand());
        Objects.requireNonNull(instance.getCommand("cosmetic")).setTabCompleter(new CosmeticCompleter());
        Objects.requireNonNull(instance.getCommand("boutique")).setExecutor(new BoutiqueCommand());
        Objects.requireNonNull(instance.getCommand("marketCurrency")).setExecutor(new MarketCurrencyCommand());
        Objects.requireNonNull(instance.getCommand("votegoal")).setExecutor(new VoteGoalCommand());
        Objects.requireNonNull(instance.getCommand("votegoal")).setTabCompleter(new VoteGoalCommand());
        Objects.requireNonNull(instance.getCommand("vote")).setExecutor(new VoteCommand());
        Objects.requireNonNull(instance.getCommand("inventory")).setExecutor(new InventoryCommand());
        Objects.requireNonNull(instance.getCommand("inventory")).setTabCompleter(new InventoryCommand());
        Objects.requireNonNull(instance.getCommand("votetopreward")).setExecutor(new VoteTopRewardCommand());
    }

    void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents(new TagEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new EntityBossBar(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerSit(), this);
        this.getServer().getPluginManager().registerEvents(new ColoredChat(), this);
        this.getServer().getPluginManager().registerEvents(new PetManager(), this);
        this.getServer().getPluginManager().registerEvents(this,this);
        this.getServer().getPluginManager().registerEvents(new InventoryInstanceEventHandler(),this);
        this.getServer().getPluginManager().registerEvents(AfkManager.getINSTANCE(),this);
    }



    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        TPlayer tplayer = playerContainer.get(event.getEntity().getUniqueId());
        if(tplayer.getEquipment() == null)
        {
            Player player = (Player) event.getEntity();
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if (!player.isOnline() || !player.isGliding())
                    {
                        this.cancel();
                    }
                        ElytraTrails trail = tplayer.getActiveTrail();
                        if (trail != ElytraTrails.NONE)
                        {
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
                            builder.location(Util.Locations.backward(event.getEntity().getLocation(), 2));
                            builder.receivers(100);
                            if (trail == ElytraTrails.REDSTONE)
                                builder.color(Color.RED);
                            builder.spawn();
                        }
                    }
            }.runTaskTimer(TesseractLib.instance, 0, 2);
        }
    }

    @EventHandler void onFly(PlayerToggleFlightEvent event){
        TPlayer tplayer = playerContainer.get(event.getPlayer());
        if(tplayer.getEquipment() == null)
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    if(!event.getPlayer().isFlying())this.cancel();
                    FlyFilter selectedFilter = tplayer.getFlyFilter();
                    if(selectedFilter==FlyFilter.NONE)return;
                    Location loc = event.getPlayer().getLocation();
                    loc = loc.add(-Math.cos(loc.getYaw()),0,-Math.sin(loc.getYaw()));
                    Particle particle = selectedFilter.getParticle();
                    double extra = event.getPlayer().isSprinting() ? .5 : 0;
                    if (selectedFilter == FlyFilter.REDSTONE)
                        loc.getWorld()
                           .spawnParticle(particle, loc, 15, .5, .5, .5, .5, new Particle.DustOptions(Color.RED,
                                                                                                       1.2f));
                    else if (selectedFilter == FlyFilter.POTION || selectedFilter == FlyFilter.MUSICAL)
                        loc.getWorld().spawnParticle(particle, loc, 15, .5, .5, .5, 0.2);
                    else
                        loc.getWorld().spawnParticle(particle, loc, 15, .5, .5, .5, extra);
                }
            }.runTaskTimer(TesseractLib.instance, 0, 10);
        }
    }

    public static Logger logger()
    {
        return TesseractLib.instance.getLogger();
    }

    public static TPlayer getPlayer(final OfflinePlayer player)
    {
        return playerContainer.get(player);
    }

    public static TPlayer getPlayer(final UUID player)
    {
        return playerContainer.get(player);
    }

    public static PlayerContainer<? extends TPlayer, ? extends Equipment> getPlayerContainer()
    {
        return playerContainer;
    }
}
