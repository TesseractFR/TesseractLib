package onl.tesseract.tesseractlib.cosmetics;

import net.kyori.adventure.text.Component;
import org.bukkit.Particle;

public enum FlyFilter implements Cosmetic {
    AMOUR,
    COLERE,
    CONNAISSANCE,
    MUSICAL,
    ENDER,
    INCENDIAIRE,
    REDSTONE,
    POTION,
    FUMEE,
    NEBULEUX,
    NONE;

    @Override
    public Component getObtainMessage()
    {
        return Component.text("Vous avez obtenu le filtre de vol " + getName());
    }

    @Override
    public String getName()
    {
        return toString().charAt(0) + toString().substring(1).toLowerCase();
    }

    public Particle getParticle()
    {
        return switch (this)
                {
                    case AMOUR -> Particle.HEART;
                    case COLERE -> Particle.VILLAGER_ANGRY;
                    case CONNAISSANCE -> Particle.ENCHANTMENT_TABLE;
                    case MUSICAL -> Particle.NOTE;
                    case ENDER -> Particle.DRAGON_BREATH;
                    case INCENDIAIRE -> Particle.LAVA;
                    case REDSTONE -> Particle.REDSTONE;
                    case POTION -> Particle.SPELL_MOB;
                    case FUMEE -> Particle.SMOKE_LARGE;
                    case NEBULEUX -> Particle.END_ROD;
                    default -> Particle.FLAME;
                };
    }
}
