package onl.tesseract.lib.command

import onl.tesseract.commandBuilder.CommandContext
import onl.tesseract.commandBuilder.annotation.Argument
import onl.tesseract.commandBuilder.annotation.Command
import onl.tesseract.commandBuilder.annotation.Perm
import onl.tesseract.lib.command.argument.LocaleArg
import onl.tesseract.lib.translation.LanguageManager
import org.bukkit.entity.Player
import java.util.*

@Command(name = "translation")
class TranslationCommand : CommandContext() {

    @Command(name = "reload", permission = Perm(value = "tesseract.staff.translation", mode = Perm.Mode.AUTO))
    fun reloadCommand() {
        LanguageManager.reloadLanguages()
    }

    @Command(name = "download", permission = Perm(value = "tesseract.staff.translation", mode = Perm.Mode.AUTO))
    fun downloadCommand() {
        LanguageManager.redownloadLanguages()
    }

    @Command
    fun selectCommand(@Argument(value = "locale", clazz = LocaleArg::class) locale: Locale, sender: Player) {
        LanguageManager.setLocale(locale, sender)
        sender.sendMessage(
            LanguageManager["lib.command.translation.select.success", mapOf(
                "locale" to locale.toLanguageTag()
            ), sender])
    }
}