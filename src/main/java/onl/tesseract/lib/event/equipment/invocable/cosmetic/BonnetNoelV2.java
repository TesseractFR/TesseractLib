package onl.tesseract.lib.event.equipment.invocable.cosmetic;

import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.equipment.Invocable;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import onl.tesseract.tesseractlib.util.ItemLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BonnetNoelV2 extends Invocable {

    public BonnetNoelV2(@NotNull UUID playerUUID, boolean isInvoked, int handSlot) {
        super(playerUUID, isInvoked, handSlot);
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
    public @NotNull EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public @NotNull String getUniqueName() {
        return "INVOCABLE_BONNET_NOEL_V2";
    }

    @Override
    public void onUninvoke(@NotNull Player player, boolean manuelRemoval) {

    }

    @Override
    public void onInvoke(@NotNull Player player, boolean manuelInvocation) {

    }

    @Override
    public void useInInventory(@NotNull InventoryClickEvent event) {

    }

    @Override
    public void use(@NotNull PlayerInteractEvent event) {

    }
}
