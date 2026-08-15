package onl.tesseract.lib.translation;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class LanguageLoader {

    private static final File TRANSLATIONS_FOLDER = new File("translations");

    private LanguageLoader() {
    }

    public static Map<Locale, YamlConfiguration> loadLanguages(String repoBaseUrl, List<String> listModule) {
        TRANSLATIONS_FOLDER.mkdirs();
        ensureLanguagesDownloaded(repoBaseUrl, listModule);
        return readLanguageFiles();
    }

    public static Map<Locale, YamlConfiguration> redownloadLanguages(String repoBaseUrl, List<String> listModule) {
        if (TRANSLATIONS_FOLDER.exists()) {
            deleteRecursively(TRANSLATIONS_FOLDER);
        }
        return loadLanguages(repoBaseUrl, listModule);
    }

    private static void deleteRecursively(File file) {
        if (!file.exists()) {
            return;
        }
        try {
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to delete directory " + file.getPath() + ": " + e.getMessage());
        }
    }

    private static void ensureLanguagesDownloaded(String repoBaseUrl, List<String> listModule) {
        List<Locale> locales = List.of(Locale.FRENCH, Locale.ENGLISH);
        for (Locale locale : locales) {
            File localeFolder = new File(TRANSLATIONS_FOLDER, locale.toLanguageTag());
            if (!localeFolder.exists()) {
                localeFolder.mkdirs();
            }
            for (String module : listModule) {
                File moduleFile = new File(localeFolder, module + ".yml");
                if (!moduleFile.exists()) {
                    downloadTranslationFile(repoBaseUrl, module, locale, moduleFile);
                }
            }
        }
    }

    private static void downloadTranslationFile(String repoBaseUrl, String module, Locale locale, File targetFile) {
        String url = repoBaseUrl + "/" + locale.toLanguageTag() + "/" + module + ".yml";
        try (InputStream inputStream = URI.create(url).toURL().openStream()) {
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("Failed to download translation file from " + url + ": " + e.getMessage());
        }
    }

    private static Map<Locale, YamlConfiguration> readLanguageFiles() {
        Map<Locale, YamlConfiguration> result = new HashMap<>();

        File[] localeDirs = TRANSLATIONS_FOLDER.listFiles(File::isDirectory);
        if (localeDirs == null) {
            return Collections.emptyMap();
        }

        for (File localeDir : localeDirs) {
            Locale locale = Locale.forLanguageTag(localeDir.getName());
            YamlConfiguration mergedConfig = new YamlConfiguration();

            File[] ymlFiles = localeDir.listFiles(file -> file.isFile() && file.getName().endsWith(".yml"));
            if (ymlFiles == null) {
                continue;
            }

            for (File file : ymlFiles) {
                try {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    for (String key : config.getKeys(true)) {
                        if (!config.isConfigurationSection(key)) {
                            mergedConfig.set(key, config.get(key));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load YAML file: " + file.getPath());
                }
            }

            result.put(locale, mergedConfig);
        }

        return result;
    }
}
