package onl.tesseract.tesseractlib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

public class Line {
    Particle particle;
    Color color;
    final Location source;
    final Location dest;
    float delay = 1;
    double spacing = 0.3f;
    Consumer<Void> onFinish;
    double offset = 0;

    public Line setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public Line setColor(Color color) {
        this.color = color;
        return this;
    }

    public Line setDelay(float delay) {
        this.delay = delay;
        return this;
    }

    public Line setSpacing(double spacing) {
        this.spacing = spacing;
        return this;
    }

    public Line setOnFinish(Consumer<Void> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public Line setDuration(int duration)
    {
        this.setDelay((float)((spacing * duration) / dest.distance(source)));
        return this;
    }

    public Line setOffset(double offset) {
        this.offset = offset;
        return this;
    }

    public Line(Particle particle, Location source, Location dest)
    {
        this.particle = particle;
        this.source = source;
        this.dest = dest;
    }

    public Line draw()
    {
        ParticleBuilder builder = Animation.buildParticle(particle, color, dest);
        Vector vector = dest.clone().subtract(source).toVector().normalize().multiply(spacing);
        float distance = (float)(dest.distance(source));

        new BukkitRunnable() {
            float i = 0;
            float timer = (delay > 0) ? 0 : (float)(distance / spacing) + 1;
            final Location particleLocation = source.clone();
            @Override
            public void run()
            {
                if (delay > 0)
                    timer += 1 / delay;
                while (timer >= 1) {
                    timer--;
                    particleLocation.add(vector);
                    builder.location(particleLocation);
                    builder.offset(offset, offset, offset);
                    builder.spawn();
                    i += spacing;
                    if (i > distance) {
                        this.cancel();
                        if (onFinish != null)
                            onFinish.accept(null);
                        return;
                    }
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);
        return this;
    }
}
