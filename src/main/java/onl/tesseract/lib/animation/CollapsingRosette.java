package onl.tesseract.lib.animation;

import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class CollapsingRosette {
    Location location;
    private Particle particle = Particle.FLAME;
    private double speed = 0.18;
    private double radius = 3.5;
    private int count = 75;
    private double time = 300;

    public CollapsingRosette location(final Location location)
    {
        this.location = location.add(0, 0.25, 0);
        return this;
    }

    public CollapsingRosette particle(final Particle particle)
    {
        this.particle = particle;
        return this;
    }

    public CollapsingRosette speed(final double speed)
    {
        this.speed = speed;
        return this;
    }

    public CollapsingRosette radius(final double radius)
    {
        this.radius = radius;
        return this;
    }

    public CollapsingRosette count(final int count)
    {
        this.count = count;
        return this;
    }

    public CollapsingRosette time(final double time)
    {
        this.time = time;
        return this;
    }

    public Animation build()
    {
        return this::draw;
    }

    public CollapsingRosette draw()
    {
        var circle = AnimationUtil.getCircle(location, radius, count);
        Vector center = location.toVector();
        var world = location.getWorld();

        new BukkitRunnable() {
            int i = 0;
            final int maxIndex = count - 1;
            final int indexStep = maxIndex / 4;
            int j = maxIndex;

            int timer = 0;

            final Location[] inward = new Location[4];
            final Location[] outward = new Location[4];
            final Vector[] inwardVector = new Vector[4];
            final Vector[] outwardVector = new Vector[4];

            @Override
            public void run()
            {
                if (maxIndex == 0)
                    this.cancel();
                for (var k = 0; k < 4; k++)
                {
                    inward[k] = circle.get((i + (k * indexStep)) % maxIndex);
                    inwardVector[k] = inward[k].toVector().subtract(center).normalize();
                }
                for (var k = 0; k < 4; k++)
                {
                    outward[k] = circle.get((j + (k * indexStep)) % maxIndex);
                    outwardVector[k] = outward[k].toVector().subtract(center).normalize();
                }

                try
                {
                    for (int k = 0; k < 4; k++)
                    {
                        world.spawnParticle(particle, inward[k], 0, -inwardVector[k].getX(), 0, -inwardVector[k].getZ(), speed);
                        world.spawnParticle(particle, outward[k], 0, -outwardVector[k].getX(), 0, -outwardVector[k].getZ(), speed);
                    }
                }catch (IllegalArgumentException e)
                {
                    TesseractLib.logger().log(Level.SEVERE, "Failed to draw animation", e);
                    this.cancel();
                }

                i = ++i % maxIndex;
                j = maxIndex - i;

                if (++timer > time)
                    this.cancel();
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);

        return this;
    }
}
