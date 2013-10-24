package com.mosaic.utils.concurrent;


import com.mosaic.lang.Failure;
import com.mosaic.lang.functional.CompletedCallback;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.VoidFunction1;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 *
 */
@SuppressWarnings({"unchecked", "Convert2Diamond"})
public class FutureTest {

// COMPLETED FUTURE FROM MOMENT OF CONSTRUCTION

    @Test
    public void givenCompletedFutureWithResult_getResult_expectResultImmediately() {
        Future<String> f = Future.successful( "hello" );

        assertEquals( "hello", f.getResultNoBlock() );
    }

    @Test
    public void givenCompletedFutureWithError_getResult_expectNullImmediately() {
        Future<String> f = Future.failed(new Failure(FutureTest.class, "things went south"));

        try {
            f.getResultNoBlock();

            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Unable to retrieve result as future has failed: 'things went south'", e.getMessage());
        }
    }

    @Test
    public void givenCompletedFutureWithResult_getFailure_expectNullImmediately() {
        Future<String> f = Future.successful("hello");

        assertNull(f.getFailureNoBlock());
    }

    @Test
    public void givenCompletedFutureWithError_getFailure_expectFailureImmediately() {
        Future<String> f = Future.failed(new Failure(FutureTest.class, "things went south"));

        assertNotNull(f.getFailureNoBlock());
    }

    @Test
    public void givenCompletedFutureWithResult_isComplete_expectTrue() {
        Future<String> f = Future.successful("hello");

        assertTrue(f.isComplete());
    }

    @Test
    public void givenCompletedFutureWithFailure_isComplete_expectTrue() {
        Future<String> f = Future.failed(new Failure(FutureTest.class, "things went south"));

        assertTrue(f.isComplete());
    }

    @Test
    public void givenCompletedFutureWithResult_hasResult_expectTrue() {
        Future<String> f = Future.successful("hello");

        assertTrue(f.hasResult());
    }

    @Test
    public void givenCompletedFutureWithFailure_hasResult_expectFalse() {
        Future<String> f = Future.failed(new Failure(FutureTest.class, "things went south"));

        assertFalse(f.hasResult());
    }

    @Test
    public void givenCompletedFutureWithResult_hasFailure_expectFalse() {
        Future<String> f = Future.successful("hello");

        assertFalse(f.hasFailure());
    }

    @Test
    public void givenCompletedFutureWithFailure_hasFailure_expectTrue() {
        Future<String> f = Future.failed( new Failure(FutureTest.class, "things went south") );

        assertTrue(f.hasFailure());
    }



    @Test
    public void givenCompletedFutureWithResult_mapResult_expectNewFutureWithMappedResult() {
        Future<String> f1 = Future.successful("hello");
        Future<Integer> f2 = f1.mapResult( new Function1<String,Integer>() {
            public Integer invoke( String v ) {
                return v.length();
            }
        });

        assertCompletedFutureWithResult( f1, "hello" );
        assertCompletedFutureWithResult( f2, 5 );
    }

    @Test
    public void givenCompletedFutureWithFailure_mapResult_expectSameFutureBackAsFailuresANotMapped() {
        Future<String> f1 = Future.failed(new Failure(this.getClass(), "splat"));
        Future<Integer> f2 = f1.mapResult( new Function1<String,Integer>() {
            public Integer invoke( String v ) {
                return v.length();
            }
        });

        assertSame( f1, f2 );
    }

    @Test
    public void givenCompletedFutureWithResult_mapFailure_expectSameFutureBackAsResultsANotMapped() {
        Future<String> f1 = Future.successful("hello");
        Future<String> f2 = f1.mapFailure( new Function1<Failure,Failure>() {
            public Failure invoke( Failure v ) {
                return new Failure(v.getSource(), "modified message");
            }
        });

        assertSame( f1, f2 );
    }

    @Test
    public void givenCompletedFutureWithFailure_mapFailure_expectNewFutureWithMappedFailure() {
        Future<String> f1 = Future.failed(new Failure(this.getClass(), "splat"));
        Future<String> f2 = f1.mapFailure( new Function1<Failure,Failure>() {
            public Failure invoke( Failure v ) {
                return new Failure(v.getSource(), "modified message");
            }
        });

        assertCompletedFutureWithFailure( f1, "splat" );
        assertCompletedFutureWithFailure( f2, new Failure(new Failure(this.getClass(), "splat"), new Failure(this.getClass(),"modified message")) );
    }

    @Test
    public void givenCompletedFutureWithResult_recover_expectSameFutureBackAsThereWasNothingToRecover() {
        Future<String> f1 = Future.successful("hello");
        Future<String> f2 = f1.recover( new Function1<Failure,String>() {
            public String invoke( Failure f ) {
                return f.getMessage();
            }
        });

        assertSame( f1, f2 );
    }

