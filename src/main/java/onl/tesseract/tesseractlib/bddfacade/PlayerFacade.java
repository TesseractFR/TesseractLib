package onl.tesseract.tesseractlib.bddfacade;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.achievement.Achievement;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.cosmetics.FlyFilter;
import onl.tesseract.tesseractlib.player.TPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerFacade {

    final static String bddtableAchivement = "t_player_achievement";
    static private final String bddtable = "t_player";
    public final UUID uuid;

    public PlayerFacade(UUID uuid)
    {
        this.uuid = uuid;
    }

    public static boolean exist(UUID uniqueId)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM " + bddtable + " WHERE uuid = ?");
            preparedStatement.setString(1, String.valueOf(uniqueId));
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                return true;
            }
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
        return false;
    }

    public static void addtodatabase(UUID uniqueId)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + bddtable + "(uuid,genre) VALUES (?,'OTHER')");
            preparedStatement.setString(1, String.valueOf(uniqueId));
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }

    public TPlayer.Gender getGender()
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT genre FROM " + bddtable + " WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
                return TPlayer.Gender.valueOf(result.getString("genre"));
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
        return TPlayer.Gender.OTHER;
    }

    public void setGender(TPlayer.Gender gender)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE " + bddtable + " SET genre = ? WHERE uuid = ?");
            preparedStatement.setString(1, "" + gender.toString());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }

    public boolean hasAchievement(Achievement achievement)
    {
        try
        {
            final PreparedStatement preparedStatement =
                    TesseractLib.getBddManager().getBddConnection().getConnection().prepareStatement(
                            "SELECT achievement_id FROM " + bddtableAchivement
                                    + " WHERE player_uuid = ? AND achievement_id = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, achievement.getId());
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                return true;
            }
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }

        return false;
    }

    public List<Achievement> getAllAchievements()
    {
        List<Achievement> achievements = new ArrayList<>();
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT achievement_id FROM " + bddtableAchivement + " WHERE player_uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                achievements.add(Achievement.getAchievementFromId(resultSet.getInt("achievement_id")));
            }
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
        return achievements;
    }

    public void addAchievements(Achievement achievement)
    {
        try
        {
            final PreparedStatement preparedStatement =
                    TesseractLib.getBddManager().getBddConnection().getConnection().prepareStatement(
                            "INSERT INTO " + bddtableAchivement + " (player_uuid, achievement_id) VALUES (?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, achievement.getId());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }

    public void removeAchievement(Achievement achievement)
    {
        try
        {
            final PreparedStatement preparedStatement =
                    TesseractLib.getBddManager().getBddConnection().getConnection().prepareStatement(
                            "DELETE FROM " + bddtableAchivement + " WHERE player_uuid=? AND achievement_id=?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, achievement.getId());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }

    public ElytraTrails getActiveTrails()
    {
        try
        {
            final PreparedStatement preparedStatement =
                    TesseractLib.getBddManager().getBddConnection().getConnection().prepareStatement(
                            "SELECT active_trail FROM t_player WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return ElytraTrails.valueOf(resultSet.getString(1));
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
        return ElytraTrails.NONE;
    }

    public void setActiveTrails(ElytraTrails trails)
    {
        try
        {
            final PreparedStatement preparedStatement = TesseractLib
                    .getBddManager().getBddConnection()
                    .getConnection().prepareStatement(
                            "UPDATE t_player SET active_trail =? WHERE uuid =?");
            preparedStatement.setString(1, trails.toString());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }

    public int getMarketCurrency()
    {
        try
        {
            final PreparedStatement preparedStatement = TesseractLib
                    .getBddManager().getBddConnection()
                    .getConnection().prepareStatement(
                            "SELECT market_currency FROM t_player WHERE uuid =?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
        return 0;
    }

    public void setMarketCurrency(int currency)
    {
        try
        {
            final PreparedStatement preparedStatement = TesseractLib
                    .getBddManager().getBddConnection()
                    .getConnection().prepareStatement(
                            "UPDATE t_player SET market_currency =? WHERE uuid =?");
            preparedStatement.setInt(1, currency);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }

    public void setFlyFilter(FlyFilter flyFilter)
    {
        try
        {
            final PreparedStatement preparedStatement = TesseractLib
                    .getBddManager().getBddConnection()
                    .getConnection().prepareStatement(
                            "UPDATE t_player SET active_fly_filter =? WHERE uuid =?");
            preparedStatement.setString(1, flyFilter.toString());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }

    public FlyFilter getFlyFilter()
    {
        try
        {
            final PreparedStatement preparedStatement = TesseractLib
                    .getBddManager().getBddConnection()
                    .getConnection().prepareStatement(
                            "SELECT active_fly_filter FROM t_player WHERE uuid =?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return FlyFilter.valueOf(resultSet.getString(1));
            }
        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
        return FlyFilter.NONE;
    }

    public void addMarketCurrency(int amount)
    {
        try
        {
            PreparedStatement preparedStatement = TesseractLib.getBddManager().getBddConnection().getConnection()
                                                              .prepareStatement(
                                                                      "UPDATE " + bddtable
                                                                              + " SET market_currency = market_currency + ? WHERE uuid = ?");
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();

        }
        catch (SQLException throwables)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to execute sql statement", throwables);
        }
    }
}
