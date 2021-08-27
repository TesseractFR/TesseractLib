package onl.tesseract.tesseractlib.menu.cosmetic.pet;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;
import onl.tesseract.tesseractlib.cosmetics.familier.Pet;
import onl.tesseract.tesseractlib.cosmetics.familier.PetCategory;
import onl.tesseract.tesseractlib.cosmetics.familier.PetManager;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.menu.Button;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class PetsSelectionMenu extends InventoryMenu {
    private final PetCategory petCategory;
    TPlayer player;


    public PetsSelectionMenu(TPlayer player, PetCategory petCategory)
    {
        super(18, ChatColor.BLUE + "Sélection d'un familier");
        this.petCategory = petCategory;
        this.player = player;
        this.previous = new PetTypeSelection(player, this);
    }

    @Override
    public void open(Player viewer)
    {
        this.fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        List<Pet> pets = petCategory.getPets();
        int i = 0;
        for(Pet pet : pets){
            boolean hasPet = CosmeticManager.hasCosmetic(player.getBukkitPlayer(),Pet.getTypeName(),pet);
            String lore = NEW_LINE+(hasPet?ChatColor.GREEN+"Possédé":ChatColor.RED+"Non Possédé")+NEW_LINE+NEW_LINE;
            if(hasPet){
                lore += "Cliquez pour invoquer "+pet.getname();
            }
            else {
                lore += "Cliquez pour acheter "+pet.getname()+NEW_LINE+
                ChatColor.GRAY + "Coût : "+pet.getPrice()+" lys d'or"+NEW_LINE+
                ChatColor.GRAY + "Vous avez : "+player.getMarketCurrency()+" lys d'or";
            }



            addButton(i++,pet.getHead(),ChatColor.YELLOW+pet.getname(),lore,event->{
                if(hasPet)
                    PetManager.invokePet(viewer,pet);
                else
                {
                    if (player.getMarketCurrency() >= pet.getPrice())
                        openConfirmationMenu(viewer,"Être vous sur de vouloir acheter",this,event2 -> player.buyCosmetic(Pet.getTypeName(), pet, pet.getPrice()));
                    else
                        player.sendMessage(ChatFormats.PET_ERROR.append(
                                Component.text("Vous n'avez pas assez de lys d'or, cliquez ici pour en acheter")
                                        .hoverEvent(HoverEvent.showText(
                                Component
                                        .text("Cliquez ici pour accéder à la boutique.", NamedTextColor.GOLD)))
                                         .clickEvent(ClickEvent.openUrl("https://tesseract.craftingstore.net/"))));
                }
                this.close();
                    });
        }

        addButton(13, new Button(new ItemBuilder(Material.NAME_TAG)
                                 .name("Désinvocation", NamedTextColor.YELLOW)
                                 .lore(NEW_LINE + ChatColor.GRAY + "Cliquez pour désinvoquer votre familier")
                                 .build()
                , event -> {
            if (PetManager.hasPetInvocked(viewer))
            {
                PetManager.invokePet(viewer, null);
                viewer.sendMessage(ChatFormats.PET.append(Component.text("Votre familier a été désinvoqué", NamedTextColor.GREEN)));
                this.close();
            }
            else
            {
                viewer.sendMessage(ChatFormats.PET.append(Component.text("Vous n'avez pas de familier invoqué", NamedTextColor.RED)));
            }
        }));
        this.addBackButton();
        this.addQuitButton();
        super.open(viewer);
    }
}