    @Test
    public void givenCompletedFutureWithFailure_recover_expectNewFutureWithResultValue() {
        Future<String> f1 = Future.failed(new Failure(this.getClass(), "spam and eggs"));
        Future<String> f2 = f1.recover( new Function1<Failure,String>() {
            public String invoke( Failure f ) {
                return f.getMessage();
            }
        });

        assertCompletedFutureWithFailure( f1, "spam and eggs" );
        assertCompletedFutureWithResult( f2, "spam and eggs" );
    }




// COMPLETE A PROMISE

    @Test
    public void givenPromise_isComplete_expectFalse() {
        Future<String> promise = new Future<String>();

        assertFalse( promise.isComplete() );
    }

    @Test
    public void givenPromise_hasResult_expectFalse() {
        Future<String> promise = new Future<String>();

        assertFalse( promise.hasResult() );
    }

    @Test
    public void givenPromise_hasFailure_expectFalse() {
        Future<String> promise = new Future<String>();

        assertFalse(promise.hasFailure());
    }

    @Test
    public void givenPromise_completeWithResult_expectGetResultToReturnValue() {
        Future<String> promise = new Future<String>();

        promise.completeWithResult( "hello" );

        assertTrue( promise.isComplete() );
        assertTrue( promise.hasResult() );
        assertEquals("hello", promise.getResultNoBlock());
    }

    @Test
    public void givenPromise_completeWithResult_expectGetErrorToReturnNull() {
        Future<String> promise = new Future<String>();

        promise.completeWithResult("hello");

        assertNull(promise.getFailureNoBlock());
    }

    @Test
    public void givenPromise_completeWithError_expectGetResultToReturnNull() {
        Future<String> promise = new Future<String>();

        promise.completeWithFailure(new Failure(FutureTest.class, "splat"));

        try {
            promise.getResultNoBlock();

            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Unable to retrieve result as future has failed: 'splat'", e.getMessage());
        }
    }

    @Test
    public void givenPromise_completeWithError_expectGetErrorToReturnFailure() {
        Future<String> promise = new Future<String>();

        Failure failure = new Failure(FutureTest.class, "splat");
        promise.completeWithFailure(failure);

        assertSame(failure, promise.getFailureNoBlock());
    }


    @Test
    public void givenPromise_mapResultThenCompleteFirstFuture_expectSecondFutureToBeCompletedWithMappedResult() {
        Future<String> promise = new Future<String>();
        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureWithResult( promise, "hello world" );
        assertCompletedFutureWithResult( f2, 11 );
    }

    @Test
    public void givenPromise_mapResultThenCompleteFirstFutureWithFailure_expectSecondFutureToBeCompletedWithFailure() {
        Future<String> promise = new Future<String>();
        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureWithResult( promise, "hello world" );
        assertCompletedFutureWithResult( f2, 11 );
    }

    @Test
    public void givenPromise_mapResultThenCompleteFirstFutureWithResultAndHaveMappingFunctionThrowException_expectSecondFutureToBeCompletedWithFailure() {
        Future<String> promise = new Future<String>();
        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                throw new RuntimeException( "splat" );
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureWithResult( promise, "hello world" );
        assertCompletedFutureWithFailure( f2, new Failure(new RuntimeException("splat")) );
    }

