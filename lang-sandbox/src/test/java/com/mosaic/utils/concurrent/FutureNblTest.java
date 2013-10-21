package com.mosaic.utils.concurrent;


import com.mosaic.lang.Failure;
import com.mosaic.lang.functional.*;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 *
 */
@SuppressWarnings({"unchecked", "Convert2Diamond"})
public class FutureNblTest {

// COMPLETED FutureNbl FROM MOMENT OF CONSTRUCTION

    @Test
    public void givenCompletedFutureNblWithResult_getResult_expectResultImmediately() {
        FutureNbl<String> f = FutureNbl.successful(Nullable.createNullable("hello"));

        assertEquals( "hello", f.getResultNoBlock().getValue() );
    }

    @Test
    public void givenCompletedFutureNblWithNullResult_getResult_expectNullResultImmediately() {
        FutureNbl<String> f = FutureNbl.successful(Nullable.NULL);

        assertEquals(Nullable.NULL, f.getResultNoBlock());
    }

    @Test
    public void givenCompletedFutureNblWithError_getResult_expectNullImmediately() {
        FutureNbl<String> f = FutureNbl.failed(new Failure(FutureNblTest.class, "things went south"));

        assertNull(f.getResultNoBlock());
    }

    @Test
    public void givenCompletedFutureNblWithResult_getFailure_expectNullImmediately() {
        FutureNbl<String> f = FutureNbl.successful(Nullable.createNullable("hello"));

        assertNull(f.getFailureNoBlock());
    }

    @Test
    public void givenCompletedFutureNblWithError_getFailure_expectFailureImmediately() {
        FutureNbl<String> f = FutureNbl.failed(new Failure(FutureNblTest.class, "things went south"));

        assertNull(f.getResultNoBlock());
    }

    @Test
    public void givenCompletedFutureNblWithResult_isComplete_expectTrue() {
        FutureNbl<String> f = FutureNbl.successful(Nullable.createNullable("hello"));

        assertTrue(f.isComplete());
    }

    @Test
    public void givenCompletedFutureNblWithFailure_isComplete_expectTrue() {
        FutureNbl<String> f = FutureNbl.failed(new Failure(FutureNblTest.class, "things went south"));

        assertTrue(f.isComplete());
    }

    @Test
    public void givenCompletedFutureNblWithResult_hasResult_expectTrue() {
        FutureNbl<String> f = FutureNbl.successful(Nullable.createNullable("hello"));

        assertTrue(f.hasResult());
    }

    @Test
    public void givenCompletedFutureNblWithFailure_hasResult_expectFalse() {
        FutureNbl<String> f = FutureNbl.failed(new Failure(FutureNblTest.class, "things went south"));

        assertFalse(f.hasResult());
    }

    @Test
    public void givenCompletedFutureNblWithResult_hasFailure_expectFalse() {
        FutureNbl<String> f = FutureNbl.successful(Nullable.createNullable("hello"));

        assertFalse(f.hasFailure());
    }

    @Test
    public void givenCompletedFutureNblWithFailure_hasFailure_expectTrue() {
        FutureNbl<String> f = FutureNbl.failed( new Failure(FutureNblTest.class, "things went south") );

        assertTrue(f.hasFailure());
    }


// MAP RESULT

    @Test
    public void givenCompletedFutureNblWithResult_mapResult_expectNewFutureNblWithMappedResult() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.createNullable("hello"));
        FutureNbl<Integer> f2 = f1.mapResult( new Function1<String,Integer>() {
            public Integer invoke( String v ) {
                return v.length();
            }
        });

        assertCompletedFutureNblWithResult( f1, "hello" );
        assertCompletedFutureNblWithResult( f2, 5 );
    }

    @Test
    public void givenCompletedFutureNblWithNullResult_mapResult_expectUnmodifiedFutureAndNoMappingCall() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.NULL);
        final AtomicBoolean flag = new AtomicBoolean(false);

        FutureNbl<Integer> f2 = f1.mapResult( new Function1<String,Integer>() {
            public Integer invoke( String v ) {
                flag.set(true);

                return v.length();
            }
        });

        assertFalse( flag.get() );
        assertSame( f1, f2 );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_mapResult_expectSameFutureNblBackAsFailuresANotMapped() {
        FutureNbl<String> f1 = FutureNbl.failed(new Failure(this.getClass(), "splat"));
        FutureNbl<Integer> f2 = f1.mapResult( new Function1<String,Integer>() {
            public Integer invoke( String v ) {
                return v.length();
            }
        });

        assertSame( f1, f2 );
    }

    @Test
    public void givenCompletedFutureNblWithResult_mapFailure_expectSameFutureNblBackAsResultsANotMapped() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.createNullable("hello"));
        FutureNbl<String> f2 = f1.mapFailure( new Function1<Failure,Failure>() {
            public Failure invoke( Failure v ) {
                return new Failure(v.getSource(), "modified message");
            }
        });

        assertSame( f1, f2 );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_mapFailure_expectNewFutureNblWithMappedFailure() {
        FutureNbl<String> f1 = FutureNbl.failed(new Failure(this.getClass(), "splat"));
        FutureNbl<String> f2 = f1.mapFailure( new Function1<Failure,Failure>() {
            public Failure invoke( Failure v ) {
                return new Failure(v.getSource(), "modified message");
            }
        });

        assertCompletedFutureNblWithFailure( f1, "splat" );
        assertCompletedFutureNblWithFailure( f2, new Failure(new Failure(this.getClass(), "splat"), new Failure(this.getClass(),"modified message")) );
    }

    @Test
    public void givenCompletedFutureNblWithResult_recover_expectSameFutureNblBackAsThereWasNothingToRecover() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.createNullable("hello"));
        FutureNbl<String> f2 = f1.recover( new Function1<Failure,String>() {
            public String invoke( Failure f ) {
                return f.getMessage();
            }
        });

        assertSame( f1, f2 );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_recover_expectNewFutureNblWithResultValue() {
        FutureNbl<String> f1 = FutureNbl.failed(new Failure(this.getClass(), "spam and eggs"));
        FutureNbl<String> f2 = f1.recover( new Function1<Failure,String>() {
            public String invoke( Failure f ) {
                return f.getMessage();
            }
        });

        assertCompletedFutureNblWithFailure( f1, "spam and eggs" );
        assertCompletedFutureNblWithResult( f2, "spam and eggs" );
    }




