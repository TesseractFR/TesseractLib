package onl.tesseract.tesseractlib.achievement;


import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.player.TPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

public class Title {


    static private final String bddtable = "t_title";

    static private final HashMap<String, Title> titles = new HashMap<>();
    private final String text_m;
    private final String text_f;
    private final String name;

    Title(String name, String text_m, String text_f)
    {
        this.text_m = text_m;
        this.text_f = text_f;
        this.name = name;
        titles.put(name,this);
    }

    public static Collection<Title> getTitles()
    {
        return titles.values();
    }

    static public void loadAll(){
        try
        {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM "+bddtable);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Title.getTitleFromSQLResult(resultSet);
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    static public Title getTitleFromName(String name)
    {
        if (name == null)
            return null;
        name = name.toUpperCase();
        if (name.equals("NULL"))
            return null;
        if (titles.containsKey(name))
            return titles.get(name);
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
