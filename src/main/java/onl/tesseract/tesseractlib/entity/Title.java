package onl.tesseract.tesseractlib.entity;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import onl.tesseract.tesseractlib.player.Gender;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE)
public enum Title {
    ADMINISTRATEUR("Administrateur", "Administratrice"),
    AGRICULTEUR("Paysan", "Paysane"),
    ALCHIMISTE_FOU("Alchimiste fou", "Alchimiste folle"),
    ANIMATEUR("Animateur", "Animatrice"),
    APPRENTI_AGRICULTEUR("Apprenti paysan", "Apprentie paysane"),
    APPRENTI_BUCHERON("Apprenti bûcheron", "Apprentie bûcheronne"),
    APPRENTI_CHASSEUR("Apprenti trappeur", "Apprentie trappeuse"),
    APPRENTI_GARDIEN("Apprenti sentinelle", "Apprentie sentinelle"),
    APPRENTI_HERBORISTE("Apprenti fleuriste", "Apprentie fleuriste"),
    APPRENTI_MINEUR("Apprenti joaillier", "Apprentie joaillière"),
    APPRENTI_PECHEUR("Apprenti pêcheur", "Apprentie pêcheuse"),
    APPRENTI_PILLEUR("Apprenti pilleur", "Apprentie pilleuse"),
    APPRENTI_TERRASSIER("Apprenti paysagiste", "Apprentie paysagiste"),
    ASSASSIN("Assassin", "Assassin"),
    AS_DE_LAIR("As de l'air", "As de l'air"),
    BARON("Baron", "Baronne"),
    BERSERKER("Berserk", "Berserk"),
    BUCHERON("Bûcheron", "Bûcheronne"),
    CHASSEUR("Trappeur", "Trappeuse"),
    CITOYEN("Citoyen", "Citoyenne"),
    CLERC("Clerc", "Clerc"),
    COMTE("Comte", "Comtesse"),
    ELYSEEN("Élyséen", "Élyséenne"),
    GARDIEN("Sentinelle", "Sentinelle"),
    GEOMANCIEN("Géomancien", "Géomancienne"),
    GUIDE("Guide", "Guide"),
    HERAULT("Héraut de la capitale", "Héraut de la capitale"),
    HERBORISTE("Fleuriste", "Fleuriste"),
    ILLUSIONNISTE("Illusionniste", "Illusionniste"),
    INITIE("Initié", "Initiée"),
    INVESTISSEUR("Investisseur", "Investisseuse"),
    LOCATAIRE("Locataire", "Locataire"),
    MAITRE_AGRICULTEUR("Maître paysan", "Maître paysane"),
    MAITRE_BUCHERON("Maître bûcheron", "Maître bûcheronne"),
    MAITRE_CHASSEUR("Maître trappeur", "Maître trappeuse"),
    MAITRE_GARDIEN("Maître sentinelle", "Maître sentinelle"),
    MAITRE_HERBORISTE("Maître fleuriste", "Maître fleuriste"),
    MAITRE_MINEUR("Maître joaillier", "Maître joaillière"),
    MAITRE_PECHEUR("Maître pêcheur", "Maître pêcheuse"),
    MAITRE_PILLEUR("Maître pilleur", "Maître pilleuse"),
    MAITRE_TERRASSIER("Maître paysagiste", "Maître paysagiste"),
    MARAUDEUR("Maraudeur", "Maraudeuse"),
    MEDIATEUR("Médiateur", "Médiatrice"),
    MINEUR("Joaillier", "Joaillière"),
    MODERATEUR("Modérateur", "Modératrice"),
    NOBLE("Noble", "Noble"),
    NOMADE("Nomade", "Nomade"),
    OCCULTISTE("Occultiste", "Occultiste"),
    PALADIN("Paladin", "Paladin"),
    PECHEUR("Pêcheur", "Pêcheuse"),
    PILLEUR("Pilleur", "Pilleuse"),
    PYROMANE("Pyromane", "Pyromane"),
    TERRASSIER("Paysagiste", "Paysagiste"),
    TRAVAILLEUR("Travailleur", "Travailleuse"),
    VICOMTE("Vicomte", "Vicomtesse"),
    VIP("VIP", "VIP"),
    VIPPLUS("VIP+", "VIP+"),
    APPRENTI("Apprenti", "Apprentie"),
    CONCEPTEUR("Concepteur", "Conceptrice"),
    CREATEUR("Créateur", "Créatrice"),
    INGENIEUR("Ingénieur", "Ingénieure"),
    BATISSEUR("Bâtisseur", "Bâtisseuse"),
    BUILDER("Builder", "Buildeuse"),
    ARCHITECTE("Architecte","Architecte" );

    final String text_m;
    final String text_f;


    Title(String text_m, String text_f) {
        this.text_m = text_m;
        this.text_f = text_f;
    }

    public String getDisplayName(Gender gender) {
        if (gender.equals(Gender.FEMALE)) {
            return text_f;
        }
        return text_m;
    }

    @Nullable
    static public Title getTitleFromName(String name) {
        if (name == null)
            return null;
        name = name.toUpperCase();
        if (name.equals("NULL"))
            return null;
        return Title.valueOf(name);
    }
}
