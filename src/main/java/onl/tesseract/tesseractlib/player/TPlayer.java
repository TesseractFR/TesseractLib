package onl.tesseract.tesseractlib.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.chat.BaseComponent;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.cosmetics.*;
import onl.tesseract.tesseractlib.entity.Achievement;
import onl.tesseract.tesseractlib.entity.TPlayerInfo;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceManager;
import onl.tesseract.tesseractlib.service.TPlayerInfoService;
import onl.tesseract.tesseractlib.util.ChatFormat;
import onl.tesseract.tesseractlib.util.ChatFormats;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class TPlayer implements Listener {

    static public final String folderPath = "plugins/Tesseract/joueurs/joueurs/";
    /**
     * Maps every Player who has played before with a TPlayer instance.
     */
    public String skinValue;
    public String skinSignature;
    protected OfflinePlayer player;
    protected Equipment equipment = null;
    /**
     * Function to call the next time this player chats. The message is passed as a parameter.
     */
    protected Consumer<String> chatEntryCallback;
    protected Consumer<Component> chatEntryComponentCallback;
    protected BukkitRunnable chatEntryRunnable;
    protected Consumer<String[]> commandEntryCallback;
    protected BukkitRunnable commandEntryRunnable;
    protected Map<UUID, Consumer<String[]>> commandEntryCallbacks = new HashMap<>();
    protected Map<UUID, BukkitRunnable> commandEntryRunnables = new HashMap<>();
    protected String dateSinceLastConnection = null;
    protected String dateFirstConnection = new Date().toString();
    protected boolean playedToday = false;
    protected PlayerProfile playerProfile;
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

    /**
     * Loads the player profile to store the skin texture.
     */
    public void loadPlayerProfile() {
        // Get the PlayerProfile in order to store the skin texture to avoid lag later.
        this.playerProfile = Bukkit.createProfile(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                playerProfile.complete();
                for (ProfileProperty profileProperty : playerProfile.getProperties()) {
                    if (profileProperty.getName().equals("textures")) {
                        skinValue = profileProperty.getValue();
                        skinSignature = profileProperty.getSignature();
                    }
                }
            }
        }.runTaskAsynchronously(TesseractLib.instance);
    }

    /**
     * Gets the player profile stored at server start by TPlayer#loadPlayerProfile
     *
     * @return the player profile
     */
    public PlayerProfile getPlayerProfile() {
        return playerProfile;
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
            HandlerList.unregisterAll(this.equipment);
            HandlerList.unregisterAll(this);
            this.save();
            this.equipment = null;
        }
    }

    /**
     * Saves the player
     */
    public void save() {
        TPlayerInfoService.getInstance().save(this.tPlayerInfo);
        if (equipment != null)
            this.equipment.save();
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
        if (getOfflinePlayer().isOnline()) {
            if (equipment != null)
                HandlerList.unregisterAll(this.equipment);
            this.equipment = TesseractLib.getPlayerContainer().loadEquipment(getUUID());
        }
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

    /**
     * Get the next player's input in the chat. Expires within 30 seconds.
     *
     * @param message  Message to prompt to the player.
     * @param function Callback. The player's message is given as parameter.
     */
    @Deprecated
    public void getChatEntry(String message, Consumer<String> function) {
        getChatEntry(message, 30, function);
    }

    @Deprecated
    public void getChatEntry(String message, int seconds, Consumer<String> function) {
        getChatEntry(ChatFormat.CHAT, message, seconds, function);
    }

    @Deprecated
    public void getChatEntry(String format, String message, Consumer<String> function) {
        getChatEntry(format, message, 30, function);
    }

    @Deprecated
    public void getChatEntry(String format, String message, int seconds, Consumer<String> function) {
        if (!player.isOnline())
            return;
        Objects.requireNonNull(player.getPlayer()).sendMessage(format + message);
        this.chatEntryCallback = function;
        this.chatEntryRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                chatEntryCallback = null;
            }
        };
        chatEntryRunnable.runTaskLater(TesseractLib.instance, 20L * seconds);
    }

    public void chatEntry(Component message, Consumer<Component> function) {
        chatEntry(message, 30, function);
    }

    public void chatEntry(String message, Consumer<Component> function) {
        chatEntry(Component.text(message), function);
    }

    public void chatEntry(Component format, String message, Consumer<Component> function) {
        chatEntry(format.append(Component.text(message)), 30, function);
    }

    public void chatEntry(Component message, int seconds, Consumer<Component> function) {
        if (!player.isOnline())
            return;
        Objects.requireNonNull(player.getPlayer()).sendMessage(ChatFormats.CHAT.append(message)
                .append(Component.text(" : ", NamedTextColor.GRAY)));
        this.chatEntryComponentCallback = function;
        this.chatEntryRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                chatEntryComponentCallback = null;
            }
        };
        chatEntryRunnable.runTaskLater(TesseractLib.instance, 20L * seconds);
    }

    @EventHandler
    @Deprecated
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId()))
            return;
        if (this.chatEntryCallback != null) {
            // Make a sync call
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (chatEntryCallback == null)
                        return;
                    chatEntryCallback.accept(event.getMessage());
                    chatEntryCallback = null;
                }
            }.runTask(TesseractLib.instance);
            chatEntryRunnable.cancel();
            event.setCancelled(true);
        }
    }

    public void sendMessage(Component format, String message) {
        sendMessage(format.append(Component.text(message)));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId()))
            return;
        if (this.chatEntryComponentCallback != null) {
            // Make a sync call
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (chatEntryComponentCallback == null)
                        return;
                    chatEntryComponentCallback.accept(event.message());
                    chatEntryComponentCallback = null;
                }
            }.runTask(TesseractLib.instance);
            chatEntryRunnable.cancel();
            event.setCancelled(true);
        }
    }

    /**
     * Gets the arguments of the next /command of this player.
     *
     * @param function Callback to call. Passes the args as parameter
     */
    public void getChatCommand(Consumer<String[]> function) {
        if (!player.isOnline())
            return;
        commandEntryCallback = function;
        commandEntryRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                commandEntryCallback = null;
            }
        };
        commandEntryRunnable.runTaskLater(TesseractLib.instance, 20 * 60 * 5);
    }

    @Nullable
    public ClickEvent clickCommand(Runnable callback) {
        if (!player.isOnline())
            return null;
        UUID uuid = UUID.randomUUID();
        commandEntryCallbacks.put(uuid, args -> callback.run());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                commandEntryCallbacks.remove(uuid);
                commandEntryRunnables.remove(uuid);
            }
        };
        runnable.runTaskLater(TesseractLib.instance, 20 * 60 * 3);
        commandEntryRunnables.put(uuid, runnable);
        return ClickEvent.runCommand("/commandCallback " + uuid);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId()))
            return;
        String[] parts = event.getMessage().split(" ");
        if (parts[0].equals("/command")) {
            event.setCancelled(true);
            if (commandEntryCallback == null || parts.length == 1)
                return;
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, parts.length - 1);
            commandEntryCallback.accept(args);
            commandEntryRunnable.cancel();
            if (!parts[1].equals("shopswapsign")) {
                commandEntryCallback = null;
            }
            event.setCancelled(true);
        } else if (parts[0].equals("/commandCallback")) {
            event.setCancelled(true);
            if (commandEntryCallbacks.isEmpty())
                return;
            try {
                UUID uuid = UUID.fromString(parts[1]);
                Consumer<String[]> consumer = commandEntryCallbacks.get(uuid);
                if (consumer != null) {
                    String[] args = new String[parts.length - 1];
                    System.arraycopy(parts, 1, args, 0, parts.length - 1);
                    consumer.accept(args);
                }
            } catch (IllegalArgumentException e) {
                return;
            }
        }
    }

    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public Player getBukkitPlayer() {
        return player.getPlayer();
    }

    public Equipment getEquipment() {
        return equipment;
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
