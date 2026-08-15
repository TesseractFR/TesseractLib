package onl.tesseract.lib.animation;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

public class Impact {
    Location impactLocation;
    LivingEntity entity;
    Block block;

    public Impact(Location impactLocation) {
        this.impactLocation = impactLocation;
        if (impactLocation.getBlock().getType().isSolid())
            this.block = impactLocation.getBlock();
    }

    public Impact(LivingEntity entity)
    {
        this.entity = entity;
    }

    public Impact(LivingEntity entity, Location impactLocation)
    {
        this.entity = entity;
        this.impactLocation = impactLocation;
    }

    public Impact(Block block)
    {
        this.block = block;
    }

    public Impact(Block block, Location impactLocation)
    {
        this.block = block;
        this.impactLocation = impactLocation;
    }

    public Block getBlock() {
        return block;
    }

    public Location getImpactLocation() {
        return impactLocation;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public boolean hasImpactLocation()
    {
        return impactLocation != null;
    }

    public boolean hasBlock()
    {
        return block != null;
    }

    public boolean hasEntity()
    {
        return entity != null;
    }
}
