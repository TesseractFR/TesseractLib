package onl.tesseract.tesseractlib.inventory;

import onl.tesseract.tesseractlib.TesseractLib;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class InventoryInstanceManager {
    private static final String FOLDER_PATH = "plugins/Tesseract/inventories/";
    private static final Map<String, InventoryInstanceConfiguration> configurations = new HashMap<>();

    public static void loadConfigurations()
    {
        File configFile = new File(FOLDER_PATH + "config.json");
        File folder = new File(FOLDER_PATH);
        if (!configFile.exists())
        {
            if (!folder.exists())
                new File(FOLDER_PATH).mkdirs();
            return;
        }

        try
        {
            InventoryInstanceConfiguration.load(configFile)
                                          .forEach(config -> configurations.put(config.getName(), config));
        }
        catch (IOException e)
        {
            TesseractLib.logger().log(Level.SEVERE, "Failed to load inventories configurations", e);
        }
    }
}

