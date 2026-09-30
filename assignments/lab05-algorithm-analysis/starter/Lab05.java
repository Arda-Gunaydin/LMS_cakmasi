/**
 * CSE201 Lab 5 - Algorithm Analysis.
 *
 * Read the description before you start. Everything you write goes into this file.
 * The declarations below are the ones the description requires; fill in the bodies.
 *
 * Only Lab05 is public. Do not add a package declaration.
 */
public class Lab05 {

    /** Your playground for the Run button. It is not graded; change it freely. */
    public static void main(String[] args) {
        try {
            System.out.println("countLinear(5)            = " + countLinear(5));
            System.out.println("countConditionChecks(5)   = " + countConditionChecks(5));
            System.out.println("countNested(5)            = " + countNested(5));
            System.out.println("countPairs(5)             = " + countPairs(5));
            System.out.println("countTriangle(5)          = " + countTriangle(5));
            System.out.println("countHalvings(5)          = " + countHalvings(5));
            System.out.println("dominantTerm(20, 5, 0, 0) = " + dominantTerm(20, 5, 0, 0));
            int[] a = {1, 2, 3, 4, 5, 6};
            System.out.println("countPairsWithSum(a, 7)   = " + countPairsWithSum(a, 7));
            System.out.println("twoPointerComparisons(a, 7) = " + twoPointerComparisons(a, 7));
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }

    /** Part 1. Body executions of: for (i = 0; i < n; i++). */
    public static long countLinear(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 1. Condition checks of the same loop, including the final false check. */
    public static long countConditionChecks(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 1. Inner body executions: i from 0 to n-1, j from 0 to n-1. */
    public static long countNested(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 1. Inner body executions: i from 0 to n-1, j from i+1 to n-1. */
    public static long countPairs(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 1. Inner body executions: i from 0 to n-1, j from 0 to i. */
    public static long countTriangle(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 2. Body executions of: k = n; while (k > 1) k = k / 2. */
    public static int countHalvings(int n) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 2. Big-O class of c0 + c1*n + c2*n^2 + c3*n^3. */
    public static String dominantTerm(long c0, long c1, long c2, long c3) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 3. Number of pairs i < j with sorted[i] + sorted[j] == target, using two pointers. */
    public static int countPairsWithSum(int[] sorted, int target) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 3. Number of sum comparisons the two-pointer method makes for the same input. */
    public static int twoPointerComparisons(int[] sorted, int target) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
