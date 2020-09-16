package onl.tesseract.tesseractlib.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.md_5.bungee.api.chat.BaseComponent;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.util.ChatFormat;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TPlayer implements Listener {
    static public final String folderPath = "plugins/Tesseract/joueurs/joueurs/";
    /**
     * Maps every Player who has played before with a TPlayer instance.
     */
    static public HashMap<UUID, TPlayer> playerMap = new HashMap<>();


    public enum Gender{
        MALE("Masculin"),FEMALE("Féminin"),OTHER("Non renseigné");
        private final String string ;
        Gender(String string) {
            this.string = string ;
        }
        public String getName() {
            return  this.string ;
        }
    }

    protected OfflinePlayer player;
    protected Equipment equipment = null;
    protected Gender gender = Gender.OTHER;

    /**
     * Function to call the next time this player chats. The message is passed as a parameter.
     */
    protected  Consumer<String> chatEntryCallback;
    protected  BukkitRunnable chatEntryRunnable;
    protected  Consumer<String[]> commandEntryCallback;
    protected  BukkitRunnable commandEntryRunnable;

    protected String dateSinceLastConnection = null;
    protected String dateFirstConnection = new Date().toString();
    public Date lastUpdateHF = null;
    // Mailer mailer = new Mailer(this);

    protected boolean playedToday = false;

    protected boolean adminMode = false;
    protected Inventory adminInventory = Bukkit.createInventory(null, InventoryType.PLAYER);
    protected Inventory playerInventory = Bukkit.createInventory(null, InventoryType.PLAYER);

    public String skinValue;
    public String skinSignature;
    protected PlayerProfile playerProfile;

    /**
     * Loads a player
     * @param player OfflinePlayer to load
     */
    public TPlayer(OfflinePlayer player) {
        this.player = player;
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
            public void run() {
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
     * @return the player profile
     */
    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    protected void dailyConnection()
    {
        // ...
    }

    public void onJoin(OfflinePlayer player) {
        this.player = player;

        Bukkit.getServer().getPluginManager().registerEvents(this, TesseractLib.instance);
        this.loadOnConnection();
        //Tesseract.permissions.playerAddGroup(getOfflinePlayer().getPlayer(), rank.getPermGroup());

        /*
        // Check mails
        if (mailer.hasUnreadMail())
            sendMessage(new ComponentBuilder(ChatFormat.MAIL + "Vous avez des messages non lus. Cliquez pour les consulter.")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mail read")).create());

         */

        // First connection of the day
        if (! hasPlayedToday())
            setPlayedToday(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (event.getPlayer().equals(getOfflinePlayer()))
        {
            this.save();
            HandlerList.unregisterAll(this.equipment);
            this.equipment = null;
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * Saves the player
     */
    public void save() {
        if (equipment != null)
            this.equipment.save();

        File file = new File(folderPath + getOfflinePlayer().getUniqueId().toString() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set("name", getOfflinePlayer().getName());
        yaml.set("First_co",dateFirstConnection);
        yaml.set("gender",gender.toString());
        yaml.set("hasPlayedToday", playedToday);
        if (getOfflinePlayer().isOnline()) {
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy");
            String date = sdf.format(new Date());
            yaml.set("dateSinceLastConnection", date);
        }
        /*
        List<String> achievlist = new ArrayList<>();
        for(Achievement a : achievements){
            achievlist.add(a.toString());
        }
        yaml.set("Achievement",achievlist);

         */
        //this.mailer.save(yaml);

        // Save the second inventory if admin
        if (player.isOnline())
        {
            yaml.set("playerInventory", playerInventory.getContents());

            yaml.set("adminmode", adminMode);
            if (!adminMode)
                yaml.set("adminInventory", adminInventory.getContents());
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loading done only once when the server starts
     */
    protected void loadOnServerStarts()
    {

    }

    /**
     * Loads player's information that need the player to be online. (equipment, permissions)
     */
    public void loadOnConnection()
    {
        if (getOfflinePlayer().isOnline()){
            this.equipment = Equipment.load(this);
            //Tesseract.permissions.playerAddGroup(getOfflinePlayer().getPlayer(), rank.getPermGroup());

            // Load secondary staff inventory
            File file = new File(folderPath + getOfflinePlayer().getUniqueId().toString() + ".yml");
            if (file.exists())
            {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                adminMode = yaml.getBoolean("adminmode");
                player.getPlayer().setGameMode(adminMode ? GameMode.CREATIVE : GameMode.SURVIVAL);

                getPlayerInventory().setContents(loadInventory(yaml, "playerInventory"));
                getAdminInventory().setContents(loadInventory(yaml, "adminInventory"));
            }
        }
    }

    protected ItemStack[] loadInventory(YamlConfiguration yaml, String inv)
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
    public void load() {
        File file = new File(folderPath + getOfflinePlayer().getUniqueId().toString() + ".yml");
        if (file.exists()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            this.dateSinceLastConnection = yaml.getString("dateSinceLastConnection");
            dateFirstConnection = yaml.getString("First_co");
            if (yaml.contains("hasPlayedToday"))
                playedToday = yaml.getBoolean("hasPlayedToday");
            if (yaml.contains("gender"))
                gender = Gender.valueOf(yaml.getString("gender"));
        }
    }

    /**
     * Checks if the player is online
     * @return True if online
     */
    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }

    /**
     * Sends a message to the bukkit player if he is online.
     * @param message Message to send
     */
    public void sendMessage(String message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public void sendMessage(String[] message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public void sendMessage(BaseComponent message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public void sendMessage(BaseComponent... message) {
        if (isOnline())
            getBukkitPlayer().sendMessage(message);
    }

    public String getDateFirstConnection(){
        return dateFirstConnection;
    }

    /**
     * Get the next player's input in the chat. Expires within 30 seconds.
     * @param message Message to prompt to the player.
     * @param function Callback. The player's message is given as parameter.
     */
    public void getChatEntry(String message, Consumer<String> function) {
        getChatEntry(message, 30, function);
    }

    public void getChatEntry(String message, int seconds, Consumer<String> function) {
        getChatEntry(ChatFormat.CHAT, message, seconds, function);
    }

    public void getChatEntry(String format, String message, Consumer<String> function) {
        getChatEntry(format, message, 30, function);
    }

    public void getChatEntry(String format, String message, int seconds, Consumer<String> function) {
        if (! player.isOnline()) return;
        player.getPlayer().sendMessage(format + message);
        this.chatEntryCallback = function;
        this.chatEntryRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                chatEntryCallback = null;
            }
        };
        chatEntryRunnable.runTaskLater(TesseractLib.instance, 20 * seconds);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (! event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId()))
            return;
        if (this.chatEntryCallback != null) {
            // Make a sync call
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    chatEntryCallback.accept(event.getMessage());
                    chatEntryCallback = null;
                }
            }.runTask(TesseractLib.instance);
            chatEntryRunnable.cancel();
            event.setCancelled(true);
        }
    }

    /**
     * Gets the arguments of the next /command of this player.
     * @param function Callback to call. Passes the args as parameter
     */
    public void getChatCommand(Consumer<String[]> function) {
        if (! player.isOnline()) return;
        commandEntryCallback = function;
        commandEntryRunnable = new BukkitRunnable() {
            @Override
            public void run()
            {
                commandEntryCallback = null;
            }
        };
        commandEntryRunnable.runTaskLater(TesseractLib.instance, 20*60*5);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (! event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId())) return;
        String[] parts = event.getMessage().split(" ");
        if (commandEntryCallback != null && parts[0].equals("/command") && parts.length > 1) {
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, parts.length - 1);
            commandEntryCallback.accept(args);
            commandEntryRunnable.cancel();
            if(!parts[1].equals("shopswapsign")){
                commandEntryCallback = null;
            }
            event.setCancelled(true);
        }
        if (parts[0].equals("/command"))
            event.setCancelled(true);

        /*
        if (!parts[0].equals("/mail") && mailer.hasUnreadMail())
            sendMessage(new ComponentBuilder(ChatFormat.MAIL + "Vous avez des messages non lus. Cliquez pour les consulter.")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mail read")).create());

         */
    }

    public OfflinePlayer getOfflinePlayer() {
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
        if (playedToday)
            this.dailyConnection();
    }

    public boolean isAdminMode()
    {
        return adminMode;
    }

    public void setAdminMode(boolean adminMode)
    {
        this.adminMode = adminMode;
    }

    public Inventory getAdminInventory()
    {
        return adminInventory;
    }

    public void setAdminInventory(PlayerInventory adminInventory)
    {
        this.adminInventory = adminInventory;
    }

    public Inventory getPlayerInventory()
    {
        return playerInventory;
    }

    public void setPlayerInventory(PlayerInventory playerInventory)
    {
        this.playerInventory = playerInventory;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TPlayer)) return false;
        return getOfflinePlayer().getUniqueId().equals(((TPlayer) other).getOfflinePlayer().getUniqueId());
    }

    ////////////////////
    // Static methods //

    static public TPlayer get(Player player) {
        return TPlayer.playerMap.get(player.getUniqueId());
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    public Gender getGender()
    {
        if (gender == null) setGender(Gender.OTHER);
        return gender;
    }
}
