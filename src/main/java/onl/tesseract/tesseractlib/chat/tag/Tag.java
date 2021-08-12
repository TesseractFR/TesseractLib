package onl.tesseract.tesseractlib.chat.tag;

import net.kyori.adventure.text.TextComponent;
import onl.tesseract.tesseractlib.player.TPlayer;

import java.util.HashSet;

public interface Tag<T> {
    HashSet<Tag<?>> tags = new HashSet<>();

    boolean matches(TextComponent component);

    TextComponent apply(TextComponent component, TPlayer sender);

    TextComponent hover(T obj);

    static TextComponent applyAll(TextComponent component, TPlayer sender)
    {
        for (var tag : tags)
        {
            if (tag.matches(component))
                component = tag.apply(component, sender);
        }
        return component;
    }

    static <T> void registerTag(final Tag<T> tag)
    {
        tags.add(tag);
    }
}

