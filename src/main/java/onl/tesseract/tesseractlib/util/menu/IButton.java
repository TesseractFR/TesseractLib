package onl.tesseract.tesseractlib.util.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IButton {
    @Nullable
    Consumer<InventoryClickEvent> getFunction();

    void onClick(final InventoryClickEvent event);

    void draw(final InventoryMenu menu, final int index);
}
