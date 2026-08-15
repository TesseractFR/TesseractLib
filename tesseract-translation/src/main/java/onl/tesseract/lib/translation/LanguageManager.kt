package onl.tesseract.lib.translation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import onl.tesseract.lib.gender
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*


private const val LANG_MANIFEST_YML = "lang_manifest.yml"

object LanguageManager {
    val langFolder: File = File("plugins/Tesseract/lang/")
    private val defaultLocale: Locale = Locale.FRANCE
    private val languages = mutableMapOf<Locale, YamlConfiguration>()
    private val miniMessage = MiniMessage.miniMessage()
    private lateinit var repoBaseUrl: String
    private lateinit var listModule: List<String>
    private lateinit var playerLanguageProvider: PlayerLocaleRepository

    fun reloadLanguages() {
        languages.clear()
        languages.putAll(LanguageLoader(repoBaseUrl, LANG_MANIFEST_YML).loadAll(listModule))
    }

    fun loadLanguages(repoBaseUrl: String, listModule: List<String>, playerLanguageProvider: PlayerLocaleRepository) {
        this.repoBaseUrl = repoBaseUrl
        this.listModule = listModule
        this.playerLanguageProvider = playerLanguageProvider
        reloadLanguages()
    }


    operator fun get(key: String, placeholders: Map<String, Any> = emptyMap(), player: Player): Component =
        get(key, placeholders, player.uniqueId)

    operator fun get(key: String, placeholders: Map<String, Any> = emptyMap(), playerUUID: UUID): Component = get(
        key, placeholders,
        playerLanguageProvider.getLocale(playerUUID))

    operator fun get(key: String, playerUUID: UUID): Component =
        get(key, locale = playerLanguageProvider.getLocale(playerUUID))

    operator fun get(key: String, player: Player): Component =
        get(key, player.uniqueId)

    operator fun get(
        key: String,
        placeholders: Map<String, Any> = emptyMap(),
        locale: Locale = Locale.FRANCE,
    ): Component {
        val config = languages[locale] ?: languages[defaultLocale]
        val raw = config?.getString(key) ?: languages[defaultLocale]?.getString(key) ?: notifError(key)

        // Préremplace les %key% pour les Strings, marque les Components comme <key>
        var resolved = raw
        val componentPlaceholders = mutableMapOf<String, Component>()

        placeholders.forEach { (k, v) ->
            val tag = "%$k%"
            when (v) {
                is String -> resolved = resolved.replace(tag, v)
                is Component -> {
                    resolved = resolved.replace(tag, "<$k>")
                    componentPlaceholders[k] = v
                }
                is GenderedEnum<*> -> {
                    val enumVal = v.value
                    val category = enumVal::class.simpleName?.lowercase(Locale.ROOT) ?: "enum"
                    val enumKey = "$category.${enumVal.name}.${v.gender.name.lowercase(Locale.ROOT)}"
                    val translated = config?.getString(enumKey)
                            ?: languages[defaultLocale]?.getString(enumKey)
                            ?: enumVal.name
                    resolved = resolved.replace(tag, translated)
                }

                is Enum<*> -> {
                    val category = v::class.simpleName?.lowercase(Locale.ROOT) ?: "enum"
                    val enumKey = "$category.${v.name}"
                    val translated = config?.getString(enumKey)
                            ?: languages[defaultLocale]?.getString(enumKey)
                            ?: v.name
                    resolved = resolved.replace(tag, translated)
                }
                else -> resolved = resolved.replace(tag, v.toString())
            }
        }

        var result = miniMessage.deserialize(resolved)

        // Applique les composants finaux
        componentPlaceholders.forEach { (k, comp) ->
            result = result.replaceText { builder ->
                builder.matchLiteral("<$k>")
                        .replacement(comp)
            }
        }
        return result
    }

    fun redownloadLanguages() {
        languages.clear()
        languages.putAll(LanguageLoader(repoBaseUrl, LANG_MANIFEST_YML, true).loadAll(listModule))
    }

    fun getAvailableLocales(): Set<Locale> = languages.keys
    fun setLocale(locale: Locale, sender: Player) {
        playerLanguageProvider.setLocale(sender, locale)
    }

    private fun notifError(key: String): Nothing {
        Bukkit.getOperators()
                .filter { it.isOnline }
                .forEach { it.player?.sendMessage("Missing key: $key") }
        error("Missing key: $key")
    }
}