package com.mosaic.collections;

import com.mosaic.junitpro.Benchmark;
import com.mosaic.junitpro.JUnitPro;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 */
@SuppressWarnings("unchecked")
@RunWith(JUnitPro.class)
public class FastStackBenchmark {

    private Stack     jdkStack      = new Stack();
    private FastStack fastStack     = new FastStack(1);
    private FastStack fastStack200  = new FastStack(200);
    private FastStack<String> fastStackString200 = new FastStack(String.class, 200);
    private List      jdkArraysList = new ArrayList(200);


/*
138.04ns
55.46ns
54.69ns
55.27ns
55.36ns
54.89ns
*/
    @Benchmark(durationResultMultiplier = 1.0/3)
    public long jdkStackBenchmark( int iterationCount ) {
        long count=0;

        for ( int i=0; i<iterationCount; i++ ) {
            jdkStack.push("a");
            jdkStack.push("b");
            jdkStack.push("c");
            count += jdkStack.pop().toString().length();
            count += jdkStack.pop().toString().length();
            count += jdkStack.pop().toString().length();
        }

        return count;
    }

/*
93.28ns
11.70ns
8.27ns
8.66ns
5.77ns
5.85ns
*/
    @Benchmark(durationResultMultiplier = 1.0/3)
    public long jdkArraysListBenchmark( int iterationCount ) {
        long count=0;

        for ( int i=0; i<iterationCount; i++ ) {
            jdkArraysList.add("a");
            jdkArraysList.add("b");
            jdkArraysList.add("c");
            count += jdkArraysList.remove(2).toString().length();
            count += jdkArraysList.remove(1).toString().length();
            count += jdkArraysList.remove(0).toString().length();
        }

        return count;
    }


/**
 49.73ns
 7.97ns
 4.81ns
 6.35ns
 6.49ns
 6.33ns
*/
    @Benchmark(durationResultMultiplier = 1.0/3)
    public long fastStackBenchmark( int iterationCount ) {
        long count=0;

        for ( int i=0; i<iterationCount; i++ ) {
            fastStack.push("a");
            fastStack.push("b");
            fastStack.push("c");
            count += fastStack.pop().toString().length();
            count += fastStack.pop().toString().length();
            count += fastStack.pop().toString().length();
        }

        return count;
    }

/**
 37.30ns
 3.87ns
 2.35ns
 2.34ns
 1.11ns
 1.14ns
*/
    @Benchmark(durationResultMultiplier = 1.0/3)
    public long fastStack200Benchmark( int iterationCount ) {
        long count=0;

        for ( int i=0; i<iterationCount; i++ ) {
            fastStack200.push("a");
            fastStack200.push("b");
            fastStack200.push("c");
            count += fastStack200.pop().toString().length();
            count += fastStack200.pop().toString().length();
            count += fastStack200.pop().toString().length();
        }

        return count;
    }

/*
31.83ns
2.11ns
3.62ns
2.02ns
1.12ns
1.12ns
     */

    @Benchmark(durationResultMultiplier = 1.0/3)
    public long fastStackString200Benchmark( int iterationCount ) {
        long count=0;

        for ( int i=0; i<iterationCount; i++ ) {
            fastStackString200.push("a");
            fastStackString200.push("b");
            fastStackString200.push("c");
            count += fastStackString200.pop().length();
            count += fastStackString200.pop().length();
            count += fastStackString200.pop().length();
        }

        return count;
    }

/**
 38.15ns  // using Arrays.fill to clear
 2.82ns
 2.87ns
 2.25ns
 2.27ns
 2.28ns


 27.90ns  // using for loop to clear
 2.43ns
 2.66ns
 1.86ns
 1.93ns
 1.86ns
*/
    @Benchmark(durationResultMultiplier = 1.0/3)
    public long fastStack200ClearBenchmark( int iterationCount ) {
        long count=0;

        for ( int i=0; i<iterationCount; i++ ) {
            fastStack200.push("a");
            fastStack200.push("b");
            fastStack200.push("c");

            count += fastStack200.size();

            fastStack200.clear();
        }

        return count;
    }

}
