package onl.tesseract.tesseractlib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ItemLoreBuilder {
    final int width;
    final List<Component> lines = new ArrayList<>();
    Component lastLine = Component.text("");
    int lastLineLength = 0;

    public ItemLoreBuilder(final int width)
    {
        this.width = width;
    }

    public ItemLoreBuilder()
    {
        this(35);
    }

    public ItemLoreBuilder append(String text, TextColor color)
    {
        return append(text, color, Set.of());
    }

    public ItemLoreBuilder append(String text, TextDecoration decoration)
    {
        return append(text, null, decoration);
    }

    public ItemLoreBuilder append(String text, TextColor color, TextDecoration decoration)
    {
        return append(text, color, Set.of(decoration));
    }

    public ItemLoreBuilder append(String text, TextColor color, @NotNull Set<TextDecoration> decoration)
    {
        var words = text.split(" ");
        for (int i = 0; i < words.length; i++)
        {
            var word = words[i];
            // Get real length
            int wordLen = word.length();

            boolean isNewLine = word.strip().equals(Util.NEW_LINE.strip());
            if (isNewLine)
            {
                // Split
                lines.add(lastLine);
                lastLine = Component.text("");
                lastLineLength = 0;
            }
            else if (lastLineLength + wordLen > width)
            {
                // Split
                lines.add(lastLine);
                lastLine = Component.text(word + " ");
                if (color != null)
                    lastLine = lastLine.color(color);
                for (var deco : decoration)
                    lastLine = lastLine.decorate(deco);
                lastLineLength = wordLen;
            }
            else
            {
                var component = Component.text(word + (i == words.length - 1 ? "" : " "));
                if (color != null)
                    lastLine = lastLine.color(color);
                for (var deco : decoration)
                    lastLine = lastLine.decorate(deco);
                lastLine = lastLine.append(component);
                lastLineLength += 1 + wordLen;
            }
        }

        return this;
    }

    public ItemLoreBuilder append(String text)
    {
        var words = text.split(" ");
        final AtomicReference<NamedTextColor> lastColor = new AtomicReference<>(null);
        final AtomicReference<TextDecoration> lastDeco = new AtomicReference<>(null);
        for (int i = 0; i < words.length; i++)
        {
            var word = words[i];
            // Get real length
            int wordLen = word.replaceAll("§.", "").length();
            // Get the last used color.
            int colorIndex = word.lastIndexOf('§');
            if (colorIndex != -1 && colorIndex + 1 < word.length())
            {
                char c = word.charAt(colorIndex + 1);
                colorFromChar(c)
                        .ifPresent(lastColor::set);
                decorationFromChar(c)
                        .ifPresent(lastDeco::set);
                if (isReset(c))
                {
                    lastColor.set(null);
                    lastDeco.set(null);
                }
            }

            boolean isNewLine = word.strip().equals(Util.NEW_LINE.strip());
            if (isNewLine)
            {
                // Split
                lines.add(lastLine);
                lastLine = Component.text("");
                lastLineLength = 0;
            }
            else if (lastLineLength + wordLen > width)
            {
                // Split
                lines.add(lastLine);
                lastLine = Component.empty();
                var component = Component.text(word + " ");
                if (lastColor.get() != null)
                    component = component.color(lastColor.get());
                if (lastDeco.get() != null)
                    component = (TextComponent) component.decorate(lastDeco.get());
                lastLine = lastLine.append(component);
                lastLineLength = wordLen;
            }
            else
            {
                var component = Component.text(word + (i == words.length - 1 ? "" : " "));
                if (lastColor.get() != null)
                    component = component.color(lastColor.get());
                if (lastDeco.get() != null)
                    component = (TextComponent) component.decorate(lastDeco.get());
                lastLine = lastLine.append(component);
                lastLineLength += 1 + wordLen;
            }
        }

        return this;
    }

    public ItemLoreBuilder append(Component component)
    {
        if (component instanceof TextComponent textComponent)
        {
            Set<TextDecoration> decorations = new HashSet<>();
            textComponent.decorations()
                         .forEach((deco, state) -> {
                             if (state == TextDecoration.State.TRUE)
                                 decorations.add(deco);
                         });
            append(textComponent.content(), textComponent.color(), decorations);
        }
        else
            lastLine = lastLine.append(component);

        component.children().forEach(this::append);
        return this;
    }

    private static Optional<NamedTextColor> colorFromChar(char c)
    {
        var color = switch (c)
                {
                    case '0' -> NamedTextColor.BLACK;
                    case '1' -> NamedTextColor.DARK_BLUE;
                    case '2' -> NamedTextColor.DARK_GREEN;
                    case '3' -> NamedTextColor.DARK_AQUA;
                    case '4' -> NamedTextColor.DARK_RED;
                    case '5' -> NamedTextColor.DARK_PURPLE;
                    case '6' -> NamedTextColor.GOLD;
                    case '7' -> NamedTextColor.GRAY;
                    case '8' -> NamedTextColor.DARK_GRAY;
                    case '9' -> NamedTextColor.BLUE;
                    case 'a' -> NamedTextColor.GREEN;
                    case 'b' -> NamedTextColor.AQUA;
                    case 'c' -> NamedTextColor.RED;
                    case 'd' -> NamedTextColor.LIGHT_PURPLE;
                    case 'e' -> NamedTextColor.YELLOW;
                    case 'f' -> NamedTextColor.WHITE;
                    default -> null;
                };
        return Optional.ofNullable(color);
    }

    private static Optional<TextDecoration> decorationFromChar(char c)
    {
        var deco = switch (c)
                {
                    case 'n' -> TextDecoration.UNDERLINED;
                    case 'l' -> TextDecoration.BOLD;
                    case 'm' -> TextDecoration.STRIKETHROUGH;
                    case 'k' -> TextDecoration.OBFUSCATED;
                    case 'o' -> TextDecoration.ITALIC;
                    default -> null;
                };
        return Optional.ofNullable(deco);
    }

    private static boolean isReset(char c)
    {
        return c == 'r';
    }

    public List<Component> get()
    {
        if (lastLineLength > 0)
            lines.add(lastLine);
        return lines;
    }
}

