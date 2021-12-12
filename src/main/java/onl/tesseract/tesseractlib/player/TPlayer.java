package onl.tesseract.tesseractlib.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.chat.BaseComponent;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.achievement.Achievement;
import onl.tesseract.tesseractlib.bddfacade.PlayerFacade;
import onl.tesseract.tesseractlib.cosmetics.Cosmetic;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceManager;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

public class TPlayer implements Listener {
    public enum Gender {
        MALE("Masculin"),
        FEMALE("Féminin"),
        OTHER("Non renseigné");
        private final String string;

        Gender(String string)
        {
            this.string = string;
        }

        public String getName()
        {
            return this.string;
        }
    }

    static public final String folderPath = "plugins/Tesseract/joueurs/joueurs/";
    /**
     * Maps every Player who has played before with a TPlayer instance.
     */
    static public final HashMap<UUID, TPlayer> playerMap = new HashMap<>();
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
    // Mailer mailer = new Mailer(this);
    protected BukkitRunnable commandEntryRunnable;
    protected String dateSinceLastConnection = null;
    protected String dateFirstConnection = new Date().toString();
    protected boolean playedToday = false;
    protected PlayerProfile playerProfile;
    protected Gender gender;
    protected PlayerFacade playerFacade;
    protected List<Achievement> achievements = new ArrayList<>();
    private UUID uuid;
    protected ElytraTrails trails = ElytraTrails.NONE;
    protected int marketCurrency = 0;

    public FlyFilter getFlyFilter()
    {
        return flyFilter;
    }

    public void setFlyFilter(FlyFilter flyFilter)
    {
        this.flyFilter = flyFilter;
        playerFacade.setFlyFilter(flyFilter);
    }

    protected FlyFilter flyFilter =  FlyFilter.NONE;

    /**
     * Loads a player
     *
     * @param player OfflinePlayer to load
     */
    public TPlayer(OfflinePlayer player)
    {
        this.player = player;
        checkFirstJoin(player.getUniqueId());
    }

    static public TPlayer get(Player player)
    {
        return TPlayer.playerMap.get(player.getUniqueId());
    }

    static public TPlayer get(UUID uuid)
    {
        return playerMap.get(uuid);
    }

    public void buyCosmetic(String type, Cosmetic cosmetic,int price)
    {
        CosmeticManager.giveCosmetic(getUUID(),type,cosmetic);
        addMarketCurrency(-price);
    }

    public void setMarketCurrency(int currency)
    {
        marketCurrency = currency;
        playerFacade.setMarketCurrency(marketCurrency);
    }

    public void addMarketCurrency(int amount){
        playerFacade.addMarketCurrency(amount);
        marketCurrency = playerFacade.getMarketCurrency();
    }

