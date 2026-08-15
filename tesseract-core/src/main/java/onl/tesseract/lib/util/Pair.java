package onl.tesseract.lib.util;

import java.util.Objects;

public class Pair<LEFT, RIGHT> {

    private final LEFT left;
    private final RIGHT right;

    public Pair(final LEFT left, final RIGHT right)
    {
        this.left = left;
        this.right = right;
    }

    public LEFT getLeft()
    {
        return left;
    }

    public RIGHT getRight()
    {
        return right;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(left, right);
    }
}
