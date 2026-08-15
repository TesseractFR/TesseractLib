package onl.tesseract.lib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class AnimationUtil {
    static public ParticleBuilder buildParticle(Particle particle, Color color, Location location)
    {
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

    public static List<Location> getLine(final Location origin, final Location dest, double step)
    {
        List<Location> points = new ArrayList<>();

        Vector distanceVector = dest.toVector()
                                    .subtract(origin.toVector());
        Vector vector = distanceVector.clone()
                .normalize()
                .multiply(step);
        int stepCount = (int) (distanceVector.length() / step);

        Location cursor = origin.clone();

        for (int i = 0; i <= stepCount; i++)
        {
            points.add(cursor);
            cursor = cursor.clone().add(vector);
        }
        return points;
    }
}
