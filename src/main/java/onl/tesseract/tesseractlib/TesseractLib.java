package onl.tesseract.tesseractlib;

import onl.tesseract.lib.inventory.InventoryInstanceManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TesseractLib extends JavaPlugin implements Listener {
    public static JavaPlugin instance;

    static public int port;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

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

    public static Logger logger()
    {
        return TesseractLib.instance.getLogger();
    }
}
