package onl.tesseract.lib.chat.tag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import onl.tesseract.tesseractlib.player.TPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public interface Tag<T> {
    Set<Tag<?>> tags = new HashSet<>();

    Matcher getMatcher(TextComponent component);

    TextComponent getComponent(T obj);

    TextComponent apply(TextComponent component, TPlayer sender);

    TextComponent hover(T obj);

    static Component applyAll(Component component, TPlayer sender)
    {
        if (component instanceof TextComponent)
        {
            for (var tag : tags)
            {
                var matcher = tag.getMatcher((TextComponent) component);
                while (matcher.matches())
                {
                    component = tag.apply((TextComponent) component, sender);
                    matcher = tag.getMatcher((TextComponent) component);
                }
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

