package onl.tesseract.tesseractlib.util;

public class Pair<E> {

    private final E a;
    private final E b;

    public Pair(final E a, final E b)
    {
        this.a = a;
        this.b = b;
    }

    public E getA()
    {
        return a;
    }

    public E getB()
    {
        return b;
    }
}
