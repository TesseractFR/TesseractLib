package onl.tesseract.tesseractlib.player.def;

import onl.tesseract.tesseractlib.player.PlayerContainer;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StandalonePlayerContainer implements PlayerContainer<StandaloneTesseractPlayer> {

    private final Map<UUID, StandaloneTesseractPlayer> playerMap = new HashMap<>();

    @Override
    public StandaloneTesseractPlayer get(final OfflinePlayer player)
    {
        return playerMap.get(player.getUniqueId());
    }

    @Override
    public StandaloneTesseractPlayer get(final UUID player)
    {
        return playerMap.get(player);
    }

    @Override
    public StandaloneTesseractPlayer newPlayer(final OfflinePlayer player)
    {
        final StandaloneTesseractPlayer standaloneTesseractPlayer = new StandaloneTesseractPlayer(player);
        playerMap.put(player.getUniqueId(), standaloneTesseractPlayer);
        return standaloneTesseractPlayer;
    }

    @Override
    public boolean exists(final UUID player)
    {
        return playerMap.containsKey(player);
    }

    @Override
    public Collection<StandaloneTesseractPlayer> getPlayers()
    {
        return playerMap.values();
    }

    @Override
    public boolean register(final StandaloneTesseractPlayer player)
    {
        return playerMap.put(player.getUUID(), player) == null;
    }
}