// COMPLETE A PROMISE

    @Test
    public void givenPromise_isComplete_expectFalse() {
        FutureNbl<String> promise = new FutureNbl<String>();

        assertFalse( promise.isComplete() );
    }

    @Test
    public void givenPromise_hasResult_expectFalse() {
        FutureNbl<String> promise = new FutureNbl<String>();

        assertFalse( promise.hasResult() );
    }

    @Test
    public void givenPromise_hasFailure_expectFalse() {
        FutureNbl<String> promise = new FutureNbl<String>();

        assertFalse(promise.hasFailure());
    }

    @Test
    public void givenPromise_completeWithResult_expectGetResultToReturnValue() {
        FutureNbl<String> promise = new FutureNbl<String>();

        promise.completeWithResult( "hello" );

        assertTrue( promise.isComplete() );
        assertTrue( promise.hasResult() );
        assertEquals("hello", promise.getResultNoBlock().getValueNbl());
    }

    @Test
    public void givenPromise_completeWithNullResult_expectGetResultToReturnNullValue() {
        FutureNbl<String> promise = new FutureNbl<String>();

        promise.completeWithResultNbl(Nullable.NULL);

        assertTrue( promise.isComplete() );
        assertTrue( promise.hasResult() );
        assertEquals(Nullable.NULL, promise.getResultNoBlock());
    }

    @Test
    public void givenPromise_completeWithResult_expectGetErrorToReturnNull() {
        FutureNbl<String> promise = new FutureNbl<String>();

        promise.completeWithResult("hello");

        assertNull(promise.getFailureNoBlock());
    }

    @Test
    public void givenPromise_completeWithError_expectGetResultToReturnNull() {
        FutureNbl<String> promise = new FutureNbl<String>();

        promise.completeWithFailure(new Failure(FutureNblTest.class, "splat"));

        assertNull(promise.getResultNoBlock());
    }

    @Test
    public void givenPromise_completeWithError_expectGetErrorToReturnFailure() {
        FutureNbl<String> promise = new FutureNbl<String>();

        Failure failure = new Failure(FutureNblTest.class, "splat");
        promise.completeWithFailure(failure);

        assertSame(failure, promise.getFailureNoBlock());
    }


