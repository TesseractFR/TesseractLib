package onl.tesseract.tesseractlib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

public class Sphere {
    private Particle particle;
    private Color color;
    private AnimationTarget target;
    private float radius;
    private float delay;
    private float rotationCount;
    private Consumer<Player> onHit;

    public Animation build()
    {
        return this::draw;
    }

    private void draw()
    {
        ParticleBuilder builder = Animation.buildParticle(particle, color, target.getLocation());
        // Creates a runnable that will be ran each tick
        new BukkitRunnable() {
            float rho = 0;
            float timer = (delay > 0) ? 0 : (float)((Math.PI / 0.05) * 2 * rotationCount);
            @Override
            public void run()
            {
                if (target.hasExpired())
                    this.cancel();
                if (delay > 0)
                    timer += 1 / delay;
                while (timer >= 1) {
                    timer--;
                    // For each angle
                    for (float theta = 0; theta < Math.PI * 2; theta += 0.05)
                    {
                        double x = Math.cos(rho) * Math.cos(theta) * radius;
                        double z = Math.sin(rho) * Math.cos(theta) * radius;
                        double y = Math.sin(theta) * radius;
                        Location particleLocation = target.getLocation().clone().add(x, y, z);
                        builder.location(particleLocation);
                        builder.spawn();
                    }

                    rho += 0.05;
                    if (rho > Math.PI * 2 * rotationCount)
                        this.cancel();
                }
                // Detect players
                if (onHit != null)
                    target.getLocation().getNearbyPlayers(radius).forEach(onHit::accept);
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);
    }

    public Sphere setParticle(final Particle particle)
    {
        this.particle = particle;
        return this;
    }

    public Sphere setColor(final Color color)
    {
        this.color = color;
        return this;
    }

    public Sphere setTarget(final AnimationTarget target)
    {
        this.target = target;
        return this;
    }

    public Sphere setRadius(final float radius)
    {
        this.radius = radius;
        return this;
    }

    public Sphere setDelay(final float delay)
    {
        this.delay = delay;
        return this;
    }

    public Sphere setRotationCount(final float rotationCount)
    {
        this.rotationCount = rotationCount;
        return this;
    }

    public Sphere setOnHit(final Consumer<Player> onHit)
    {
        this.onHit = onHit;
        return this;
    }
}
