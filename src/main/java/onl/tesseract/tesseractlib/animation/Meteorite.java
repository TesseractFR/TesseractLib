package onl.tesseract.tesseractlib.animation;

import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Consumer;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Meteorite {
    final List<ArmorStand> blocks = new ArrayList<>();
    final Location impactLocation;
    final Location spawnLocation;

    public Meteorite(Location impactLocation, int spawnOffset) {
        this.impactLocation = impactLocation;
        this.spawnLocation = impactLocation.clone().add(0, spawnOffset, 0);
        this.spawnMeteorite();
    }

    /**
     * Charges the meteorite. The meteorite will stay still during the given duration
     * @param duration Duration of the charge. In seconds
     * @param callback Callback
     */
    public void charge(int duration, Consumer<Void> callback) {
        // Play particles every 10 ticks
        BukkitTask charging = new BukkitRunnable() {
            @Override
            public void run()
            {
                Location middle = blocks.get(72).getLocation();
                middle.getWorld().spawnParticle(Particle.LARGE_SMOKE, middle, 100, .6, .6, .6, 0.01);
                middle.getWorld().spawnParticle(Particle.FLAME, middle, 150, .6, .6, .6, 0.01);
            }
        }.runTaskTimer(TesseractLib.instance, 0, 10);
        new BukkitRunnable() {
            @Override
            public void run()
            {
                charging.cancel();
                callback.accept(null);
            }
        }.runTaskLater(TesseractLib.instance, 20L * duration);
    }

    public void move(float speed, Consumer<Void> callback) {
        Vector direction = this.impactLocation.clone().subtract(spawnLocation).toVector().normalize().multiply(speed);
        new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run()
            {
                for (ArmorStand entity : blocks) {
                    if (entity.isDead()) {
                        this.cancel();
                        return;
                    }
                    if (! entity.hasGravity())
                        entity.setGravity(true);
                    if (entity.isOnGround()) {
                        this.cancel();
                        callback.accept(null);
                        return;
                    }
                    entity.setVelocity(direction);
                }
                // Particles
                if (timer > 10) {
                    Location middle = blocks.get(72).getLocation();
                    middle.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, middle, 40, .6, .6, .6, 0.001);
                    middle.getWorld().spawnParticle(Particle.LARGE_SMOKE, middle, 100, .6, .6, .6, 0.001);
                    middle.getWorld().spawnParticle(Particle.FLAME, middle, 50, .6, .6, .6, 0.001);
                    timer = 0;
                }
                timer++;
            }
        }.runTaskTimer(TesseractLib.instance, 0, 1);

    }

    public void explode(Consumer<Player> onHit) {
        new ShockWave(Particle.DRIPPING_LAVA, null, this.impactLocation, 20, 0.2, onHit);
        impactLocation.getWorld().spawnParticle(Particle.FLAME, impactLocation, 500, .6, .6, .6, 0.5);
        impactLocation.getWorld().spawnParticle(Particle.EXPLOSION, impactLocation, 5, .6, .6, .6, 0.001);
        impactLocation.getWorld().playSound(impactLocation, Sound.ENTITY_GENERIC_EXPLODE, 50, 1);
        this.removeBlocks();
    }

    private void spawnMeteorite() {
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                for (int k = -2; k < 3; k++) {
                    Location loc = spawnLocation.clone().add(i * 0.4, j * 0.4, k * 0.4);
                    spawnMagmaBlock(loc);
                }
            }
        }
    }

    void spawnMagmaBlock(Location location) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(true);

        stand.getEquipment().setHelmet(new ItemStack(Material.MAGMA_BLOCK));
        blocks.add(stand);

        double x = Math.random() * Math.PI;
        double y = Math.random() * Math.PI;
        double z = Math.random() * Math.PI;
        stand.setHeadPose(new EulerAngle(x, y, z));
    }

    private void removeBlocks() {
        for (ArmorStand stand : blocks) {
            stand.remove();
        }
    }
}