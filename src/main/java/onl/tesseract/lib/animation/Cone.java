package onl.tesseract.lib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.lib.util.Util;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.util.*;

public class Cone {
    Particle particle;
    final Location origin;
    Color color;
    int radius;
    double openingAngle;
    float direction;
    float delay;
    Consumer<Player> onHit;

    public Cone(Particle particle, Location origin)
    {
        this.particle = particle;
        this.origin = origin;
    }

    public Cone setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public Cone setColor(Color color) {
        this.color = color;
        return this;
    }

    public Cone setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public Cone setOpeningAngle(double openingAngle) {
        this.openingAngle = openingAngle;
        return this;
    }

    public Cone setDirection(float direction) {
        this.direction = direction;
        return this;
    }

    public Cone setDelay(float delay) {
        this.delay = delay;
        return this;
    }

    public Cone setOnHit(Consumer<Player> onHit) {
        this.onHit = onHit;
        return this;
    }

    public Animation build()
    {
        return this::draw;
    }

    public void draw()
    {
        ParticleBuilder builder = AnimationUtil.buildParticle(particle, color, origin);
        // Cast angles in degree to radian
        openingAngle = (float)(Math.toRadians(openingAngle) / 2);
        direction = (float)Math.toRadians(direction);
        // Register all angles at which to draw lines
        Collection<Double> angles = new ArrayList<>();
        for (double theta = direction - openingAngle; theta <= direction + openingAngle; theta += 0.05)
            angles.add(theta);
        // Set of hit players
        Set<Player> hit = new HashSet<>();

        // Creates a runnable that will be ran each tick
        new BukkitRunnable() {
            float dist = 0;
            float timer = (delay > 0) ? 0 : (float)((dist / 0.3));
            @Override
            public void run()
            {
                if (delay > 0)
                    timer += 1 / delay;
                while (timer >= 1) {
                    timer--;
                    // For each angle
                    for (Iterator<Double> iterator = angles.iterator(); iterator.hasNext(); ) {
                        double theta = iterator.next();
                        Location particleLocation = origin.clone().add(dist * Math.cos(theta), 0, dist * Math.sin(theta));
                        if (! particleLocation.getBlock().getType().isSolid())
                        {
                            builder.location(particleLocation);
                            builder.spawn();
                        } else // Block the wave if there is an obstacle
                            iterator.remove();
                        if (onHit != null) {
                            origin.getWorld().getPlayers().forEach(player -> {
                                if (! hit.contains(player) && Util.Locations.flatDistance(player.getLocation(), particleLocation) < 1 && Util.isNear(player.getLocation().getY(), particleLocation.getY(), 3)) {
                                    hit.add(player);
                                    onHit.accept(player);
                                }
                            });
                        }
                    }

                    dist += 0.3;
                    if (dist > radius)
                        this.cancel();
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);
    }
}
