package onl.tesseract.tesseractlib.player.def;

import onl.tesseract.tesseractlib.bddfacade.PlayerRepository;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class StandaloneTesseractPlayer extends TPlayer {
    public StandaloneTesseractPlayer(final OfflinePlayer player, final PlayerRepository repository)
    {
        super(player, repository);
    }

    public StandaloneTesseractPlayer(final OfflinePlayer player)
    {
        super(player);
    }

    @Override
    protected PlayerRepository newRepository(final UUID uuid)
    {
        return new PlayerRepository(uuid);
    }
}
