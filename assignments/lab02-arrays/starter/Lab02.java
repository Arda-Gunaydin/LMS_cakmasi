/**
 * CSE201 Lab 2 - Arrays and the ScoreBook.
 *
 * Read the description before you start. Everything you write goes into this file.
 * The declarations below are the ones the description requires; fill in the bodies.
 *
 * Only Lab02 is public. Do not add a package declaration.
 * Do not use java.util, System.arraycopy or clone(): copy arrays with your own loops.
 */

/** Part 1. Static helpers for int arrays. */
final class ArrayTools {

    private ArrayTools() {
    }

    public static int sum(int[] a) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static double average(int[] a) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static int indexOfMax(int[] a) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static int[] reversed(int[] a) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static int countInRange(int[] a, int lo, int hi) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static int[] withoutDuplicates(int[] a) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static void rotateLeft(int[] a, int k) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static int[][] transpose(int[][] m) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/** Part 2. A growable list of exam scores backed by an int array. */
class ScoreBook {

    private int[] scores;
    private int size;

    public ScoreBook() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ScoreBook(int initialCapacity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int capacity() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void add(int score) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int get(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void set(int index, int score) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void insertAt(int index, int score) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int removeAt(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int[] toArray() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int[] letterCounts() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/** Your playground for the Run button. It is not graded; change it freely. */
public class Lab02 {

    public static void main(String[] args) {
        try {
            int[] a = { 70, 95, 70, 40, 88, 95 };
            System.out.println("a                    = " + show(a));
            System.out.println("sum                  = " + ArrayTools.sum(a));
            System.out.println("average              = " + ArrayTools.average(a));
            System.out.println("indexOfMax           = " + ArrayTools.indexOfMax(a));
            System.out.println("reversed             = " + show(ArrayTools.reversed(a)));
            System.out.println("countInRange(60, 90) = " + ArrayTools.countInRange(a, 60, 90));
            System.out.println("withoutDuplicates    = " + show(ArrayTools.withoutDuplicates(a)));
            ArrayTools.rotateLeft(a, 2);
            System.out.println("after rotateLeft(2)  = " + show(a));

            ScoreBook book = new ScoreBook(2);
            book.add(90);
            book.add(75);
            book.add(60);
            System.out.println();
            System.out.println("book = " + book + "  size " + book.size() + ", capacity " + book.capacity());
            book.insertAt(0, 100);
            System.out.println("removed " + book.removeAt(2) + " -> " + book);
            System.out.println("letterCounts = " + show(book.letterCounts()));
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }

    private static String show(int[] a) {
        String s = "[";
        for (int i = 0; i < a.length; i++) {
            s += (i == 0 ? "" : ", ") + a[i];
        }
        return s + "]";
    }
}
