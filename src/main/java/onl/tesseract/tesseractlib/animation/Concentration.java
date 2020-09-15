package onl.tesseract.tesseractlib.animation;

import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class Concentration {
    /**
     * Makes a concentration animation
     * @param particle Particle to use
     * @param color Color of the particle in case of redstone. Or null
     * @param target Target of the concentration
     * @param radius Radius.
     * @param count Number of converging lines;
     */
    public Concentration(Particle particle, Color color, AnimationTarget target, int radius, int count)
    {
        new BukkitRunnable() {
            int current = 0;
            @Override
            public void run()
            {
                double rho = (Math.random() * Math.PI * 2);
                double theta = (Math.random() * Math.PI * 2);
                double x = Math.cos(rho) * Math.cos(theta) * radius;
                double z = Math.sin(rho) * Math.cos(theta) * radius;
                double y = Math.sin(theta) * radius;
                Location source = target.getLocation().add(x, y, z);
                new Line(particle, source, target.getLocation()).setColor(color).setDelay(1);

                current++;
                if (this.current == count)
                    this.cancel();
            }
        }.runTaskTimer(TesseractLib.instance, 0, 5);
    }
}
