package onl.tesseract.lib.command.argument;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IntegerCommandArgument extends CommandArgument<Integer> {
    public IntegerCommandArgument(@NotNull final String name)
    {
        super(name);
    }

    @Override
    public void define(final CommandArgumentBuilderSteps.@NotNull Parser<Integer> builder)
    {
        builder.parser((input, env) -> {
                   return Integer.parseInt(input);
               })
               .tabCompleter((input, env) -> List.of("<" + getName() + ">"))
               .errorHandler(NumberFormatException.class, "Nombre invalide");
    }
}
