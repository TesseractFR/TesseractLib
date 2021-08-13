package onl.tesseract.tesseractlib;

import onl.tesseract.tesseractlib.achievement.Achievement;
import onl.tesseract.tesseractlib.achievement.Title;
import onl.tesseract.tesseractlib.bdd.BDDManager;
import onl.tesseract.tesseractlib.chat.tag.TagEventHandler;
import onl.tesseract.tesseractlib.command.*;
import onl.tesseract.tesseractlib.command.staff.CosmeticCommand;
import onl.tesseract.tesseractlib.command.staff.SocialSpy;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.event.ColoredChat;
import onl.tesseract.tesseractlib.event.EntityBossBar;
import onl.tesseract.tesseractlib.event.PlayerSit;
import onl.tesseract.tesseractlib.familier.PetManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class TesseractLib extends JavaPlugin {
    public static JavaPlugin instance;

    static public final String configFilepath = "plugins/Tesseract/config.yml";

    static private String host, database, username, password;
    private static BDDManager bddManager;
    static public int port;

    static public BDDManager getBddManager() {
        if(bddManager == null){
            loadConfig();
            bddManager= new BDDManager(host,port,username,password,database);
        }
        return bddManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        loadConfig();
        bddManager = new BDDManager(host,port,username,password,database);
        registerEvents();
        registerCommands();
        System.out.println("Loading title...");
        Title.loadAll();
        System.out.println("Loading achievement...");
        Achievement.loadAll();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        bddManager.close();

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        CosmeticManager.loadPlayer(event.getPlayer().getUniqueId());
    }

    void registerCommands()
    {
        Objects.requireNonNull(instance.getCommand("animation")).setExecutor(new Animation());
        Objects.requireNonNull(instance.getCommand("equipment")).setExecutor(new EquipmentCommand());
        Objects.requireNonNull(instance.getCommand("socialspy")).setExecutor(new SocialSpy());
        Objects.requireNonNull(instance.getCommand("msg")).setExecutor(new MsgCommand());
        Objects.requireNonNull(instance.getCommand("reply")).setExecutor(new ReplyToMsg());
        Objects.requireNonNull(instance.getCommand("familier")).setExecutor(new FamilierCommand());
        Objects.requireNonNull(instance.getCommand("comestic")).setExecutor(new CosmeticCommand());
    }

    void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents(new TagEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new EntityBossBar(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerSit(), this);
        this.getServer().getPluginManager().registerEvents(new ColoredChat(), this);
        this.getServer().getPluginManager().registerEvents(new PetManager(), this);
    }


    public static void loadConfig()
    {
        File file = new File(configFilepath);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        host = yaml.getString("db_host");
        database = yaml.getString("db_database");
        username = yaml.getString("db_username");
        password = yaml.getString("db_password");
        port = yaml.getInt("db_port");
    }


}