    @Test
    public void givenPromise_mapResultTwiceInChainThenCompleteFirstFuture_expectThirdFutureToBeCompletedWithMappedResult() {
        Future<String> promise = new Future<String>();
        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });
        Future<Integer> f3 = f2.mapResult( new Function1<Integer, Integer>() {
            public Integer invoke(Integer arg) {
                return arg * 2;
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureWithResult( promise, "hello world" );
        assertCompletedFutureWithResult( f2, 11 );
        assertCompletedFutureWithResult( f3, 22 );
    }

    @Test
    public void givenPromise_mapFirstFutureTwiceNotChained_expectFirstFutureToHandleMultipleCallbacksAndThusCompleteEachFuture() {
        Future<String> promise = new Future<String>();

        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        Future<String> f3 = promise.mapResult( new Function1<String, String>() {
            public String invoke(String arg) {
                return arg + " " + arg;
            }
        });

        promise.completeWithResult( "hello world" );

        assertCompletedFutureWithResult( promise, "hello world" );
        assertCompletedFutureWithResult( f2, 11 );
        assertCompletedFutureWithResult( f3, "hello world hello world" );
    }

    @Test
    public void givenPromise_mapResultTwiceThenCompleteFirstFutureWithFailure_expectThirdFutureToBeCompletedWithFailure() {
        Future<String> promise = new Future<String>();
        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });
        Future<Integer> f3 = f2.mapResult( new Function1<Integer, Integer>() {
            public Integer invoke(Integer arg) {
                return arg * 2;
            }
        });

        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureWithFailure( promise, "splat" );
        assertCompletedFutureWithFailure( f2, "splat" );
        assertCompletedFutureWithFailure( f3, "splat" );
    }


    @Test
    public void givenPromise_mapResultThenRecoverCompleteFirstFutureWithResult_expectThirdFutureToBeCompleteWithResult() {
        Future<String> promise = new Future<String>();

        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        Future<Integer> f3 = f2.recover( new Function1<Failure, Integer>() {
            public Integer invoke(Failure f) {
                return -1;
            }
        });

        promise.completeWithResult( "rock on" );

        assertCompletedFutureWithResult( promise, "rock on" );
        assertCompletedFutureWithResult( f2, 7 );
        assertCompletedFutureWithResult( f3, 7 );
    }

    @Test
    public void givenPromise_mapResultThenRecoverCompleteFirstFutureWithFailure_expectThirdFutureToBeCompleteWithRecoveredResult() {
        Future<String> promise = new Future<String>();

        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        Future<Integer> f3 = f2.recover( new Function1<Failure, Integer>() {
            public Integer invoke(Failure f) {
                return -1;
            }
        });

        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureWithFailure( promise, "splat" );
        assertCompletedFutureWithFailure( f2, "splat" );
        assertCompletedFutureWithResult( f3, -1 );
    }

    @Test
    public void givenPromise_mapResultThenRecoverCompleteFirstFutureWithFailureAndHaveRecoverFunctionThrowException_expectThirdFutureToBeCompleteWithFailureFromTheRecoverFunction() {
        Future<String> promise = new Future<String>();

        Future<Integer> f2 = promise.mapResult( new Function1<String, Integer>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        Future<Integer> f3 = f2.recover( new Function1<Failure, Integer>() {
            public Integer invoke(Failure f) {
                throw new RuntimeException( "woops" );
            }
        });

        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureWithFailure( promise, "splat" );
        assertCompletedFutureWithFailure( f2, "splat" );
        assertCompletedFutureWithFailure( f3, new Failure(new Failure(this.getClass(),"splat"), new RuntimeException("woops") ) );
    }




    @Test
    public void givenPromise_mapFailureThenCompleteFirstFutureWithFailure_expectSecondFutureToCompleteWithMappedFuture() {
        Future<String> promise = new Future<String>();

        Future<String> f2 = promise.mapFailure( new Function1<Failure, Failure>() {
            public Failure invoke( Failure f ) {
                return new Failure( String.class, "translation" );
            }
        });


        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureWithFailure( promise, "splat" );
        assertCompletedFutureWithFailure( f2, new Failure(new Failure(this.getClass(),"splat"), new Failure(String.class, "translation") ) );
    }

    @Test
    public void givenPromise_mapFailureThenCompleteFirstFutureWithFailureAndHaveTheMappingFunctionThrowAnException_expectSecondFutureToCompleteWithTheExceptionFromTheMappingFunction() {
        Future<String> promise = new Future<String>();

        Future<String> f2 = promise.mapFailure( new Function1<Failure, Failure>() {
            public Failure invoke( Failure f ) {
                throw new RuntimeException( "woops" );
            }
        });


        promise.completeWithFailure( new Failure(this.getClass(), "splat") );

        assertCompletedFutureWithFailure( promise, "splat" );
        assertCompletedFutureWithFailure( f2, new Failure(new Failure(this.getClass(),"splat"), new RuntimeException("woops") ) );
    }

    @Test
    public void givenPromise_mapFailureThenCompleteFirstFutureWithResult_expectSecondFutureToCompleteWithResult() {
        Future<String> promise = new Future<String>();

        Future<String> f2 = promise.mapFailure( new Function1<Failure, Failure>() {
            public Failure invoke( Failure f ) {
                throw new RuntimeException( "woops" );
            }
        });


        promise.completeWithResult( "rar" );

        assertCompletedFutureWithResult( promise, "rar" );
        assertCompletedFutureWithResult( f2, "rar" );
        assertNotSame( promise, f2 );
    }


// FLAT MAP RESULTS ON PROMISES

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureWithFailure_expectSecondFutureToCompleteWithFailure() {
        Future<String> promise = new Future<String>();

        Future<Integer> f2 = promise.flatMapResult( new Function1<String, Try<Integer>>() {
            public Try<Integer> invoke( String v ) {
                return Future.successful(v.length());
            }
        });


        promise.completeWithFailure( new Failure(this.getClass(), "bomb") );

        assertCompletedFutureWithFailure( promise, "bomb" );
        assertCompletedFutureWithFailure( f2, "bomb" );
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureWithCompletedResult_expectSecondFutureToCompleteWithMappedResult() {
        Future<String> promise = new Future<String>();

        Future<Integer> f2 = promise.flatMapResult( new Function1<String, Try<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return Future.successful(v.length());
            }
        });


        promise.completeWithResult("rar");

        assertCompletedFutureWithResult(promise, "rar");
        assertCompletedFutureWithResult(f2, 3);
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureWithPromise_expectSecondFutureToBeIncompletePromise() {
        Future<String> promise = new Future<String>();

        Future<Integer> f2 = promise.flatMapResult( new Function1<String, Try<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return Future.promise();
            }
        });


        promise.completeWithResult("rar");

        assertCompletedFutureWithResult(promise, "rar");
        assertFalse(f2.isComplete());
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureWithPromiseThenComplete_expectSecondFutureToHaveSameResultAsTheCompletedPromise() {
        Future<String>  promise      = new Future<String>();
        final Future<Integer> childPromise = Future.promise();

        Future<Integer> f2 = promise.flatMapResult( new Function1<String, Try<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return childPromise;
            }
        });


        promise.completeWithResult( "rar" );
        childPromise.completeWithResult(42);

        assertCompletedFutureWithResult(promise, "rar");
        assertCompletedFutureWithResult(f2, 42);
    }

    @Test
    public void givenPromise_flatMapResultThenCompleteFirstFutureWithResultAndHaveMappingFunctionThrowAnException_expectFinalFutureToHoldFailure() {
        Future<String> promise = new Future<String>();

        Future<Integer> f2 = promise.flatMapResult( new Function1<String, Try<Integer>>() {
            public Future<Integer> invoke( String v ) {
                throw new IllegalStateException( "bang" );
            }
        });


        promise.completeWithResult( "rar" );

        assertCompletedFutureWithResult( promise, "rar" );
        assertCompletedFutureWithFailure(f2, new Failure(new IllegalStateException("bang")));
    }


