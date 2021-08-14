package onl.tesseract.tesseractlib.cosmetics;

import net.kyori.adventure.text.Component;

public interface Cosmetic {
    Component getObtainMessage();

    default int getPrice(){
        return 200;
    };

    String getName();
}
