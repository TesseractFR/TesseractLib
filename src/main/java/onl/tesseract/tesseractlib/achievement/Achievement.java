package onl.tesseract.tesseractlib.achievement;


import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.bddfacade.AchievementFacade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Achievement {


    static private final HashMap<String, Achievement> achievements = new HashMap<>();
    static private final HashMap<Integer,Achievement> achievementsbyId = new HashMap<>();
    static private final AchievementFacade facade = new AchievementFacade();
    public static List<Achievement> tutoriel = Arrays.asList(Achievement.getAchievementFromName("TUTO_CLIC_BOUSSOLE"),
                                                             Achievement
                                                                     .getAchievementFromName("TUTO_INVOQUE_AILE_MENU"),
                                                             Achievement.getAchievementFromName(
                                                                     "TUTO_PROPULSION_SYNERGIQUE"),
                                                             Achievement.getAchievementFromName("TUTO_BOOST_VOL"),
                                                             Achievement.getAchievementFromName("TUTO_WARP_ANTERRA"),
                                                             Achievement.getAchievementFromName("TUTO_SELECT_METIER"),
                                                             Achievement.getAchievementFromName("TUTO_TP_SPAWN"),
                                                             Achievement.getAchievementFromName("TUTO_VLIST"),
                                                             Achievement.getAchievementFromName("TUTO_VSPAWN"),
                                                             Achievement.getAchievementFromName(
                                                                     "TUTO_MENU_SEARCH_OBJECT"),
                                                             Achievement.getAchievementFromName(
                                                                     "TUTO_MENU_SEARCH_PARCELLE")
    );
    private final Title title;
    private final String condition;
    private final String name;
    private final float lys;
    private final int ptsIllumination;
    private final int id;
    private final String displayName;


    public Achievement(int id, Title title, String name,String displayName, String condition, float lys, int ptsIllumination)
    {
        this.id = id;
        this.title = title;
        this.name = name;
        this.displayName = displayName;
        this.condition = condition;
        this.lys = lys;
        this.ptsIllumination = ptsIllumination;
        achievements.put(name, this);
        achievementsbyId.put(id,this);
    }

    static public void loadAll(){
        try{
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM t_achievement");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                new Achievement(resultSet.getInt("id"),Title.getTitleFromName(resultSet.getString("title")),
                                resultSet.getString("name"),
                                resultSet.getString("text"),resultSet.getString("condition"),
                                resultSet.getFloat("lys"),resultSet.getInt("illumination"));
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }


    static public Achievement getAchievementFromName(String name)
    {
        name = name.toUpperCase();
        if (achievements.containsKey(name))
            return achievements.get(name);
        return facade.getFromName(name);
    }

    static public Achievement getAchievementFromId(int id){
        if(achievementsbyId.containsKey(id)){
            return achievementsbyId.get(id);
        }
        throw new NullPointerException("No achievement founds");
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getCondition()
    {
        return condition;
    }

    public Title getTitle()
    {
        return title;
    }

    public float getLys()
    {
        return lys;
    }

    public int getPtsIllumination()
    {
        return ptsIllumination;
    }

    public int getId()
    {
        return id;
    }

    public static HashMap<String,Achievement> getAll(){
        return achievements;
    }
}



