package onl.tesseract.tesseractlib.animation;

import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FlameRosette {
    Location location;
    private Particle particle;

    public FlameRosette location(final Location location)
    {
        this.location = location.add(0, 0.25, 0);
        return this;
    }

    public FlameRosette particle(final Particle particle)
    {
        this.particle = particle;
        return this;
    }

    public FlameRosette draw()
    {
        var circle = Animation.getCircle(location, 3.5, 75);
        Vector center = location.toVector();
        var world = location.getWorld();

        new BukkitRunnable() {
            int i = 0;
            int j = 74;

            int timer = 0;

            final Location[] inward = new Location[4];
            final Location[] outward = new Location[4];
            final Vector[] inwardVector = new Vector[4];
            final Vector[] outwardVector = new Vector[4];

            @Override
            public void run()
            {
                for (var k = 0; k < 4; k++)
                {
                    inward[k] = circle.get((i + (k * 18)) % 74);
                    inwardVector[k] = inward[k].toVector().subtract(center).normalize();
                }
                for (var k = 0; k < 4; k++)
                {
                    outward[k] = circle.get((j + (k * 18)) % 74);
                    outwardVector[k] = outward[k].toVector().subtract(center).normalize();
                }

                try
                {
                    for (int k = 0; k < 4; k++)
                    {
                        world.spawnParticle(particle, inward[k], 0, -inwardVector[k].getX(), 0, -inwardVector[k].getZ(), 0.2);
                        world.spawnParticle(particle, outward[k], 0, -outwardVector[k].getX(), 0, -outwardVector[k].getZ(), 0.2);
                    }
                }catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                    this.cancel();
                }

                i = ++i % 74;
                j = 74 - i;

                if (++timer > 300)
                    this.cancel();
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);

        return this;
    }
}
