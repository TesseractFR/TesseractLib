package onl.tesseract.tesseractlib;

import onl.tesseract.lib.chat.tag.TagEventHandler;
import onl.tesseract.lib.inventory.InventoryInstanceEventHandler;
import onl.tesseract.lib.inventory.InventoryInstanceManager;
import onl.tesseract.tesseractlib.bdd.BDDManager;
import onl.tesseract.tesseractlib.command.*;
import onl.tesseract.tesseractlib.command.staff.*;
import onl.tesseract.tesseractlib.cosmetics.familier.PetManager;
import onl.tesseract.tesseractlib.event.ColoredChat;
import onl.tesseract.tesseractlib.event.EntityBossBar;
import onl.tesseract.tesseractlib.event.PlayerSit;
import onl.tesseract.tesseractlib.placeholder.TesseractPlaceHolder;
import onl.tesseract.tesseractlib.vote.VoteManager;
import onl.tesseract.tesseractlib.vote.goal.VoteGoalManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
        bddManager= new BDDManager(config.getDb_host(),config.getDb_port(),config.getDb_username(),config.getDb_password(),config.getDb_database());
        registerEvents();
        registerCommands();
        new TesseractPlaceHolder(this).register();

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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        bddManager.close();
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreJoin(AsyncPlayerPreLoginEvent event)
    {
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
    }

    public static Logger logger()
    {
        return TesseractLib.instance.getLogger();
    }
}
