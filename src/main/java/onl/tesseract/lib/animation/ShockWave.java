package onl.tesseract.lib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShockWave {
    ParticleBuilder builder;
    List<Float> angles;

    /**
     * Creates a shockwave animation
     * @param particle particle to use
     * @param color color of the particule if it is redstone. Null otherwise
     * @param location center of the shockwave
     * @param radius Radius of thee shockwave
     * @param delay Delay in ticks between each wave
     */
    public ShockWave(Particle particle, Color color, Location location, int radius, double delay, Consumer<Player> onHit, Plugin plugin) {
        init(particle, color, location);

        new BukkitRunnable() {
            float i = 0;
            float timer = 0;
            // This function run() will be executed every tick
            @Override
            public void run() {
                timer += 1 / delay;
                while (timer >= 1) {
                    draw(location, this.i);
                    i += 0.3;
                    if (i > radius)
                        this.cancel();
                    timer--;
                }
            }
        }.runTaskTimer(plugin, 0, 1);

        if (onHit != null)
            this.detectPlayer(location, radius, onHit);
    }

    public ShockWave(Particle particle, Color color, Location location, int radius, Consumer<Player> onHit) {
        init(particle, color, location);

        for (float i = 0; i < radius; i += 0.3) {
            draw(location, i);
        }

        if (onHit != null)
            this.detectPlayer(location, radius, onHit);
    }

    void detectPlayer(Location location, int radius, Consumer<Player> onHit) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld())) {
                if (player.getLocation().equals(location)) {
                    onHit.accept(player);
                    continue;
                }

                Vector direction = player.getLocation().subtract(location).toVector().normalize();
                RayTraceResult result = player.getWorld().rayTrace(location, direction, radius, FluidCollisionMode.NEVER, true, 1, entity -> entity.getType() == EntityType.PLAYER);
                if (result != null && result.getHitEntity() != null) {
                    Player hitPlayer = (Player) result.getHitEntity();
                    onHit.accept(hitPlayer);
                }
            }
        }
    }

    void init(Particle particle, Color color, Location location) {
        this.builder = new ParticleBuilder(particle);
        if (color != null)
            builder.color(color);
        builder.location(location);
        builder.extra(0);
        builder.receivers(50); // Every player in a radius of 50 blocks will see the animation

        this.angles = new ArrayList<>();
        for (float angle = 0; angle < Math.PI * 2; angle += 0.05)
            angles.add(angle);
    }

    /**
     * Draws a circle of radius i
     * @param location center
     * @param i Radius
     */
    public void draw(Location location, float i) {
        for (Iterator<Float> iterator = angles.iterator(); iterator.hasNext(); ) {
            float angle = iterator.next();
            Location particleLocation = location.clone().add(i * Math.cos(angle), 0, i * Math.sin(angle));
            if (! particleLocation.getBlock().getType().isSolid())
            {
                builder.location(particleLocation);
                builder.spawn();
            }
            else
            {
                iterator.remove();
            }
        }
    }
}
