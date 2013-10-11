package com.mosaic.lang;


import com.mosaic.lang.function.Function0;
import com.mosaic.lang.function.Function1;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;


/**
 *
 */
@SuppressWarnings({"AssertEqualsBetweenInconvertibleTypes", "unchecked"})
public class NullableTest {


// NULL VALUE TESTS

    @Test
    public void givenNullValue_callGetValueNbl_expectNull() {
        Nullable<String> v = Nullable.createNullable(null);

        assertNull( v.getValueNbl() );
    }

    @Test
    public void givenNullValue_callIsNull_expectTrue() {
        Nullable<String> v = Nullable.createNullable(null);

        assertTrue(v.isNull());
    }

    @Test
    public void givenNullValue_callIsNotNull_expectFalse() {
        Nullable<String> v = Nullable.createNullable(null);

        assertFalse(v.isNotNull());
    }

    @Test
    public void givenNullValue_callGetStateEnum_expectNull() {
        Nullable<String> v = Nullable.createNullable(null);

        assertEquals(Nullable.NULL_ENUM, v.getStateEnum());
    }

    @Test
    public void givenNullValue_callGetValue_expectNullPointerException() {
        Nullable<String> v = Nullable.createNullable(null);

        try {
            v.getValue();
            fail( "expected NPE" );
        } catch ( NullPointerException e ) {

        }
    }

    @Test
    public void givenNullValue_callMapValue_expectNullResultAndMappingFunctionToNotBeCalled() {
        Nullable<String> v = Nullable.createNullable(null);
        final AtomicLong counter = new AtomicLong(0);

        Nullable<Integer> result = v.mapValue(new Function1<Integer,String>() {
            public Integer invoke(String arg) {
                counter.incrementAndGet();

                return null;
            }
        });

        assertTrue( result.isNull() );
        assertEquals( 0, counter.get() );
    }

    @Test
    public void givenNullValue_callFlatMapValue_expectNullResultAndMappingFunctionToNotBeCalled() {
        Nullable<String> v = Nullable.createNullable(null);
        final AtomicLong counter = new AtomicLong(0);

        Nullable<Integer> result = v.flatMapValue(new Function1<Nullable<Integer>,String>() {
            public Nullable<Integer> invoke(String arg) {
                counter.incrementAndGet();

                return null;
            }
        });

        assertTrue( result.isNull() );
        assertEquals( 0, counter.get() );
    }

    @Test
    public void givenNullValue_callReplaceNullHavingMappingFunctionReturnNull_expectToReturnNull() {
        Nullable<String> v = Nullable.createNullable(null);
        final AtomicLong counter = new AtomicLong(0);

        Nullable<String> result = v.replaceNull(new Function0<Nullable<String>>() {
            public Nullable<String> invoke() {
                counter.incrementAndGet();

                return null;
            }
        });

        assertTrue( result.isNull() );
        assertEquals( 1, counter.get() );
    }

    @Test
    public void givenNullValue_callReplaceNullHavingMappingFunctionReturnValue_expectToReturnValue() {
        Nullable<String> v = Nullable.createNullable(null);

        Nullable<String> result = v.replaceNull(new Function0<Nullable<String>>() {
            public Nullable<String> invoke() {
                return Nullable.createNullable("hello");
            }
        });

        assertEquals( Nullable.createNullable("hello"), result );
    }

    @Test
    public void givenNullValue_callReplaceNullHavingMappingFunctionThrowException_expectExceptionToBeThrown() {
        Nullable<String> v = Nullable.createNullable(null);

        try {
            v.replaceNull(new Function0<Nullable<String>>() {
                public Nullable<String> invoke() {
                    throw new IllegalStateException("splat");
                }
            });

            fail("expected ISE");
        } catch ( IllegalStateException e ) {

        }
    }


// NONE NULL_ENUM VALUE TESTS

    @Test
    public void givenValue_callGetValue_expectValue() {
        Nullable<String> v = Nullable.createNullable("welcome");

        assertEquals( "welcome", v.getValue() );
    }

    @Test
    public void givenValue_callIsNull_expectFalse() {
        Nullable<String> v = Nullable.createNullable("welcome");

        assertFalse(v.isNull());
    }

