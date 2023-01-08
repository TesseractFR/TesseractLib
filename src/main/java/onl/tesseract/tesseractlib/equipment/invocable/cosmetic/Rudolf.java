package onl.tesseract.tesseractlib.equipment.invocable.cosmetic;

import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.equipment.Equipment;
import onl.tesseract.tesseractlib.equipment.invocable.Invocable;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Rudolf extends Invocable {
    public Rudolf(final Equipment equipment)
    {
        super(equipment, EquipmentSlot.HEAD, "INVOCABLE_RUDOLF");
        equipment.unblockedHelmet.add(this);
    }

    public Rudolf(final Equipment equipment, final Map<String, Object> yamlMap)
    {
        super(equipment, EquipmentSlot.HEAD, "INVOCABLE_RUDOLF", yamlMap);
        equipment.unblockedHelmet.add(this);
    }

    @Override
    protected ItemStack createItem()
    {
        return new ItemBuilder(Material.QUARTZ)
                .setCustomModelData(13)
                .name("Bois de cerf", NamedTextColor.DARK_GREEN)
                .build();
    }

    @Override
    protected void onUninvoke(final boolean manualUninvocation)
    {

    }

    @Override
    protected void onInvoke(final boolean manualInvocation)
    {

    }

    @Override
    protected void use(final PlayerInteractEvent event)
    {

    }

    @Override
    protected void useInInventory(final InventoryClickEvent event)
    {

    }
}
