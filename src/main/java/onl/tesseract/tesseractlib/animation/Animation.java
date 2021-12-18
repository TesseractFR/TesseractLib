package onl.tesseract.tesseractlib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface Animation {

    void draw();

    static public ParticleBuilder buildParticle(Particle particle, Color color, Location location) {
        ParticleBuilder builder = new ParticleBuilder(particle);
        if (color != null)
            builder.color(color);
        builder.location(location);
        builder.receivers(50); // Every player in a radius of 50 blocks will see the animation
        builder.extra(0);
        return builder;
    }

    static public List<Location> getCircle(final Location center, double radius, int count)
    {
        List<Location> res = new ArrayList<>();
        double angleStep = Math.PI * 2 / count;
        for (double angle = 0; angle < Math.PI * 2; angle += angleStep)
        {
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            res.add(new Location(center.getWorld(), x, center.getY(), z));
        }

        return res;
    }
}
