package onl.tesseract.tesseractlib.cosmetics;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum ElytraTrails implements Cosmetic {
    ENDER(ChatColor.DARK_PURPLE + "Ender", Material.ENDER_PEARL, 1, Particle.DRAGON_BREATH),
    FLAME(ChatColor.GOLD + "Flammes", Material.BLAZE_POWDER, 2, Particle.FLAME),
    CLOUD(ChatColor.GRAY + "Nuages", Material.PHANTOM_MEMBRANE, 3, Particle.CLOUD),
    LOVE(ChatColor.GRAY + "Amour", Material.APPLE, 4, Particle.HEART),
    MUSICAL(ChatColor.DARK_AQUA + "Musical", Material.NOTE_BLOCK, 5, Particle.NOTE),
    REDSTONE(ChatColor.DARK_RED + "Redstone", Material.REDSTONE, 6, Particle.REDSTONE),
    SMOKE(ChatColor.DARK_GRAY + "Fumée noire", Material.CHARCOAL, 7, Particle.SMOKE_LARGE),
    GREEN(ChatColor.GREEN + "Verdoyant", Material.LILY_PAD, 10, Particle.VILLAGER_HAPPY),
    ANGER(ChatColor.DARK_RED + "Colère", Material.NETHER_WART, 11, Particle.VILLAGER_ANGRY),
    INCENDIARY(ChatColor.GOLD + "Incendiaire", Material.FIRE_CHARGE, 12, Particle.LAVA),
    NEBULOUS(ChatColor.WHITE + "Nébuleux", Material.FEATHER, 13, Particle.END_ROD),
    TOTEM(ChatColor.DARK_GREEN + "Totem", Material.TOTEM_OF_UNDYING, 14, Particle.TOTEM),
    POTION(ChatColor.LIGHT_PURPLE + "Potion", Material.DRAGON_BREATH, 15, Particle.SPELL_MOB),
    SHINNING(ChatColor.WHITE + "Scintillant", Material.PRISMARINE_CRYSTALS, 16, Particle.FIREWORKS_SPARK),
    NONE(ChatColor.GRAY + "Sans sillage", Material.STRUCTURE_VOID, 0, null);

    String name;
    Material material;
    int index;
    Particle particle;

    ElytraTrails(String s, Material m, int i, Particle p)
    {
        name = s;
        material = m;
        index = i;
        particle = p;
    }

    public int getIndex()
    {
        return index;
    }

    public Material getMaterial()
    {
        return material;
    }

    public String getName()
    {
        return name;
    }

    public Particle getParticle()
    {
        return particle;
    }

    @Override
    public Component getObtainMessage()
    {
        return Component.text("Vous avez obtenu le sillage d'ailes "+toString().charAt(0)+toString().substring(1).toLowerCase());
    }
    public static String getTypeName()
    {
        return "ElytraTrails";
    }
}
