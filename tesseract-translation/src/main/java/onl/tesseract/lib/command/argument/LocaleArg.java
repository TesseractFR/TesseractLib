package onl.tesseract.lib.command.argument;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps;
import onl.tesseract.lib.translation.LanguageManager;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.stream.Collectors;

public class LocaleArg extends CommandArgument<Locale> {

    public LocaleArg(@NotNull String name) {
        super(name);
    }

    @Override
    public void define(CommandArgumentBuilderSteps.@NotNull Parser<Locale> builder) {
        builder.parser((input, env) ->
                LanguageManager.getAvailableLocales()
                        .stream()
                        .filter(locale -> locale.toLanguageTag().equals(input))
                        .findFirst()
                        .orElse(null)
        ).tabCompleter((input, env) ->
                LanguageManager.getAvailableLocales()
                        .stream()
                        .map(Locale::toLanguageTag)
                        .filter(tag -> tag.startsWith(input))
                        .collect(Collectors.toList())
        );
    }
}
