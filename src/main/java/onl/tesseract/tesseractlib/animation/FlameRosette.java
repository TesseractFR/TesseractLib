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

            @Override
            public void run()
            {
                var v1 = circle.get(i).toVector().subtract(center).normalize();
                var v2 = circle.get((i + 37) % 74).toVector().subtract(center).normalize();
                var v3 = circle.get(j).toVector().subtract(center).normalize();
                var v4 = circle.get((j + 37) % 74).toVector().subtract(center).normalize();
                var v5 = circle.get((j + 18) % 74).toVector().subtract(center).normalize();
                var v6 = circle.get((i + 18) % 74).toVector().subtract(center).normalize();
                var v7 = circle.get((j + 56) % 74).toVector().subtract(center).normalize();
                var v8 = circle.get((i + 56) % 74).toVector().subtract(center).normalize();

                try
                {
                    world.spawnParticle(particle, circle.get(i), 0, -v1.getX(), 0, -v1.getZ(), 0.2);
                    world.spawnParticle(particle, circle.get((i + 37) % 74), 0, -v2.getX(), 0, -v2.getZ(), 0.2);
                    world.spawnParticle(particle, circle.get(j), 0, -v3.getX(), 0, -v3.getZ(), 0.2);
                    world.spawnParticle(particle, circle.get((j + 37) % 74), 0, -v4.getX(), 0, -v4.getZ(), 0.2);
                    world.spawnParticle(particle, circle.get((j + 18) % 74), 0, -v5.getX(), 0, -v5.getZ(), 0.2);
                    world.spawnParticle(particle, circle.get((i + 18) % 74), 0, -v6.getX(), 0, -v6.getZ(), 0.2);
                    world.spawnParticle(particle, circle.get((j + 56) % 74), 0, -v7.getX(), 0, -v7.getZ(), 0.2);
                    world.spawnParticle(particle, circle.get((i + 56) % 74), 0, -v8.getX(), 0, -v8.getZ(), 0.2);
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
