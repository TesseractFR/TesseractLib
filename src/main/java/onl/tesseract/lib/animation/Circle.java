package onl.tesseract.lib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Circle extends AnimationBuilder<Circle> {
    private final Plugin plugin;
    ParticleBuilder builder;
    Consumer<Void> onFinish;
    Consumer<Circle> onDraw;
    float spacing = 0.05f;
    int originCount = 1;
    BukkitTask task;
    private float radius;
    private double delay;
    private float rotationCount;
    private Vector direction = new Vector(1E-15, 1, 1E-15).normalize();

    /**
     * Creates a circle. Can be drawn with Circle#drawn
     * @param particle PArticle to show
     * @param target Target of the animation
     */
    public Circle(Particle particle, AnimationTarget target, Plugin plugin)
    {
        this.plugin = plugin;
        this.particle = particle;
        this.origin = target;
    }

    @Override
    protected Circle self()
    {
        return this;
    }

    public Circle setOriginCount(int originCount) {
        this.originCount = originCount;
        return this;
    }

    public Circle setOnDraw(Consumer<Circle> onDraw) {
        this.onDraw = onDraw;
        return this;
    }

    public Circle setOnFinish(Consumer<Void> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    /**
     * Sets the angular distance between each particle
     * @param spacing Angle in radian
     * @return this
     */
    public Circle setSpacing(float spacing) {
        this.spacing = spacing;
        return this;
    }

    /**
     * Sets the duration of the animation. Will alter one of the following parameter : speed, number of rotations.
     * @param duration Duration in tick.
     * @param increaseSpeed True to adapt the speed. False to adapt the number of rotation.
     * @return this circle.
     */
    public Circle setDuration(float duration, boolean increaseSpeed)
    {
        if (increaseSpeed)
            this.setDelay((float) ((spacing * duration) / (2 * Math.PI * rotationCount)));
        else
            this.setRotationCount((float)(((duration / delay) * spacing) / (2* Math.PI)));
        return this;
    }

    public Circle setRotationCount(final float rotationCount)
    {
        this.rotationCount = rotationCount;
        return this;
    }

    public Circle setDelay(final float delay)
    {
        this.delay = delay;
        return this;
    }

    public Circle setRadius(final float radius)
    {
        this.radius = radius;
        return this;
    }

    @Override
    public Animation build()
    {
        return this::draw;
    }

    public Circle draw()
    {
        builder = getParticleBuilder();
        // Compute the maximum angle to reach
        final float maxAngle = (float)(Math.PI * 2 * rotationCount);
        // Set containing entities that have been hit
        Set<LivingEntity> hits = new HashSet<>();
        // Creates a runnable that will be ran each tick
        Circle that = this;
        task = new BukkitRunnable() {
            // Current angle, from 0 to maxAngle
            float angle = 0;
            // Timer that determine how many particle to print each tick. In case of non-null delay, will be computed
            // each tick. Otherwise, it is computed so that the entire circle is drawn is one tick.
            // Ex: If delay = 0.1 (0.1 tick per particle), then 10 particles will be drawn per tick.
            float timer = (delay > 0) ? 0 : (float)((Math.PI / spacing) * 2 * rotationCount) + 1;
            @Override
            public void run()
            {
                // Recompute the number of particle to draw this tick
                if (delay > 0)
                    timer += 1 / delay;
                // Angular distance between each drawing origins
                double originSpacing = (2*Math.PI) / originCount;
                // Print <timer> particle
                while (timer >= 1) {
                    timer--;
                    for (int k = 0; k < originCount; k++)
                    {
                        double currentAngle = angle + k * originSpacing;
                        Vector p = new Vector(- direction.getZ(), direction.getY(), direction.getX());
                        Vector c = p.crossProduct(direction);
                        Vector v = c.rotateAroundAxis(direction, currentAngle).normalize().multiply(radius);

                        Location particeLocation = origin.getLocation().add(v);
                        builder.location(particeLocation);
                        builder.receivers(50);
                        builder.spawn();
                    }

                    angle += spacing;
                    if (angle > maxAngle) {
                        this.cancel();
                        if (onFinish != null)
                            onFinish.accept(null);
                        break;
                    }
                }

                if (onDraw != null)
                    onDraw.accept(that);

                if (onHit != null) {
                    origin.getLocation().getNearbyLivingEntities(radius).forEach(entity -> {
                        if (hits.add(entity)) {
                            onHit.accept(entity);
                            if (multipleHits) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        hits.remove(entity);
                                    }
                                }.runTaskLater(plugin, 10);
                            }
                        }
                    });
                }
            }
        }.runTaskTimer(plugin, 0, 1);
        return this;
    }

    public void stop()
    {
        if (task != null)
        {
            task.cancel();
            task = null;
        }
    }

    public Circle setDelay(final double delay)
    {
        this.delay = delay;
        return this;
    }

    public Circle setDirection(final Vector direction)
    {
        this.direction = direction;
        return this;
    }
}
