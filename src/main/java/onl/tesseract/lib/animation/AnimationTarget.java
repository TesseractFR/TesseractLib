package onl.tesseract.lib.animation;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class AnimationTarget {
    public enum Type {
        ENTITY, LOCATION
    }

    Entity entity;
    Location location;
    final Type type;

    public AnimationTarget(Entity entity) {
        this.entity = entity;
        this.type = Type.ENTITY;
    }

    public AnimationTarget(Location location) {
        this.location = location;
        this.type = Type.LOCATION;
    }

    public Location getLocation() {
        if (this.type == Type.ENTITY)
            return this.entity.getLocation().add(0, 1, 0);
        else
            return this.location.clone();
    }

    public boolean hasExpired()
    {
        return entity != null && entity.isDead();
    }
}
