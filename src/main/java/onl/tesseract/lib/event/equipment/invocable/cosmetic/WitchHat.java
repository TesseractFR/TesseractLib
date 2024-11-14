package onl.tesseract.lib.event.equipment.invocable.cosmetic;

import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.lib.equipment.Invocable;
import onl.tesseract.lib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WitchHat extends Invocable {

    public WitchHat(@NotNull UUID playerUUID, boolean isInvoked, int handSlot) {
        super(playerUUID, isInvoked, handSlot);
    }

    @Override
    protected ItemStack createItem()
    {
        return new ItemBuilder(Material.QUARTZ)
                .setCustomModelData(2)
                .name("Chapeau de sorcière", NamedTextColor.LIGHT_PURPLE)
                .build();
    }

    @Override
    public @NotNull EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public @NotNull String getUniqueName() {
        return "INVOCABLE_WHITCH_HAT";
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
