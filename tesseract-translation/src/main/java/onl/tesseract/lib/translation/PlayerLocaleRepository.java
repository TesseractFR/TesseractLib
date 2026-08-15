package onl.tesseract.lib.translation;

import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

public interface PlayerLocaleRepository {
    Locale getLocale(UUID player);

    default Locale getLocale(Player player) {
        return getLocale(player.getUniqueId());
    }

    void setLocale(UUID player, Locale locale);

    default void setLocale(Player player, Locale locale) {
        setLocale(player.getUniqueId(), locale);
    }
}
