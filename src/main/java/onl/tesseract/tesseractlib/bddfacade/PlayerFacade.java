package onl.tesseract.tesseractlib.bddfacade;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.achievement.Achievement;
import onl.tesseract.tesseractlib.player.TPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerFacade {

    static private final String bddtable = "t_player";
    final static String bddtableAchivement = "t_player_achievement";
    public UUID uuid;

    public PlayerFacade(UUID uuid)
    {
        this.uuid = uuid;
    }

    public static boolean exist(UUID uniqueId){
        try {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM "+bddtable + " WHERE uuid = ?");
            preparedStatement.setString(1, String.valueOf(uniqueId));
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static void addtodatabase(UUID uniqueId){
        try {
            final Connection connection =  TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO "+bddtable + "(uuid,genre) VALUES (?,'OTHER')");
            preparedStatement.setString(1, String.valueOf(uniqueId));
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setGender(TPlayer.Gender gender)
    {
        try {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE "+bddtable+" SET genre = ? WHERE uuid = ?");
            preparedStatement.setString(1,""+gender.toString());
            preparedStatement.setString(2,uuid.toString());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public TPlayer.Gender getGender()
    {
        try {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT genre FROM "+bddtable + " WHERE uuid = ?");
            preparedStatement.setString(1,uuid.toString());
            ResultSet result = preparedStatement.executeQuery();
            if(result.next())
                return TPlayer.Gender.valueOf(result.getString("genre"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return TPlayer.Gender.OTHER;
    }

    public boolean hasAchievement(Achievement achievement)
    {
        try
        {
            final PreparedStatement preparedStatement = TesseractLib.getBddManager().getBddConnection().getConnection().prepareStatement(
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
            throwables.printStackTrace();
        }

        return false;
    }

    public List<Achievement> getAllAchievements(){
        List<Achievement> achievements = new ArrayList<>();
        try{
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT achievement_id FROM "+bddtableAchivement+" WHERE player_uuid = ?");
            preparedStatement.setString(1,uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                achievements.add(Achievement.getAchievementFromId(resultSet.getInt("achievement_id")));
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return achievements;
    }

    public void addAchievements(Achievement achievement)
    {
        try
        {
            final PreparedStatement preparedStatement = TesseractLib.getBddManager().getBddConnection().getConnection().prepareStatement(
                    "INSERT INTO " + bddtableAchivement + " (player_uuid, achievement_id) VALUES (?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, achievement.getId());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public void removeAchievement(Achievement achievement)
    {
        try
        {
            final PreparedStatement preparedStatement = TesseractLib.getBddManager().getBddConnection().getConnection().prepareStatement(
                    "DELETE FROM " + bddtableAchivement + " WHERE player_uuid=? AND achievement_id=?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, achievement.getId());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
}
