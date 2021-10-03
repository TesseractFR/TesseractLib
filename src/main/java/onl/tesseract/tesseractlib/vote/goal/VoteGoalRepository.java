package onl.tesseract.tesseractlib.vote.goal;

import onl.tesseract.tesseractlib.TesseractLib;

import java.sql.*;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;

public final class VoteGoalRepository {
    public static Collection<VoteGoal> getCurrentGoals()
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM t_vote_goal WHERE start <= NOW() AND end > NOW()");
            final ResultSet resultSet = statement.executeQuery();

            final HashSet<VoteGoal> goals = new HashSet<>();
            while (resultSet.next())
            {
                final int id = resultSet.getInt("id");
                final Date start = resultSet.getDate("start");
                final Date end = resultSet.getDate("end");
                final int quantity = resultSet.getInt("quantity");

                goals.add(new VoteGoal(id, Instant.ofEpochMilli(start.getTime()), Instant.ofEpochMilli(end.getTime()), quantity));
            }
            return goals;
        }
        catch (SQLException throwables)
        {
            TesseractLib.instance.getLogger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
        return Collections.emptyList();
    }

    public static int getVoteCount(final Date start, final Date end)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT count(*) FROM t_vote WHERE date >= ? AND date <= ?");
            statement.setDate(1, start);
            statement.setDate(2, end);
            final ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
        catch (SQLException throwables)
        {
            TesseractLib.instance.getLogger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
            return 0;
        }
    }
}
