package onl.tesseract.tesseractlib.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.translation.Translatable

operator fun Component.plus(text: String): Component {
    return this.append(Component.text(text));
}

operator fun Component.plus(translatable: Translatable): Component {
    return this.append(Component.translatable(translatable));
}

operator fun Translatable.plus(text: String): Component {
    return Component.translatable(this) + text;
}

operator fun Component.plus(component: Component): Component {
    return this.append(component);
}

operator fun Component.plus(text: Char): Component {
    return this.append(Component.text(text));
}

operator fun TextColor.plus(text: String): Component {
    return Component.text(text, this)
}

fun Component.append(text: String): Component {
    return this.append(Component.text(text))
}

fun Component.append(text: String, color: TextColor): Component {
    return this.append(Component.text(text, color))
}