// FLAT MAP RESULTS ON ALREADY COMPLETED FUTURES

    @Test
    public void givenCompletedFutureWithResult_flatMapResult_expectResultToBeTheResultFromTheMappingFunction() {
        Future<String> f1 = Future.successful("hello");

        Future<Integer> f2 = f1.flatMapResult( new Function1<String, Try<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return Future.successful(v.length());
            }
        });


        assertCompletedFutureWithResult(f1, "hello");
        assertCompletedFutureWithResult(f2, 5);
    }

    @Test
    public void givenCompletedFutureWithResult_flatMapResultAndHaveTheMappingFunctionThrowAnException_expectResultToBeAFailureHoldingTheExceptionFromTheMappingFunction() {
        Future<String> f1 = Future.successful("hello");

        Future<Integer> f2 = f1.flatMapResult( new Function1<String, Try<Integer>>() {
            public Future<Integer> invoke( String v ) {
                throw new IllegalArgumentException("what are you trying to do to me");
            }
        });


        assertCompletedFutureWithResult(f1, "hello");
        assertCompletedFutureWithFailure(f2, new Failure(new IllegalArgumentException("what are you trying to do to me")));
    }

    @Test
    public void givenCompletedFutureWithFailure_flatMapResult_expectResultToBeFailureFromFirstFutureAsTheMappingFunctionWillNotBeCalled() {
        Future<String> f1 = Future.failed(new Failure(this.getClass(), "splat"));

        Future<Integer> f2 = f1.flatMapResult( new Function1<String, Try<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return Future.successful(v.length());
            }
        });


        assertCompletedFutureWithFailure(f1, "splat");
        assertCompletedFutureWithFailure(f2, "splat");
    }


// FLAT MAP FAILURES ON PROMISES

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureWithFailure_expectSecondFutureToCompleteWithMappedFailure() {
        Future<String> promise = new Future<String>();

        Future<String> f2 = promise.flatMapFailure( new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke( Failure f ) {
                return Future.successful(new Failure(f, new Failure(FutureTest.this.getClass(), "mapped")));
            }
        });


        promise.completeWithFailure( new Failure(this.getClass(), "bomb") );

        assertCompletedFutureWithFailure( promise, "bomb" );
        assertCompletedFutureWithFailure( f2, new Failure(new Failure(this.getClass(),"bomb"), new Failure(this.getClass(), "mapped")) );
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureWithFailureMapperReturnsResultAsFailure_expectSecondFutureToCompleteWithMappedFailure() {
        Future<String> promise = new Future<String>();

        Future<String> f2 = promise.flatMapFailure( new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke( Failure f ) {
                return Future.failed( new Failure(f, new Failure(FutureTest.this.getClass(), "mapped")) );
            }
        });


        promise.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertCompletedFutureWithFailure(promise, "bomb");
        assertCompletedFutureWithFailure(f2, new Failure(new Failure(this.getClass(), "bomb"), new Failure(this.getClass(), "mapped")));
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureWithCompletedResult_expectSecondFutureToCompleteWithSameResult() {
        Future<String> f1 = new Future<String>();

        Future<String> f2 = f1.flatMapFailure( new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke( Failure f ) {
                return Future.failed(new Failure(f, new Failure(FutureTest.this.getClass(), "mapped")));
            }
        });


        f1.completeWithResult("hello");

        assertCompletedFutureWithResult(f1, "hello");
        assertCompletedFutureWithResult(f2, "hello");
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureWithPromise_expectSecondFutureToBeIncompletePromise() {
        Future<String> f1 = new Future<String>();

        Future<String> f2 = f1.flatMapFailure( new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke( Failure f ) {
                return Future.promise();
            }
        });


        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertCompletedFutureWithFailure(f1, "bomb");
        assertFalse(f2.isComplete());
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureWithPromiseThenComplete_expectSecondFutureToHaveSameResultAsTheCompletedPromise() {
        Future<String>  f1           = new Future<String>();
        final Future<Failure> childPromise = Future.promise();

        Future<String> f2 = f1.flatMapFailure( new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke( Failure f ) {
                return childPromise;
            }
        });


        f1.completeWithFailure( new Failure(this.getClass(), "bomb") );
        childPromise.completeWithResult(new Failure(this.getClass(), "mapped"));

        assertCompletedFutureWithFailure(f1, "bomb");
        assertCompletedFutureWithFailure(f2, new Failure(new Failure(this.getClass(), "bomb"), new Failure(this.getClass(), "mapped")));
    }

    @Test
    public void givenPromise_flatMapFailureThenCompleteFirstFutureWithFailureAndHaveMappingFunctionThrowAnException_expectFinalFutureToHoldFailure() {
        Future<String> f1 = new Future<String>();

        Future<String> f2 = f1.flatMapFailure(new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke(Failure f) {
                throw new RuntimeException("whoops");
            }
        });


        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertCompletedFutureWithFailure(f1, "bomb");
        assertCompletedFutureWithFailure( f2, new Failure(new Failure(this.getClass(), "bomb"), new RuntimeException("whoops")) );
    }


