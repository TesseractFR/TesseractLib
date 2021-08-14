package onl.tesseract.tesseractlib.familier;


import net.kyori.adventure.text.Component;
import onl.tesseract.tesseractlib.cosmetics.Cosmetic;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.inventory.ItemStack;

public enum Pet implements Cosmetic {
    PIGGY(PetHead.PiggyHead),
    BOARY(PetHead.BoaryHead),
    WILDY(PetHead.WildyHead),
    OWLY(PetHead.OwlyHead),
    BIRDY(PetHead.BirdyHead),
    OLDY(PetHead.OldyHead),
    DOGGY(PetHead.DoggyHead),
    PUGGY(PetHead.PuggyHead),
    BROWNY(PetHead.BrownyHead),
    CATTY(PetHead.CattyHead),
    TIGER(PetHead.TigerHead),
    NYANCAT(PetHead.NyanCatHead),
    MOOSHY(PetHead.MooshyHead),
    SPARCOW(PetHead.SparCowHead),
    COWWY(PetHead.CowwyHead),
    BEEBEE(PetHead.BeeBeeHead, "BeeBee"),
    MAGENBEE(PetHead.MagenBeeHead),
    BOOP(PetHead.BoopHead),
    SNAILY(PetHead.SnailyHead),
    GARY(PetHead.GaryHead),
    SLOWY(PetHead.SlowyHead),
    FISHY(PetHead.FishyHead),
    PUFFER(PetHead.PufferHead),
    MAGIKARP(PetHead.MagikarpHead),
    SHEEPY(PetHead.SheepyHead),
    BLUEPY(PetHead.BluepyHead),
    TEEPY(PetHead.TeepyHead);


    PetHead head;
    String name;

    Pet(PetHead head)
    {
        this.head = head;
        name = toString().charAt(0) + toString().substring(1).toLowerCase();
    }

    Pet(PetHead head, String name)
    {
        this.name = name;
        this.head = head;
    }

    public String getname()
    {
        return name;
    }

    public ItemStack getHead()
    {
        return InventoryMenu.getCustomHead("", head.data, head.signature);
    }

    @Override
    public Component getObtainMessage()
    {
        return Component.text("Vous avez obtenu le familier "+getname());
    }

    @Override
    public String getName()
    {
        return name;
    }
}
