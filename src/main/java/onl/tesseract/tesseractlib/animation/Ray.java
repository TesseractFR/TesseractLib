package onl.tesseract.tesseractlib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

public class Ray {
    BukkitTask task;

    public Ray(Particle particle, Color color, LivingEntity sender, short range, float delay, Consumer<Impact> onHit)
    {
        this(particle, color, sender, range, delay, 0.2, onHit);
    }

    public Ray(Particle particle, Color color, LivingEntity sender, short range, float delay, double hitBoxRadius, Consumer<Impact> onHit)
    {
        ParticleBuilder builder = Animation.buildParticle(particle, color, sender.getLocation());
        Location particleLocation = sender.getEyeLocation().add(sender.getLocation().getDirection());
        Vector vector = sender.getLocation().getDirection().multiply(0.3);

        task = new BukkitRunnable() {
            float i = 0;
            float timer = (delay > 0) ? 0 : (float)((float) range / 0.3);
            @Override
            public void run()
            {
                if (delay > 0)
                    timer += 1 / delay;
                while (timer >= 1) {
                    timer--;
                    particleLocation.add(vector);
                    builder.location(particleLocation);
                    builder.spawn();
                    i += 0.3;
                    if (i > range || particleLocation.getBlock().getType().isSolid()) {
                        this.cancel();
                        if (onHit != null)
                            onHit.accept(new Impact(particleLocation));
                    }
                    if (onHit == null) continue;
                    Object[] hit = particleLocation.getNearbyLivingEntities(hitBoxRadius).toArray();
                    if (hit.length > 0) {
                        LivingEntity entity = (LivingEntity) hit[0];
                        if (! entity.equals(sender)) {
                            onHit.accept(new Impact(entity, particleLocation));
                            this.cancel();
                            return;
                        }
                    }
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);
    }

    public void stop()
    {
        this.task.cancel();
    }
}
