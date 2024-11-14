package onl.tesseract.lib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Spiral {
    ParticleBuilder builder;
    Particle particle;
    Color color = null;
    AnimationTarget target;
    float startRadius = 2;
    float finalRadius = 2;
    double delay = 1;
    double length = 5;
    Consumer<LivingEntity> onHit;
    Consumer<Void> onFinish;
    Consumer<Void> onDraw;
    float spacing = 0.02f;
    int originCount = 1;
    boolean multipleHits = false;

    Vector direction = new Vector(1E-15, 1, 1E-15).normalize();

    /**
     * Creates a circle. Can be drawn with Circle#drawn
     * @param particle PArticle to show
     * @param target Target of the animation
     */
    public Spiral(Particle particle, AnimationTarget target)
    {
        this.particle = particle;
        this.target = target;
    }

    public Spiral setParticle(Particle particle)
    {
        this.particle = particle;
        return this;
    }

    public Spiral setColor(Color color)
    {
        this.color = color;
        return this;
    }

    public Spiral setOriginCount(int originCount) {
        this.originCount = originCount;
        return this;
    }

    public Spiral setTarget(AnimationTarget target) {
        this.target = target;
        return this;
    }

    public Spiral setStartRadius(float startRadius) {
        this.startRadius = startRadius;
        return this;
    }

    public Spiral setFinalRadius(float finalRadius) {
        this.finalRadius = finalRadius;
        return this;
    }

    /**
     * Sets the delay in tick between each particle.
     * @param delay Delay in tick.
     * @return the circle
     */
    public Spiral setDelay(double delay) {
        this.delay = delay;
        return this;
    }

    public Spiral setLength(double length) {
        this.length = length;
        return this;
    }

    public Spiral setOnHit(Consumer<LivingEntity> onHit) {
        this.onHit = onHit;
        return this;
    }

    public Spiral setOnDraw(Consumer<Void> onDraw) {
        this.onDraw = onDraw;
        return this;
    }

    public Spiral setOnFinish(Consumer<Void> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    /**
     * Sets the distance between two particles
     * @param spacing distance in block
     * @return this
     */
    public Spiral setSpacing(float spacing) {
        this.spacing = spacing;
        return this;
    }

    /**
     * Sets the duration of the animation. Will alter one of the following parameter : speed, length.
     * @param duration Duration in tick.
     * @param increaseSpeed True to adapt the speed. False to adapt the length.
     * @return this circle.
     */
    public Spiral setDuration(float duration, boolean increaseSpeed)
    {
        if (increaseSpeed)
            this.setDelay((spacing * duration) / (length));
        else
            this.length = (duration / delay) * spacing;
        return this;
    }

    public void draw()
    {
        builder = AnimationUtil.buildParticle(particle, color, target.getLocation());
        // Set containing entities that have been hit
        Set<LivingEntity> hits = new HashSet<>();
        double radiusStep = spacing * (finalRadius - startRadius) / length;
        // Creates a runnable that will be ran each tick
        new BukkitRunnable() {
            // Current angle
            float angle = 0;
            // Timer that determine how many particle to print each tick. In case of non-null delay, will be computed
            // each tick. Otherwise, it is computed so that the entire circle is drawn is one tick.
            // Ex: If delay = 0.1 (0.1 tick per particle), then 10 particles will be drawn per tick.
            float timer = (delay > 0) ? 0 : (float)(length / spacing);
            double i = 0;
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
                        Vector v = c.rotateAroundAxis(direction, currentAngle).normalize().multiply(startRadius);
                        startRadius += radiusStep;

                        Location particeLocation = target.getLocation().add(direction.clone().multiply(i)).add(v);
                        builder.location(particeLocation);
                        builder.receivers(50);
                        builder.spawn();
                    }
                    if (onDraw != null)
                        onDraw.accept(null);

                    angle += 0.05f;
                    i += spacing;
                    if (i > length) {
                        this.cancel();
                        if (onFinish != null)
                            onFinish.accept(null);
                        break;
                    }
                }
                if (onHit != null) {
                    target.getLocation().add(direction.clone().multiply(i)).getNearbyLivingEntities(startRadius).forEach(entity -> {
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
    }

    public Spiral setDirection(Vector direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Determines if an entity can be hit multiple times. If true, the entity will be hit every 10 ticks will
     * in the radius of the circle.
     * @param multipleHits True if an entity can be hit multiple times.
     * @return this
     */
    public Spiral setMultipleHits(boolean multipleHits) {
        this.multipleHits = multipleHits;
        return this;
    }
}
