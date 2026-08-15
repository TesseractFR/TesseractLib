package onl.tesseract.lib.animation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class SmoothCylinder extends AnimationBuilder<SmoothCylinder> {
    private final Plugin plugin;
    private Location location;
    private int height;
    private boolean rev;
    private float radius;
    private double delay;

    public SmoothCylinder(Plugin plugin) {
        this.plugin = plugin;
    }

    public Animation build()
    {
        return this::draw;
    }

    @Override
    protected SmoothCylinder self()
    {
        return this;
    }

    private void draw()
    {
        Set<Player> players = new HashSet<>();
        int sign = rev ? -1 : 1;
        int start = rev ? height : 0;
        // Creates a runnable that will be ran each tick
        new BukkitRunnable() {
            float y = 0;
            // If there is a delay, start the timer at 0. Else, set the timer so that it can complete the animation in
            // one loop
            float timer = (delay > 0) ? 0 : (float)(height / 0.1);
            @Override
            public void run()
            {
                // Increase the timer. If it get to 1, draw a circle
                if (delay > 0)
                    timer += 1 / delay;
                while (timer >= 1) {
                    timer--;
                    Location circleLocation = location.clone().add(0, y * sign + start, 0);
                    new Circle(particle, new AnimationTarget(circleLocation), plugin).setColor(color).setRadius(radius).setDelay(0).setRotationCount(1)
                        .setSpacing(0.4f)
                        .draw();
                    y += 0.1;
                    if (y > height)
                        this.cancel();

                    if (onHit != null) {
                        circleLocation.getNearbyPlayers(radius).forEach(player -> {
                            if (player.getLocation().getBlockY() == circleLocation.getBlockY()) {
                                if (! players.contains(player)) {
                                    onHit.accept(player);
                                    players.add(player);
                                }
                            }
                        });
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public SmoothCylinder setLocation(final Location location)
    {
        this.location = location;
        return this;
    }

    public SmoothCylinder setRadius(final int radius)
    {
        this.radius = radius;
        return this;
    }

    public SmoothCylinder setHeight(final int height)
    {
        this.height = height;
        return this;
    }

    public SmoothCylinder setRev(final boolean rev)
    {
        this.rev = rev;
        return this;
    }

    public SmoothCylinder setRadius(final float radius)
    {
        this.radius = radius;
        return this;
    }

    public SmoothCylinder setDelay(final double delay)
    {
        this.delay = delay;
        return this;
    }
}
