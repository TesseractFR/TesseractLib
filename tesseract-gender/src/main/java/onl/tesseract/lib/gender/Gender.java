package onl.tesseract.lib.gender;

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
