/*
 * A second Java code timing example using System.nanoTime().  The program
 * prints half a million integers to standard output (usually the screen)
 * and reports the elapsed time as measured by before and after calls to
 * System.nanoTime().  Note that nanoTime() was added to the API
 * in version 1.5; this demo will not work with earlier versions
 * of the language.
 *
 *   Author:  L. McCann
 *     Date:  March 3, 2010
 * 
 * To compile and execute:  javac Timing2.java
 *                          java Timing2
 *
 * Performing execution timing in Java isn't very exact, because of the
 * "write once, run anywhere" philosophy of the language.  Ordinarily,
 * we would want to measure CPU time, not "wall clock" time.  But, that's
 * difficult to do in Java, so we'll live with "wall clock" time.  Worse,
 * the timing granularities are determined by the operating system and
 * hardware.
 */

import java.io.*;

public class Timing2 {

    public static void main (String [] args) {

        final double BILLION = 1000000000.0; // a nanosec = billionth of a sec

        long   startTime,     // the system time before the execution of the
                              //     algorithm being measured, in nanoseconds
               elapsedTime;   // the time required to execute the algorithm,
                              //     in nanoseconds
        double seconds;       // the time required to execute the algorithm,
                              //     in seconds

        System.gc();  // Just to suggest that we'd appreciate getting any
                      // garbage collection activities out of the way
                      // before the time-critical code is executed.

        startTime = System.nanoTime();

        for (int i = 1; i <= 500000; i++) {
            System.out.print(i + " ");
            if (i%10 == 0) System.out.println();
        }

        elapsedTime = System.nanoTime() - startTime;
        seconds =  elapsedTime / BILLION;

        System.out.print("\n\n");
        System.out.println(seconds + " seconds of execution time"
                         + " were required to print a mess of integers.\n");

    }
}
