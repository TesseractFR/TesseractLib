package onl.tesseract.tesseractlib.player;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.chat.BaseComponent;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.cosmetics.*;
import onl.tesseract.tesseractlib.entity.Achievement;
import onl.tesseract.tesseractlib.entity.TPlayerInfo;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceManager;
import onl.tesseract.tesseractlib.service.TPlayerInfoService;
import onl.tesseract.tesseractlib.util.ChatFormats;
import org.bukkit.Bukkit;
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
    @Getter
    protected TPlayerInfo tPlayerInfo;
    @Getter
    @Setter
    protected TeleportationAnimation tp_animation = TeleportationAnimation.WATER;

    /**
     * Loads a player
     *
     * @param player OfflinePlayer to load
     */
    public TPlayer(final OfflinePlayer player) {
        this.player = player;
        this.tPlayerInfo = TPlayerInfoService.getInstance().get(player.getUniqueId());
    }

    public static TPlayer get(final OfflinePlayer player) {
        return TesseractLib.getPlayer(player);
    }

    public static TPlayer get(final UUID uuid) {
        return TesseractLib.getPlayer(uuid);
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

    public FlyFilter getFlyFilter() {
        return this.tPlayerInfo.getActive_fly_filter();
    }

    public void setFlyFilter(FlyFilter flyFilter) {
        this.tPlayerInfo.setActive_fly_filter(flyFilter);
    }

    public void buyCosmetic(Cosmetic cosmetic, int price) {
        CosmeticManager.giveCosmetic(getUUID(), cosmetic);
        addMarketCurrency(-price);
    }

    public void addMarketCurrency(int amount) {
        TPlayerInfoService.getInstance().addMarketCurrency(tPlayerInfo, amount);
        TesseractLib.logger().info(String.format("[Market Currency] %s earn %d lys d'or", player.getName(), amount));
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
        TPlayerInfoService.getInstance().save(this.tPlayerInfo);
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

    ////////////////////
    // Static methods //

    public Gender getGender() {
        return tPlayerInfo.getGenre();
    }

    public void setGender(Gender gender) {
        tPlayerInfo.setGenre(gender);
    }

    public boolean hasAchievement(Achievement achievement) {
        return tPlayerInfo.getAchievements().contains(achievement);
    }

    public void addAchievements(Achievement achievement) {
        addAchievements(achievement, true);
    }

    public void addAchievements(Achievement achievement, boolean foreveryone) {
        if (tPlayerInfo.getAchievements().contains(achievement))
            return;
        tPlayerInfo.getAchievements().add(achievement);
        sendMessage(ChatFormats.HAUT_FAIT.append(Component.text("Vous avez obtenu le haut-fait ")));
        sendMessage(Component.empty()
                .append(Component.text("      « ").color(NamedTextColor.AQUA))
                .append(Component.text(achievement.getDisplayName()).color(NamedTextColor.AQUA))
                .hoverEvent(HoverEvent.showText(Component.text(achievement.getCondition()).color(NamedTextColor.AQUA)))
                .append(Component.text(" » ").color(NamedTextColor.AQUA)));
        if (foreveryone) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(this.getBukkitPlayer()))
                    continue;
                p.sendMessage(ChatFormats.HAUT_FAIT
                        .append(Component.text(getOfflinePlayer().getName() + " a obtenu le haut-fait ")));
                p.sendMessage(Component.empty()
                        .append(Component.text("      « ").color(NamedTextColor.AQUA))
                        .append(Component.text(achievement.getDisplayName()).color(NamedTextColor.AQUA))
                        .hoverEvent(HoverEvent.showText(Component.text(achievement.getCondition()).color(NamedTextColor.AQUA)))
                        .append(Component.text(" » ").color(NamedTextColor.AQUA)));
            }
        }

    }

    public boolean hasAllAchievement(Iterable<Achievement> list) {
        for (Achievement a : list) {
            if (!hasAchievement(a))
                return false;
        }
        return true;
    }

    public void removeAchievement(Achievement achievement) {
        tPlayerInfo.getAchievements().remove(achievement);
    }

    public ElytraTrails getActiveTrail() {
        return tPlayerInfo.getActive_trail();
    }

    public void setActiveTrail(ElytraTrails elytraTrails) {
        tPlayerInfo.setActive_trail(elytraTrails);
    }

    public int getMarketCurrency() {
        TPlayerInfoService.getInstance().refresh(tPlayerInfo);
        return tPlayerInfo.getMarket_currency();
    }


    public int getShopPoint() {
        TPlayerInfoService.getInstance().refresh(tPlayerInfo);
        return tPlayerInfo.getShop_point();
    }

    public void addShopPoint(int amount) {
        TPlayerInfoService.getInstance().addShopPoint(tPlayerInfo, amount);
        TesseractLib.logger().info(String.format("[Market Currency] %s earn %d shop point", player.getName(), amount));
    }
}
