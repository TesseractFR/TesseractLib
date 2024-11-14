package onl.tesseract.lib.command.argument;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringArg extends CommandArgument<String> {
    public StringArg(@NotNull final String name)
    {
        super(name);
    }

    @Override
    public void define(final CommandArgumentBuilderSteps.@NotNull Parser<String> builder)
    {
        builder.parser((input, env) -> input)
               .tabCompleter((input, env) -> List.of());
    }
}
