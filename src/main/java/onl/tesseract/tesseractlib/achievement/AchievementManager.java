package onl.tesseract.tesseractlib.achievement;

import lombok.Getter;
import onl.tesseract.tesseractlib.dao.AchievementDAO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AchievementManager {
    @Getter
    private static AchievementManager instance = new AchievementManager();
    private final AchievementDAO achievementDAO = AchievementDAO.getInstance();
    private final HashMap<String,Achievement> achievements = new HashMap<>();

    private final List<Achievement> tutoriel = Arrays.asList(getByName("TUTO_CLIC_BOUSSOLE"),
            getByName("TUTO_INVOQUE_AILE_MENU"),
            getByName(
                    "TUTO_PROPULSION_SYNERGIQUE"),
            getByName("TUTO_BOOST_VOL"),
            getByName("TUTO_WARP_ANTERRA"),
            getByName("TUTO_SELECT_METIER"),
            getByName("TUTO_TP_SPAWN"),
            getByName("TUTO_VLIST"),
            getByName("TUTO_VSPAWN"),
            getByName(
                    "TUTO_MENU_SEARCH_OBJECT"),
            getByName(
                    "TUTO_MENU_SEARCH_PARCELLE"));

    public void loadAll() {
        achievementDAO.getAll().forEach(achievement ->
                achievements.put(achievement.getName(), achievement));
    }

    public Achievement getByName(String name) {
        if(achievements.containsKey(name)) {
            return achievements.get(name);
        }
        Achievement a = achievementDAO.getByName(name);
        if(a == null) {return null;}
        achievements.put(name, a);
        return a;
    }


}
