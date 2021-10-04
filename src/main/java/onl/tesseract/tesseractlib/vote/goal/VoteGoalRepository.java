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

            final Collection<VoteGoal> goals = new HashSet<>();
            while (resultSet.next())
            {
                final int id = resultSet.getInt("id");
                final Instant start = resultSet.getTimestamp("start").toInstant();
                final Instant end = resultSet.getTimestamp("end").toInstant();
                final int quantity = resultSet.getInt("quantity");
                final String rewardTypeName = resultSet.getString("reward_type");
                final String rewardRaw = resultSet.getString("reward_raw");
                final VoteGoalRewardType type = VoteGoalRewardManager.getRewardType(rewardTypeName);
                final VoteGoalReward reward = type != null ? type.deserialize(rewardRaw) : null;

                goals.add(new VoteGoal(id, start, end, quantity, reward));
            }
            return goals;
        }
        catch (SQLException throwables)
        {
            TesseractLib.instance.getLogger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
        return Collections.emptyList();
    }

    public static int getVoteCount(final Timestamp start, final Timestamp end)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT count(*) FROM t_vote WHERE date >= ? AND date <= ?");
            statement.setTimestamp(1, start);
            statement.setTimestamp(2, end);
            final ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
        catch (SQLException throwables)
        {
            TesseractLib.instance.getLogger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
            return 0;
        }
    }

    public static int getVoteCount(final VoteGoal goal)
    {
        return getVoteCount(new Timestamp(goal.getStart().toEpochMilli()), new Timestamp(goal.getEnd().toEpochMilli()));
    }

    public static void createVoteGoal(final VoteGoal toCreate)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO t_vote_goal (start, end, quantity, reward_type, reward_raw) VALUES (?, ?, ?, ?, ?)");
            statement.setTimestamp(1, new Timestamp(toCreate.getStart().toEpochMilli()));
            statement.setTimestamp(2, new Timestamp(toCreate.getEnd().toEpochMilli()));
            statement.setInt(3, toCreate.getRequiredQuantity());
            statement.setString(4, toCreate.getReward() == null ? null : toCreate.getReward().getType().getName());
            statement.setString(5, toCreate.getReward() == null ? null : toCreate.getReward().toString());
            statement.executeUpdate();
        }
        catch (SQLException throwables)
        {
            TesseractLib.instance.getLogger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }
}
