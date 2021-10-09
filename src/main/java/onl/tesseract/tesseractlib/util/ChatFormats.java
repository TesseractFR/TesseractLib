package onl.tesseract.tesseractlib.util;

import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class ChatFormats {
    public static final Component EQUIPMENT = empty()
            .color(GRAY)
            .append(text("[").color(DARK_GRAY))
            .append(text("Équipement").color(GOLD))
            .append(text("] ").color(DARK_GRAY));
    public static final Component EQUIPMENT_ERROR = EQUIPMENT.color(RED);
    public static final Component EQUIPMENT_SUCCESS = EQUIPMENT.color(GREEN);

    public static final Component JETPACK = empty()
            .color(GRAY)
            .append(text("[").color(GOLD))
            .append(text("Équipement").color(YELLOW))
            .append(text("] ").color(GOLD));
    public static final Component JETPACK_ERROR = EQUIPMENT.color(RED);

    public static final Component HAUT_FAIT = empty()
            .color(GRAY)
            .append(text("[").color(DARK_GRAY))
            .append(text("Haut-Fait").color(GOLD))
            .append(text("] ").color(DARK_GRAY));

    public static final Component CHAT = empty()
            .color(GRAY)
            .append(text("[Chat] : ").color(DARK_AQUA));
    public static final Component CHAT_ERROR = CHAT.color(RED);

    public static final Component GROUP = empty()
            .color(GRAY)
            .append(text("[").color(DARK_AQUA))
            .append(text("Groupe").color(BLUE))
            .append(text("] ").color(DARK_AQUA));
    public static final Component GROUP_ERROR = GROUP.color(RED);
    public static final Component GROUP_SUCCESS = GROUP.color(GREEN);

    public static final Component CHAT_GROUP = empty()
            .color(GRAY)
            .append(text("[").color(DARK_AQUA))
            .append(text("Chat Groupe").color(BLUE))
            .append(text("] ").color(DARK_AQUA));

    public static final Component PET = empty()
            .color(YELLOW)
            .append(text("[").color(GOLD))
            .append(text("Familier").color(YELLOW))
            .append(text("] ").color(GOLD));

    public static final Component PET_SUCCESS = PET.color(GREEN);
    public static final Component PET_ERROR = PET.color(RED);

    public static final Component COSMETICS = empty()
            .color(YELLOW)
            .append(text("[").color(GOLD))
            .append(text("Cosmetique").color(YELLOW))
            .append(text("] ").color(GOLD));
    public static final Component COSMETICS_SUCCESS = COSMETICS.color(GREEN);
    public static final Component COSMETICS_ERROR = COSMETICS.color(RED);

    public static final Component VOTE = Component.text("", YELLOW)
                                                  .append(Component.text("[", RED, BOLD))
                                                  .append(Component.text("Vote", GOLD))
                                                  .append(Component.text("] ", RED, BOLD));
}
