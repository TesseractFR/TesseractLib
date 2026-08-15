package onl.tesseract.lib.animation;

import com.destroystokyo.paper.ParticleBuilder;
import onl.tesseract.lib.util.Util;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

public class Ring {
    public Ring(Particle particle, Color color, Location location, int innerRadius, int outerRadius, Consumer<Player> onHit) {
        ParticleBuilder builder = AnimationUtil.buildParticle(particle, color, location);

        for (float angle = 0; angle < Math.PI * 2; angle += 0.05)
        {
            for (float dist = innerRadius; dist < outerRadius; dist += 0.3)
            {
                Location particleLocation = location.clone().add(dist * Math.cos(angle), 0, dist * Math.sin(angle));
                builder.location(particleLocation);
                builder.spawn();
            }
        }

        // Player detection
        if (onHit != null)
        {
            for (Player player : location.getWorld().getPlayers())
            {
                // Checks that the player is within the ring
                float flatDistance = Util.Locations.flatDistance(player.getLocation(), location);
                if (flatDistance >= innerRadius && flatDistance <= outerRadius)
                {
                    // Checks that the player is within +/- 4 blocks of height
                    if (Util.isNear(player.getLocation().getY(), location.getY(), 4))
                        onHit.accept(player);
                }
            }
        }
    }
}
