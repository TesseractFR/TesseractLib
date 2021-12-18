package onl.tesseract.tesseractlib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class Sphere extends AnimationBuilder<Sphere> {

    @Override
    public Animation build()
    {
        return this::draw;
    }

    @Override
    protected Sphere self()
    {
        return this;
    }

    private void draw()
    {
        ParticleBuilder builder = AnimationUtil.buildParticle(particle, color, target.getLocation());
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
}
