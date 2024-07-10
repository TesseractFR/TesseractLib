package onl.tesseract.tesseractlib.achievement;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.player.Gender;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

@Entity
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Title {


    static private final String bddtable = "t_title";

    static private final HashMap<String, Title> titles = new HashMap<>();
    protected String text_m;
    protected String text_f;
    @Id
    protected String name;

    public Title(String name, String text_m, String text_f) {
        this.text_m = text_m;
        this.text_f = text_f;
        this.name = name;
    }

    public static Collection<Title> getTitles() {
        return titles.values();
    }

    static public void loadAll() {
        try {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM " + bddtable);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var title = Title.getTitleFromSQLResult(resultSet);
                titles.put(title.name, title);
            }
        } catch (SQLException throwables) {
            TesseractLib.logger().log(Level.SEVERE, "Failed to load all titles", throwables);
        }
    }

    @Nullable
    static public Title getTitleFromName(String name) {
        if (name == null)
            return null;
        name = name.toUpperCase();
        if (name.equals("NULL"))
            return null;
        if (titles.containsKey(name))
            return titles.get(name);
        throw new IllegalArgumentException("This title doesn't exsit (" + name + ")");
    }

    private static Title getTitleFromSQLResult(ResultSet result) throws SQLException {
        return new Title(result.getString("name"), result.getString("text_m"), result.getString("text_f"));
    }

    public String getDisplayName(Gender gender) {
        if (gender.equals(Gender.FEMALE)) {
            return text_f;
        }
        return text_m;
    }

    public String getName() {
        return name;
    }
}
