package onl.tesseract.tesseractlib.animation;

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

public class Cone3D {
    Particle particle;
    Color color = null;
    Location origin;
    double length = 5;
    double openingAngle = Math.PI/4;
    double delay = 1;
    Vector direction;
    float baseRadius = 0;
    Consumer<LivingEntity> onHit;
    Consumer<Void> onFinish;

    public Cone3D(Particle particle, Location origin)
    {
        this.particle = particle;
        this.origin = origin;
        this.direction = origin.getDirection();
    }

    public void draw()
    {
        Vector vector = direction.multiply(0.3);
        Set<LivingEntity> hits = new HashSet<>();

        new BukkitRunnable() {
            float i = 0;
            float timer = (delay > 0) ? 0 : (float)(length / 0.3);
            final Location particleLocation = origin.clone();
            @Override
            public void run()
            {
                if (delay > 0)
                    timer += 1 / delay;
                while (timer >= 1) {
                    timer--;
                    particleLocation.add(vector);
                    Circle circle = new Circle(particle, new AnimationTarget(particleLocation))
                            .setDirection(direction)
                            .setDelay(0)
                            .setRadius(baseRadius + i * (float)Math.sin(openingAngle))
                            .setColor(color);
                    if (onHit != null) {
                        circle.setOnHit(entity -> {
                            if (hits.add(entity))
                                onHit.accept(entity);
                        });
                    }
                    circle.draw();
                    i += 0.3;
                    if (i > length) {
                        this.cancel();
                        if (onFinish != null)
                            onFinish.accept(null);
                        return;
                    }
                }
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);
    }

    public Cone3D setOnHit(Consumer<LivingEntity> onHit) {
        this.onHit = onHit;
        return this;
    }

    public Cone3D setColor(Color color) {
        this.color = color;
        return this;
    }

    public Cone3D setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public Cone3D setOrigin(Location origin) {
        this.origin = origin;
        return this;
    }

    public Cone3D setLength(double length) {
        this.length = length;
        return this;
    }

    /**
     * Sets the opening angle
     * @param openingAngle Angle in degrees
     * @return this cone
     */
    public Cone3D setOpeningAngle(double openingAngle) {
        this.openingAngle = Math.toRadians(openingAngle);
        return this;
    }

    public Cone3D setDirection(Vector direction) {
        this.direction = direction;
        return this;
    }

    public Cone3D setDelay(double delay) {
        this.delay = delay;
        return this;
    }

    public Cone3D setBaseRadius(float baseRadius) {
        this.baseRadius = baseRadius;
        return this;
    }

    public void setOnFinish(Consumer<Void> onFinish) {
        this.onFinish = onFinish;
    }
}
