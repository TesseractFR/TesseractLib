package onl.tesseract.tesseractlib.util;

public abstract class Either<LEFT, RIGHT> {

    private Either()
    {
    }

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract LEFT getLeft() throws IllegalStateException;

    public abstract RIGHT getRight() throws IllegalStateException;

    public static <LEFT, RIGHT> Either<LEFT, RIGHT> ofLeft(final LEFT left)
    {
        return new Left<>(left);
    }

    public static <LEFT, RIGHT> Either<LEFT, RIGHT> ofRight(final RIGHT right)
    {
        return new Right<>(right);
    }

    private static final class Left<LEFT, RIGHT> extends Either<LEFT, RIGHT> {
        private final LEFT value;

        private Left(final LEFT left)
        {
            this.value = left;
        }

        @Override
        public boolean isLeft()
        {
            return true;
        }

        @Override
        public boolean isRight()
        {
            return false;
        }

        @Override
        public LEFT getLeft() throws IllegalStateException
        {
            return value;
        }

        @Override
        public RIGHT getRight() throws IllegalStateException
        {
            throw new IllegalStateException();
        }
    }

    private static final class Right<LEFT, RIGHT> extends Either<LEFT, RIGHT> {
        private final RIGHT value;

        private Right(final RIGHT left)
        {
            this.value = left;
        }

        @Override
        public boolean isLeft()
        {
            return false;
        }

        @Override
        public boolean isRight()
        {
            return true;
        }

        @Override
        public LEFT getLeft() throws IllegalStateException
        {
            throw new IllegalStateException();
        }

        @Override
        public RIGHT getRight() throws IllegalStateException
        {
            return value;
        }
    }
}
