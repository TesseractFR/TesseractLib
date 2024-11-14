package onl.tesseract.tesseractlib.player;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import onl.tesseract.lib.inventory.InventoryInstanceManager;
import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public abstract class TPlayer implements Listener {

    static public final String folderPath = "plugins/Tesseract/joueurs/joueurs/";
    protected OfflinePlayer player;
    protected String dateSinceLastConnection = null;
    protected String dateFirstConnection = new Date().toString();
    protected boolean playedToday = false;

    /**
     * Loads a player
     *
     * @param player OfflinePlayer to load
     */
    public TPlayer(final OfflinePlayer player) {
        this.player = player;
    }

    public static ItemStack[] loadInventory(ConfigurationSection yaml, String inv) {
        ItemStack[] list = new ItemStack[41];
        if (yaml.contains(inv)) {
            int i = 0;
            for (Object item : Objects.requireNonNull(yaml.getList(inv))) {
                ItemStack itemStack = (ItemStack) item;
                if (itemStack != null)
                    list[i] = itemStack;
                i++;
            }
        }
        return list;
    }

    protected void dailyConnection() {
        // ...
    }

    public void onJoin(OfflinePlayer player) {
        this.player = player;
        this.loadOnConnection();

        // First connection of the day
        if (!hasPlayedToday()) {
            this.dailyConnection();

        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (event.getPlayer().equals(getOfflinePlayer())) {
            HandlerList.unregisterAll(this);
            this.save();
        }
    }

    /**
     * Saves the player
     */
    public void save() {
        File file = new File(folderPath + getOfflinePlayer().getUniqueId() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set("name", getOfflinePlayer().getName());
        yaml.set("First_co", dateFirstConnection);
        yaml.set("hasPlayedToday", playedToday);
        if (getOfflinePlayer().isOnline()) {
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy");
            String date = sdf.format(new Date());
            yaml.set("dateSinceLastConnection", date);
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            TesseractLib.logger().log(Level.SEVERE, "Failed to save player file", e);
        }
    }

    /**
     * Loads player's information that need the player to be online. (equipment, permissions)
     */
    public void loadOnConnection() {
    }

    public UUID getUUID() {
        return getOfflinePlayer().getUniqueId();
    }

    public void load() {

        File file = new File(folderPath + getOfflinePlayer().getUniqueId() + ".yml");
        if (file.exists()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            this.dateSinceLastConnection = yaml.getString("dateSinceLastConnection");
            dateFirstConnection = Date.from(Instant.ofEpochMilli(getOfflinePlayer().getFirstPlayed())).toString();
            if (yaml.contains("hasPlayedToday"))
                playedToday = yaml.getBoolean("hasPlayedToday");
        }
    }

    /**
     * Checks if the player is online
     *
     * @return True if online
     */
    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }

    /**
     * Sends a message to the bukkit player if he is online.
     *
     * @param message Message to send
     */
    @Deprecated(forRemoval = true)
    public void sendMessage(String message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    @Deprecated(forRemoval = true)
    public void sendMessage(String[] message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    @Deprecated
    public void sendMessage(BaseComponent message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    @Deprecated
    public void sendMessage(BaseComponent... message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public void sendMessage(Component message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public void sendMessage(Component... message) {
        if (isOnline()) {
            for (var component : message)
                getBukkitPlayer().sendMessage(component);
        }
    }

    public String getDateFirstConnection() {
        return dateFirstConnection;
    }



    public void sendMessage(Component format, String message) {
        sendMessage(format.append(Component.text(message)));
    }

    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public Player getBukkitPlayer() {
        return player.getPlayer();
    }

    public String getDateSinceLastConnection() {
        return dateSinceLastConnection;
    }

    public boolean hasPlayedToday() {
        return playedToday;
    }

    public void setPlayedToday(boolean playedToday) {
        this.playedToday = playedToday;
    }

    public boolean isAdminMode() {
        return InventoryInstanceManager.getSelectedConfigName(getBukkitPlayer()).equals("admin");
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TPlayer))
            return false;
        return getOfflinePlayer().getUniqueId().equals(((TPlayer) other).getOfflinePlayer().getUniqueId());
    }
}
