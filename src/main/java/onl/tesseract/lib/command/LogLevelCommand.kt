package onl.tesseract.lib.command

import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.commandBuilder.CommandArgument
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps
import onl.tesseract.commandBuilder.CommandContext
import onl.tesseract.commandBuilder.annotation.Argument
import onl.tesseract.commandBuilder.annotation.Command
import onl.tesseract.commandBuilder.annotation.Perm
import onl.tesseract.lib.command.argument.StringArg
import onl.tesseract.lib.logger.LoggerFactory
import onl.tesseract.lib.util.plus
import org.bukkit.command.CommandSender
import org.slf4j.Logger
import org.slf4j.event.Level

@Command(name = "log", permission = Perm("admin.log"))
class LogLevelCommand : CommandContext() {

    @Command(name = "level")
    fun levelCommand(@Argument("logger") loggerArg: StringArg, @Argument("level") levelArg: LevelArgument, sender: CommandSender) {
        LoggerFactory.setLogLevel(loggerArg.get(), levelArg.get())
        sender.sendMessage(NamedTextColor.RED + "Log level set to ${levelArg.get()} for logger ${loggerArg.get()}")
    }
}

class LoggerArgument(name: String) : CommandArgument<Logger>(name) {
    override fun define(builder: CommandArgumentBuilderSteps.Parser<Logger?>) {
        builder.parser { input, env ->
            LoggerFactory.getLogger(input)
        }.tabCompleter { input, env -> listOf<String>() }
    }
}

class LevelArgument(name: String) : CommandArgument<Level>(name) {
    override fun define(builder: CommandArgumentBuilderSteps.Parser<Level?>) {
        builder.parser { input, env -> Level.valueOf(input) }
                .tabCompleter { _, _ -> Level.entries.map { it.name } }
                .errorHandler(IllegalArgumentException::class.java, "Invalid level")
    }
}