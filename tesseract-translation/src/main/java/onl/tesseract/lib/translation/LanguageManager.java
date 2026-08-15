package onl.tesseract.lib.translation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class LanguageManager {

    private static final Map<Locale, YamlConfiguration> languages = new HashMap<>();
    private static Locale defaultLocale = Locale.FRENCH;
    private static String repoBaseUrl;
    private static List<String> listModule;
    private static PlayerLocaleRepository playerLanguageProvider;

    private LanguageManager() {
    }

    public static void init(@NotNull Locale defaultLoc, @NotNull PlayerLocaleRepository provider) {
        defaultLocale = defaultLoc;
        playerLanguageProvider = provider;
    }

    public static void loadLanguages(@NotNull String repoUrl, @NotNull List<String> modules, @NotNull PlayerLocaleRepository provider) {
        repoBaseUrl = repoUrl;
        listModule = modules;
        playerLanguageProvider = provider;
        languages.putAll(LanguageLoader.loadLanguages(repoUrl, modules));
    }

    public static void reloadLanguages() {
        languages.putAll(LanguageLoader.loadLanguages(repoBaseUrl, listModule));
    }

    public static void redownloadLanguages() {
        languages.putAll(LanguageLoader.redownloadLanguages(repoBaseUrl, listModule));
    }

    public static String get(@NotNull String key, @Nullable Map<String, ?> params, @NotNull Locale locale) {
        YamlConfiguration config = languages.get(locale);
        if (config == null) {
            config = languages.get(defaultLocale);
        }

        String resolved = null;
        if (config != null) {
            resolved = config.getString(key);
        }
        if (resolved == null && languages.get(defaultLocale) != null) {
            resolved = languages.get(defaultLocale).getString(key);
        }
        if (resolved == null) {
            resolved = key;
        }

        if (params != null) {
            for (Map.Entry<String, ?> entry : params.entrySet()) {
                String tag = "%" + entry.getKey() + "%";
                Object value = entry.getValue();
                if (value instanceof Enum<?> enumVal) {
                    String category = enumVal.getDeclaringClass().getSimpleName().toLowerCase(Locale.ROOT);
                    String enumKey = category + "." + enumVal.name();
                    String translated = null;
                    if (config != null) {
                        translated = config.getString(enumKey);
                    }
                    if (translated == null && languages.get(defaultLocale) != null) {
                        translated = languages.get(defaultLocale).getString(enumKey);
                    }
                    if (translated == null) {
                        translated = enumVal.name();
                    }
                    resolved = resolved.replace(tag, translated);
                } else if (value != null) {
                    resolved = resolved.replace(tag, value.toString());
                } else {
                    resolved = resolved.replace(tag, "null");
                }
            }
        }
        return resolved;
    }

    public static String get(@NotNull String key, @Nullable Map<String, ?> params, @NotNull Player player) {
        Locale locale = getPlayerLocale(player);
        return get(key, params, locale);
    }

    public static String get(@NotNull String key, @Nullable Map<String, ?> params, @NotNull UUID uuid) {
        Locale locale = getPlayerLocale(uuid);
        return get(key, params, locale);
    }

    public static String get(@NotNull String key, @NotNull Player player) {
        return get(key, Collections.emptyMap(), player);
    }

    public static String get(@NotNull String key, @NotNull UUID uuid) {
        return get(key, Collections.emptyMap(), uuid);
    }

    public static String get(@NotNull String key, @NotNull Locale locale) {
        return get(key, Collections.emptyMap(), locale);
    }

    public static String get(@NotNull String key) {
        return get(key, Collections.emptyMap(), defaultLocale);
    }

    public static Locale getPlayerLocale(@NotNull Player player) {
        return playerLanguageProvider.getLocale(player);
    }

    public static Locale getPlayerLocale(@NotNull UUID uuid) {
        return playerLanguageProvider.getLocale(uuid);
    }

    public static void setLocale(@NotNull Locale locale, @NotNull Player player) {
        playerLanguageProvider.setLocale(player, locale);
    }

    public static void setLocale(@NotNull Locale locale, @NotNull UUID uuid) {
        playerLanguageProvider.setLocale(uuid, locale);
    }

    public static Set<Locale> getAvailableLocales() {
        return languages.keySet();
    }

    public static Map<Locale, YamlConfiguration> getLanguages() {
        return languages;
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static void setDefaultLocale(@NotNull Locale locale) {
        defaultLocale = locale;
    }
}
