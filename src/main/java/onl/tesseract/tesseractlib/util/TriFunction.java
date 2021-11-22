package onl.tesseract.tesseractlib.util;

@FunctionalInterface
public interface TriFunction<R, T, K, V> {

    V call(R r, T t, K k);

}
