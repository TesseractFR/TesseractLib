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
                    "SELECT * FROM t_vote WHERE player_uuid = ? AND service_name = ? ORDER BY date DESC LIMIT 1"
            );
            statement.setString(1, playerUUID.toString());
            statement.setString(2, serviceName);
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

    public static int getPeriodVoteHelper(final String playerCondition, final String dateCondition, final String serviceCondition)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();

            StringJoiner conditions = new StringJoiner(" AND ", "WHERE ", "");

            if (playerCondition != null)
                conditions.add(playerCondition);
            if (serviceCondition != null)
                conditions.add(serviceCondition);
            if (dateCondition != null)
                conditions.add(dateCondition);

            final String conditionsString = conditions.length() > 0
                                            ? conditions.toString()
                                            : "";

            PreparedStatement statement = connection.prepareStatement(
                    "SELECT count(*) FROM t_vote " + conditionsString
            );
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                return resultSet.getInt(1);
            }
            return 0;
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to retrieve vote sites", throwables);
        }
        return 0;
    }

    public static int getDailyVote(final UUID playerUUID, final String serviceName)
    {
        return getPeriodVoteHelper(
                "player_uuid = '" + playerUUID.toString() + "'",
                "DAYOFYEAR(DATE(date)) = DAYOFYEAR(NOW())"
                        + " AND YEAR(DATE(date)) = YEAR(NOW())",
                "service_name = '" + serviceName + "'"
        );
    }

    public static int getMonthlyVote(final UUID playerUUID, final String serviceName)
    {
        return getPeriodVoteHelper(
                "player_uuid = '" + playerUUID.toString() + "'",
                "MONTH(DATE(date)) = MONTH(NOW())"
                        + " AND YEAR(DATE(date)) = YEAR(NOW())",
                "service_name = '" + serviceName + "'"
        );
    }

    public static int getYearlyVote(final UUID playerUUID, final String serviceName)
    {
        return getPeriodVoteHelper(
                "player_uuid = '" + playerUUID.toString() + "'",
                "YEAR(DATE(date)) = YEAR(NOW())",
                "service_name = '" + serviceName + "'"
        );
    }

    public static int getDailyVote(final String serviceName)
    {
        return getPeriodVoteHelper(
                null,
                "DAYOFYEAR(DATE(date)) = DAYOFYEAR(NOW())"
                        + " AND YEAR(DATE(date)) = YEAR(NOW())",
                "service_name = '" + serviceName + "'"
        );
    }

    public static int getMonthlyVote(final String serviceName)
    {
        return getPeriodVoteHelper(
                null,
                "MONTH(DATE(date)) = MONTH(NOW())"
                        + " AND YEAR(DATE(date)) = YEAR(NOW())",
                "service_name = '" + serviceName + "'"
        );
    }

    public static int getYearlyVote(final String serviceName)
    {
        return getPeriodVoteHelper(
                null,
                "YEAR(DATE(date)) = YEAR(NOW())",
                "service_name = '" + serviceName + "'"
        );
    }

    public static int getAllVotes(final UUID playerUUID, final String serviceName)
    {
        return getPeriodVoteHelper(
                "player_uuid = '" + playerUUID.toString() + "'",
                null,
                "service_name = '" + serviceName + "'"
        );
    }

    public static int getAllVotes(final String serviceName)
    {
        return getPeriodVoteHelper(
                null,
                null,
                "service_name = '" + serviceName + "'"
        );
    }
}
