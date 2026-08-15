package onl.tesseract.lib.util;

public abstract class Either<LEFT, RIGHT> {

    private Either()
    {
    }

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public boolean isEmpty()
    {
        return !isLeft() && !isRight();
    }

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

    public static <LEFT, RIGHT> Either<LEFT, RIGHT> neither()
    {
        return Neither.getInstance();
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

    private static final class Neither<LEFT, RIGHT> extends Either<LEFT, RIGHT> {

        private static final Neither<?, ?> INSTANCE = new Neither<>();

        private Neither()
        {
            if (INSTANCE != null)
                throw new IllegalStateException("Already instantiated");
        }

        @SuppressWarnings("unchecked")
        private static <LEFT, RIGHT> Neither<LEFT, RIGHT> getInstance()
        {
            return (Neither<LEFT, RIGHT>) INSTANCE;
        }

        @Override
        public boolean isLeft()
        {
            return false;
        }

        @Override
        public boolean isRight()
        {
            return false;
        }

        @Override
        public LEFT getLeft() throws IllegalStateException
        {
            throw new IllegalStateException();
        }

        @Override
        public RIGHT getRight() throws IllegalStateException
        {
            throw new IllegalStateException();
        }
    }
}
