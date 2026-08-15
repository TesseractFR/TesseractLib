package onl.tesseract.lib.command;

import onl.tesseract.commandBuilder.CommandContext;
import onl.tesseract.commandBuilder.annotation.Argument;
import onl.tesseract.commandBuilder.annotation.Command;
import onl.tesseract.commandBuilder.annotation.Perm;
import onl.tesseract.lib.command.argument.LocaleArg;
import onl.tesseract.lib.translation.LanguageManager;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

@Command(name = "translation")
public class TranslationCommand extends CommandContext {

    @Command(name = "reload", permission = @Perm(value = "tesseract.staff.translation", mode = Perm.Mode.AUTO))
    public void reloadCommand() {
        LanguageManager.reloadLanguages();
    }

    @Command(name = "download", permission = @Perm(value = "tesseract.staff.translation", mode = Perm.Mode.AUTO))
    public void downloadCommand() {
        LanguageManager.redownloadLanguages();
    }

    @Command
    public void selectCommand(@Argument(value = "locale", clazz = LocaleArg.class) Locale locale, Player sender) {
        LanguageManager.setLocale(locale, sender);
        sender.sendMessage(
                LanguageManager.get("lib.command.translation.select.success", Map.of(
                        "locale", locale.toLanguageTag()
                ), sender)
        );
    }
}
