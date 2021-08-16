package onl.tesseract.tesseractlib.command.staff;

import onl.tesseract.tesseractlib.cosmetics.Cosmetic;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            return CosmeticManager.getTypes()
                         .stream().filter(sub -> sub.startsWith(args[2])).collect(Collectors.toList());

        }
        if (args.length == 4){
            return CosmeticManager.getCosmetics(args[2]).stream().map(Cosmetic::toString)
                                  .filter(sub -> sub.startsWith(args[3])).collect(Collectors.toList());
        }
        return null;
    }
}
