package onl.tesseract.tesseractlib.animation;

import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class Star {
    AnimationTarget target;
    private Particle particle = Particle.FLAME;
    private double radius = 2.1;
    private int angleCount = 5;
    private double time = 300;
    private Color color;

    public Star target(final AnimationTarget target)
    {
        this.target = target;
        return this;
    }

    public Star particle(final Particle particle)
    {
        this.particle = particle;
        return this;
    }

    public Star radius(final double radius)
    {
        this.radius = radius;
        return this;
    }

    public Star angleCount(final int count)
    {
        this.angleCount = count;
        return this;
    }

    public Star time(final double time)
    {
        this.time = time;
        return this;
    }

    public Star color(final Color color)
    {
        this.color = color;
        return this;
    }

    public Star draw()
    {
        new BukkitRunnable() {
            int timer = 0;
            final double angleStep = Math.PI * 2d / angleCount;

            @Override
            public void run()
            {
                if (target.hasExpired())
                {
                    cancel();
                    return;
                }

                Location[] points = new Location[angleCount];
                for (int k = 0; k < angleCount; k++)
                    points[k] = target.getLocation().clone().add(radius * Math.cos(k * angleStep), 0,
                                                                 radius * Math.sin(k * angleStep));
                for (int k = 0; k < angleCount; k++)
                {
                    try
                    {
                        new Line(particle, points[k], points[(k + 2) % angleCount])
                                .setColor(color)
                                .setDelay(0)
                                .draw();
                        new Line(particle, points[k], points[(k + 3) % angleCount])
                                .setColor(color)
                                .setDelay(0)
                                .draw();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        cancel();
                    }
                }

                if (++timer >= time)
                    this.cancel();
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);
        return this;
    }
}