// MAP RESULT ON A PROMISE

    @Test
    public void givenPromise_mapResultThenCompleteFirstFutureNbl_expectSecondFutureNblToBeCompletedWithMappedResult() {
        FutureNbl<String> promise = new FutureNbl<String>();
        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureNblWithResult( promise, "hello world" );
        assertCompletedFutureNblWithResult( f2, 11 );
    }

    @Test
    public void givenPromise_mapResultThenCompleteFirstFutureWithNull_expectSecondFutureNblToBeCompletedWithMappedResult() {
        FutureNbl<String> promise = new FutureNbl<String>();
        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        promise.completeWithResultNbl( Nullable.NULL );

        assertCompletedFutureNblWithResult( promise, null );
        assertCompletedFutureNblWithResult( f2, null );
    }

    @Test
    public void givenPromise_mapResultThenCompleteFirstFutureNblWithFailure_expectSecondFutureNblToBeCompletedWithFailure() {
        FutureNbl<String> promise = new FutureNbl<String>();
        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureNblWithResult( promise, "hello world" );
        assertCompletedFutureNblWithResult( f2, 11 );
    }

    @Test
    public void givenPromise_mapResultThenCompleteFirstFutureNblWithResultAndHaveMappingFunctionThrowException_expectSecondFutureNblToBeCompletedWithFailure() {
        FutureNbl<String> promise = new FutureNbl<String>();
        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                throw new RuntimeException( "splat" );
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureNblWithResult( promise, "hello world" );
        assertCompletedFutureNblWithFailure( f2, new Failure(new RuntimeException("splat")) );
    }

    @Test
    public void givenPromise_mapResultTwiceInChainThenCompleteFirstFutureNbl_expectThirdFutureNblToBeCompletedWithMappedResult() {
        FutureNbl<String> promise = new FutureNbl<String>();
        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });
        FutureNbl<Integer> f3 = f2.mapResult( new Function1<Integer, Integer>() {
            public Integer invoke(Integer arg) {
                return arg * 2;
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureNblWithResult( promise, "hello world" );
        assertCompletedFutureNblWithResult( f2, 11 );
        assertCompletedFutureNblWithResult( f3, 22 );
    }

    @Test
    public void givenPromise_mapFirstFutureNblTwiceNotChained_expectFirstFutureNblToHandleMultipleCallbacksAndThusCompleteEachFutureNbl() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        FutureNbl<String> f3 = promise.mapResult( new Function1<String, String>() {
            public String invoke(String arg) {
                return arg + " " + arg;
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureNblWithResult( promise, "hello world" );
        assertCompletedFutureNblWithResult( f2, 11 );
        assertCompletedFutureNblWithResult( f3, "hello world hello world" );
    }

    @Test
    public void givenPromise_mapResultTwiceThenCompleteFirstFutureNblWithFailure_expectThirdFutureNblToBeCompletedWithFailure() {
        FutureNbl<String> promise = new FutureNbl<String>();
        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });
        FutureNbl<Integer> f3 = f2.mapResult( new Function1<Integer, Integer>() {
            public Integer invoke(Integer arg) {
                return arg * 2;
            }
        });

        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureNblWithFailure( promise, "splat" );
        assertCompletedFutureNblWithFailure( f2, "splat" );
        assertCompletedFutureNblWithFailure( f3, "splat" );
    }


    @Test
    public void givenPromise_mapResultThenRecoverCompleteFirstFutureNblWithResult_expectThirdFutureNblToBeCompleteWithResult() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        FutureNbl<Integer> f3 = f2.recover( new Function1<Failure, Integer>() {
            public Integer invoke(Failure f) {
                return -1;
            }
        });

        promise.completeWithResult( "rock on" );

        assertCompletedFutureNblWithResult( promise, "rock on" );
        assertCompletedFutureNblWithResult( f2, 7 );
        assertCompletedFutureNblWithResult( f3, 7 );
    }

    @Test
    public void givenPromise_mapResultThenRecoverCompleteFirstFutureNblWithFailure_expectThirdFutureNblToBeCompleteWithRecoveredResult() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        FutureNbl<Integer> f3 = f2.recover( new Function1<Failure, Integer>() {
            public Integer invoke(Failure f) {
                return -1;
            }
        });

        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureNblWithFailure( promise, "splat" );
        assertCompletedFutureNblWithFailure( f2, "splat" );
        assertCompletedFutureNblWithResult( f3, -1 );
    }

    @Test
    public void givenPromise_mapResultThenRecoverCompleteFirstFutureNblWithFailureAndHaveRecoverFunctionThrowException_expectThirdFutureNblToBeCompleteWithFailureFromTheRecoverFunction() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        FutureNbl<Integer> f3 = f2.recover( new Function1<Failure, Integer>() {
            public Integer invoke(Failure f) {
                throw new RuntimeException( "woops" );
            }
        });

        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureNblWithFailure( promise, "splat" );
        assertCompletedFutureNblWithFailure( f2, "splat" );
        assertCompletedFutureNblWithFailure( f3, new Failure(new Failure(this.getClass(),"splat"), new RuntimeException("woops") ) );
    }




    @Test
    public void givenPromise_mapFailureThenCompleteFirstFutureNblWithFailure_expectSecondFutureNblToCompleteWithMappedFutureNbl() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<String> f2 = promise.mapFailure( new Function1<Failure, Failure>() {
            public Failure invoke( Failure f ) {
                return new Failure( String.class, "translation" );
            }
        });


        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureNblWithFailure( promise, "splat" );
        assertCompletedFutureNblWithFailure( f2, new Failure(new Failure(this.getClass(),"splat"), new Failure(String.class, "translation") ) );
    }

    @Test
    public void givenPromise_mapFailureThenCompleteFirstFutureNblWithFailureAndHaveTheMappingFunctionThrowAnException_expectSecondFutureNblToCompleteWithTheExceptionFromTheMappingFunction() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<String> f2 = promise.mapFailure( new Function1<Failure, Failure>() {
            public Failure invoke( Failure f ) {
                throw new RuntimeException( "woops" );
            }
        });


        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureNblWithFailure( promise, "splat" );
        assertCompletedFutureNblWithFailure( f2, new Failure(new Failure(this.getClass(),"splat"), new RuntimeException("woops") ) );
    }

    @Test
    public void givenPromise_mapFailureThenCompleteFirstFutureNblWithResult_expectSecondFutureNblToCompleteWithResult() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<String> f2 = promise.mapFailure( new Function1<Failure, Failure>() {
            public Failure invoke( Failure f ) {
                throw new RuntimeException( "woops" );
            }
        });


        promise.completeWithResult( "rar" );

        assertCompletedFutureNblWithResult( promise, "rar" );
        assertCompletedFutureNblWithResult( f2, "rar" );
        assertNotSame( promise, f2 );
    }


