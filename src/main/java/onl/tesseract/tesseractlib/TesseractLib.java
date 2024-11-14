package onl.tesseract.tesseractlib;

import onl.tesseract.lib.chat.tag.TagEventHandler;
import onl.tesseract.lib.command.Animation;
import onl.tesseract.lib.command.EquipmentCommand;
import onl.tesseract.lib.command.InventoryCommand;
import onl.tesseract.lib.inventory.InventoryInstanceEventHandler;
import onl.tesseract.lib.inventory.InventoryInstanceManager;
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

    static public int port;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        registerEvents();
        registerCommands();

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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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


        Objects.requireNonNull(instance.getCommand("inventory")).setExecutor(new InventoryCommand());
        Objects.requireNonNull(instance.getCommand("inventory")).setTabCompleter(new InventoryCommand());

    }

    void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents(new TagEventHandler(), this);

        this.getServer().getPluginManager().registerEvents(this,this);
        this.getServer().getPluginManager().registerEvents(new InventoryInstanceEventHandler(),this);
    }

    public static Logger logger()
    {
        return TesseractLib.instance.getLogger();
    }
}
