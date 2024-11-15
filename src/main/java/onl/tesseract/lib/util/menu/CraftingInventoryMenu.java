package onl.tesseract.lib.util.menu;

import net.kyori.adventure.text.Component;
import onl.tesseract.lib.menu.Menu;
import onl.tesseract.lib.menu.MenuSize;
import onl.tesseract.lib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CraftingInventoryMenu extends Menu {
    protected List<Integer> ingredientSlots = Collections.emptyList();
    protected Function<ItemStack[], ItemStack> updateFunction = items -> null;
    protected int resultSlot = -1;
    protected Player player;
    protected Plugin plugin;

    public CraftingInventoryMenu(final MenuSize size, final String title, final Menu previous, Plugin plugin)
    {
        super(size, Component.text(title), previous, true);
        this.plugin = plugin;
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

    @EventHandler
    public void onDrag(final InventoryDragEvent event)
    {
        if (event.getRawSlots().size() != 1)
        {
            event.setCancelled(true);
            return;
        }
        onClick(InventoryClickOrDragEvent.of(event));
    }

    private void onClick(InventoryClickOrDragEvent event)
    {
        if (this.getView() == null)
            return;
        Inventory topInventory = this.getView().getTopInventory();
        Inventory bottomInventory = this.getView().getBottomInventory();
        // Chek that the click happened in this inventory
        if (!event.getInventory().equals(topInventory) || event.getClickedInventory() == null || (!event.getClickedInventory().equals(topInventory) && !event.getClickedInventory().equals(bottomInventory)))
            return;

        // If simple click in bottom, do nothing
        if (!event.getClickedInventory().equals(topInventory))
        {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                new BukkitRunnable() {
                    @Override
                    public void run()
                    {
                        update();
                    }
                }.runTask(plugin);
            return;
        }
        // If simple click in any non-craft slot, cancel
        if (event.getSlot() != resultSlot && !ingredientSlots.contains(event.getSlot()))
        {
            if (event.getEvent() instanceof InventoryClickEvent click)
                super.onClick(click);
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
            }.runTask(plugin);
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
                CraftedQuantity quantity = event.isRightClick() && !event.isShiftClick() ? CraftedQuantity.HALF : CraftedQuantity.ALL;
                if (quantity == CraftedQuantity.HALF && !canCraftHalf())
                {
                    event.setCancelled(true);
                    return;
                }
                clearIngredientSlots(quantity);
                onCraft();
            }
        }
    }

    @Override
    @EventHandler
    public void onClick(final InventoryClickEvent event)
    {
        onClick(InventoryClickOrDragEvent.of(event));
    }

    protected void onCraft() {

    }

    protected boolean canCraftHalf()
    {
        return true;
    }

    protected void clearIngredientSlots(final CraftedQuantity quantity)
    {
        for (final int ingredientSlot : ingredientSlots)
        {
            getView().getTopInventory().setItem(ingredientSlot, null);
        }
    }

    protected boolean canCraft()
    {
        return updateFunction.apply(getIngredients()) != null;
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event)
    {
        if (event.getInventory() == getView().getTopInventory())
        {
            for (final Integer ingredientSlot : ingredientSlots)
            {
                ItemStack item = getView().getTopInventory().getItem(ingredientSlot);
                if (item != null && item.getType() != Material.AIR)
                    player.getInventory().addItem(item);
            }
        }
        super.close();
    }

    @Override
    public void placeButtons(@NotNull Player viewer) {
        this.player = viewer;
        ItemStack background = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .name(" ").build();
        for (int i = 0; i < getView().getTopInventory().getSize(); i++)
        {
            if (ingredientSlots.contains(i))
                continue;
            getView().getTopInventory().setItem(i, background);
        }
    }

    protected ItemStack[] getIngredients()
    {
        int i = 0;
        ItemStack[] items = new ItemStack[ingredientSlots.size()];
        for (final Integer ingredientSlot : ingredientSlots)
        {
            items[i++] = getView().getTopInventory().getItem(ingredientSlot);
        }
        return items;
    }

    private void update()
    {
        ItemStack result = updateFunction.apply(getIngredients());
        getView().getTopInventory().setItem(resultSlot, Objects.requireNonNullElseGet(result, () -> new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)));
    }

    protected enum CraftedQuantity {
        ALL,
        HALF,
    }
}
