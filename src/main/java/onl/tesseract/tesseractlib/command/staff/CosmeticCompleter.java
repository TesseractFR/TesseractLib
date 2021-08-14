package onl.tesseract.tesseractlib.command.staff;

import onl.tesseract.tesseractlib.cosmetics.CosmeticType;
import onl.tesseract.tesseractlib.cosmetics.ElytraTrails;
import onl.tesseract.tesseractlib.familier.Pet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CosmeticCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] args)
    {
        if (args.length == 1)
            return List.of("give", "remove")
                       .stream().filter(sub -> sub.startsWith(args[0])).collect(Collectors.toList());
        if (args.length == 3)
        {
            return Arrays.stream(CosmeticType.values()).toList().stream().map(CosmeticType::toString)
                         .collect(Collectors.toList())
                         .stream().filter(sub -> sub.startsWith(args[2])).collect(Collectors.toList());

        }
        if (args.length == 4){
            CosmeticType type;
            try
            {
                type=CosmeticType.valueOf(args[2]);
            }
            catch (IllegalArgumentException e){
                return  List.of("");
            }
            switch (type){

                case PET -> {
                    return Arrays.stream(Pet.values()).toList().stream().map(Pet::toString)
                                 .collect(Collectors.toList())
                                 .stream().filter(sub -> sub.startsWith(args[3])).collect(Collectors.toList());
                }
                case ELYTRA_TRAIL -> {
                    return Arrays.stream(ElytraTrails.values()).toList().stream().map(ElytraTrails::toString)
                            .collect(Collectors.toList()).stream().filter(sub -> sub.startsWith(args[3])).collect(
                                    Collectors.toList());
                }
            }
        }
        return null;
    }
}
