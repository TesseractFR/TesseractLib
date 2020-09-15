package onl.tesseract.tesseractlib;

import org.bukkit.plugin.java.JavaPlugin;

public final class TesseractLib extends JavaPlugin {
    public static JavaPlugin instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
