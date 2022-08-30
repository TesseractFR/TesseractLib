package onl.tesseract.tesseractlib.util.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class InventoryPaginatedMenu<T> extends InventoryMenu {

    private int page = 0;

    protected InventoryPaginatedMenu(final int size, final String title)
    {
        super(size, title);
    }

    protected InventoryPaginatedMenu(final int size, final String title, final InventoryMenu previous)
    {
        super(size, title, previous);
    }

    protected InventoryPaginatedMenu(final int size, final String title, final InventoryMenu previous, final boolean freezeBottom)
    {
        super(size, title, previous, freezeBottom);
    }

    protected abstract List<T> getElements();

    protected abstract int getStartLine();

    protected abstract int height();

    protected abstract Button buttonFor(T element, Player viewer);

    @Override
    public void open(final Player viewer)
    {
        this.open(viewer, this.page);
    }

    public void open(final Player viewer, int page)
    {
        clear();
        this.page = page;
        super.open(viewer);
        List<T> elements = getElements();
        int elementsPerPage = (height() - 1) * 9;

        int i;
        for (i = elementsPerPage * page; i < elementsPerPage * (page + 1) && i < elements.size(); i++)
        {
            addButton(i - page * elementsPerPage + getStartLine() * 9, buttonFor(elements.get(i), viewer));
        }
        if (fillEmptyWithBarrier())
        {
            for (; i < elementsPerPage * (page + 1); i++)
                add(i - page * elementsPerPage + getStartLine() * 9, Material.BARRIER, Component.empty());
        }

        // Page buttons
        this.addButton((getStartLine() + height() - 1) * 9 + getPreviousPageButtonOffset(), new Button(new ItemBuilder(Material.PAPER)
                .name("Page précédente", NamedTextColor.WHITE)
                .build(), event -> {
            if (page > 0)
                this.open(viewer, page - 1);
        }));

        this.addButton((getStartLine() + height() - 1) * 9 + getNextPageButtonOffset(), new Button(new ItemBuilder(Material.PAPER)
                .name("Page suivante", NamedTextColor.WHITE)
                .build(), event -> {
            if ((page + 1) * elementsPerPage < elements.size())
                this.open(viewer, page + 1);
        }));
    }

    protected int getPreviousPageButtonOffset()
    {
        return 3;
    }

    protected int getNextPageButtonOffset()
    {
        return 5;
    }

    protected boolean fillEmptyWithBarrier() { return false; }

    public int getPage()
    {
        return page;
    }
}
