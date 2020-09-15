package onl.tesseract.tesseractlib.util;

import org.bukkit.ChatColor;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.DARK_AQUA;

public class ChatFormat {
    static final public String EQUIPMENT = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Équipement" + ChatColor.DARK_GRAY +
            "]" + ChatColor.GRAY + " ";
    static final public String EQUIPMENT_ERROR = EQUIPMENT + ChatColor.RED;
    static final public String EQUIPMENT_SUCCESS = EQUIPMENT + ChatColor.GREEN;

    static final public String JETPACK = ChatColor.GOLD + "[" + ChatColor.YELLOW + "Jetpack" + ChatColor.GOLD + "]" + ChatColor.DARK_GRAY + " : " + ChatColor.DARK_AQUA;
    static final public String JETPACK_ERROR = ChatColor.GOLD + "[" + ChatColor.YELLOW + "Jetpack" + ChatColor.GOLD + "]" + ChatColor.DARK_GRAY + " : " + ChatColor.RED;

    static final public String HAUT_FAIT = ChatColor.GOLD + "["+ ChatColor.YELLOW + "Haut-Fait" + ChatColor.GOLD+ "] : " + ChatColor.RESET + ChatColor.GRAY;

    static final public String CHAT = ChatColor.DARK_AQUA + "[Chat] : " + ChatColor.GRAY;
    static final public String CHAT_ERROR = ChatColor.DARK_AQUA + "[Chat] : " + ChatColor.RED;

    public static final String GROUP = DARK_AQUA + "[" + BLUE + "Groupe" + DARK_AQUA + "] " + GRAY;
    public static final String GROUP_ERROR = DARK_AQUA + "[" + BLUE + "Groupe" + DARK_AQUA + "] " + RED;
    public static final String GROUP_SUCCESS = DARK_AQUA + "[" + BLUE + "Groupe" + DARK_AQUA + "] " + GREEN;
    public static final String CHAT_GROUP = DARK_AQUA + "[" + BLUE + "Chat Groupe" + DARK_AQUA + "] ";
}
