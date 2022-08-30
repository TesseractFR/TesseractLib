package onl.tesseract.tesseractlib.util.menu;

import net.kyori.adventure.text.format.NamedTextColor;
import onl.tesseract.tesseractlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class InventoryPaginatedMenu<T> extends InventoryMenu {

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
        this.open(viewer, 0);
    }

    public void open(final Player viewer, int page)
    {
        clear();
        super.open(viewer);
        List<T> elements = getElements();
        int elementsPerPage = (height() - 1) * 9;

        for (int i = elementsPerPage * page; i < elementsPerPage * (page + 1) && i < elements.size(); i++)
        {
            addButton(i - page * elementsPerPage + getStartLine() * 9, buttonFor(elements.get(i), viewer));
        }

        // Page buttons
        this.addButton((getStartLine() + height() - 1) * 9 + 3, new Button(new ItemBuilder(Material.PAPER)
                .name("Page précédente", NamedTextColor.WHITE)
                .build(), event -> {
            if (page > 0)
                this.open(viewer, page - 1);
        }));

        this.addButton((getStartLine() + height() - 1) * 9 + 5, new Button(new ItemBuilder(Material.PAPER)
                .name("Page suivante", NamedTextColor.WHITE)
                .build(), event -> {
            if ((page + 1) * elementsPerPage < elements.size())
                this.open(viewer, page + 1);
        }));
    }
}
