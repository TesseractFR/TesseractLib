package onl.tesseract.tesseractlib.animation;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

public abstract class AnimationBuilder<T extends AnimationBuilder<T>> {
    protected Particle particle;
    protected Color color;
    protected AnimationTarget target;
    protected float radius;
    protected float delay;
    protected float rotationCount;
    protected Consumer<LivingEntity> onHit;
    protected Vector direction = new Vector(1E-15, 1, 1E-15).normalize();;
    protected boolean multipleHits;

    protected abstract T self();

    public abstract Animation build();

    public T setParticle(final Particle particle)
    {
        this.particle = particle;
        return self();
    }

    public T setColor(final Color color)
    {
        this.color = color;
        return self();
    }

    public T setTarget(final AnimationTarget target)
    {
        this.target = target;
        return self();
    }

    public T setRadius(final float radius)
    {
        this.radius = radius;
        return self();
    }

    public T setDelay(final float delay)
    {
        this.delay = delay;
        return self();
    }

    public T setRotationCount(final float rotationCount)
    {
        this.rotationCount = rotationCount;
        return self();
    }

    public T setOnHit(final Consumer<LivingEntity> onHit)
    {
        this.onHit = onHit;
        return self();
    }

    public T setDirection(final Vector direction)
    {
        this.direction = direction;
        return self();
    }

    public T setMultipleHits(final boolean multipleHits)
    {
        this.multipleHits = multipleHits;
        return self();
    }
}
