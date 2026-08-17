package onl.tesseract.lib;

public record Tick(long value) {
    public static Tick ofSeconds(long seconds) {
        return new Tick(seconds * 20);
    }
}
