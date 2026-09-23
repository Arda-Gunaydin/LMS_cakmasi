/**
 * CSE201 Lab 3 - Stacks.
 *
 * Read the description before you start. Everything you write goes into this file.
 * The declarations below are the ones the description requires; fill in the bodies.
 *
 * Only Lab03 is public. Do not add a package declaration.
 * Do not use any java.util collection (ArrayStack must be your own array-based implementation).
 */

/** Part 1. A generic, array-based, growable stack. */
class ArrayStack<T> {

    public ArrayStack(int initialCapacity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ArrayStack() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void push(T item) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T pop() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public T peek() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int capacity() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

/** Your playground for the Run button. It is not graded; change it freely. */
public class Lab03 {

    public static void main(String[] args) {
        try {
            ArrayStack<Integer> s = new ArrayStack<>();
            s.push(10);
            s.push(20);
            s.push(30);
            System.out.println("stack                = " + s);
            System.out.println("pop()                = " + s.pop());
            System.out.println("stack after pop      = " + s);

            System.out.println();
            System.out.println("isBalanced(\"(a + [b * c])\") = " + isBalanced("(a + [b * c])"));
            System.out.println("isBalanced(\"(a + [b)]\")     = " + isBalanced("(a + [b)]"));
            System.out.println("evaluatePostfix(\"3 4 + 2 *\") = " + evaluatePostfix("3 4 + 2 *"));
        } catch (UnsupportedOperationException e) {
            System.out.println();
            System.out.println("Stopped at a method that is not implemented yet:");
            System.out.println("  " + e.getStackTrace()[0]);
        }
    }

    /** Part 2. Checks that (), [] and {} are balanced and properly nested, using an ArrayStack. */
    public static boolean isBalanced(String expr) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /** Part 2. Evaluates a postfix (RPN) expression, using an ArrayStack. */
    public static int evaluatePostfix(String expr) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
