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

public class SmoothCylinder {
    private Particle particle;
    private Color color;
    private Location location;
    private int radius;
    private int height;
    private boolean rev;
    private double delay;
    private Consumer<Player> onHit;

    public Animation build()
    {
        return this::draw;
    }

    private void draw()
    {
        Set<Player> players = new HashSet<>();
        int sign = rev ? -1 : 1;
        int start = rev ? height : 0;
        // Creates a runnable that will be ran each tick
        new BukkitRunnable() {
            float y = 0;
            // If there is a delay, start the timer at 0. Else, set the timer so that it can complete the animation in
            // one loop
            float timer = (delay > 0) ? 0 : (float)(height / 0.1);
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
                        .setSpacing(0.4f)
                        .draw();
                    y += 0.1;
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

    public SmoothCylinder setParticle(final Particle particle)
    {
        this.particle = particle;
        return this;
    }

    public SmoothCylinder setColor(final Color color)
    {
        this.color = color;
        return this;
    }

    public SmoothCylinder setLocation(final Location location)
    {
        this.location = location;
        return this;
    }

    public SmoothCylinder setRadius(final int radius)
    {
        this.radius = radius;
        return this;
    }

    public SmoothCylinder setHeight(final int height)
    {
        this.height = height;
        return this;
    }

    public SmoothCylinder setRev(final boolean rev)
    {
        this.rev = rev;
        return this;
    }

    public SmoothCylinder setDelay(final double delay)
    {
        this.delay = delay;
        return this;
    }

    public SmoothCylinder setOnHit(final Consumer<Player> onHit)
    {
        this.onHit = onHit;
        return this;
    }
}
