package onl.tesseract.tesseractlib.player;

import onl.tesseract.tesseractlib.equipment.Equipment;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public interface PlayerContainer<E extends TPlayer, T extends Equipment> {

    E get(final OfflinePlayer player);

    E get(final UUID player);

    E newPlayer(final OfflinePlayer player) throws SQLException;

    boolean exists(final UUID player);

    Collection<E> getPlayers();

    boolean register(final E player);

    T loadEquipment(final UUID uuid);
}
