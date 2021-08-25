package onl.tesseract.tesseractlib.command;

import onl.tesseract.tesseractlib.animation.*;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Animation implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage("Nope");
                return true;
            }
            Player player = (Player)sender;
            int radius;
            double delay;
            Location loc;
            switch (args[0]) {
                case "shockwave":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    delay = (args.length >= 3) ? Double.parseDouble(args[2]) : 0;
                    loc = new Location(((Player)sender).getWorld(), 140, 71, 155);
                    if (delay > 0)
                        new ShockWave(Particle.DRIP_LAVA, null, loc, radius, delay, Animation::callbackDamage);
                    else {
                        new ShockWave(Particle.REDSTONE, Color.AQUA, player.getLocation(), radius, Animation::callbackDamage);
                    }
                    break;

                case "circle":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    delay = (args.length >= 3) ? Double.parseDouble(args[2]) : 0;
                    new Circle(Particle.REDSTONE, new AnimationTarget(player))
                            .setColor(Color.FUCHSIA)
                            .setRadius(radius)
                            .setDelay(delay)
                            .setRotationCount(4)
                            .draw();
                    break;

                case "disc":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    new Disc(Particle.REDSTONE, Color.FUCHSIA, player.getLocation(), radius);
                    break;

                case "cylinder":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    delay = (args.length >= 3) ? Double.parseDouble(args[2]) : 0;
                    new Cylinder(Particle.REDSTONE, Color.FUCHSIA, player.getLocation(), radius, 10, true, delay, Animation::callbackDamage);
                    break;

                case "ring":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    loc = new Location(((Player)sender).getWorld(), 140, 71, 155);
                    new Ring(Particle.REDSTONE, Color.FUCHSIA, loc, 3, 5, Animation::callbackDamage);
                    break;

                case "line":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    delay = (args.length >= 3) ? Double.parseDouble(args[2]) : 0;
                    //new Line(Particle.REDSTONE, Color.FUCHSIA, player.getLocation(), player.getLocation().add(10, 0, 0), 0.5f);
                    break;

                case "concentration":
                    radius = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    new Concentration(Particle.VILLAGER_HAPPY, null, new AnimationTarget(player), 5, 20);
                    break;

                case "ray":
                    int range = (args.length >= 2) ? Integer.parseInt(args[1]) : 15;
                    new Ray(Particle.REDSTONE, Color.GRAY, player, (short) range, 0.3f, Animation::callbackImpact);
                    break;

                case "cone":
                    //new Cone(Particle.REDSTONE, Color.FUCHSIA, player.getLocation(), 10, 90, 90, 0.5f, Animation::callbackDamage);
                    break;

                case "sphere":
                    new Sphere(Particle.REDSTONE, Color.FUCHSIA, new AnimationTarget(player), 4, 0.5f, 5, null);
                    break;

                case "flameRosette":
                    Particle particle = Particle.valueOf(args[1]);
                    new FlameRosette().location(player.getLocation())
                                      .particle(particle)
                                      .draw();

                default:
                    sender.sendMessage(ChatColor.RED + "Animation non trouvée");
                    break;
            }
        }
        return true;
    }

    static public void callbackDamage(Player player) {
        player.sendMessage("Tu as été touché");
    }
    static public void callbackImpact(LivingEntity entity) {
        System.out.println("touché !");
    }
    static public void callbackImpact(Impact entity) {
        System.out.println("touché !");
    }
}