// FLAT MAP FAILURES ON ALREADY COMPLETED FUTURES

    @Test
    public void givenCompletedFutureWithResult_flatMapFailure_expectResultToBeUntouched() {
        Future<String> f1 = Future.successful("up and at 'em");

        Future<String> f2 = f1.flatMapFailure( new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke( Failure f ) {
                return Future.failed( new Failure(f, new Failure(FutureTest.this.getClass(), "mapped")) );
            }
        });


        assertCompletedFutureWithResult( f1, "up and at 'em" );
        assertCompletedFutureWithResult( f2, "up and at 'em" );
    }

    @Test
    public void givenCompletedFutureWithFailure_flatMapFailure_expectResultToBeMappedFailure() {
        Future<String> f1 = Future.failed(new Failure(this.getClass(), "bomb"));

        Future<String> f2 = f1.flatMapFailure( new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke( Failure f ) {
                return Future.failed( new Failure(f, new Failure(FutureTest.this.getClass(), "mapped")) );
            }
        });


        assertCompletedFutureWithFailure(f1, "bomb");
        assertCompletedFutureWithFailure(f2, new Failure(new Failure(this.getClass(), "bomb"), new Failure(this.getClass(), "mapped")));
    }

    @Test
    public void givenCompletedFutureWithFailure_flatMapFailureAndHaveTheMappingFunctionThrowAnException_expectResultToBeAFailureHoldingTheExceptionFromTheMappingFunction() {
        Future<String> f1 = Future.failed(new Failure(this.getClass(), "bomb"));

        Future<String> f2 = f1.flatMapFailure( new Function1<Failure, Try<Failure>>() {
            public Future<Failure> invoke( Failure f ) {
                throw new RuntimeException("whoops");
            }
        });


        assertCompletedFutureWithFailure(f1, "bomb");
        assertCompletedFutureWithFailure(f2, new Failure(new Failure(this.getClass(), "bomb"), new RuntimeException("whoops")));
    }



