package onl.tesseract.tesseractlib.bddfacade;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.vote.Vote;
import onl.tesseract.tesseractlib.vote.VoteManager;
import onl.tesseract.tesseractlib.vote.VoteSite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

public class VoteRepository {
    public static Collection<VoteSite> getVoteSites()
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM t_vote_site"
            );
            final Collection<VoteSite> voteSites = new HashSet<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                voteSites.add(new VoteSite(
                        resultSet.getString("service_name"),
                        resultSet.getString("address"),
                        Duration.ofMinutes(resultSet.getInt("delay_minutes"))
                ));
            }
            return voteSites;
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to retrieve vote sites", throwables);
        }
        return Collections.emptyList();
    }

    public static Optional<Vote> getLastVote(final UUID playerUUID, final String serviceName)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM t_vote WHERE player_uuid = ? ORDER BY date DESC LIMIT 1"
            );
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                Vote vote = new Vote(
                        resultSet.getInt("id"),
                        UUID.fromString(resultSet.getString("player_uuid")),
                        resultSet.getTimestamp("date").toInstant(),
                        VoteManager.getInstance().getVoteSite(resultSet.getString("service_name"))
                );
                return Optional.of(vote);
            }
            return Optional.empty();
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to retrieve vote sites", throwables);
        }
        return Optional.empty();
    }
}
