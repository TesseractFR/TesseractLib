package onl.tesseract.tesseractlib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

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

    public ItemLoreBuilder append(String text)
    {
        var words = text.split(" ");
        NamedTextColor lastColor = null;
        for (int i = 0; i < words.length; i++)
        {
            var word = words[i];
            // Get real length
            int wordLen = word.replaceAll("§.", "").length();
            // Get the last used color.
            int colorIndex = word.lastIndexOf('§');
            if (colorIndex != -1 && colorIndex + 1 < word.length())
                lastColor = colorFromChar(word.charAt(colorIndex + 1));

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
                if (lastColor != null)
                    component = component.color(lastColor);
                lastLine = lastLine.append(component);
                lastLineLength = wordLen;
            }
            else
            {
                var component = Component.text(word + (i == words.length - 1 ? "" : " "));
                if (lastColor != null)
                    component = component.color(lastColor);
                lastLine = lastLine.append(component);
                lastLineLength += 1 + wordLen;
            }
        }

        return this;
    }

    private static NamedTextColor colorFromChar(char c)
    {
        return switch (c)
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
    }

    public List<Component> get()
    {
        if (lastLineLength > 0)
            lines.add(lastLine);
        return lines;
    }
}

