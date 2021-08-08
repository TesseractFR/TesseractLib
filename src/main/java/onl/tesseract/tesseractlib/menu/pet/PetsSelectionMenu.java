package onl.tesseract.tesseractlib.menu.pet;


import onl.tesseract.tesseractlib.familier.Pet;
import onl.tesseract.tesseractlib.familier.PetCategory;
import onl.tesseract.tesseractlib.familier.PetManager;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormat;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PetsSelectionMenu extends InventoryMenu {
    static ItemStack is;
    final String key = "Familier.";
    private final PetCategory petCategory;
    TPlayer player;


    public PetsSelectionMenu(TPlayer player, PetCategory petCategory)
    {
        super(18, ChatColor.BLUE + "Sélection d'un familier");
        this.petCategory = petCategory;
        this.player = player;
        this.previous = new PetTypeSelection(player);
    }

    @Override
    public void open(Player viewer)
    {
        this.fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        List<Pet> pets = petCategory.getPets();
        for (int index = 0; index < pets.size(); index++)
        {
            String obtenue = ChatColor.GREEN + "Possédé";


            final boolean lePet = player.hasPet(pets.get(index));
            if (!lePet)
            {
                obtenue = ChatColor.RED + "Non possédé";
            }
            Pet pet = pets.get(index);
            this.addButton(index, pet.getHead(), ChatColor.YELLOW + pet.getname(),
                           NEW_LINE + ChatColor.GRAY + "Cliquez pour invoquer " + pet.getname() + NEW_LINE
                                   + NEW_LINE + obtenue, event -> {
                        if (PetManager.invokedPets.get(viewer.getUniqueId()) != null && lePet)
                        {
                            PetManager.invokePet(viewer, null, false);
                        }
                        PetManager.invokePet(viewer, pet, true);
                        if (lePet)
                        {
                            this.close();
                        }
                    });


        }
        this.addButton(13, Material.NAME_TAG, ChatColor.YELLOW + "Désinvocation",
                       NEW_LINE + ChatColor.GRAY + "Cliquez pour désinvoquer votre familier", event -> {
                    if (PetManager.hasPetInvocked(viewer))
                    {
                        PetManager.invokePet(viewer, null, false);
                        viewer.sendMessage(ChatFormat.PET + ChatColor.GREEN + "Votre familier a été désinvoqué");
                        this.close();
                    }
                    else
                    {
                        viewer.sendMessage(ChatFormat.PET + ChatColor.RED + "Vous n'avez pas de familier invoqué");
                    }
                });
        this.addBackButton();
        this.addQuitButton();
        super.open(viewer);
    }
}
