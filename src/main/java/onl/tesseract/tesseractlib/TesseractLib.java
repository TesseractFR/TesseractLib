package onl.tesseract.tesseractlib;

import onl.tesseract.tesseractlib.command.Animation;
import onl.tesseract.tesseractlib.command.EquipmentCommand;
import onl.tesseract.tesseractlib.command.MsgCommand;
import onl.tesseract.tesseractlib.command.ReplyToMsg;
import onl.tesseract.tesseractlib.command.staff.SocialSpy;
import onl.tesseract.tesseractlib.equipment.invocable.Elytra;
import onl.tesseract.tesseractlib.event.ChatDing;
import onl.tesseract.tesseractlib.event.ColoredChat;
import onl.tesseract.tesseractlib.event.EntityBossBar;
import onl.tesseract.tesseractlib.event.PlayerSit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public final class TesseractLib extends JavaPlugin {
    public static JavaPlugin instance;

    static public final String configFilepath = "plugins/Tesseract/config.yml";

    static public String host, database, username, password;
    static public int port;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        registerEvents();
        registerCommands();

        loadConfig();
        connect();

        try {
            Elytra.Trail.registerTrails();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

    void registerCommands()
    {
        Objects.requireNonNull(instance.getCommand("animation")).setExecutor(new Animation());
        Objects.requireNonNull(instance.getCommand("equipment")).setExecutor(new EquipmentCommand());
        Objects.requireNonNull(instance.getCommand("socialspy")).setExecutor(new SocialSpy());
        Objects.requireNonNull(instance.getCommand("msg")).setExecutor(new MsgCommand());
        Objects.requireNonNull(instance.getCommand("reply")).setExecutor(new ReplyToMsg());
    }

    void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents(new ChatDing(), this);
        this.getServer().getPluginManager().registerEvents(new EntityBossBar(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerSit(), this);
        this.getServer().getPluginManager().registerEvents(new ColoredChat(), this);
    }


    public static Connection getConnection() {
        return connection;
    }

    static Connection connection;

    public void loadConfig()
    {
        File file = new File(configFilepath);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        host = yaml.getString("db.host", "localhost");
        database = yaml.getString("db.database", "tesseract");
        username = yaml.getString("db.username", "admin");
        password = yaml.getString("db.password", "admin");
        port = yaml.getInt("db.port", 3306);
    }

    public void connect()
    {
        try {
            if (connection != null && ! connection.isClosed())
                return;

            synchronized (this)
            {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:sql://" + host + ":" + port + "/" + database;
                connection = DriverManager.getConnection(url, username, password);
                System.out.println(connection != null);
            }


        } catch (SQLException | ClassNotFoundException throwables) {
            TesseractLib.instance.getLogger().log(Level.SEVERE, "Could not connect to Tesseract database!");
            throwables.printStackTrace();
        }
    }
}
