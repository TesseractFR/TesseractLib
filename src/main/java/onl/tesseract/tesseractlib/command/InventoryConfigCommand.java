package onl.tesseract.tesseractlib.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.commandBuilder.CommandArgument;
import onl.tesseract.commandBuilder.CommandBuilder;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceConfiguration;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceConfigurationBuilder;
import onl.tesseract.tesseractlib.inventory.InventoryInstanceManager;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryConfigCommand extends CommandBuilder {
    public InventoryConfigCommand(final String commandName)
    {
        super(commandName);
        description("Configurer les différents inventaires.");
        permission("inventory.config");

        subCommand(new CommandBuilder("create")
                .description("Créer un nouvel inventaire")
                .withArg(new CommandArgument("nom", String.class)
                        .supplier((input, env) -> input)
                        .tabCompletion((sender, env) -> List.of("<nom>")))
                .command((sender, env) -> {
                    String name = env.get("nom", String.class);
                    InventoryInstanceConfiguration config = new InventoryInstanceConfigurationBuilder()
                            .setName(name)
                            .build();
                    InventoryInstanceManager.addConfig(config);
                    sender.sendMessage(Component.text("Inventaire créé !", NamedTextColor.GREEN));
                }));

        subCommand(new CommandBuilder("remove")
                .description("Supprimer un inventaire")
                .withArg(new CommandArgument("nom", String.class)
                        .supplier((input, env) -> input)
                        .tabCompletion((sender, env) -> InventoryInstanceManager.getAllConfigs()
                                                                                .stream()
                                                                                .map(InventoryInstanceConfiguration::getName)
                                                                                .collect(Collectors.toList())))
                .command((sender, env) -> {
                    String configName = env.get("nom", String.class);
                    InventoryInstanceManager.removeConfig(configName);
                }));
    }
}
