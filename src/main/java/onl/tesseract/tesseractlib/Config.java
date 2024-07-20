package onl.tesseract.tesseractlib;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Config {
    @Getter
    static private final Config instance = new Config();
    @Getter
    static private final String configFilepath = "plugins/Tesseract/config.yml";

    Location firstSpawnLocation;
    String db_host;

    String db_database;

    String db_username;

    String db_password;
    int db_port;

    private Config() {
        File file = new File(Config.configFilepath);
        if (!file.exists()) {
            createNewConfigFile();
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        this.db_host = yaml.getString("db_host");
        this.db_database = yaml.getString("db_database");
        this.db_username = yaml.getString("db_username");
        this.db_password = yaml.getString("db_password");
        this.db_port = yaml.getInt("db_port");
        this.firstSpawnLocation = yaml.getLocation("firstSpawnLocation");
    }

    private void createNewConfigFile() {
        File file = new File(configFilepath);

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        yaml.set("db_host", "localhost");
        yaml.set("db_database", "tesseract");
        yaml.set("db_username", "user");
        yaml.set("db_password", "password");
        yaml.set("db_port", 3306);
        World w = Bukkit.getWorlds().getFirst();
        yaml.set("firstSpawnLocation", new Location( (w), 0,0,0));
    }


}