// FLAT MAP RESULTS ON PROMISES

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureNblWithFailure_expectSecondFutureNblToCompleteWithFailure() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<Integer> f2 = promise.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public TryNbl<Integer> invoke( String v ) {
                return FutureNbl.successful(Nullable.createNullable(v.length()));
            }
        });


        promise.completeWithFailure( new Failure(this.getClass(), "bomb") );

        assertCompletedFutureNblWithFailure( promise, "bomb" );
        assertCompletedFutureNblWithFailure( f2, "bomb" );
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureNblWithCompletedResult_expectSecondFutureNblToCompleteWithMappedResult() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<Integer> f2 = promise.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                return FutureNbl.successful(Nullable.createNullable(v.length()));
            }
        });


        promise.completeWithResult("rar");

        assertCompletedFutureNblWithResult(promise, "rar");
        assertCompletedFutureNblWithResult(f2, 3);
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureNblWithNull_expectSecondFutureNblToCompleteWithNull() {
        FutureNbl<String> promise = new FutureNbl<String>();
        final AtomicBoolean flag = new AtomicBoolean(false);

        FutureNbl<Integer> f2 = promise.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                flag.set(true);

                return FutureNbl.successful(Nullable.createNullable(v.length()));
            }
        });


        promise.completeWithResultNbl(Nullable.NULL);

        assertFalse( flag.get() );
        assertEquals( Nullable.NULL, f2.getResultNoBlock() );
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureNblWithPromise_expectSecondFutureNblToBeIncompletePromise() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<Integer> f2 = promise.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                return FutureNbl.promise();
            }
        });


        promise.completeWithResult("rar");

        assertCompletedFutureNblWithResult(promise, "rar");
        assertFalse(f2.isComplete());
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureNblWithPromiseThenComplete_expectSecondFutureNblToHaveSameResultAsTheCompletedPromise() {
        FutureNbl<String>  promise      = new FutureNbl<String>();
        final FutureNbl<Integer> childPromise = FutureNbl.promise();

        FutureNbl<Integer> f2 = promise.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                return childPromise;
            }
        });


        promise.completeWithResult( "rar" );
        childPromise.completeWithResult(42);

        assertCompletedFutureNblWithResult(promise, "rar");
        assertCompletedFutureNblWithResult(f2, 42);
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureNblWithResultAndHaveMappingFunctionThrowAnException_expectFinalFutureNblToHoldFailure() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<Integer> f2 = promise.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                throw new IllegalStateException( "bang" );
            }
        });


        promise.completeWithResult( "rar" );

        assertCompletedFutureNblWithResult( promise, "rar" );
        assertCompletedFutureNblWithFailure(f2, new Failure(new IllegalStateException("bang")));
    }


// FLAT MAP RESULTS ON ALREADY COMPLETED FutureNblS

    @Test
    public void givenCompletedFutureNblWithResult_flatMapResult_expectResultToBeTheResultFromTheMappingFunction() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.createNullable("hello"));

        FutureNbl<Integer> f2 = f1.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                return FutureNbl.successful(Nullable.createNullable(v.length()));
            }
        });


        assertCompletedFutureNblWithResult(f1, "hello");
        assertCompletedFutureNblWithResult(f2, 5);
    }

    @Test
    public void givenCompletedFutureNblWithNullResult_flatMapResult_expectNoCallToMappingFunction() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.NULL);
        final AtomicBoolean flag = new AtomicBoolean(false);

        FutureNbl<Integer> f2 = f1.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                flag.set(true);
                return FutureNbl.successful(Nullable.createNullable(v.length()));
            }
        });


        assertFalse( flag.get() );
        assertSame(f1, f2);
    }

    @Test
    public void givenCompletedFutureNblWithResult_flatMapResultAndHaveTheMappingFunctionThrowAnException_expectResultToBeAFailureHoldingTheExceptionFromTheMappingFunction() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.createNullable("hello"));

        FutureNbl<Integer> f2 = f1.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                throw new IllegalArgumentException("what are you trying to do to me");
            }
        });


        assertCompletedFutureNblWithResult(f1, "hello");
        assertCompletedFutureNblWithFailure(f2, new Failure(new IllegalArgumentException("what are you trying to do to me")));
    }

    @Test
    public void givenCompletedFutureNblWithFailure_flatMapResult_expectResultToBeFailureFromFirstFutureNblAsTheMappingFunctionWillNotBeCalled() {
        FutureNbl<String> f1 = FutureNbl.failed(new Failure(this.getClass(), "splat"));

        FutureNbl<Integer> f2 = f1.flatMapResult( new Function1<String, TryNbl<Integer>>() {
            public FutureNbl<Integer> invoke( String v ) {
                return FutureNbl.successful(Nullable.createNullable(v.length()));
            }
        });


        assertCompletedFutureNblWithFailure(f1, "splat");
        assertCompletedFutureNblWithFailure(f2, "splat");
    }


