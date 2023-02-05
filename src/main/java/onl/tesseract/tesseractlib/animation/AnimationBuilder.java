package onl.tesseract.tesseractlib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.Collection;

public abstract class AnimationBuilder<T extends AnimationBuilder<T>> {
    protected Particle particle;
    protected Color color;
    protected AnimationTarget origin;
    protected float radius;
    protected double delay;
    protected float rotationCount;
    protected Consumer<LivingEntity> onHit;
    protected Vector direction = new Vector(1E-15, 1, 1E-15).normalize();
    protected boolean multipleHits;
    protected int receiverRadius;
    protected Collection<Player> receivers;

    protected abstract T self();

    public abstract Animation build();

    protected ParticleBuilder getParticleBuilder()
    {
        ParticleBuilder builder = new ParticleBuilder(particle)
                .color(color)
                .extra(0);
        if (origin.location != null)
            builder = builder.location(origin.location);
        if (receivers != null)
            builder.receivers(receivers);
        if (receiverRadius != 0)
            builder.receivers(receiverRadius);
        return builder;
    }

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

    public T setOrigin(final AnimationTarget origin)
    {
        this.origin = origin;
        return self();
    }

    public T setRadius(final float radius)
    {
        this.radius = radius;
        return self();
    }

    public T setDelay(final double delay)
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

    public T setReceiverRadius(final int receiverRadius)
    {
        this.receiverRadius = receiverRadius;
        return self();
    }

    public T setReceivers(final Collection<Player> receivers)
    {
        this.receivers = receivers;
        return self();
    }
}
