package onl.tesseract.lib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.annotations.Beta;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

public class Disc {
    public Disc(Particle particle, Color color, Location location, int radius)
    {
        ParticleBuilder builder = AnimationUtil.buildParticle(particle, color, location);
        for (float angle = 0; angle < Math.PI * 2; angle += 0.05)
        {
            for (float dist = 0; dist < radius; dist += 0.3)
            {
                Location particeLocation = location.clone().add(dist * Math.cos(angle), 0, dist * Math.sin(angle));
                builder.location(particeLocation);
                builder.spawn();
            }
        }
    }

    @Beta
    public Disc(Particle particle, Color color, Location location, int radius, double delay, float rotationCount, Consumer<LivingEntity> onHit, Plugin plugin)
    {
        ParticleBuilder builder = AnimationUtil.buildParticle(particle, color, location);
        final float maxAngle = (float)(Math.PI * 2 * rotationCount);
        new BukkitRunnable() {
            float angle = 0;
            float timer = (delay > 0) ? 0 : (float)((Math.PI / 0.05) * 2) + 1;
            @Override
            public void run()
            {
                if (delay > 0)
                    timer += 1 / delay;
                while (timer >= 1) {
                    timer--;
                    for (float i = 0; i < radius; i += 0.3) {
                        Location particeLocation = location.clone().add(i * Math.cos(angle), 0, i * Math.sin(angle));
                        builder.location(particeLocation);
                        builder.spawn();
                    }
                    angle += 0.05;
                    if (angle > maxAngle)
                        this.cancel();
                }
                if (onHit != null)
                    location.getNearbyLivingEntities(radius).forEach(onHit::accept);
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
