package onl.tesseract.lib.util;

@FunctionalInterface
public interface TriConsumer<K, V, U> {

    void accept(K k, V v, U u);
}
