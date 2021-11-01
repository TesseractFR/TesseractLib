package onl.tesseract.tesseractlib;

import net.kyori.adventure.text.Component;

public interface MarketObject {
    Component getObtainMessage();

    default int getPrice(){
        return 200;
    }

    String getName();
}
