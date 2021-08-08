package onl.tesseract.tesseractlib.menu.pet;


import onl.tesseract.tesseractlib.familier.PetCategory;
import onl.tesseract.tesseractlib.familier.PetManager;
import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormat;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PetTypeSelection extends InventoryMenu {
    TPlayer player;

    /***************************************************************************************
     Declaration head
     **************************************************************************************/


    public PetTypeSelection(TPlayer player)
    {
        super(18, ChatColor.BLUE + "Les familiers");
        this.player = player;
        this.previous = new BoussoleMenu(player);
    }

    @Override
    public void open(Player viewer)
    {
        this.fill(Material.GRAY_STAINED_GLASS_PANE, " ");
        PetCategory[] petCategories = PetCategory.values();
        for (int Index = 0; Index < petCategories.length; Index++)
        {
            int finalIndex = Index;
            PetCategory petCategory = petCategories[Index];
            this.addButton(Index, petCategory.getHead(),
                           ChatColor.YELLOW + "Familiers de type " + petCategories[Index].name(),
                           NEW_LINE + ChatColor.GRAY + "Cliquez pour avoir la liste des familiers du type "
                                   + petCategories[Index],
                           event -> new PetsSelectionMenu(player, petCategories[finalIndex]).open(viewer));
        }
        this.addButton(13, Material.NAME_TAG, ChatColor.YELLOW + "Désinvocation",
                       NEW_LINE + ChatColor.GRAY + "Cliquez pour désinvoquer votre familier", event -> {
                    if (PetManager.invokedPets.get(viewer.getUniqueId()) != null)
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
