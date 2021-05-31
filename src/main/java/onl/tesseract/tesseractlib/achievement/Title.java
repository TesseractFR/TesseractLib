package onl.tesseract.tesseractlib.achievement;


import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.player.TPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Title {


    static private final String bddtable = "t_title";

    static private HashMap<String, Title> titles = new HashMap<>();
    private String text_m;
    private String text_f;
    private String name;

    Title(String name, String text_m, String text_f)
    {
        this.text_m = text_m;
        this.text_f = text_f;
        this.name = name;
    }

    static public Title getTitleFromName(String name)
    {
        name = name.toUpperCase();
        if (name.equals("NULL"))
            return null;
        if (titles.containsKey(name))
            return titles.get(name);
        try
        {
            final Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM " + bddtable + " WHERE name = ?");
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                Title t = getTitleFromSQLResult(result);
                titles.put(name, t);
                return t;
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        throw new NullPointerException("This title doesn't exsit (" + name + ")");
    }

    private static Title getTitleFromSQLResult(ResultSet result) throws SQLException
    {
        return new Title(result.getString("name"), result.getString("text_m"), result.getString("text_f"));
    }

    public String getDisplayName(TPlayer.Gender gender)
    {
        if (gender.equals(TPlayer.Gender.FEMALE))
        {
            return text_f;
        }
        return text_m;
    }

    public String getName()
    {
        return name;
    }
}