// FLAT MAP FAILURES ON PROMISES

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureNblWithFailure_expectSecondFutureNblToCompleteWithMappedFailure() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<String> f2 = promise.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                return FutureNbl.successful(Nullable.createNullable(new Failure(f, new Failure(FutureNblTest.this.getClass(), "mapped"))));
            }
        });


        promise.completeWithFailure( new Failure(this.getClass(), "bomb") );

        assertCompletedFutureNblWithFailure( promise, "bomb" );
        assertCompletedFutureNblWithFailure( f2, new Failure(new Failure(this.getClass(),"bomb"), new Failure(this.getClass(), "mapped")) );
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureNblWithFailureMapperReturnsResultAsFailure_expectSecondFutureNblToCompleteWithMappedFailure() {
        FutureNbl<String> promise = new FutureNbl<String>();

        FutureNbl<String> f2 = promise.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                return FutureNbl.failed( new Failure(f, new Failure(FutureNblTest.this.getClass(), "mapped")) );
            }
        });


        promise.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertCompletedFutureNblWithFailure(promise, "bomb");
        assertCompletedFutureNblWithFailure(f2, new Failure(new Failure(this.getClass(), "bomb"), new Failure(this.getClass(), "mapped")));
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureNblWithCompletedResult_expectSecondFutureNblToCompleteWithSameResult() {
        FutureNbl<String> f1 = new FutureNbl<String>();

        FutureNbl<String> f2 = f1.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                return FutureNbl.failed(new Failure(f, new Failure(this.getClass(), "mapped")));
            }
        });


        f1.completeWithResult("hello");

        assertCompletedFutureNblWithResult(f1, "hello");
        assertCompletedFutureNblWithResult(f2, "hello");
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureNblWithNull_expectSecondFutureNblToCompleteWithSameResult() {
        FutureNbl<String> f1 = new FutureNbl<String>();
        final AtomicBoolean flag = new AtomicBoolean(false);

        FutureNbl<String> f2 = f1.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                flag.set(true);

                return FutureNbl.failed(new Failure(f, new Failure(this.getClass(), "mapped")));
            }
        });


        f1.completeWithResultNbl( Nullable.NULL );

        assertFalse(flag.get());
        assertCompletedFutureNblWithResult(f1, null);
        assertCompletedFutureNblWithResult(f2, null);
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureNblWithPromise_expectSecondFutureNblToBeIncompletePromise() {
        FutureNbl<String> f1 = new FutureNbl<String>();

        FutureNbl<String> f2 = f1.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                return FutureNbl.promise();
            }
        });


        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertCompletedFutureNblWithFailure(f1, "bomb");
        assertFalse(f2.isComplete());
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureNblWithPromiseThenComplete_expectSecondFutureNblToHaveSameResultAsTheCompletedPromise() {
        FutureNbl<String>  f1           = new FutureNbl<String>();
        final FutureNbl<Failure> childPromise = FutureNbl.promise();

        FutureNbl<String> f2 = f1.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                return childPromise;
            }
        });


        f1.completeWithFailure( new Failure(this.getClass(), "bomb") );
        childPromise.completeWithResult(new Failure(this.getClass(), "mapped"));

        assertCompletedFutureNblWithFailure(f1, "bomb");
        assertCompletedFutureNblWithFailure(f2, new Failure(new Failure(this.getClass(), "bomb"), new Failure(this.getClass(), "mapped")));
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureNblWithFailureAndHaveMappingFunctionThrowAnException_expectFinalFutureNblToHoldFailure() {
        FutureNbl<String> f1 = new FutureNbl<String>();

        FutureNbl<String> f2 = f1.flatMapFailure(new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke(Failure f) {
                throw new RuntimeException("whoops");
            }
        });


        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertCompletedFutureNblWithFailure(f1, "bomb");
        assertCompletedFutureNblWithFailure( f2, new Failure(new Failure(this.getClass(), "bomb"), new RuntimeException("whoops")) );
    }


