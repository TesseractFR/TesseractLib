package onl.tesseract.tesseractlib.equipment.invocable.cosmetic;

import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BonnetNoelV2 extends Invocable {
    public BonnetNoelV2(Equipment equipment)
    {
        super(equipment, EquipmentSlot.HEAD, "INVOCABLE_BONNET_NOEL_V2");
        this.equipment.unblockedHelmet.add(this);
    }

    public BonnetNoelV2(Equipment equipment, Map<String, Object> yamlMap)
    {
        super(equipment, EquipmentSlot.HEAD, "INVOCABLE_BONNET_NOEL_V2", yamlMap);
        this.equipment.unblockedHelmet.add(this);
    }

    @Override
    protected ItemStack createItem()
    {
        return new ItemBuilder(Material.IRON_HOE)
                .setCustomModelData(2)
                .name("Chapeau de Noël")
                .lore(new ItemLoreBuilder().newline(1).append("Objet de collection", NamedTextColor.GOLD).get())
                .build();
    }

    @Override
    protected void onUninvoke(boolean b)
    {

    }

    @Override
    protected void onInvoke(boolean b)
    {

    }

    @Override
    protected void use(PlayerInteractEvent playerInteractEvent)
    {

    }

    @Override
    protected void useInInventory(InventoryClickEvent inventoryClickEvent)
    {

    }
}
