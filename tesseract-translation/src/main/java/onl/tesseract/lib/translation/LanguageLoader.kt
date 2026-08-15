package onl.tesseract.lib.translation

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.net.URI
import java.util.*

class LanguageLoader(
    val repoBaseUrl: String,
    val manifestName: String = "lang_manifest.yml",
    val forceDownload: Boolean = false,
) {
    val langFolder: File = File("plugins/Tesseract/lang/")
    private val manifest: Map<String, List<String>> = loadManifest()

    private fun loadManifest(): Map<String, List<String>> {
        val localFile = File(LanguageManager.langFolder, manifestName)
        localFile.parentFile.mkdirs()
        val config = getLocalOrDownload(localFile, "$repoBaseUrl/$manifestName")
                ?: error("[LanguageLoader] Impossible de charger le manifeste de traductions.")
        return config.getKeys(false)
                .associateWith { lang -> config.getStringList(lang) }
    }

    fun loadAll(listModule: List<String>): Map<Locale, YamlConfiguration> {
        val locals = mutableMapOf<Locale, YamlConfiguration>()
        manifest.entries.forEach { locale ->
            locals.put(Locale.forLanguageTag(locale.key), loadFor(locale.key, listModule))

        }
        return locals
    }

    private fun loadFor(localeTag: String, listModule: List<String>): YamlConfiguration {
        val merged = YamlConfiguration()
        val modules = manifest[localeTag] ?: emptyList()
        //Pour chaque module on merge dans un fichier unique
        modules.filter { it in listModule }
                .forEach { module ->
                    val fileName = "${localeTag}/$module.yml"
                    val fileUrl = "$repoBaseUrl/$fileName"
                    val localFile = File(langFolder, fileName)
                    //On tente de charger le fichier local s'il est manquant on tape le serveur distant
                    val yaml = getLocalOrDownload(localFile, fileUrl)

                    if (yaml != null) {
                        mergeYaml(merged, yaml)
                    } else {
                        error("Impossible de charger le module '$module' pour la langue '$localeTag' en local ou distant.")
                    }
                    println(
                        merged.getKeys(true)
                                .joinToString(prefix = " - ", separator = "\n - "))
                }
        return merged
    }

    private fun mergeYaml(dst: YamlConfiguration, src: YamlConfiguration, pathPrefix: String = "") {
        for (key in src.getKeys(false)) {
            val fullPath = if (pathPrefix.isEmpty()) key else "$pathPrefix.$key"

            val srcVal = src[fullPath]
            val dstVal = dst[fullPath]

            if (srcVal is Map<*, *> && dstVal is Map<*, *>) {
                mergeYaml(dst, src, fullPath)
            } else if (dstVal != null && dstVal != srcVal) {
                println("/!\\ [LanguageLoader] Conflit sur '$fullPath' : gardé '${dstVal}', ignoré '${srcVal}'")
            } else {
                dst[fullPath] = srcVal
            }
        }
    }

    private fun getLocalOrDownload(localFile: File, url: String): YamlConfiguration? {
        return if (!this.forceDownload && localFile.exists()) {
            println("[LanguageLoader] Fichier existant, chargement local : ${localFile.name}")
            YamlConfiguration.loadConfiguration(localFile)
        } else
            try {
                println("[LanguageLoader] Téléchargement de : ${localFile.name}")
                localFile.parentFile.mkdirs()
                URI(url).toURL()
                        .openStream()
                        .use { input ->
                            localFile.outputStream()
                                    .use { input.copyTo(it) }
                        }
                YamlConfiguration.loadConfiguration(localFile)
            } catch (e: Exception) {
                println("[LanguageLoader] Échec du téléchargement de $url : ${e.message}")
                null
            }
    }
}