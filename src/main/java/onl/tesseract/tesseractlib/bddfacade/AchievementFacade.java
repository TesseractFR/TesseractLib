package onl.tesseract.tesseractlib.bddfacade;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.achievement.Achievement;
import onl.tesseract.tesseractlib.achievement.Title;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AchievementFacade {
    static private final String bddtable = "t_achievement";


    private Achievement getAchievementFromSQLResult(ResultSet result) throws SQLException
    {
        return new Achievement(result.getInt("id"),
                               Title.getTitleFromName(result.getString("title")),
                               result.getString("name"),
                               result.getString("text"),
                               result.getString("condition"),
                               result.getFloat("lys"),
                               result.getInt("illumination"));
    }


    public Achievement getFromName(String name)
    {
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM " + bddtable + " WHERE name = ?");
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                return getAchievementFromSQLResult(result);
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        throw new NullPointerException("This achievement doesn't exsit");
    }
}
