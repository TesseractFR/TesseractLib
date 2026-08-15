package onl.tesseract.lib.translation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageManagerTest {

    private final Map<UUID, Locale> playerLocales = new HashMap<>();

    private enum TestEnum {
        FIRST_VALUE,
        SECOND_VALUE
    }

    @BeforeEach
    void setUp() {
        playerLocales.clear();
        LanguageManager.init(Locale.FRENCH, new PlayerLocaleRepository() {
            @Override
            public Locale getLocale(UUID player) {
                return playerLocales.getOrDefault(player, Locale.FRENCH);
            }

            @Override
            public void setLocale(UUID player, Locale locale) {
                playerLocales.put(player, locale);
            }
        });
    }

    @Test
    void testParameterInterpolation() {
        UUID playerId = UUID.randomUUID();
        String result = LanguageManager.get("Bonjour %name% !", Map.of("name", "Alice"), playerId);
        assertEquals("Bonjour Alice !", result);
    }

    @Test
    void testEnumParameter() {
        UUID playerId = UUID.randomUUID();
        String result = LanguageManager.get("Value: %enum%", Map.of("enum", TestEnum.FIRST_VALUE), playerId);
        assertEquals("Value: FIRST_VALUE", result);
    }

    @Test
    void testPlayerLocaleRepository() {
        UUID playerId = UUID.randomUUID();
        assertEquals(Locale.FRENCH, LanguageManager.getPlayerLocale(playerId));

        LanguageManager.setLocale(Locale.ENGLISH, playerId);
        assertEquals(Locale.ENGLISH, LanguageManager.getPlayerLocale(playerId));
    }
}
