package onl.tesseract.tesseractlib.util.menu;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CraftingInventoryMenu extends InventoryMenu {
    protected List<Integer> ingredientSlots = Collections.emptyList();
    protected Function<ItemStack[], ItemStack> updateFunction = items -> null;
    protected int resultSlot = -1;
    protected Player player;

    public CraftingInventoryMenu(final int size, final String title)
    {
        super(size, title);
    }

    public void setIngredientSlots(final List<Integer> ingredientSlots)
    {
        this.ingredientSlots = ingredientSlots;
    }

    public void setUpdateFunction(final Function<ItemStack[], ItemStack> updateFunction)
    {
        this.updateFunction = updateFunction;
    }

    public void setResultSlot(final int resultSlot)
    {
        this.resultSlot = resultSlot;
    }

    @Override
    @EventHandler
    public void onClick(final InventoryClickEvent event)
    {
        // Chek that the click happened in this inventory
        if (!event.getInventory().equals(this.inventory) || event.getClickedInventory() == null || (!event.getClickedInventory().equals(this.inventory) && !event.getClickedInventory().equals(view.getBottomInventory())))
            return;

        // If simple click in bottom, do nothing
        if (!event.getClickedInventory().equals(inventory))
        {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                new BukkitRunnable() {
                    @Override
                    public void run()
                    {
                        update();
                    }
                }.runTask(TesseractLib.instance);
            return;
        }
        // If simple click in any non-craft slot, cancel
        if (event.getSlot() != resultSlot && !ingredientSlots.contains(event.getSlot()))
        {
            event.setCancelled(true);
            return;
        }
        if (ingredientSlots.contains(event.getSlot()))
        {
            new BukkitRunnable() {
                @Override
                public void run()
                {
                    update();
                }
            }.runTask(TesseractLib.instance);
            return;
        }
        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR)
        {
            event.setCancelled(true);
        }
        else
        {
            if (!canCraft())
            {
                update();
                event.setCancelled(true);
            }
            else
            {
                clearIngredientSlots(event.isRightClick() && !event.isShiftClick() ? CraftedQuantity.HALF : CraftedQuantity.ALL);
                onCraft();
            }
        }
    }

    protected void onCraft() {

    }

    protected void clearIngredientSlots(final CraftedQuantity quantity)
    {
        for (final int ingredientSlot : ingredientSlots)
        {
            inventory.setItem(ingredientSlot, null);
        }
    }

    protected boolean canCraft()
    {
        return updateFunction.apply(getIngredients()) != null;
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event)
    {
        if (event.getInventory() == inventory)
        {
            for (final Integer ingredientSlot : ingredientSlots)
            {
                ItemStack item = inventory.getItem(ingredientSlot);
                if (item != null && item.getType() != Material.AIR)
                    player.getInventory().addItem(item);
            }
        }
    }

    @Override
    public void open(final Player player)
    {
        this.player = player;
        ItemStack background = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .name(" ").build();
        for (int i = 0; i < inventory.getSize(); i++)
        {
            if (ingredientSlots.contains(i))
                continue;
            inventory.setItem(i, background);
        }
        super.open(player);
    }

    protected ItemStack[] getIngredients()
    {
        int i = 0;
        ItemStack[] items = new ItemStack[ingredientSlots.size()];
        for (final Integer ingredientSlot : ingredientSlots)
        {
            items[i++] = inventory.getItem(ingredientSlot);
        }
        return items;
    }

    private void update()
    {
        ItemStack result = updateFunction.apply(getIngredients());
        inventory.setItem(resultSlot, Objects.requireNonNullElseGet(result, () -> new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)));
    }

    protected enum CraftedQuantity {
        ALL,
        HALF,
    }
}