// FLAT MAP FAILURES ON ALREADY COMPLETED FutureNblS

    @Test
    public void givenCompletedFutureNblWithResult_flatMapFailure_expectResultToBeUntouched() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.createNullable("up and at 'em"));

        FutureNbl<String> f2 = f1.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                return FutureNbl.failed( new Failure(f, new Failure(this.getClass(), "mapped")) );
            }
        });


        assertCompletedFutureNblWithResult( f1, "up and at 'em" );
        assertCompletedFutureNblWithResult( f2, "up and at 'em" );
    }

    @Test
    public void givenCompletedFutureNblWithNull_flatMapFailure_expectResultToBeUntouched() {
        FutureNbl<String> f1 = FutureNbl.successful(Nullable.NULL);
        final AtomicBoolean flag = new AtomicBoolean(false);

        FutureNbl<String> f2 = f1.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                flag.set(true);

                return FutureNbl.failed( new Failure(f, new Failure(this.getClass(), "mapped")) );
            }
        });


        assertFalse(flag.get());
        assertCompletedFutureNblWithResult( f1, null );
        assertCompletedFutureNblWithResult( f2, null );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_flatMapFailure_expectResultToBeMappedFailure() {
        FutureNbl<String> f1 = FutureNbl.failed(new Failure(this.getClass(), "bomb"));

        FutureNbl<String> f2 = f1.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                return FutureNbl.failed( new Failure(f, new Failure(FutureNblTest.this.getClass(), "mapped")) );
            }
        });


        assertCompletedFutureNblWithFailure(f1, "bomb");
        assertCompletedFutureNblWithFailure(f2, new Failure(new Failure(this.getClass(), "bomb"), new Failure(this.getClass(), "mapped")));
    }

    @Test
    public void givenCompletedFutureNblWithFailure_flatMapFailureAndHaveTheMappingFunctionThrowAnException_expectResultToBeAFailureHoldingTheExceptionFromTheMappingFunction() {
        FutureNbl<String> f1 = FutureNbl.failed(new Failure(this.getClass(), "bomb"));

        FutureNbl<String> f2 = f1.flatMapFailure( new Function1<Failure, TryNbl<Failure>>() {
            public FutureNbl<Failure> invoke( Failure f ) {
                throw new RuntimeException("whoops");
            }
        });


        assertCompletedFutureNblWithFailure(f1, "bomb");
        assertCompletedFutureNblWithFailure(f2, new Failure(new Failure(this.getClass(), "bomb"), new RuntimeException("whoops")));
    }



// FLAT RECOVER ON PROMISES

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureNblWithFailure_expectSecondFutureNblToCompleteWithResultFromRecoveryFunction() {
        FutureNbl<String> f1 = FutureNbl.promise();

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                return FutureNbl.successful(Nullable.createNullable("recovered"));
            }
        });

        f1.completeWithFailure( new Failure(this.getClass(), "bomb") );

        assertCompletedFutureNblWithFailure( f1, "bomb" );
        assertCompletedFutureNblWithResult( f2, "recovered" );
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureNblWithCompletedResult_expectSecondFutureNblToCompleteWithSameResult() {
        FutureNbl<String> f1 = FutureNbl.promise();

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                return FutureNbl.successful(Nullable.createNullable("recovered"));
            }
        });

        f1.completeWithResult("rar");

        assertCompletedFutureNblWithResult(f1, "rar");
        assertCompletedFutureNblWithResult( f2, "rar" );
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureNblWithNull_expectSecondFutureNblToCompleteWithSameResult() {
        FutureNbl<String> f1 = FutureNbl.promise();
        final AtomicBoolean flag = new AtomicBoolean(false);

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                flag.set(true);

                return FutureNbl.successful(Nullable.createNullable("recovered"));
            }
        });

        f1.completeWithResultNbl(Nullable.NULL);

        assertFalse(flag.get());
        assertCompletedFutureNblWithResult(f1, null);
        assertCompletedFutureNblWithResult( f2, null );
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureNblWithPromise_expectSecondFutureNblToBeIncompletePromise() {
        FutureNbl<String> f1 = FutureNbl.promise();

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                return FutureNbl.promise();
            }
        });

        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertFalse(f2.isComplete());
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureNblWithPromiseAndLaterCompleteThat_expectSecondFutureNblToBeCompletedWithResult() {
        FutureNbl<String> f1 = FutureNbl.promise();
        final FutureNbl<String> recoveryPromise = FutureNbl.promise();

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                return recoveryPromise;
            }
        });

        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));
        recoveryPromise.completeWithResult("recovered");

        assertCompletedFutureNblWithResult( f2, "recovered" );
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureNblWithFailureAndHaveRecoveryFunctionThrowAnException_expectFinalFutureNblToHoldFailure() {
        FutureNbl<String> f1 = FutureNbl.promise();

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                throw new IllegalArgumentException("bang");
            }
        });

        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertCompletedFutureNblWithFailure(f2, new Failure(new Failure(this.getClass(),"bomb"), new IllegalArgumentException("bang")));
    }


// FLAT RECOVER ON ALREADY COMPLETED FutureNblS

    @Test
    public void givenCompletedFutureNblWithResult_flatRecover_expectResultToBeUntouched() {
        FutureNbl<String> f1 = FutureNbl.successful( Nullable.createNullable("hello") );

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                return FutureNbl.successful(Nullable.createNullable("recovered"));
            }
        });


        assertCompletedFutureNblWithResult( f2, "hello" );
    }

    @Test
    public void givenCompletedFutureNblWithNull_flatRecover_expectResultToBeUntouched() {
        FutureNbl<String> f1 = FutureNbl.successful( Nullable.NULL );
        final AtomicBoolean flag = new AtomicBoolean(false);

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                flag.set(true);

                return FutureNbl.successful(Nullable.createNullable("recovered"));
            }
        });


        assertFalse(flag.get());
        assertCompletedFutureNblWithResult( f2, null );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_flatRecover_expectResultToBeRecovered() {
        FutureNbl<String> f1 = FutureNbl.failed(new Failure(this.getClass(), "squawk"));

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                return FutureNbl.successful(Nullable.createNullable("recovered"));
            }
        });


        assertCompletedFutureNblWithResult( f2, "recovered" );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_flatRecoverAndHaveTheMappingFunctionThrowAnException_expectResultToBeAFailureHoldingTheExceptionFromTheMappingFunction() {
        FutureNbl<String> f1 = FutureNbl.failed(new Failure(this.getClass(), "squawk"));

        FutureNbl<String> f2 = f1.flatRecover(new Function1<Failure, TryNbl<String>>() {
            public FutureNbl<String> invoke(Failure f) {
                throw new IllegalStateException("whoops");
            }
        });


        assertCompletedFutureNblWithFailure(f2, new Failure(new Failure(this.getClass(),"squawk"), new IllegalStateException("whoops")));
    }


