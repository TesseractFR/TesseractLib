package onl.tesseract.tesseractlib.chat.tag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import onl.tesseract.tesseractlib.player.TPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public interface Tag<T> {
    HashSet<Tag<?>> tags = new HashSet<>();

    boolean matches(TextComponent component);

    TextComponent apply(TextComponent component, TPlayer sender);

    TextComponent hover(T obj);

    static Component applyAll(Component component, TPlayer sender)
    {
        if (component instanceof TextComponent)
        {
            for (var tag : tags)
            {
                while (tag.matches((TextComponent) component))
                    component = tag.apply((TextComponent) component, sender);
            }
        }
        List<Component> newChildren = new ArrayList<>();
        for (var child : component.children())
        {
            child = applyAll(child, sender);
            newChildren.add(child);
        }
        return component.children(newChildren);
    }

    static <T> void registerTag(final Tag<T> tag)
    {
        tags.add(tag);
    }
}

