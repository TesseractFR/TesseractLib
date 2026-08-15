package onl.tesseract.lib.animation;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Concentration {
    private Particle particle;
    private Color color;
    private AnimationTarget target;
    private int radius;
    private int count;
    private final Plugin plugin;

    public Concentration(Plugin plugin) {
        this.plugin = plugin;
    }

    private void draw()
    {
        new BukkitRunnable() {
            int current = 0;
            @Override
            public void run()
            {
                double rho = (Math.random() * Math.PI * 2);
                double theta = (Math.random() * Math.PI * 2);
                double x = Math.cos(rho) * Math.cos(theta) * radius;
                double z = Math.sin(rho) * Math.cos(theta) * radius;
                double y = Math.sin(theta) * radius;
                Location source = target.getLocation().add(x, y, z);
                new Line(particle, source, target.getLocation(), plugin).setColor(color).setDelay(1);

                current++;
                if (this.current == count)
                    this.cancel();
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    public Animation build()
    {
        return this::draw;
    }

    public Concentration setParticle(final Particle particle)
    {
        this.particle = particle;
        return this;
    }

    public Concentration setColor(final Color color)
    {
        this.color = color;
        return this;
    }

    public Concentration setTarget(final AnimationTarget target)
    {
        this.target = target;
        return this;
    }

    public Concentration setRadius(final int radius)
    {
        this.radius = radius;
        return this;
    }

    public Concentration setCount(final int count)
    {
        this.count = count;
        return this;
    }
}
