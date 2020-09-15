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

import java.util.HashSet;

public class Circle {
    ParticleBuilder builder;
    Particle particle;
    Color color = null;
    AnimationTarget target;
    float radius = 2;
    double delay = 1;
    float rotationCount = 1;
    Consumer<LivingEntity> onHit;
    Consumer<Void> onFinish;
    Consumer<Circle> onDraw;
    float spacing = 0.05f;
    int originCount = 1;
    boolean multipleHits = false;
    BukkitTask task;

    Vector direction = new Vector(1E-15, 1, 1E-15).normalize();

    /**
     * Creates a circle. Can be drawn with Circle#drawn
     * @param particle PArticle to show
     * @param target Target of the animation
     */
    public Circle(Particle particle, AnimationTarget target)
    {
        this.particle = particle;
        this.target = target;
    }

    public Circle setParticle(Particle particle)
    {
        this.particle = particle;
        return this;
    }

    public Circle setColor(Color color)
    {
        this.color = color;
        return this;
    }

    public Circle setOriginCount(int originCount) {
        this.originCount = originCount;
        return this;
    }

    public Circle setTarget(AnimationTarget target) {
        this.target = target;
        return this;
    }

    public Circle setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    /**
     * Sets the delay in tick between each particle.
     * @param delay Delay in tick.
     * @return the circle
     */
    public Circle setDelay(double delay) {
        this.delay = delay;
        return this;
    }

    public Circle setRotationCount(float rotationCount) {
        this.rotationCount = rotationCount;
        return this;
    }

    public Circle setOnHit(Consumer<LivingEntity> onHit) {
        this.onHit = onHit;
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
            this.setDelay((spacing * duration) / (2 * Math.PI * rotationCount));
        else
            this.setRotationCount((float)(((duration / delay) * spacing) / (2* Math.PI)));
        return this;
    }

    public Circle draw()
    {
        builder = Animation.buildParticle(particle, color, target.getLocation());
        // Compute the maximum angle to reach
        final float maxAngle = (float)(Math.PI * 2 * rotationCount);
        // Set containing entities that have been hit
        HashSet<LivingEntity> hits = new HashSet<>();
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

                        Location particeLocation = target.getLocation().add(v);
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
                    target.getLocation().getNearbyLivingEntities(radius).forEach(entity -> {
                        if (hits.add(entity)) {
                            onHit.accept(entity);
                            if (multipleHits) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        hits.remove(entity);
                                    }
                                }.runTaskLater(TesseractLib.instance, 10);
                            }
                        }
                    });
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);
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

    public Circle setDirection(Vector direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Determines if an entity can be hit multiple times. If true, the entity will be hit every 10 ticks will
     * in the radius of the circle.
     * @param multipleHits True if an entity can be hit multiple times.
     * @return this
     */
    public Circle setMultipleHits(boolean multipleHits) {
        this.multipleHits = multipleHits;
        return this;
    }
}
