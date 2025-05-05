package onl.tesseract.lib.translation

import org.bukkit.entity.Player
import java.util.*

interface PlayerLocaleRepository {
    fun getLocale(player: UUID): Locale

    fun getLocale(player: Player): Locale = getLocale(player.uniqueId)

    fun setLocale(player: UUID, locale: Locale)

    fun setLocale(player: Player, locale: Locale) = setLocale(player.uniqueId, locale)

}