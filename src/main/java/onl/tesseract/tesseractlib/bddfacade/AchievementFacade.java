package onl.tesseract.tesseractlib.bddfacade;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.achievement.Achievement;
import onl.tesseract.tesseractlib.achievement.Title;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

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
            TesseractLib.logger().log(Level.SEVERE, "Failed to get achievement from name " + name, throwables);
        }
        throw new IllegalArgumentException("This achievement doesn't exsit : " + name);
    }
}