    /**
     * Loads the player profile to store the skin texture.
     */
    public void loadPlayerProfile()
    {
        // Get the PlayerProfile in order to store the skin texture to avoid lag later.
        this.playerProfile = Bukkit.createProfile(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run()
            {
                playerProfile.complete();
                for (ProfileProperty profileProperty : playerProfile.getProperties())
                {
                    if (profileProperty.getName().equals("textures"))
                    {
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
    public PlayerProfile getPlayerProfile()
    {
        return playerProfile;
    }

    protected void dailyConnection()
    {
        // ...
    }

    public void onJoin(OfflinePlayer player)
    {
        this.player = player;
        checkFirstJoin(player.getUniqueId());
        this.loadOnConnection();


        // First connection of the day
        if (!hasPlayedToday())
        {
            this.dailyConnection();

        }

    }

    protected void checkFirstJoin(UUID uniqueId)
    {
        if (!PlayerFacade.exist(uniqueId))
        {
            addtodatabase(uniqueId);
        }
    }

    private void addtodatabase(UUID uniqueId)
    {
        PlayerFacade.addtodatabase(uniqueId);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        if (event.getPlayer().equals(getOfflinePlayer()))
        {
            HandlerList.unregisterAll(this.equipment);
            HandlerList.unregisterAll(this);
            this.save();
            this.equipment = null;
        }
    }

    /**
     * Saves the player
     */
    public void save()
    {
        if (equipment != null)
            this.equipment.save();
        File file = new File(folderPath + getOfflinePlayer().getUniqueId() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set("name", getOfflinePlayer().getName());
        yaml.set("First_co", dateFirstConnection);
        yaml.set("hasPlayedToday", playedToday);
        if (getOfflinePlayer().isOnline())
        {
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy");
            String date = sdf.format(new Date());
            yaml.set("dateSinceLastConnection", date);
        }

        try
        {
            yaml.save(file);
        }
        catch (IOException e)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to save player file", e);
        }
    }

    /**
     * Loading done only once when the server starts
     */
    public void loadOnServerStarts()
    {

    }

    /**
     * Loads player's information that need the player to be online. (equipment, permissions)
     */
    public void loadOnConnection()
    {
        if (getOfflinePlayer().isOnline())
        {
            this.equipment = Equipment.load(this);
        }
    }

        public UUID getUUID()
    {
        return getOfflinePlayer().getUniqueId();
    }

    public static ItemStack[] loadInventory(ConfigurationSection yaml, String inv)
    {
        ItemStack[] list = new ItemStack[41];
        if (yaml.contains(inv))
        {
            int i = 0;
            for (Object item : Objects.requireNonNull(yaml.getList(inv)))
            {
                ItemStack itemStack = (ItemStack) item;
                if (itemStack != null)
                    list[i] = itemStack;
                i++;
            }
        }
        return list;
    }

    /**
     * Loads player's general information that does not need the player to be online
     */
    public void load()
    {
        playerFacade = new PlayerFacade(getOfflinePlayer().getUniqueId());
        gender = playerFacade.getGender();
        trails = playerFacade.getActiveTrails();
        marketCurrency = playerFacade.getMarketCurrency();
        flyFilter = playerFacade.getFlyFilter();
        achievements.clear();
        achievements = playerFacade.getAllAchievements();
        File file = new File(folderPath + getOfflinePlayer().getUniqueId() + ".yml");
        if (file.exists())
        {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            this.dateSinceLastConnection = yaml.getString("dateSinceLastConnection");
            dateFirstConnection = Date.from(Instant.ofEpochMilli(getOfflinePlayer().getFirstPlayed())).toString();
            if (yaml.contains("hasPlayedToday"))
                playedToday = yaml.getBoolean("hasPlayedToday");
        }
    }

    public void load(ResultSet resultSet)
    {
        playerFacade = new PlayerFacade(getOfflinePlayer().getUniqueId());
        try
        {
            gender = Gender.valueOf(resultSet.getString("genre"));
            trails = ElytraTrails.valueOf(resultSet.getString("active_trail"));
            marketCurrency = resultSet.getInt("market_currency");
            flyFilter = FlyFilter.valueOf(resultSet.getString("active_fly_filter"));
        }
        catch (SQLException throwables)
        {
            gender = Gender.OTHER;
            marketCurrency = 0;
        }
        achievements.clear();
        achievements = playerFacade.getAllAchievements();
        File file = new File(folderPath + getOfflinePlayer().getUniqueId() + ".yml");
        if (file.exists())
        {
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
    public boolean isOnline()
    {
        return getOfflinePlayer().isOnline();
    }

    /**
     * Sends a message to the bukkit player if he is online.
     *
     * @param message Message to send
     */
    public void sendMessage(String message)
    {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public void sendMessage(String[] message)
    {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    @Deprecated
    public void sendMessage(BaseComponent message)
    {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    @Deprecated
    public void sendMessage(BaseComponent... message)
    {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public void sendMessage(Component message)
    {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public void sendMessage(Component... message)
    {
        if (isOnline())
        {
            for (var component : message)
                getBukkitPlayer().sendMessage(component);
        }
    }

    public String getDateFirstConnection()
    {
        return dateFirstConnection;
    }

    /**
     * Get the next player's input in the chat. Expires within 30 seconds.
     *
     * @param message Message to prompt to the player.
     * @param function Callback. The player's message is given as parameter.
     */
    @Deprecated
    public void getChatEntry(String message, Consumer<String> function)
    {
        getChatEntry(message, 30, function);
    }

    @Deprecated
    public void getChatEntry(String message, int seconds, Consumer<String> function)
    {
        getChatEntry(ChatFormat.CHAT, message, seconds, function);
    }

    @Deprecated
    public void getChatEntry(String format, String message, Consumer<String> function)
    {
        getChatEntry(format, message, 30, function);
    }

    @Deprecated
    public void getChatEntry(String format, String message, int seconds, Consumer<String> function)
    {
        if (!player.isOnline())
            return;
        Objects.requireNonNull(player.getPlayer()).sendMessage(format + message);
        this.chatEntryCallback = function;
        this.chatEntryRunnable = new BukkitRunnable() {
            @Override
            public void run()
            {
                chatEntryCallback = null;
            }
        };
        chatEntryRunnable.runTaskLater(TesseractLib.instance, 20L * seconds);
    }

    public void chatEntry(Component message, Consumer<Component> function)
    {
        chatEntry(message, 30, function);
    }

    public void chatEntry(Component message, int seconds, Consumer<Component> function)
    {
        if (!player.isOnline())
            return;
        Objects.requireNonNull(player.getPlayer()).sendMessage(ChatFormats.CHAT.append(message)
                                                                               .append(Component.text(" : ", NamedTextColor.GRAY)));
        this.chatEntryComponentCallback = function;
        this.chatEntryRunnable = new BukkitRunnable() {
            @Override
            public void run()
            {
                chatEntryComponentCallback = null;
            }
        };
        chatEntryRunnable.runTaskLater(TesseractLib.instance, 20L * seconds);
    }

    @EventHandler
    @Deprecated
    public void onChat(AsyncPlayerChatEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId()))
            return;
        if (this.chatEntryCallback != null)
        {
            // Make a sync call
            new BukkitRunnable() {
                @Override
                public void run()
                {
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

    public void sendMessage(Component format, String message)
    {
        sendMessage(format.append(Component.text(message)));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId()))
            return;
        if (this.chatEntryComponentCallback != null)
        {
            // Make a sync call
            new BukkitRunnable() {
                @Override
                public void run()
                {
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
    public void getChatCommand(Consumer<String[]> function)
    {
        if (!player.isOnline())
            return;
        commandEntryCallback = function;
        commandEntryRunnable = new BukkitRunnable() {
            @Override
            public void run()
            {
                commandEntryCallback = null;
            }
        };
        commandEntryRunnable.runTaskLater(TesseractLib.instance, 20 * 60 * 5);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if (!event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId()))
            return;
        String[] parts = event.getMessage().split(" ");
        if (commandEntryCallback != null && parts[0].equals("/command") && parts.length > 1)
        {
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, parts.length - 1);
            commandEntryCallback.accept(args);
            commandEntryRunnable.cancel();
            if (!parts[1].equals("shopswapsign"))
            {
                commandEntryCallback = null;
            }
            event.setCancelled(true);
        }
        if (parts[0].equals("/command"))
            event.setCancelled(true);
    }

    public OfflinePlayer getOfflinePlayer()
    {
        return player;
    }

    public Player getBukkitPlayer()
    {
        return player.getPlayer();
    }

    public Equipment getEquipment()
    {
        return equipment;
    }

    public String getDateSinceLastConnection()
    {
        return dateSinceLastConnection;
    }

    public boolean hasPlayedToday()
    {
        return playedToday;
    }

    public void setPlayedToday(boolean playedToday)
    {
        this.playedToday = playedToday;
    }

    public boolean isAdminMode()
    {
        return InventoryInstanceManager.getSelectedConfigName(getBukkitPlayer()).equals("admin");
    }

    ////////////////////
    // Static methods //

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof TPlayer))
            return false;
        return getOfflinePlayer().getUniqueId().equals(((TPlayer) other).getOfflinePlayer().getUniqueId());
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
        playerFacade.setGender(gender);
    }

    public boolean hasAchievement(Achievement achievement)
    {
        return achievements.contains(achievement);
    }

    public void addAchievements(Achievement achievement)
    {
        addAchievements(achievement, true);
    }

    public void addAchievements(Achievement achievement, boolean foreveryone)
    {
        if (achievements.contains(achievement))
            return;
        achievements.add(achievement);
        playerFacade.addAchievements(achievement);
        sendMessage(ChatFormats.HAUT_FAIT.append(Component.text("Vous avez obtenu le haut-fait ")));
        sendMessage(Component.empty()
                             .append(Component.text("      « ").color(NamedTextColor.AQUA))
                             .append(Component.text(achievement.getDisplayName()).color(NamedTextColor.AQUA))
                             .hoverEvent(HoverEvent.showText(Component.text(achievement.getCondition()).color(NamedTextColor.AQUA)))
                             .append(Component.text(" » ").color(NamedTextColor.AQUA)));
        if (foreveryone)
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
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

    public boolean hasAllAchievement(Iterable<Achievement> list)
    {
        for (Achievement a : list)
        {
            if (!hasAchievement(a))
                return false;
        }
        return true;
    }

    public void removeAchievement(Achievement achievement)
    {
        achievements.remove(achievement);
        playerFacade.removeAchievement(achievement);
    }

    public ElytraTrails getActiveTrail()
    {
        return trails;
    }

    public void setActiveTrail(ElytraTrails elytraTrails){
        trails = elytraTrails;
        playerFacade.setActiveTrails(trails);
    }

    public int getMarketCurrency()
    {
        return marketCurrency;
    }
}
