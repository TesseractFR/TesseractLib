package onl.tesseract.tesseractlib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Builder class to construct an item lore, and to automatically wrap it to fit a given maximum width.
 * The result is a collection of {@link Component} that can be used to set the lore of an {@link
 * org.bukkit.inventory.meta.ItemMeta}
 *
 * @see org.bukkit.inventory.ItemStack
 * @see org.bukkit.inventory.meta.ItemMeta
 * @see Component
 */
public class ItemLoreBuilder {
    private static final int DEFAULT_LINE_WIDTH = 35;
    final int width;
    final List<Component> lines = new ArrayList<>();
    Component lastLine = Component.text("").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    int lastLineLength = 0;

    /**
     * Instantiate a new builder with the given line width
     *
     * @param width Maximum line width
     */
    public ItemLoreBuilder(final int width)
    {
        this.width = width;
    }

    /**
     * Instantiate a new builder. Default line width set to {@value DEFAULT_LINE_WIDTH}
     */
    public ItemLoreBuilder()
    {
        this(DEFAULT_LINE_WIDTH);
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

    /**
     * Append a text with a color
     *
     * @param text text
     * @param color color
     *
     * @return this
     */
    public ItemLoreBuilder append(String text, TextColor color)
    {
        return append(text, color, Set.of());
    }

    /**
     * Append a text with a decoration
     *
     * @param text text
     * @param decoration decoration
     *
     * @return this
     */
    public ItemLoreBuilder append(String text, TextDecoration decoration)
    {
        return append(text, null, decoration);
    }

    /**
     * Append a text with a color and decoration
     *
     * @param text text
     * @param color color
     * @param decoration decoration
     *
     * @return this
     */
    public ItemLoreBuilder append(String text, TextColor color, TextDecoration decoration)
    {
        return append(text, color, Set.of(decoration));
    }

    /**
     * Append a text with a color and a set of decoration
     *
     * @param text text
     * @param color color
     * @param decoration decorations
     *
     * @return this
     */
    public ItemLoreBuilder append(String text, TextColor color, @NotNull Set<TextDecoration> decoration)
    {
        if (text == null)
            return this;
        if(text.equals("")){
            return this.newline();
        }
        var words = text.split(" ");
        for (int i = 0; i < words.length; i++)
        {
            var word = words[i];
            if (i == words.length - 1 && text.endsWith(" "))
                word = word + " ";
            // Get real length
            int wordLen = word.length();

            boolean isNewLine = word.strip().equals(Util.NEW_LINE.strip());
            if (isNewLine)
            {
                // Split
                lines.add(lastLine);
                lastLine = Component.text("").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                lastLineLength = 0;
            }
            else if (lastLineLength + wordLen > width)
            {
                // Split
                lines.add(lastLine);
                lastLine = Component.text(word + (i == words.length - 1 ? "" : " "))
                                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
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
                    component = component.color(color);
                for (var deco : decoration)
                    component = (TextComponent) component.decorate(deco);
                lastLine = lastLine.append(component);
                lastLineLength += 1 + wordLen;
            }
        }

        return this;
    }

    /**
     * Append a text. This text can include values of {@link org.bukkit.ChatColor}, as they will be converted to
     * Component
     *
     * @param text text
     *
     * @return this
     *
     * @see Component
     */
    public ItemLoreBuilder append(String text)
    {
        if (text == null)
            return this;
        var words = text.split(" ");
        final AtomicReference<NamedTextColor> lastColor = new AtomicReference<>(null);
        final AtomicReference<TextDecoration> lastDeco = new AtomicReference<>(null);
        for (int i = 0; i < words.length; i++)
        {
            var word = words[i];
            if (i == words.length - 1 && text.endsWith(" "))
                word = word + " ";
            // Get real length
            int wordLen = word.replaceAll("§.", "").length();
            // Get the last used color.
            var colors = getLegacyColorCodes(word);
            for (char c : colors)
            {
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
                lastLine = Component.text("").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                lastLineLength = 0;
            }
            else if (lastLineLength + wordLen > width)
            {
                // Split
                lines.add(lastLine);
                lastLine = Component.empty().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
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

    protected List<Character> getLegacyColorCodes(final String str)
    {
        List<Character> res = new ArrayList<>();
        for (int i = 0; i < str.length() - 1; i++)
        {
            if (str.charAt(i) == '§')
                res.add(str.charAt(i + 1));
        }
        return res;
    }

    /**
     * Append a component and its children. TextComponent will be split to fit the width
     *
     * @param component component
     *
     * @return this
     */
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

    /**
     * Append a list of component. TextComponents will be split to fit the width
     */
    public ItemLoreBuilder append(List<Component> components)
    {
        for(Component component : components){
            append(component);
        }
        return this;
    }

    /**
     * Insert a newline
     *
     * @return this
     */
    public ItemLoreBuilder newline()
    {
        lines.add(lastLine);
        lastLine = Component.text("").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        lastLineLength = 0;
        return this;
    }

    /**
     * Insert {@code count} newlines
     *
     * @param count Quantity of newlines to insert
     *
     * @return this
     */
    public ItemLoreBuilder newline(int count)
    {
        for (int i = 0; i < count; i++)
            newline();
        return this;
    }

    /**
     * Get the built lore
     *
     * @return List of component representing the lore
     */
    public List<Component> get()
    {
        if (lastLineLength > 0)
            lines.add(lastLine);
        return lines;
    }
}

