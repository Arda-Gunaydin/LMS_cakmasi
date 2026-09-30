/** Hidden tests for last year's Lab 6: Stacks and Queues (school style: one test = one check). */
public class Tests {

    static final String F = "Lab06.java";

    static Object[] data(Object o) {
        return (Object[]) T.field(o, "data");
    }

    static String arr(Object o) {
        return java.util.Arrays.toString(data(o));
    }

    static Object stackOf(String cls, Object... items) {
        Object s = T.make(cls);
        for (Object x : items) {
            T.call(s, "push", x);
        }
        return s;
    }

    static Object queueOf(String cls, Object... items) {
        Object q = T.make(cls);
        for (Object x : items) {
            T.call(q, "enqueue", x);
        }
        return q;
    }

    public static void main(String[] args) {

        T.test("(structure)", "test_Lab06_structure", "classes implement the interfaces", () -> {
            T.expectTrue("ArrayStack must implement Stack", () -> T.isA("ArrayStack", "Stack"));
            T.expectTrue("LinkedStack must implement Stack", () -> T.isA("LinkedStack", "Stack"));
            T.expectTrue("ArrayQueue must implement Queue", () -> T.isA("ArrayQueue", "Queue"));
            T.expectTrue("LinkedQueue must implement Queue", () -> T.isA("LinkedQueue", "Queue"));
            T.expectTrue("Stack and Queue must extend List", () -> T.isA("Stack", "List") && T.isA("Queue", "List"));
            for (String c : new String[] {"ArrayStack", "ArrayQueue", "LinkedStack", "LinkedQueue"}) {
                T.expectTrue("all fields of " + c + " must be private", T.fieldsPrivate(c));
            }
        });

        // ---------------------------------------------------------------- ArrayStack
        T.test("ArrayStack", "test_ArrayStack_empty", "a new ArrayStack", () -> {
            Object s = T.make("ArrayStack");
            T.expectTrue("a new ArrayStack must be empty", () -> T.call(s, "isEmpty"));
            T.expect("a new ArrayStack should have size 0", 0, () -> T.call(s, "size"));
            T.expectNull("pop() on an empty ArrayStack should return null", () -> T.call(s, "pop"));
            T.expectNull("top() on an empty ArrayStack should return null", () -> T.call(s, "top"));
            T.expect("size() should still be 0 after pop() on an empty stack", 0, () -> T.call(s, "size"));
        });

        T.test("ArrayStack", "test_ArrayStack_lifo", "ArrayStack push, pop and top", () -> {
            T.method("ArrayStack", "push", 1);
            Object s = stackOf("ArrayStack", 1, 2, 3);
            T.expect("size() should be 3 after three pushes", 3, () -> T.call(s, "size"));
            T.expectFalse("the stack must not be empty after pushes", (boolean) T.call(s, "isEmpty"));
            T.expect("top() should be 3 (the last pushed item)", 3, () -> T.call(s, "top"));
            T.expect("top() must not remove: size() should still be 3", 3, () -> T.call(s, "size"));
            T.expect("pop() should return 3", 3, () -> T.call(s, "pop"));
            T.expect("pop() should then return 2", 2, () -> T.call(s, "pop"));
            T.call(s, "push", 7);
            T.expect("after push(7), pop() should return 7", 7, () -> T.call(s, "pop"));
            T.expect("pop() should then return 1", 1, () -> T.call(s, "pop"));
            T.expectTrue("the stack must be empty after popping every item", () -> T.call(s, "isEmpty"));
            T.expect("data[0] should hold the bottom item", "[a, b, null, null]", () -> arr(stackOf("ArrayStack", "a", "b")));
        });

        T.test("ArrayStack", "test_ArrayStack_full", "ArrayStack fixed capacity", () -> {
            Object s = stackOf("ArrayStack", 1, 2, 3, 4);
            T.expect("size() should be 4 when full", 4, () -> T.call(s, "size"));
            T.call(s, "push", 5);
            T.expect("push onto a full stack must be ignored: size() should stay 4", 4, () -> T.call(s, "size"));
            T.expect("push onto a full stack must be ignored: top() should stay 4", 4, () -> T.call(s, "top"));
            T.expect("the array must not be resized: data.length should stay 4", 4, () -> data(s).length);
            T.expect("pop() should return 4", 4, () -> T.call(s, "pop"));
            T.call(s, "push", 9);
            T.expect("after one pop there is room again: top() should be 9", 9, () -> T.call(s, "top"));
            Object t = stackOf("ArrayStack", "x");
            T.expectNoThrow("push(null) must not throw", () -> T.call(t, "push", (Object) null));
            T.expect("push(null) must be ignored: size() should stay 1", 1, () -> T.call(t, "size"));
            T.expect("push(null) must be ignored: top() should stay x", "x", () -> T.call(t, "top"));
        });

        T.test("ArrayStack", "test_ArrayStack_clearsSlot", "ArrayStack pop clears the slot", () -> {
            Object s = stackOf("ArrayStack", "a", "b", "c");
            T.call(s, "pop");
            T.expect("after pushing a, b, c and one pop, data should be [a, b, null, null]", "[a, b, null, null]", () -> arr(s));
            T.call(s, "pop");
            T.call(s, "pop");
            T.expect("after popping everything, data should be all null", "[null, null, null, null]", () -> arr(s));
            T.expect("the top field should be 0 when empty", 0, () -> T.field(s, "top"));
        });

        // ---------------------------------------------------------------- ArrayQueue
        T.test("ArrayQueue", "test_ArrayQueue_empty", "a new ArrayQueue", () -> {
            Object q = T.make("ArrayQueue");
            T.expectTrue("a new ArrayQueue must be empty", () -> T.call(q, "isEmpty"));
            T.expect("a new ArrayQueue should have size 0", 0, () -> T.call(q, "size"));
            T.expectNull("dequeue() on an empty ArrayQueue should return null", () -> T.call(q, "dequeue"));
            T.expectNull("front() on an empty ArrayQueue should return null", () -> T.call(q, "front"));
            T.expect("size() should still be 0 after dequeue() on an empty queue", 0, () -> T.call(q, "size"));
        });

        T.test("ArrayQueue", "test_ArrayQueue_fifo", "ArrayQueue enqueue, dequeue and front", () -> {
            T.method("ArrayQueue", "enqueue", 1);
            Object q = queueOf("ArrayQueue", "a", "b", "c");
            T.expect("size() should be 3 after three enqueues", 3, () -> T.call(q, "size"));
            T.expect("front() should be a (the first item)", "a", () -> T.call(q, "front"));
            T.expect("front() must not remove: size() should still be 3", 3, () -> T.call(q, "size"));
            T.expect("dequeue() should return a", "a", () -> T.call(q, "dequeue"));
            T.expect("dequeue() should then return b", "b", () -> T.call(q, "dequeue"));
            T.expect("front() should now be c", "c", () -> T.call(q, "front"));
            T.expect("dequeue() should return c", "c", () -> T.call(q, "dequeue"));
            T.expectTrue("the queue must be empty after dequeuing every item", () -> T.call(q, "isEmpty"));
            T.expect("after enqueue a, b, c and three dequeues, data should be all null", "[null, null, null, null]",
                    () -> arr(q));
        });

        T.test("ArrayQueue", "test_ArrayQueue_full", "ArrayQueue fixed capacity", () -> {
            Object q = queueOf("ArrayQueue", 1, 2, 3, 4);
            T.call(q, "enqueue", 5);
            T.expect("enqueue on a full queue must be ignored: size() should stay 4", 4, () -> T.call(q, "size"));
            T.expect("the array must not be resized: data.length should stay 4", 4, () -> data(q).length);
            T.expect("data should be [1, 2, 3, 4]", "[1, 2, 3, 4]", () -> arr(q));
            T.expect("dequeue() should return 1", 1, () -> T.call(q, "dequeue"));
            Object n = queueOf("ArrayQueue", "x");
            T.call(n, "enqueue", (Object) null);
            T.expect("enqueue(null) must be ignored: size() should stay 1", 1, () -> T.call(n, "size"));
        });

        T.test("ArrayQueue", "test_ArrayQueue_wrap", "ArrayQueue is circular", () -> {
            Object q = queueOf("ArrayQueue", 1, 2, 3, 4);
            T.call(q, "dequeue");
            T.call(q, "dequeue");
            T.expect("after two dequeues data should be [null, null, 3, 4]", "[null, null, 3, 4]", () -> arr(q));
            T.expect("front field should be 2", 2, () -> T.field(q, "front"));
            T.call(q, "enqueue", 5);
            T.expect("enqueue(5) should wrap around to index 0: [5, null, 3, 4]", "[5, null, 3, 4]", () -> arr(q));
            T.call(q, "enqueue", 6);
            T.expect("enqueue(6) should go to index 1: [5, 6, 3, 4]", "[5, 6, 3, 4]", () -> arr(q));
            T.expect("the wrapped queue holds 4 items", 4, () -> T.call(q, "size"));
            T.call(q, "enqueue", 7);
            T.expect("the wrapped queue is full: enqueue(7) must be ignored", "[5, 6, 3, 4]", () -> arr(q));
            T.expect("dequeue() should return 3", 3, () -> T.call(q, "dequeue"));
            T.expect("dequeue() should return 4", 4, () -> T.call(q, "dequeue"));
            T.expect("dequeue() should return 5 (front wrapped to index 0)", 5, () -> T.call(q, "dequeue"));
            T.expect("front() should be 6", 6, () -> T.call(q, "front"));
        });

        T.test("ArrayQueue", "test_ArrayQueue_manyRounds", "ArrayQueue over many rounds", () -> {
            Object q = T.make("ArrayQueue");
            StringBuilder got = new StringBuilder();
            for (int i = 0; i < 30; i++) {
                T.call(q, "enqueue", i);
                T.call(q, "enqueue", i + 100);
                got.append(T.call(q, "dequeue")).append(' ');
                if (i % 2 == 1) {
                    got.append(T.call(q, "dequeue")).append(' ');
                }
            }
            StringBuilder want = new StringBuilder();
            java.util.ArrayDeque<Integer> ref = new java.util.ArrayDeque<>();
            for (int i = 0; i < 30; i++) {
                if (ref.size() < 4) {
                    ref.add(i);
                }
                if (ref.size() < 4) {
                    ref.add(i + 100);
                }
                want.append(ref.poll()).append(' ');
                if (i % 2 == 1) {
                    want.append(ref.poll()).append(' ');
                }
            }
            T.expect("30 rounds of enqueue/enqueue/dequeue(/dequeue) should dequeue in FIFO order", want.toString().trim(),
                    () -> got.toString().trim());
            T.expect("size() after the rounds should be " + ref.size(), ref.size(), () -> T.call(q, "size"));
        });

        // ---------------------------------------------------------------- LinkedStack
        T.test("LinkedStack", "test_LinkedStack_lifo", "LinkedStack push, pop and top", () -> {
            T.method("LinkedStack", "push", 1);
            Object s = stackOf("LinkedStack", "a", "b", "c");
            T.expect("size() should be 3", 3, () -> T.call(s, "size"));
            T.expect("top() should be c", "c", () -> T.call(s, "top"));
            T.expect("the top node should hold c", "c", () -> T.field(T.field(s, "top"), "data"));
            T.expect("pop() should return c", "c", () -> T.call(s, "pop"));
            T.expect("pop() should return b", "b", () -> T.call(s, "pop"));
            T.expect("size() should be 1", 1, () -> T.call(s, "size"));
            T.call(s, "push", (Object) null);
            T.expect("push(null) must be ignored: size() should stay 1", 1, () -> T.call(s, "size"));
            T.expect("pop() should return a", "a", () -> T.call(s, "pop"));
            T.expectTrue("the stack must be empty now", () -> T.call(s, "isEmpty"));
        });

        T.test("LinkedStack", "test_LinkedStack_empty", "LinkedStack when empty", () -> {
            Object s = T.make("LinkedStack");
            T.expectTrue("a new LinkedStack must be empty", () -> T.call(s, "isEmpty"));
            T.expectNull("pop() on an empty LinkedStack should return null", () -> T.call(s, "pop"));
            T.expectNull("top() on an empty LinkedStack should return null", () -> T.call(s, "top"));
            T.expect("size() should stay 0", 0, () -> T.call(s, "size"));
            T.call(s, "push", 5);
            T.call(s, "pop");
            T.expectNull("the top field should be null after popping the only item", () -> T.field(s, "top"));
        });

        T.test("LinkedStack", "test_LinkedStack_many", "LinkedStack has no capacity limit", () -> {
            Object s = T.make("LinkedStack");
            for (int i = 0; i < 100; i++) {
                T.call(s, "push", i);
            }
            T.expect("after 100 pushes size() should be 100", 100, () -> T.call(s, "size"));
            T.expect("top() should be 99", 99, () -> T.call(s, "top"));
            T.expectTrue("popping 100 items should return 99, 98, ..., 0", () -> {
                for (int i = 99; i >= 0; i--) {
                    if (!Integer.valueOf(i).equals(T.call(s, "pop"))) {
                        return false;
                    }
                }
                return true;
            });
        });

        // ---------------------------------------------------------------- LinkedQueue
        T.test("LinkedQueue", "test_LinkedQueue_fifo", "LinkedQueue enqueue, dequeue and front", () -> {
            T.method("LinkedQueue", "enqueue", 1);
            Object q = queueOf("LinkedQueue", 1, 2, 3);
            T.expect("size() should be 3", 3, () -> T.call(q, "size"));
            T.expect("front() should be 1", 1, () -> T.call(q, "front"));
            T.expect("the rear node should hold 3", 3, () -> T.field(T.field(q, "rear"), "data"));
            T.expect("dequeue() should return 1", 1, () -> T.call(q, "dequeue"));
            T.call(q, "enqueue", 4);
            T.expect("dequeue() should return 2", 2, () -> T.call(q, "dequeue"));
            T.expect("dequeue() should return 3", 3, () -> T.call(q, "dequeue"));
            T.expect("dequeue() should return 4", 4, () -> T.call(q, "dequeue"));
            T.call(q, "enqueue", (Object) null);
            T.expect("enqueue(null) must be ignored: size() should be 0", 0, () -> T.call(q, "size"));
        });

        T.test("LinkedQueue", "test_LinkedQueue_empty", "LinkedQueue when emptied", () -> {
            Object q = T.make("LinkedQueue");
            T.expectNull("dequeue() on an empty LinkedQueue should return null", () -> T.call(q, "dequeue"));
            T.expectNull("front() on an empty LinkedQueue should return null", () -> T.call(q, "front"));
            T.call(q, "enqueue", "x");
            T.call(q, "dequeue");
            T.expectTrue("the queue must be empty after removing its only item", () -> T.call(q, "isEmpty"));
            T.expectNull("front must be null when the queue becomes empty", () -> T.field(q, "front"));
            T.expectNull("rear must be null when the queue becomes empty", () -> T.field(q, "rear"));
            T.call(q, "enqueue", "y");
            T.call(q, "enqueue", "z");
            T.expect("after emptying, enqueue y, z: front() should be y", "y", () -> T.call(q, "front"));
            T.expect("dequeue() should return y", "y", () -> T.call(q, "dequeue"));
            T.expect("dequeue() should return z", "z", () -> T.call(q, "dequeue"));
        });

        T.test("LinkedQueue", "test_LinkedQueue_many", "LinkedQueue has no capacity limit", () -> {
            Object q = T.make("LinkedQueue");
            for (int i = 0; i < 100; i++) {
                T.call(q, "enqueue", i);
            }
            T.expect("after 100 enqueues size() should be 100", 100, () -> T.call(q, "size"));
            T.expectTrue("dequeuing 100 items should return 0, 1, ..., 99", () -> {
                for (int i = 0; i < 100; i++) {
                    if (!Integer.valueOf(i).equals(T.call(q, "dequeue"))) {
                        return false;
                    }
                }
                return true;
            });
        });

        T.test("(code rules)", "test_Lab06_rules", "Lab06 code rules", () -> {
            T.expectFalse("Lab06.java must not use java.util", T.matches(F, "\\bjava\\s*\\.\\s*util\\b"));
            T.expectFalse("only Lab06 may be public", T.isPublic("ArrayStack") || T.isPublic("LinkedQueue"));
            T.expectSilent("the methods must not print", () -> {
                for (String c : new String[] {"ArrayStack", "LinkedStack"}) {
                    try {
                        Object s = stackOf(c, 1, 2, 3, 4, 5);
                        T.call(s, "top");
                        for (int i = 0; i < 6; i++) {
                            T.call(s, "pop");
                        }
                        T.call(s, "top");
                    } catch (Throwable ignored) {
                        // printing is what matters here
                    }
                }
                for (String c : new String[] {"ArrayQueue", "LinkedQueue"}) {
                    try {
                        Object q = queueOf(c, 1, 2, 3, 4, 5);
                        T.call(q, "front");
                        for (int i = 0; i < 6; i++) {
                            T.call(q, "dequeue");
                        }
                        T.call(q, "front");
                    } catch (Throwable ignored) {
                        // printing is what matters here
                    }
                }
            });
        });

        T.done();
    }
}