// ONRESULT ON ALREADY COMPLETED FutureNblS

    @Test
    public void givenCompletedFutureNblWithResult_registerOnResultCallback_expectCallback() {
        FutureNbl<String> f1    = FutureNbl.successful(Nullable.createNullable("meadow"));
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });


        assertEquals( 1, count.get() );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_registerOnResultCallback_expectNoCallback() {
        FutureNbl<String> f1    = FutureNbl.failed(new Failure(this.getClass(), "splat"));
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });


        assertEquals( 0, count.get() );
    }


// ONRESULT ON PROMISES

    @Test
    public void givenPromise_registerOnResultCallbackAndCompleteWithResult_expectCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });


        f1.completeWithResult("fortune");

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerOnResultCallbackAndCompleteWithNull_expectCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();

                assertEquals( Nullable.NULL, arg );
            }
        });


        f1.completeWithResultNbl(Nullable.NULL);

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerTwoOnResultCallbackAndCompleteWithResult_expectCallbacks() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });


        f1.completeWithResult("fortune");

        assertEquals( 2, count.get() );
    }

    @Test
    public void givenPromise_registerOnResultCallbackAndCompleteWithFailure_expectNoCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });


        f1.completeWithFailure(new Failure(this.getClass(), "splat"));

        assertEquals( 0, count.get() );
    }

    @Test
    public void givenPromise_registerOnResultCallbackAndCompleteWithResultTwice_expectCallbackOnce() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });


        f1.completeWithResult( "hello" );

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerOnResultCallbackAndOnFailureCallbackAndCompleteWithResult_expectOnResultCallbackOnly() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });

        f1.onFailure(new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.decrementAndGet();
            }
        });


        f1.completeWithResult("hello");

        assertEquals(1, count.get());
    }


// ONFAILURE ON ALREADY COMPLETED FutureNblS

    @Test
    public void givenCompletedFutureNblWithResult_registerOnFailureCallback_expectNoCallback() {
        FutureNbl<String> f1    = FutureNbl.successful(Nullable.createNullable("meadow"));
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        assertEquals( 0, count.get() );
    }

    @Test
    public void givenCompletedFutureNblWithNull_registerOnFailureCallback_expectNoCallback() {
        FutureNbl<String> f1    = FutureNbl.successful(Nullable.NULL);
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        assertEquals( 0, count.get() );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_registerOnFailureCallback_expectCallback() {
        FutureNbl<String> f1    = FutureNbl.failed(new Failure(this.getClass(), "splat"));
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onFailure(new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        assertEquals( 1, count.get() );
    }


// ONFAILURE ON PROMISES

    @Test
    public void givenPromise_registerOnFailureCallbackAndCompleteWithResult_expectNoCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);


        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        f1.completeWithResult("hello");

        assertEquals( 0, count.get() );
    }

    @Test
    public void givenPromise_registerOnFailureCallbackAndCompleteWithNull_expectNoCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);


        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        f1.completeWithResultNbl(Nullable.NULL);

        assertEquals( 0, count.get() );
    }

    @Test
    public void givenPromise_registerOnFailureCallbackAndCompleteWithFailure_expectCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);


        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        f1.completeWithFailure(new Failure(this.getClass(), "splat"));

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerTwoOnFailureCallbackAndCompleteWithFailure_expectCallbacks() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);


        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });

        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        f1.completeWithFailure(new Failure(this.getClass(), "splat"));

        assertEquals( 2, count.get() );
    }

    @Test
    public void givenPromise_registerOnFailureCallbackAndCompleteWithFailureTwice_expectCallbackOnce() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);


        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        f1.completeWithFailure( new Failure(this.getClass(),"bang") );
        f1.completeWithFailure(new Failure(this.getClass(), "splat"));

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerOnFailureCallbackAndOnResultCallbackAndCompleteWithFailure_expectOnFailureCallbackOnly() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<Nullable<String>>() {
            public void invoke(Nullable<String> arg) {
                count.incrementAndGet();
            }
        });

        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.decrementAndGet();
            }
        });


        f1.completeWithFailure( new Failure(this.getClass(),"splat") );

        assertEquals( -1, count.get() );
    }