// FLAT RECOVER ON PROMISES

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureWithFailure_expectSecondFutureToCompleteWithResultFromRecoveryFunction() {
        Future<String> f1 = Future.promise();

        Future<String> f2 = f1.flatRecover(new Function1<Failure, Try<String>>() {
            public Future<String> invoke(Failure f) {
                return Future.successful("recovered");
            }
        });

        f1.completeWithFailure( new Failure(this.getClass(), "bomb") );

        assertCompletedFutureWithFailure( f1, "bomb" );
        assertCompletedFutureWithResult( f2, "recovered" );
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureWithCompletedResult_expectSecondFutureToCompleteWithSameResult() {
        Future<String> f1 = Future.promise();

        Future<String> f2 = f1.flatRecover(new Function1<Failure, Try<String>>() {
            public Future<String> invoke(Failure f) {
                return Future.successful("recovered");
            }
        });

        f1.completeWithResult("rar");

        assertCompletedFutureWithResult(f1, "rar");
        assertCompletedFutureWithResult( f2, "rar" );
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureWithPromise_expectSecondFutureToBeIncompletePromise() {
        Future<String> f1 = Future.promise();

        Future<String> f2 = f1.flatRecover(new Function1<Failure, Try<String>>() {
            public Future<String> invoke(Failure f) {
                return Future.promise();
            }
        });

        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertFalse(f2.isComplete());
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureWithPromiseAndLaterCompleteThat_expectSecondFutureToBeCompletedWithResult() {
        Future<String> f1 = Future.promise();
        final Future<String> recoveryPromise = Future.promise();

        Future<String> f2 = f1.flatRecover(new Function1<Failure, Try<String>>() {
            public Future<String> invoke(Failure f) {
                return recoveryPromise;
            }
        });

        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));
        recoveryPromise.completeWithResult("recovered");

        assertCompletedFutureWithResult( f2, "recovered" );
    }

    @Test
    public void givenPromise_flatRecoverThenCompleteFirstFutureWithFailureAndHaveRecoveryFunctionThrowAnException_expectFinalFutureToHoldFailure() {
        Future<String> f1 = Future.promise();

        Future<String> f2 = f1.flatRecover(new Function1<Failure, Try<String>>() {
            public Future<String> invoke(Failure f) {
                throw new IllegalArgumentException("bang");
            }
        });

        f1.completeWithFailure(new Failure(this.getClass(), "bomb"));

        assertCompletedFutureWithFailure(f2, new Failure(new Failure(this.getClass(),"bomb"), new IllegalArgumentException("bang")));
    }


// FLAT RECOVER ON ALREADY COMPLETED FUTURES

    @Test
    public void givenCompletedFutureWithResult_flatRecover_expectResultToBeUntouched() {
        Future<String> f1 = Future.successful( "hello" );

        Future<String> f2 = f1.flatRecover(new Function1<Failure, Try<String>>() {
            public Future<String> invoke(Failure f) {
                return Future.successful("recovered");
            }
        });


        assertCompletedFutureWithResult( f2, "hello" );
    }

    @Test
    public void givenCompletedFutureWithFailure_flatRecover_expectResultToBeRecovered() {
        Future<String> f1 = Future.failed(new Failure(this.getClass(), "squawk"));

        Future<String> f2 = f1.flatRecover(new Function1<Failure, Try<String>>() {
            public Future<String> invoke(Failure f) {
                return Future.successful("recovered");
            }
        });


        assertCompletedFutureWithResult( f2, "recovered" );
    }

    @Test
    public void givenCompletedFutureWithFailure_flatRecoverAndHaveTheMappingFunctionThrowAnException_expectResultToBeAFailureHoldingTheExceptionFromTheMappingFunction() {
        Future<String> f1 = Future.failed(new Failure(this.getClass(), "squawk"));

        Future<String> f2 = f1.flatRecover(new Function1<Failure, Try<String>>() {
            public Future<String> invoke(Failure f) {
                throw new IllegalStateException("whoops");
            }
        });


        assertCompletedFutureWithFailure(f2, new Failure(new Failure(this.getClass(),"squawk"), new IllegalStateException("whoops")));
    }


// ONRESULT ON ALREADY COMPLETED FUTURES

    @Test
    public void givenCompletedFutureWithResult_registerOnResultCallback_expectCallback() {
        Future<String> f1    = Future.successful("meadow");
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
                count.incrementAndGet();
            }
        });


        assertEquals( 1, count.get() );
    }

    @Test
    public void givenCompletedFutureWithFailure_registerOnResultCallback_expectNoCallback() {
        Future<String> f1    = Future.failed(new Failure(this.getClass(), "splat"));
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
                count.incrementAndGet();
            }
        });


        assertEquals( 0, count.get() );
    }


