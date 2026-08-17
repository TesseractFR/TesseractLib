package onl.tesseract.lib.menu;

/**
 * Size of a menu in number of rows
 */
public enum MenuSize {
    One(9),
    Two(18),
    Three(27),
    Four(36),
    Five(45),
    Six(54),
    Hopper(5);

    private final int size;

    MenuSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