// ONCOMPLETION ON ALREADY COMPLETED FutureNblS

    @Test
    public void givenCompletedFutureNblWithResult_registerOnCompletionCallback_expectResultCallback() {
        FutureNbl<String> f1    = FutureNbl.successful(Nullable.createNullable("meadow"));
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallbackNbl<String>() {
            public void completedWithNullResult() {
                fail("not expected");
            }

            public void completedWithResult(String result) {
                count.incrementAndGet();
            }

            public void completedWithFailure(Failure f) {
                count.decrementAndGet();
            }
        });


        assertEquals( 1, count.get() );
    }

    @Test
    public void givenCompletedFutureNblWithNull_registerOnCompletionCallback_expectResultCallback() {
        FutureNbl<String> f1    = FutureNbl.successful(Nullable.NULL);
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallbackNbl<String>() {
            public void completedWithNullResult() {
                count.incrementAndGet();
            }

            public void completedWithResult(String result) {
                fail("not expected");
            }

            public void completedWithFailure(Failure f) {
                fail("not expected");
            }
        });


        assertEquals( 1, count.get() );
    }

    @Test
    public void givenCompletedFutureNblWithFailure_registerOnCompletionCallback_expectFailureCallback() {
        FutureNbl<String> f1    = FutureNbl.failed( new Failure(this.getClass(),"splat") );
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallbackNbl<String>() {
            public void completedWithNullResult() {
                fail("not expected");
            }

            public void completedWithResult(String result) {
                count.incrementAndGet();
            }

            public void completedWithFailure(Failure f) {
                count.decrementAndGet();
            }
        });


        assertEquals( -1, count.get() );
    }


// ONCOMPLETION ON PROMISES

    @Test
    public void givenPromise_registerOnCompletionCallbackAndCompleteWithResult_expectResultCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallbackNbl<String>() {
            public void completedWithNullResult() {
                fail("not expected");
            }

            public void completedWithResult(String result) {
                count.incrementAndGet();
            }

            public void completedWithFailure(Failure f) {
                count.decrementAndGet();
            }
        });


        f1.completeWithResult("hello");

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerOnCompletionCallbackAndCompleteWithNull_expectResultCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallbackNbl<String>() {
            public void completedWithNullResult() {
                count.incrementAndGet();
            }

            public void completedWithResult(String result) {
                fail("not expected");
            }

            public void completedWithFailure(Failure f) {
                fail("not expected");
            }
        });


        f1.completeWithResultNbl(Nullable.NULL);

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerOnCompletionCallbackAndCompleteWithFailure_expectFailureCallback() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallbackNbl<String>() {
            public void completedWithNullResult() {
                fail("not expected");
            }

            public void completedWithResult(String result) {
                count.incrementAndGet();
            }

            public void completedWithFailure(Failure f) {
                count.decrementAndGet();
            }
        });


        f1.completeWithFailure( new Failure(this.getClass(),"splat") );

        assertEquals( -1, count.get() );
    }

    @Test
    public void givenPromise_registerTwoOnCompletionCallbacksAndCompleteWithFailure_expectTwoFailureCallbacks() {
        FutureNbl<String> f1    = FutureNbl.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallbackNbl<String>() {
            public void completedWithNullResult() {
                fail("not expected");
            }

            public void completedWithResult(String result) {
                count.incrementAndGet();
            }

            public void completedWithFailure(Failure f) {
                count.decrementAndGet();
            }
        });

        f1.onComplete(new CompletedCallbackNbl<String>() {
            public void completedWithNullResult() {
                fail("not expected");
            }

            public void completedWithResult(String result) {
                count.incrementAndGet();
            }

            public void completedWithFailure(Failure f) {
                count.decrementAndGet();
            }
        });


        f1.completeWithFailure( new Failure(this.getClass(),"splat") );

        assertEquals( -2, count.get() );
    }








    private <T> void assertCompletedFutureNblWithResult( FutureNbl<T> FutureNbl, T expectedResult ) {
        assertTrue( FutureNbl.isComplete() );
        assertTrue( FutureNbl.hasResult() );
        assertFalse( FutureNbl.hasFailure() );

        assertNull( FutureNbl.getFailureNoBlock() );
        assertEquals( expectedResult, FutureNbl.getResultNoBlock().getValueNbl() );
    }

    private <T> void assertCompletedFutureNblWithFailure( FutureNbl<T> FutureNbl, String expectedFailureMessage ) {
        assertCompletedFutureNblWithFailure( FutureNbl, new Failure(this.getClass(), expectedFailureMessage) );
    }

    private <T> void assertCompletedFutureNblWithFailure( FutureNbl<T> FutureNbl, Failure expectedFailure ) {
        assertTrue( FutureNbl.isComplete() );
        assertFalse( FutureNbl.hasResult() );
        assertTrue( FutureNbl.hasFailure() );

        assertNull( FutureNbl.getResultNoBlock() );
        assertEquals( expectedFailure.getSource(), FutureNbl.getFailureNoBlock().getSource() );
        assertEquals( expectedFailure.getMessage(), FutureNbl.getFailureNoBlock().getMessage() );

        assertEquals( expectedFailure, FutureNbl.getFailureNoBlock() );
    }

}

