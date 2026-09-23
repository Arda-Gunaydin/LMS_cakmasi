/** Hidden tests for Lab 3: Stacks (school style: one test = one check). */
public class Tests {

    static final String F = "Lab03.java";
    static final Class<IllegalArgumentException> IAE = IllegalArgumentException.class;
    static final Class<java.util.NoSuchElementException> NSEE = java.util.NoSuchElementException.class;
    static final Class<ArithmeticException> AE = ArithmeticException.class;

    static Object newStack(int capacity) {
        return T.make("ArrayStack", capacity);
    }

    static Object stackOf(Object... items) {
        Object s = T.make("ArrayStack");
        for (Object item : items) {
            T.call(s, "push", item);
        }
        return s;
    }

    public static void main(String[] args) {

        // ================================================================ ArrayStack
        T.test("ArrayStack", "test_ArrayStack_structure", "ArrayStack structure", () -> {
            T.expectTrue("all ArrayStack fields must be private", T.fieldsPrivate("ArrayStack"));
        });

        T.test("ArrayStack", "test_ArrayStack_constructor", "ArrayStack constructors", () -> {
            T.expect("new ArrayStack().size() should be 0", 0, () -> T.call(T.make("ArrayStack"), "size"));
            T.expect("new ArrayStack().capacity() should be 8", 8, () -> T.call(T.make("ArrayStack"), "capacity"));
            T.expect("new ArrayStack(3).capacity() should be 3", 3, () -> T.call(T.make("ArrayStack", 3), "capacity"));
            T.expectThrows("new ArrayStack(0) should throw IllegalArgumentException", IAE, () -> T.make("ArrayStack", 0));
            T.expectThrows("new ArrayStack(-2) should throw IllegalArgumentException", IAE, () -> T.make("ArrayStack", -2));
        });

        T.test("ArrayStack", "test_ArrayStack_pushGrows", "push and growing", () -> {
            T.method("ArrayStack", "push", 1);
            Object s = newStack(2);
            T.call(s, "push", 10);
            T.call(s, "push", 20);
            T.expect("after pushing 10 then 20, size() should be 2", 2, () -> T.call(s, "size"));
            T.expect("capacity should still be 2 (not full yet)", 2, () -> T.call(s, "capacity"));
            T.call(s, "push", 30);
            T.expect("pushing a 3rd item into a full ArrayStack(2) should double capacity to 4", 4,
                    () -> T.call(s, "capacity"));
            T.expect("after pushing 10, 20, 30 the top should be 30", 30, () -> T.call(s, "peek"));
            T.expect("push order should be preserved bottom to top: [10, 20, 30]", "[10, 20, 30]",
                    () -> T.call(s, "toString"));
            Object g = newStack(1);
            for (int i = 1; i <= 9; i++) {
                T.call(g, "push", i);
            }
            T.expect("pushing 9 items into ArrayStack(1) should give capacity 16", 16, () -> T.call(g, "capacity"));
        });

        T.test("ArrayStack", "test_ArrayStack_pushInvalid", "push(null)", () -> {
            T.expectThrows("push(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(T.make("ArrayStack"), "push", (Object) null));
        });

        T.test("ArrayStack", "test_ArrayStack_pop", "pop", () -> {
            T.method("ArrayStack", "pop", 0);
            Object s = stackOf(10, 20, 30);
            T.expect("pop() on [10, 20, 30] should return 30 (the top)", 30, () -> T.call(s, "pop"));
            T.expect("after popping 30, size() should be 2", 2, () -> T.call(s, "size"));
            T.expect("after popping 30, the stack should be [10, 20]", "[10, 20]", () -> T.call(s, "toString"));
            T.expect("pop() should return items in reverse push order: next is 20", 20, () -> T.call(s, "pop"));
            T.expect("pop() must not shrink the capacity", 8, () -> T.call(stackOf(10, 20, 30), "capacity"));
        });

        T.test("ArrayStack", "test_ArrayStack_peek", "peek", () -> {
            T.method("ArrayStack", "peek", 0);
            Object s = stackOf(10, 20, 30);
            T.expect("peek() on [10, 20, 30] should return 30", 30, () -> T.call(s, "peek"));
            T.expect("peek() must not remove the item: size() should still be 3", 3, () -> T.call(s, "size"));
            T.expect("peek() must not remove the item: toString() should be unchanged", "[10, 20, 30]",
                    () -> T.call(s, "toString"));
        });

        T.test("ArrayStack", "test_ArrayStack_emptyExceptions", "empty stack behavior", () -> {
            Object s = T.make("ArrayStack");
            T.expectTrue("a new ArrayStack must be empty", () -> T.call(s, "isEmpty"));
            T.expect("isEmpty() must be false after one push", false, () -> {
                T.call(s, "push", 1);
                return T.call(s, "isEmpty");
            });
            T.expectThrows("pop() on an empty stack should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make("ArrayStack"), "pop"));
            T.expectThrows("peek() on an empty stack should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make("ArrayStack"), "peek"));
            T.expectThrows("popping past the last item should throw NoSuchElementException", NSEE, () -> {
                Object t = stackOf(1);
                T.call(t, "pop");
                T.call(t, "pop");
            });
        });

        T.test("ArrayStack", "test_ArrayStack_toString", "toString", () -> {
            T.expect("toString() of a new ArrayStack should be []", "[]", () -> T.call(T.make("ArrayStack"), "toString"));
            T.expect("toString() of a stack with one item [5]", "[5]", () -> T.call(stackOf(5), "toString"));
            T.expect("toString() lists items bottom to top", "[1, 2, 3]", () -> T.call(stackOf(1, 2, 3), "toString"));
        });

        // ================================================================ isBalanced
        T.test("isBalanced", "test_isBalanced_matched", "matching brackets", () -> {
            T.expectTrue("isBalanced(\"(a + [b * c])\") should be true", () -> T.callStatic("Lab03", "isBalanced", "(a + [b * c])"));
            T.expectTrue("isBalanced(\"{[()]}\") should be true", () -> T.callStatic("Lab03", "isBalanced", "{[()]}"));
            T.expectTrue("isBalanced(\"no brackets here\") should be true", () -> T.callStatic("Lab03", "isBalanced", "no brackets here"));
            T.expectTrue("isBalanced(\"\") should be true", () -> T.callStatic("Lab03", "isBalanced", ""));
        });

        T.test("isBalanced", "test_isBalanced_mismatched", "mismatched brackets", () -> {
            T.expectTrue("isBalanced(\"(a + [b)]\") should be false (] closes ( instead of [)",
                    () -> !((Boolean) T.callStatic("Lab03", "isBalanced", "(a + [b)]")));
            T.expectTrue("isBalanced(\")(\") should be false", () -> !((Boolean) T.callStatic("Lab03", "isBalanced", ")(")));
            T.expectTrue("isBalanced(\"(a\") should be false (never closed)",
                    () -> !((Boolean) T.callStatic("Lab03", "isBalanced", "(a")));
            T.expectTrue("isBalanced(\"a)\") should be false (closes nothing)",
                    () -> !((Boolean) T.callStatic("Lab03", "isBalanced", "a)")));
        });

        T.test("isBalanced", "test_isBalanced_null", "isBalanced(null)", () -> {
            T.expectThrows("isBalanced(null) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab03", "isBalanced", (Object) null));
        });

        // ================================================================ evaluatePostfix
        T.test("evaluatePostfix", "test_evaluatePostfix_basic", "basic postfix", () -> {
            T.expect("evaluatePostfix(\"3 4 +\") should be 7", 7, () -> T.callStatic("Lab03", "evaluatePostfix", "3 4 +"));
            T.expect("evaluatePostfix(\"3 4 + 2 *\") should be 14", 14, () -> T.callStatic("Lab03", "evaluatePostfix", "3 4 + 2 *"));
            T.expect("evaluatePostfix(\"5\") should be 5 (a single value)", 5, () -> T.callStatic("Lab03", "evaluatePostfix", "5"));
            T.expect("evaluatePostfix(\"10 2 -\") should be 8", 8, () -> T.callStatic("Lab03", "evaluatePostfix", "10 2 -"));
        });

        T.test("evaluatePostfix", "test_evaluatePostfix_division", "division", () -> {
            T.expect("evaluatePostfix(\"6 2 /\") should be 3", 3, () -> T.callStatic("Lab03", "evaluatePostfix", "6 2 /"));
            T.expect("evaluatePostfix(\"7 2 /\") should be 3 (truncates toward zero)", 3,
                    () -> T.callStatic("Lab03", "evaluatePostfix", "7 2 /"));
            T.expectThrows("evaluatePostfix(\"4 0 /\") should throw ArithmeticException", AE,
                    () -> T.callStatic("Lab03", "evaluatePostfix", "4 0 /"));
        });

        T.test("evaluatePostfix", "test_evaluatePostfix_negative", "negative operands", () -> {
            T.expect("evaluatePostfix(\"7 -2 *\") should be -14", -14, () -> T.callStatic("Lab03", "evaluatePostfix", "7 -2 *"));
            T.expect("evaluatePostfix(\"-3 -4 +\") should be -7", -7, () -> T.callStatic("Lab03", "evaluatePostfix", "-3 -4 +"));
            T.expect("evaluatePostfix(\"-7 2 /\") should be -3 (truncates toward zero)", -3,
                    () -> T.callStatic("Lab03", "evaluatePostfix", "-7 2 /"));
        });

        T.test("evaluatePostfix", "test_evaluatePostfix_malformed", "malformed expressions", () -> {
            T.expectThrows("evaluatePostfix(\"3 +\") should throw IllegalArgumentException (too few operands)", IAE,
                    () -> T.callStatic("Lab03", "evaluatePostfix", "3 +"));
            T.expectThrows("evaluatePostfix(\"3 4\") should throw IllegalArgumentException (2 values left)", IAE,
                    () -> T.callStatic("Lab03", "evaluatePostfix", "3 4"));
            T.expectThrows("evaluatePostfix(null) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab03", "evaluatePostfix", (Object) null));
            T.expectThrows("evaluatePostfix(\"\") should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab03", "evaluatePostfix", ""));
            T.expectThrows("evaluatePostfix(\"   \") should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab03", "evaluatePostfix", "   "));
        });

        // ================================================================ rules
        T.test("(code rules)", "test_Lab03_rules", "Lab03 code rules", () -> {
            T.expectFalse("only Lab03 may be public", T.isPublic("ArrayStack"));
            T.expectFalse("Lab03.java must not use a java.util collection (ArrayList, LinkedList, Stack, Deque, ArrayDeque, Vector, PriorityQueue)",
                    T.uses(F, "ArrayList") || T.uses(F, "LinkedList") || T.uses(F, "Stack") || T.uses(F, "Deque")
                            || T.uses(F, "ArrayDeque") || T.uses(F, "Vector") || T.uses(F, "PriorityQueue"));
            T.expectSilent("the required methods must not print", () -> {
                try {
                    Object s = stackOf(1, 2, 3);
                    T.call(s, "pop");
                    T.call(s, "peek");
                    T.call(s, "toString");
                    T.callStatic("Lab03", "isBalanced", "(1 + 2)");
                    T.callStatic("Lab03", "evaluatePostfix", "3 4 +");
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
