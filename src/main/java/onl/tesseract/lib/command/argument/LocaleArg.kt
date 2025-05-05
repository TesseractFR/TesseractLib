package onl.tesseract.lib.command.argument

import onl.tesseract.commandBuilder.CommandArgument
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps
import onl.tesseract.lib.translation.LanguageManager
import java.util.*

class LocaleArg(name: String) : CommandArgument<Locale>(name) {
    override fun define(builder: CommandArgumentBuilderSteps.Parser<Locale?>) {
        builder.parser { input, _ ->
            LanguageManager.getAvailableLocales()
                    .firstOrNull {
                        it.toLanguageTag() == input
                    }
        }
                .tabCompleter { input, _ ->
                    LanguageManager.getAvailableLocales()
                            .map { it.toLanguageTag() }
                            .filter { it.startsWith(input) }
                            .toList()
                }
    }
}