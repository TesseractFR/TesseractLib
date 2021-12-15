package onl.tesseract.tesseractlib.player;

import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.UUID;

public interface PlayerContainer<E extends TPlayer> {

    E get(final OfflinePlayer player);

    E get(final UUID player);

    E newPlayer(final OfflinePlayer player);

    boolean exists(final UUID player);

    Collection<E> getPlayers();

    boolean register(final E player);
}
