package onl.tesseract.tesseractlib.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.Translatable

operator fun Component.plus(text:String): Component {
    return this.append(Component.text(text));
}

operator fun Component.plus(translatable: Translatable): Component {
    return this.append(Component.translatable(translatable));
}

operator fun Component.plus(component: Component): Component {
    return this.append(component);
}
