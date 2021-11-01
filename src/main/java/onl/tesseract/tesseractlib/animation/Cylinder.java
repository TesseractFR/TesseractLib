package onl.tesseract.tesseractlib.animation;

import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.util.HashSet;
import java.util.Set;

public class Cylinder {
    public Cylinder(Particle particle, Color color, Location location, int radius, int height, boolean rev, double delay, Consumer<Player> onHit)
    {
        Set<Player> players = new HashSet<>();
        int sign = rev ? -1 : 1;
        int start = rev ? height : 0;
        // Creates a runnable that will be ran each tick
        new BukkitRunnable() {
            float y = 0;
            // If there is a delay, start the timer at 0. Else, set the timer so that it can complete the animation in
            // one loop
            float timer = (delay > 0) ? 0 : (float)(height / 0.3);
            @Override
            public void run()
            {
                // Increase the timer. If it get to 1, draw a circle
                if (delay > 0)
                    timer += 1 / delay;
                while (timer >= 1) {
                    timer--;
                    Location circleLocation = location.clone().add(0, y * sign + start, 0);
                    new Circle(particle, new AnimationTarget(circleLocation)).setColor(color).setRadius(radius).setDelay(0).setRotationCount(1)
                            .draw();
                    y += 0.3;
                    if (y > height)
                        this.cancel();

                    if (onHit != null) {
                        circleLocation.getNearbyPlayers(radius).forEach(player -> {
                            if (player.getLocation().getBlockY() == circleLocation.getBlockY()) {
                                if (! players.contains(player)) {
                                    onHit.accept(player);
                                    players.add(player);
                                }
                            }
                        });
                    }
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);
    }
}
