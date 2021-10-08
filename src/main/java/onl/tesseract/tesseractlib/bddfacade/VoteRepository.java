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
    public enum VotePeriod {
        DAILY("DAYOFYEAR(DATE(date)) = DAYOFYEAR(NOW()) AND YEAR(DATE(date)) = YEAR(NOW())"),
        WEEKLY("WEEKOFYEAR(DATE(date)) = WEEKOFYEAR(NOW()) AND YEAR(DATE(date)) = YEAR(NOW())"),
        MONTHLY("MONTH(DATE(date)) = MONTH(NOW()) AND YEAR(DATE(date)) = YEAR(NOW())"),
        YEARLY("YEAR(DATE(date)) = YEAR(NOW())"),
        ;
        private final String sqlCondition;

        VotePeriod(final String sqlCondition) {this.sqlCondition = sqlCondition;}

        public String getSqlCondition()
        {
            return sqlCondition;
        }
    }

    public static class GetVoteStatementBuilder {
        private String periodCondition;
        private String playerUUIDCondition;
        private String serviceCondition;

        public int build()
        {
            try
            {
                final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();

                String statementString = "SELECT count(*) FROM t_vote ";
                StringJoiner conditions = new StringJoiner(" AND ", "WHERE ", "");

                if (playerUUIDCondition != null)
                    conditions.add(playerUUIDCondition);
                if (serviceCondition != null)
                    conditions.add(serviceCondition);
                if (periodCondition != null)
                    conditions.add(periodCondition);

                final String conditionsString = conditions.length() > 0
                                                ? conditions.toString()
                                                : "";

                PreparedStatement statement = connection.prepareStatement(statementString + conditionsString);
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

        public GetVoteStatementBuilder setPeriod(final VotePeriod period)
        {
            this.periodCondition = period.sqlCondition;
            return this;
        }

        public GetVoteStatementBuilder setPlayerUUID(final UUID playerUUID)
        {
            this.playerUUIDCondition = "player_uuid = '" + playerUUID.toString() + "'";
            return this;
        }

        public GetVoteStatementBuilder setService(final String service)
        {
            this.serviceCondition = "service_name = '" + service + "'";
            return this;
        }
    }

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

    public static LinkedHashMap<UUID, Integer> getTop()
    {
        LinkedHashMap<UUID, Integer> map = new LinkedHashMap<>();
        try
        {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT player_uuid, count(*) as amount FROM t_vote WHERE MONTH(DATE(date)) = MONTH(NOW()) AND YEAR(DATE(date)) = YEAR(NOW()) GROUP BY player_uuid ORDER BY count(*) DESC LIMIT 10");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                map.put(UUID.fromString(resultSet.getString("player_uuid")), resultSet.getInt("amount"));
            }
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to retrieve votes", throwables);
        }
        return map;
    }
}
