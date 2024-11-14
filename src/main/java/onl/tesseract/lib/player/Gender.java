package onl.tesseract.lib.player;

public enum Gender {
    MALE("Masculin"),
    FEMALE("Féminin"),
    OTHER("Non renseigné");

    private final String string;

    Gender(final String string)
    {
        this.string = string;
    }

    public String getName()
    {
        return this.string;
    }
}
