package onl.tesseract.tesseractlib.equipment.invocable;

import io.github.bananapuncher714.cartographer.core.Cartographer;
import io.github.bananapuncher714.cartographer.core.MinimapManager;
import io.github.bananapuncher714.cartographer.core.map.Minimap;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class Carte extends Invocable {
    static Cartographer cartographerMain = Cartographer.getInstance();
    static MinimapManager minimapManager = cartographerMain.getMapManager();
    static Minimap minimap = minimapManager.constructNewMinimap( "carte_invocable" );
    static ItemStack mapItem = minimapManager.getItemFor( minimap );

    /**
     * Creates a new invocable that will be added to the given equipment.
     *
     * @param equipment     Equipment of the player
     */
    public Carte(Equipment equipment)
    {
        super(equipment, EquipmentSlot.HAND, "INVOCABLE_CARTE", createItem());
    }

    public Carte(Equipment equipment, Map<String, Object> yamlMap)
    {
        super(equipment, EquipmentSlot.HAND, "INVOCABLE_CARTE", createItem(), yamlMap);
    }

    static ItemStack createItem()
    {
        ItemStack item = mapItem;
        ItemMeta meta = item.getItemMeta();
        meta.setLocalizedName("INVOCABLE_CARTE");
        meta.setDisplayName(ChatColor.DARK_GREEN + "Carte Élyséenne");
        meta.setLore(Util.splitByLines(ChatColor.AQUA + "Technologie dernier cri !" + Util.NEW_LINE + Util.NEW_LINE +
                ChatColor.GRAY + "Clic gauche pour zoomer" + Util.NEW_LINE +
                ChatColor.GRAY + "Clic droit pour dézoomer", (short) 40));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Called when this invocable is uninvoked.
     *
     * @param manualUninvocation True if uninvoked by the player himself.
     */
    @Override
    protected void onUninvoke(boolean manualUninvocation)
    {

    }

    /**
     * Called when this invocable is invoked.
     *
     * @param manualInvocation True if invoked by the player himself.
     */
    @Override
    protected void onInvoke(boolean manualInvocation)
    {

    }

    /**
     * Called when an offHand or mainHand invocable is used.
     *
     * @param event Event of the interaction.
     */
    @Override
    protected void use(PlayerInteractEvent event)
    {

    }

    /**
     * Called when the item is clicked in the inventory.
     *
     * @param event Event of the interaction
     */
    @Override
    protected void useInInventory(InventoryClickEvent event)
    {

    }

    @Override
    public boolean excludesOther()
    {
        return false;
    }
}
