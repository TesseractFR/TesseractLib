package onl.tesseract.tesseractlib.menu.cosmetic.pet;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.cosmetics.familier.PetCategory;
import onl.tesseract.tesseractlib.cosmetics.familier.PetManager;
import onl.tesseract.tesseractlib.menu.BoussoleMenu;
import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.ChatFormats;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.menu.Button;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PetTypeSelection extends InventoryMenu {
    TPlayer player;

    /***************************************************************************************
     Declaration head
     **************************************************************************************/


    public PetTypeSelection(TPlayer player, InventoryMenu previous)
    {
        super(18, ChatColor.BLUE + "Les familiers",previous==null?new BoussoleMenu(player):previous);
        this.player = player;
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
            addButton(Index, new Button(new ItemBuilder(petCategory.getHead())
                                        .name("Familiers de type " + petCategories[Index].name(), NamedTextColor.YELLOW)
                                        .lore(NEW_LINE + ChatColor.GRAY + "Cliquez pour avoir la liste des familiers du type " + petCategories[Index])
                                        .build()
                    , event -> new PetsSelectionMenu(player, petCategories[finalIndex]).open(viewer)));
        }
        addButton(13, new Button(new ItemBuilder(Material.NAME_TAG)
                                            .name("Désinvocation", NamedTextColor.YELLOW)
                                            .lore(NEW_LINE + ChatColor.GRAY + "Cliquez pour désinvoquer votre familier")
                                            .build()
                , event -> {
            if (PetManager.invokedPets.get(viewer.getUniqueId()) != null)
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
