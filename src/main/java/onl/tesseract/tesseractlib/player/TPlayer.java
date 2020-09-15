package onl.tesseract.tesseractlib.player;

import net.md_5.bungee.api.chat.BaseComponent;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.event.ChatDing;
import onl.tesseract.tesseractlib.util.ChatFormat;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TPlayer implements Listener {
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

    OfflinePlayer player;
    Equipment equipment = null;
    Group group;
    Gender gender = Gender.OTHER;

    /**
     * Function to call the next time this player chats. The message is passed as a parameter.
     */
    private Consumer<String> chatEntryCallback;
    private BukkitRunnable chatEntryRunnable;


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
        if (!event.getPlayer().getUniqueId().equals(getOfflinePlayer().getUniqueId()))
            return;
        if (this.chatEntryCallback != null) {
            // Make a sync call
            new BukkitRunnable() {
                @Override
                public void run() {
                    chatEntryCallback.accept(event.getMessage());
                    chatEntryCallback = null;
                }
            }.runTask(TesseractLib.instance);
            chatEntryRunnable.cancel();
            event.setCancelled(true);
        }
        // Group chat
        else if (event.getMessage().charAt(0) == '!' && get(event.getPlayer()).hasGroup()) {
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().performCommand("gc " + event.getMessage().replaceFirst("^!", ""));
                    ChatDing.ding(event.getMessage(), List.copyOf(get(event.getPlayer()).getGroup().getMembers()));
                }
            }.runTask(TesseractLib.instance);
        }
        /*
        // Chat
        else {
            event.setFormat(ChatFormat.getChatPrefix(TPlayer.get(event.getPlayer())));
        }

         */
    }

    static public TPlayer get(Player player) {
        return TPlayer.playerMap.get(player.getUniqueId());
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

    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public Player getBukkitPlayer()
    {
        return player.getPlayer();
    }

    public Group getGroup(){ return  group;}
    public boolean hasGroup() { return group != null; }
    public void setGroup(Group gr){this.group = gr;}

    public Equipment getEquipment()
    {
        return equipment;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TPlayer)) return false;
        return getOfflinePlayer().getUniqueId().equals(((TPlayer) other).getOfflinePlayer().getUniqueId());
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
