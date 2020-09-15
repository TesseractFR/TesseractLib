package onl.tesseract.tesseractlib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Animation {

    static public ParticleBuilder buildParticle(Particle particle, Color color, Location location) {
        ParticleBuilder builder = new ParticleBuilder(particle);
        if (color != null)
            builder.color(color);
        builder.location(location);
        builder.receivers(50); // Every player in a radius of 50 blocks will see the animation
        builder.extra(0);
        return builder;
    }
}
