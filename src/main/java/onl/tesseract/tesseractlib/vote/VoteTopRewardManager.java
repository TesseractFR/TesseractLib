package onl.tesseract.tesseractlib.vote;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.bddfacade.VoteRepository;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.logging.Level;

public class VoteTopRewardManager {

    public static void giveReward(final int[] rewards)
    {
        if (rewards.length != 3)
            throw new IllegalArgumentException("Expected array of length 3");
        LinkedHashMap<UUID, Integer> top = VoteRepository.getTop(1);

        int index = 0;
        for (final UUID player : top.keySet())
        {
            if (index == 3)
                break;
            TesseractLib.logger().log(Level.INFO, "Giving top " + (index + 1) + " vote reward to " + player.toString());
            giveReward(player, rewards[index++]);
        }
    }

    private static void giveReward(final UUID uuid, final int reward)
    {
        TPlayer tPlayer = TPlayer.get(uuid);
        if (tPlayer != null)
        {
            tPlayer.addMarketCurrency(reward);
            if (tPlayer.isOnline())
                tPlayer.sendMessage(ChatFormats.VOTE, "Tu est au top du classement des votes ! Tu as reçu " + reward + " lys d'or !");
        }
        else
            TesseractLib.logger().log(Level.WARNING, "Could not find tplayer " + uuid.toString() + " to give vote reward");
    }
}