    @Test
    public void givenValue_callIsNotNull_expectTrue() {
        Nullable<String> v = Nullable.createNullable("welcome");

        assertTrue(v.isNotNull());
    }

    @Test
    public void givenValue_callGetStateEnum_expectNotNull() {
        Nullable<String> v = Nullable.createNullable("welcome");

        assertEquals(Nullable.NOT_NULL_ENUM, v.getStateEnum());
    }

    @Test
    public void givenValue_callGetValueNbl_expectValue() {
        Nullable<String> v = Nullable.createNullable("welcome");

        assertEquals("welcome", v.getValueNbl());
    }

    @Test
    public void givenValue_callMapValue_expectMappedValueResult() {
        Nullable<String> v = Nullable.createNullable("welcome");

        Nullable<Integer> result = v.mapValue(new Function1<Integer,String>() {
            public Integer invoke(String arg) {
                return arg.length();
            }
        });

        assertEquals(7, result.getValueNbl().intValue());
    }

    @Test
    public void givenValue_callMapValueAndHaveMappingFunctionReturnNull_expectNullResult() {
        Nullable<String> v = Nullable.createNullable("welcome");

        Nullable<Integer> result = v.mapValue(new Function1<Integer,String>() {
            public Integer invoke(String arg) {
                return null;
            }
        });

        assertSame(Nullable.NULL, result);
    }

    @Test
    public void givenValue_callMapValueAndHaveMappingFunctionThrowException_expectExceptionToBeThrown() {
        Nullable<String> v = Nullable.createNullable("welcome");

        try {
            v.mapValue(new Function1<Integer,String>() {
                public Integer invoke(String arg) {
                    throw new IllegalStateException("bang, lots of smoke");
                }
            });

            fail( "expected ISE" );
        } catch ( IllegalStateException e ) {
            assertEquals( "bang, lots of smoke", e.getMessage() );
        }
    }

    @Test
    public void givenValue_callFlatMapValueAndHaveMappingFunctionReturnNull_expectNullResult() {
        Nullable<String> v = Nullable.createNullable("welcome");

        Nullable<Integer> result = v.flatMapValue(new Function1<Nullable<Integer>,String>() {
            public Nullable<Integer> invoke(String arg) {
                return null;
            }
        });

        assertSame( Nullable.NULL, result );
    }

    @Test
    public void givenValue_callFlatMapValueAndHaveMappingFunctionReturnNullNullable_expectNullResult() {
        Nullable<String> v = Nullable.createNullable("welcome");

        Nullable<Integer> result = v.flatMapValue(new Function1<Nullable<Integer>,String>() {
            public Nullable<Integer> invoke(String arg) {
                return Nullable.NULL;
            }
        });

        assertSame( Nullable.NULL, result );
    }

    @Test
    public void givenValue_callFlatMapValueAndHaveMappingFunctionThrowException_expectExceptionToBeThrown() {
        Nullable<String> v = Nullable.createNullable("welcome");

        try {
            v.flatMapValue(new Function1<Nullable<Integer>,String>() {
                public Nullable<Integer> invoke(String arg) {
                    throw new IllegalStateException("bang, lots of smoke");
                }
            });

            fail( "expected ISE" );
        } catch ( IllegalStateException e ) {
            assertEquals( "bang, lots of smoke", e.getMessage() );
        }
    }

    @Test
    public void givenValue_callFlatMapValueAndHaveMappingFunctionReturnValue_expectValue() {
        Nullable<String> v = Nullable.createNullable("welcome");

        Nullable<Integer> result = v.flatMapValue(new Function1<Nullable<Integer>,String>() {
            public Nullable<Integer> invoke(String arg) {
                return Nullable.createNullable(arg.length());
            }
        });

        assertEquals( 7, result.getValue().intValue() );
    }


    @Test
    public void givenValue_callReplaceNull_expectMappingFunctionToNotBeCalledAndResultIsOriginalValue() {
        Nullable<String> v = Nullable.createNullable("welcome");

        Nullable<String> result = v.replaceNull(new Function0<Nullable<String>>() {
            public Nullable<String> invoke() {
                return Nullable.createNullable("bonzai!");
            }
        });

        assertSame( v, result );
    }

}
