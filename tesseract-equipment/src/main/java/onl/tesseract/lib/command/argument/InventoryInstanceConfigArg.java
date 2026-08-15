package onl.tesseract.lib.command.argument;

import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandArgumentBuilderSteps;
import onl.tesseract.commandBuilder.CommandArgumentException;
import onl.tesseract.lib.inventory.InventoryInstanceConfiguration;
import onl.tesseract.lib.inventory.InventoryInstanceManager;
import org.jetbrains.annotations.NotNull;

public class InventoryInstanceConfigArg extends CommandArgument<InventoryInstanceConfiguration> {
    public InventoryInstanceConfigArg(@NotNull final String name)
    {
        super(name);
    }

    @Override
    public void define(final CommandArgumentBuilderSteps.@NotNull Parser<InventoryInstanceConfiguration> builder)
    {
        builder.parser((input, env) -> {
                   InventoryInstanceConfiguration config = InventoryInstanceManager.get(input);
                   if (config == null)
                       throw new CommandArgumentException("Inventaire invalide");
                   return config;
               })
               .tabCompleter((input, env) ->
                       InventoryInstanceManager.getAllConfigs()
                                               .stream()
                                               .map(InventoryInstanceConfiguration::getName)
                                               .toList()
               );
    }
}
