package com.mosaic.lang.functional;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class Either<L,R> {

    public static <L,R> Either<L,R> left( L v ) {
        return new Left(v);
    }

    public static <L,R> Either<L,R> right( R v ) {
        return new Right(v);
    }


    public abstract boolean isLeft();
    public abstract boolean isRight();

    public abstract L left();
    public abstract R right();



    private static class Left<L,R> extends Either<L,R> {
        private L v;

        public Left( L v ) {
            this.v = v;
        }

        public boolean isLeft() {
            return true;
        }

        public boolean isRight() {
            return false;
        }

        public L left() {
            return v;
        }

        public R right() {
            throw new IllegalStateException( "A left value cannot return right()" );
        }
    }

    private static class Right<L,R> extends Either<L,R> {
        private R v;

        public Right( R v ) {
            this.v = v;
        }

        public boolean isLeft() {
            return false;
        }

        public boolean isRight() {
            return true;
        }

        public L left() {
            throw new IllegalStateException( "A right value cannot return left()" );
        }

        public R right() {
            return v;
        }
    }

}
