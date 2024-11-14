package onl.tesseract.lib.command;

import onl.tesseract.tesseractlib.animation.*;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Animation implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (sender instanceof Player player)
        {
            if (args.length == 0)
            {
                sender.sendMessage("Nope");
                return true;
            }
            int radius;
            double delay;
            Location loc;
            Particle particle;
            switch (args[0])
            {
                case "shockwave":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    delay = (args.length >= 3) ? Double.parseDouble(args[2]) : 0;
                    loc = new Location(((Player) sender).getWorld(), 140, 71, 155);
                    if (delay > 0)
                        new ShockWave(Particle.DRIPPING_LAVA, null, loc, radius, delay, Animation::callbackDamage);
                    else
                    {
                        new ShockWave(Particle.DUST, Color.AQUA, player.getLocation(), radius, Animation::callbackDamage);
                    }
                    break;

                case "circle":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    delay = (args.length >= 3) ? Double.parseDouble(args[2]) : 0;
                    new Circle(Particle.DUST, new AnimationTarget(player))
                            .setColor(Color.FUCHSIA)
                            .setRadius(radius)
                            .setDelay(delay)
                            .setRotationCount(4)
                            .draw();
                    break;

                case "disc":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    new Disc(Particle.DUST, Color.FUCHSIA, player.getLocation(), radius);
                    break;

                case "cylinder":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    delay = (args.length >= 3) ? Double.parseDouble(args[2]) : 0;
//                    new Cylinder(Particle.REDSTONE, Color.FUCHSIA, player.getLocation(), radius, 10, true, delay, Animation::callbackDamage);
                    break;

                case "ring":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    loc = new Location(((Player) sender).getWorld(), 140, 71, 155);
                    new Ring(Particle.DUST, Color.FUCHSIA, loc, 3, 5, Animation::callbackDamage);
                    break;

                case "line":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    delay = (args.length >= 3) ? Double.parseDouble(args[2]) : 0;
                    //new Line(Particle.REDSTONE, Color.FUCHSIA, player.getLocation(), player.getLocation().add(10, 0, 0), 0.5f);
                    break;

                case "concentration":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
//                    new Concentration(Particle.VILLAGER_HAPPY, null, new AnimationTarget(player), 5, 20);
                    break;

                case "ray":
                    int range = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    new Ray(Particle.DUST, Color.GRAY, player, (short) range, 0.3f, Animation::callbackImpact);
                    break;

                case "cone":
                    //new Cone(Particle.REDSTONE, Color.FUCHSIA, player.getLocation(), 10, 90, 90, 0.5f, Animation::callbackDamage);
                    break;

                case "sphere":
//                    new Sphere(Particle.REDSTONE, Color.FUCHSIA, new AnimationTarget(player), 4, 0.5f, 5, null);
                    break;

                case "rosette":
                    particle = Particle.valueOf(args[1]);
                    new CollapsingRosette().location(player.getLocation())
                                           .particle(particle)
                                           .speed(Double.parseDouble(args[2]))
                                           .radius(Double.parseDouble(args[3]))
                                           .count(Integer.parseInt(args[4]))
                                           .time(Double.parseDouble(args[5]))
                                           .draw();

                    break;

                case "star":
                    particle = Particle.valueOf(args[1]);
                    new Star().target(args[6].equals("follow") ? new AnimationTarget(player) : new AnimationTarget(player.getLocation()))
                              .particle(particle)
                              .color(args[2].equals("null") ? null : Color.fromRGB(Integer.parseInt(args[2], 16)))
                              .radius(Double.parseDouble(args[3]))
                              .angleCount(Integer.parseInt(args[4]))
                              .time(Double.parseDouble(args[5]))
                              .draw();

                    break;

                default:
                    sender.sendMessage(ChatColor.RED + "Animation non trouvée");
                    break;
            }
        }
        return true;
    }

    static public void callbackDamage(Player player)
    {
        player.sendMessage("Tu as été touché");
    }

    static public void callbackImpact(LivingEntity entity)
    {
        System.out.println("touché !");
    }

    static public void callbackImpact(Impact entity)
    {
        System.out.println("touché !");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias,
                                                final @NotNull String[] args)
    {
        if (args.length == 1)
            return List.of("rosette", "star");
        if (args[0].equals("rosette"))
        {
            switch (args.length)
            {
                case 2:
                    return Arrays.stream(Particle.values()).map(Objects::toString)
                                 .filter(s -> s.startsWith(args[1]))
                                 .collect(Collectors.toList());
                case 3:
                    return List.of("speed");
                case 4:
                    return List.of("radius");
                case 5:
                    return List.of("count");
                case 6:
                    return List.of("time");
            }
        }
        if (args[0].equals("star"))
        {
            switch (args.length)
            {
                case 2:
                    return Arrays.stream(Particle.values()).map(Objects::toString)
                                 .filter(s -> s.startsWith(args[1]))
                                 .collect(Collectors.toList());
                case 3:
                    return List.of("color (rgb hex. ex: 'ff0012')");
                case 4:
                    return List.of("radius");
                case 5:
                    return List.of("count");
                case 6:
                    return List.of("time");
                case 7:
                    return List.of("follow", "fix");
            }
        }

        return null;
    }
}