// ONRESULT ON PROMISES

    @Test
    public void givenPromise_registerOnResultCallbackAndCompleteWithResult_expectCallback() {
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
                count.incrementAndGet();
            }
        });


        f1.completeWithResult("fortune");

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerTwoOnResultCallbackAndCompleteWithResult_expectCallbacks() {
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
                count.incrementAndGet();
            }
        });

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
                count.incrementAndGet();
            }
        });


        f1.completeWithResult("fortune");

        assertEquals( 2, count.get() );
    }

    @Test
    public void givenPromise_registerOnResultCallbackAndCompleteWithFailure_expectNoCallback() {
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
                count.incrementAndGet();
            }
        });


        f1.completeWithFailure(new Failure(this.getClass(), "splat"));

        assertEquals( 0, count.get() );
    }

    @Test
    public void givenPromise_registerOnResultCallbackAndCompleteWithResultTwice_expectCallbackOnce() {
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
                count.incrementAndGet();
            }
        });


        f1.completeWithResult( "hello" );

        assertEquals( 1, count.get() );
    }

    @Test
    public void givenPromise_registerOnResultCallbackAndOnFailureCallbackAndCompleteWithResult_expectOnResultCallbackOnly() {
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
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


// ONFAILURE ON ALREADY COMPLETED FUTURES

    @Test
    public void givenCompletedFutureWithResult_registerOnFailureCallback_expectNoCallback() {
        Future<String> f1    = Future.successful("meadow");
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onFailure( new VoidFunction1<Failure>() {
            public void invoke(Failure f) {
                count.incrementAndGet();
            }
        });


        assertEquals( 0, count.get() );
    }

    @Test
    public void givenCompletedFutureWithFailure_registerOnFailureCallback_expectCallback() {
        Future<String> f1    = Future.failed(new Failure(this.getClass(), "splat"));
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
        Future<String> f1    = Future.promise();
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
    public void givenPromise_registerOnFailureCallbackAndCompleteWithFailure_expectCallback() {
        Future<String> f1    = Future.promise();
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
        Future<String> f1    = Future.promise();
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
        Future<String> f1    = Future.promise();
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
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onResult( new VoidFunction1<String>() {
            public void invoke(String arg) {
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


// ONCOMPLETION ON ALREADY COMPLETED FUTURES

    @Test
    public void givenCompletedFutureWithResult_registerOnCompletionCallback_expectResultCallback() {
        Future<String> f1    = Future.successful("meadow");
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallback<String>() {
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
    public void givenCompletedFutureWithFailure_registerOnCompletionCallback_expectFailureCallback() {
        Future<String> f1    = Future.failed( new Failure(this.getClass(),"splat") );
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallback<String>() {
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
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallback<String>() {
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
    public void givenPromise_registerOnCompletionCallbackAndCompleteWithFailure_expectFailureCallback() {
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallback<String>() {
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
        Future<String> f1    = Future.promise();
        final AtomicInteger  count = new AtomicInteger(0);

        f1.onComplete(new CompletedCallback<String>() {
            public void completedWithResult(String result) {
                count.incrementAndGet();
            }

            public void completedWithFailure(Failure f) {
                count.decrementAndGet();
            }
        });

        f1.onComplete(new CompletedCallback<String>() {
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

// JOINALL

    // joinAll( Iterable<Future<T>> futures )

    @Test
    public void givenNoFutures_joinAll_expectFutureToBeCompletedHoldingAnEmptyList() {
        Future<List<String>> barrierFuture = Future.joinAll();

        assertCompletedFutureWithResult( barrierFuture, Arrays.<String>asList() );
    }

    @Test
    public void givenOneFuture_joinAll_expectFutureToBeIncomplete() {
        Future<String> f1 = Future.promise();

        Future<List<String>> barrierFuture = Future.joinAll( f1 );

        assertFalse( barrierFuture.isComplete() );
    }

    @Test
    public void givenOneFuture_joinAllAndCompleteFutureWithResult_expectFutureToBeCompletedWithListContainingResultsInOrder() {
        Future<String> f1 = Future.promise();

        Future<List<String>> barrierFuture = Future.joinAll( f1 );

        f1.completeWithResult( "one" );

        assertCompletedFutureWithResult( barrierFuture, Arrays.asList("one") );
    }

    @Test
    public void givenTwoFutures_joinAllAndCompleteOneFutureWithResult_expectFutureToBeIncomplete() {
        Future<String> f1 = Future.promise();
        Future<String> f2 = Future.promise();

        Future<List<String>> barrierFuture = Future.joinAll( f1, f2 );

        f1.completeWithResult( "one" );

        assertFalse( barrierFuture.isComplete() );
    }

    @Test
    public void givenTwoFutures_joinAllAndCompleteAllFuturesWithResult_expectFutureToBeCompletedWithListContainingResultsInOrder() {
        Future<String> f1 = Future.promise();
        Future<String> f2 = Future.promise();

        Future<List<String>> barrierFuture = Future.joinAll( f1, f2 );

        f2.completeWithResult( "two" );
        f1.completeWithResult( "one" );

        assertCompletedFutureWithResult( barrierFuture, Arrays.asList("one","two") );
    }

    @Test
    public void givenTwoFutures_joinAllAndCompleteOneFutureWithResultAndOtherWithFailure_expectFutureToBeCompletedWithFailure() {
        Future<String> f1 = Future.promise();
        Future<String> f2 = Future.promise();

        Future<List<String>> barrierFuture = Future.joinAll( f1, f2 );

        f2.completeWithResult( "two" );
        f1.completeWithFailure( new Failure(this.getClass(), "first failed") );

        assertCompletedFutureWithFailure( barrierFuture, "first failed" );
    }

    @Test
    public void givenTwoFutures_joinAllAndCompleteOneFutureWithFailure_expectFutureToBeCompletedWithFailure() {
        Future<String> f1 = Future.promise();
        Future<String> f2 = Future.promise();

        Future<List<String>> barrierFuture = Future.joinAll( f1, f2 );

        f1.completeWithFailure( new Failure(this.getClass(), "first failed") );

        assertCompletedFutureWithFailure( barrierFuture, "first failed" );
    }

    @Test
    public void givenTwoFutures_joinAllAndCompleteBothFutureWithFailures_expectFutureToBeCompletedWithFirstFailure() {
        Future<String> f1 = Future.promise();
        Future<String> f2 = Future.promise();

        Future<List<String>> barrierFuture = Future.joinAll( f1, f2 );

        f2.completeWithFailure( new Failure(this.getClass(), "second failed") );
        f1.completeWithFailure( new Failure(this.getClass(), "first failed") );

        assertCompletedFutureWithFailure( barrierFuture, "second failed" );
    }


// PARALLELIZE EACH

    @Test
    public void givenNoValues_parallelizeEach_expectFutureToBeCompletedWithAnEmptyListOfFutures() {
        Future<List<Integer>> barrierFuture = Future.parallelizeEach( Arrays.<String>asList(), new Function1<String,Future<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return Future.successful( v.length() );
            }
        });


        assertCompletedFutureWithResult( barrierFuture, Arrays.<Integer>asList() );
    }

    @Test
    public void givenOneValue_parallelizeEach_expectFutureNotBeCompleted() {
        Future<List<Integer>> barrierFuture = Future.parallelizeEach( Arrays.asList("one"), new Function1<String,Future<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return Future.promise();
            }
        });


        assertFalse( barrierFuture.isComplete() );
    }

    @Test
    public void givenOneValue_parallelizeEachAndCompleteFutureWithResult_expectFutureToContainListOfResultsInOrder() {
        Future<List<Integer>> barrierFuture = Future.parallelizeEach( Arrays.asList("i"), new Function1<String,Future<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return Future.successful(v.length());
            }
        });


        assertCompletedFutureWithResult( barrierFuture, Arrays.asList(1) );
    }

    @Test
    public void givenTwoValues_parallelizeEachAndCompleteBothFuturesWithResult_expectFutureToContainListOfResultsInOrder() {
        Future<List<Integer>> barrierFuture = Future.parallelizeEach( Arrays.asList("i","ii"), new Function1<String,Future<Integer>>() {
            public Future<Integer> invoke( String v ) {
                return Future.successful(v.length());
            }
        });


        assertCompletedFutureWithResult( barrierFuture, Arrays.asList(1,2) );
    }

    @Test
    public void givenTwoValues_parallelizeEachAndCompleteOneFutureWithFailure_expectFutureToContainFailure() {
        Future<List<Integer>> barrierFuture = Future.parallelizeEach( Arrays.asList("i","ii"), new Function1<String,Future<Integer>>() {
            public Future<Integer> invoke( String v ) {
                switch (v.length()) {
                    case 1:
                        return Future.successful(v.length());
                    case 2:
                        return Future.failed(new Failure(FutureTest.this.getClass(),"splat"));
                }

                return null;
            }
        });


        assertCompletedFutureWithFailure( barrierFuture, "splat" );
    }

    @Test
    public void givenTwoValues_parallelizeEachAndCompleteOneFutureWithResult_expectFutureToNotBeCompleted() {
        Future<List<Integer>> barrierFuture = Future.parallelizeEach( Arrays.asList("i","ii"), new Function1<String,Future<Integer>>() {
            public Future<Integer> invoke( String v ) {
                switch (v.length()) {
                    case 1:
                        return Future.successful(v.length());
                    case 2:
                        return Future.promise();
                }

                return null;
            }
        });


        assertFalse( barrierFuture.isComplete() );
    }


// toTryNbl

    @Test
    public void givenPromise_toTryNbl_expectIncompleteFutureNbl() {
        Future<String>    f1 = Future.promise();
        FutureNbl<String> f2 = f1.toTryNbl();

        assertFalse( f2.isComplete() );
    }

    @Test
    public void givenResult_toTryNbl_expectFutureNblWithResult() {
        Future<String>    f1 = Future.successful("foo");
        FutureNbl<String> f2 = f1.toTryNbl();

        FutureNblTest.assertCompletedFutureNblWithResult( f2, "foo" );
    }

    @Test
    public void givenFailure_toTryNbl_expectFutureNblWithFailure() {
        Future<String>    f1 = Future.failed(new Failure(FutureNblTest.class, "splat"));
        FutureNbl<String> f2 = f1.toTryNbl();

        FutureNblTest.assertCompletedFutureNblWithFailure( f2, f1.getFailureNoBlock() );
    }

    @Test
    public void givenPromise_toTryNblThenCompleteWithResult_expectFutureNblWithResult() {
        Future<String>    f1 = Future.promise();
        FutureNbl<String> f2 = f1.toTryNbl();

        f1.completeWithResult("foo");

        FutureNblTest.assertCompletedFutureNblWithResult( f2, "foo" );
    }

    @Test
    public void givenPromise_toTryNblThenCompleteWithResult_expectFutureNblWithFailure() {
        Future<String>    f1 = Future.promise();
        FutureNbl<String> f2 = f1.toTryNbl();

        f1.completeWithFailure(new Failure(FutureNblTest.class, "splat"));

        FutureNblTest.assertCompletedFutureNblWithFailure( f2, f1.getFailureNoBlock() );
    }





    private <T> void assertCompletedFutureWithResult( Future<T> future, T expectedResult ) {
        assertTrue( future.isComplete() );
        assertTrue( future.hasResult() );
        assertFalse( future.hasFailure() );

        assertEquals( expectedResult, future.getResultNoBlock() );
    }

    private <T> void assertCompletedFutureWithFailure( Future<T> future, String expectedFailureMessage ) {
        assertCompletedFutureWithFailure( future, new Failure(this.getClass(), expectedFailureMessage) );
    }

    private <T> void assertCompletedFutureWithFailure( Future<T> future, Failure expectedFailure ) {
        assertTrue( future.isComplete() );
        assertFalse( future.hasResult() );
        assertTrue( future.hasFailure() );

        assertEquals( expectedFailure.getSource(), future.getFailureNoBlock().getSource() );
        assertEquals( expectedFailure.getMessage(), future.getFailureNoBlock().getMessage() );

        assertEquals( expectedFailure, future.getFailureNoBlock() );
    }

}

