package onl.tesseract.tesseractlib;

import onl.tesseract.tesseractlib.command.Animation;
import onl.tesseract.tesseractlib.command.EquipmentCommand;
import onl.tesseract.tesseractlib.command.MsgCommand;
import onl.tesseract.tesseractlib.command.ReplyToMsg;
import onl.tesseract.tesseractlib.command.staff.SocialSpy;
import onl.tesseract.tesseractlib.event.ChatDing;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TesseractLib extends JavaPlugin {
    public static JavaPlugin instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        registerEvents();
        registerCommands();
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
    }
}
