/** Hidden tests for Lab 5: Queues (school style: one test = one check). */
public class Tests {

    static final String F = "Lab05.java";
    static final Class<IllegalArgumentException> IAE = IllegalArgumentException.class;
    static final Class<java.util.NoSuchElementException> NSEE = java.util.NoSuchElementException.class;

    static Object circular(int capacity, Object... items) {
        Object q = T.make("CircularQueue", capacity);
        for (Object item : items) {
            T.call(q, "enqueue", item);
        }
        return q;
    }

    static Object linked(Object... items) {
        Object q = T.make("LinkedQueue");
        for (Object item : items) {
            T.call(q, "enqueue", item);
        }
        return q;
    }

    static java.util.List<Object> slots(Object q) {
        return java.util.Arrays.asList((Object[]) T.field(q, "items"));
    }

    public static void main(String[] args) {

        // ================================================================ CircularQueue
        T.test("CircularQueue", "test_CircularQueue_structure", "CircularQueue structure", () -> {
            T.expectTrue("all CircularQueue fields must be private", T.fieldsPrivate("CircularQueue"));
        });

        T.test("CircularQueue", "test_CircularQueue_constructor", "CircularQueue constructors", () -> {
            T.expect("new CircularQueue().size() should be 0", 0, () -> T.call(T.make("CircularQueue"), "size"));
            T.expect("new CircularQueue().capacity() should be 8", 8, () -> T.call(T.make("CircularQueue"), "capacity"));
            T.expect("new CircularQueue(3).capacity() should be 3", 3, () -> T.call(T.make("CircularQueue", 3), "capacity"));
            T.expectThrows("new CircularQueue(0) should throw IllegalArgumentException", IAE,
                    () -> T.make("CircularQueue", 0));
            T.expectThrows("new CircularQueue(-4) should throw IllegalArgumentException", IAE,
                    () -> T.make("CircularQueue", -4));
        });

        T.test("CircularQueue", "test_CircularQueue_enqueue", "enqueue", () -> {
            T.method("CircularQueue", "enqueue", 1);
            Object q = circular(4, 10, 20, 30);
            T.expect("after enqueue(10), enqueue(20), enqueue(30), size() should be 3", 3, () -> T.call(q, "size"));
            T.expect("enqueue must not change the capacity while there is room", 4, () -> T.call(q, "capacity"));
            T.expect("the front of the queue should be the first item enqueued: 10", 10, () -> T.call(q, "peek"));
            T.expect("toString() should list the items from front to back: [10, 20, 30]", "[10, 20, 30]",
                    () -> T.call(q, "toString"));
            T.expectThrows("enqueue(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(T.make("CircularQueue"), "enqueue", (Object) null));
        });

        T.test("CircularQueue", "test_CircularQueue_dequeue", "dequeue is first-in first-out", () -> {
            T.method("CircularQueue", "dequeue", 0);
            Object q = circular(8, 10, 20, 30);
            T.expect("dequeue() on [10, 20, 30] should return 10 (the front)", 10, () -> T.call(q, "dequeue"));
            T.expect("after one dequeue, size() should be 2", 2, () -> T.call(q, "size"));
            T.expect("after one dequeue, toString() should be [20, 30]", "[20, 30]", () -> T.call(q, "toString"));
            T.expect("the next dequeue() should return 20", 20, () -> T.call(q, "dequeue"));
            T.expect("the next dequeue() should return 30", 30, () -> T.call(q, "dequeue"));
            T.expectTrue("the queue must be empty after dequeuing every item", () -> T.call(q, "isEmpty"));
            T.expect("dequeue() must not shrink the capacity", 8, () -> T.call(q, "capacity"));
        });

        T.test("CircularQueue", "test_CircularQueue_peek", "peek", () -> {
            T.method("CircularQueue", "peek", 0);
            Object q = circular(8, 10, 20, 30);
            T.expect("peek() on [10, 20, 30] should return 10", 10, () -> T.call(q, "peek"));
            T.expect("peek() must not remove the item: size() should still be 3", 3, () -> T.call(q, "size"));
            T.expect("peek() must not remove the item: toString() should be unchanged", "[10, 20, 30]",
                    () -> T.call(q, "toString"));
        });

        T.test("CircularQueue", "test_CircularQueue_emptyExceptions", "empty queue behavior", () -> {
            Object q = T.make("CircularQueue");
            T.expectTrue("a new CircularQueue must be empty", () -> T.call(q, "isEmpty"));
            T.expect("isEmpty() must be false after one enqueue", false, () -> {
                T.call(q, "enqueue", 1);
                return T.call(q, "isEmpty");
            });
            T.expectThrows("dequeue() on an empty queue should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make("CircularQueue"), "dequeue"));
            T.expectThrows("peek() on an empty queue should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make("CircularQueue"), "peek"));
            T.expectThrows("dequeuing past the last item should throw NoSuchElementException", NSEE, () -> {
                Object t = circular(4, 1);
                T.call(t, "dequeue");
                T.call(t, "dequeue");
            });
        });

        T.test("CircularQueue", "test_CircularQueue_wraparound", "wrap around", () -> {
            Object q = circular(4, 1, 2, 3, 4);
            T.call(q, "dequeue");
            T.call(q, "dequeue");
            T.call(q, "enqueue", 5);
            T.call(q, "enqueue", 6);
            T.expect("after wrapping around, the capacity must still be 4 (nothing was full)", 4,
                    () -> T.call(q, "capacity"));
            T.expect("after wrapping around, size() should be 4", 4, () -> T.call(q, "size"));
            T.expect("the internal array should be [5, 6, 3, 4]: new items reuse the freed slots at index 0 and 1",
                    java.util.Arrays.asList(5, 6, 3, 4), () -> slots(q));
            T.expect("the front index should be 2 after two dequeues", 2, () -> T.field(q, "front"));
            T.expect("toString() must read from the front and wrap: [3, 4, 5, 6]", "[3, 4, 5, 6]",
                    () -> T.call(q, "toString"));
            T.expect("dequeue() should return 3 after wrapping", 3, () -> T.call(q, "dequeue"));
            T.expect("dequeue() should return 4 after wrapping", 4, () -> T.call(q, "dequeue"));
            T.expect("dequeue() should return 5 (from the wrapped part)", 5, () -> T.call(q, "dequeue"));
            T.expect("after three dequeues in total, the front index should be 1 (2 + 3 = 5, wrapped by 4)", 1,
                    () -> T.field(q, "front"));
        });

        T.test("CircularQueue", "test_CircularQueue_grow", "growing a wrapped queue", () -> {
            Object q = circular(4, 1, 2, 3, 4);
            T.call(q, "dequeue");
            T.call(q, "dequeue");
            T.call(q, "enqueue", 5);
            T.call(q, "enqueue", 6);
            T.call(q, "enqueue", 7);
            T.expect("enqueue into a full CircularQueue(4) should double the capacity to 8", 8, () -> T.call(q, "capacity"));
            T.expect("growing must keep the order from front to back: [3, 4, 5, 6, 7]", "[3, 4, 5, 6, 7]",
                    () -> T.call(q, "toString"));
            T.expect("after growing, the front item must be copied to index 0", 0, () -> T.field(q, "front"));
            T.expect("after growing, the internal array should start with 3, 4, 5, 6, 7",
                    java.util.Arrays.asList(3, 4, 5, 6, 7, null, null, null), () -> slots(q));
            T.expect("dequeue() after growing should return 3", 3, () -> T.call(q, "dequeue"));
            Object g = circular(1);
            for (int i = 1; i <= 9; i++) {
                T.call(g, "enqueue", i);
            }
            T.expect("enqueuing 9 items into CircularQueue(1) should give capacity 16", 16, () -> T.call(g, "capacity"));
            T.expect("all 9 items must stay in order after repeated growth", "[1, 2, 3, 4, 5, 6, 7, 8, 9]",
                    () -> T.call(g, "toString"));
        });

        T.test("CircularQueue", "test_CircularQueue_clearsSlot", "dequeue clears the slot", () -> {
            Object q = circular(4, 1, 2, 3);
            T.call(q, "dequeue");
            T.expectNull("after dequeue(), the freed slot items[0] must be set back to null", () -> slots(q).get(0));
            T.call(q, "dequeue");
            T.expectNull("after two dequeues, the freed slot items[1] must be set back to null", () -> slots(q).get(1));
        });

        T.test("CircularQueue", "test_CircularQueue_toString", "toString", () -> {
            T.expect("toString() of a new CircularQueue should be []", "[]", () -> T.call(T.make("CircularQueue"), "toString"));
            T.expect("toString() of a queue with one item", "[5]", () -> T.call(circular(3, 5), "toString"));
            T.expect("toString() lists items front to back", "[1, 2, 3]", () -> T.call(circular(3, 1, 2, 3), "toString"));
            T.expect("toString() of a queue that became empty again should be []", "[]", () -> {
                Object q = circular(2, 1);
                T.call(q, "dequeue");
                return T.call(q, "toString");
            });
        });

        // ================================================================ LinkedQueue
        T.test("LinkedQueue", "test_LinkedQueue_structure", "LinkedQueue structure", () -> {
            T.expectTrue("all LinkedQueue fields must be private", T.fieldsPrivate("LinkedQueue"));
            T.expectNull("a new LinkedQueue must have head == null", () -> T.field(T.make("LinkedQueue"), "head"));
            T.expectNull("a new LinkedQueue must have tail == null", () -> T.field(T.make("LinkedQueue"), "tail"));
        });

        T.test("LinkedQueue", "test_LinkedQueue_fifo", "enqueue and dequeue", () -> {
            T.method("LinkedQueue", "enqueue", 1);
            T.method("LinkedQueue", "dequeue", 0);
            Object q = linked("a", "b", "c");
            T.expect("size() after three enqueues should be 3", 3, () -> T.call(q, "size"));
            T.expect("dequeue() should return the first item enqueued: a", "a", () -> T.call(q, "dequeue"));
            T.expect("the next dequeue() should return b", "b", () -> T.call(q, "dequeue"));
            T.expect("after two dequeues, size() should be 1", 1, () -> T.call(q, "size"));
            T.expect("the last dequeue() should return c", "c", () -> T.call(q, "dequeue"));
            T.expectTrue("the queue must be empty after dequeuing every item", () -> T.call(q, "isEmpty"));
        });

        T.test("LinkedQueue", "test_LinkedQueue_peek", "peek", () -> {
            T.method("LinkedQueue", "peek", 0);
            Object q = linked(10, 20);
            T.expect("peek() on [10, 20] should return 10", 10, () -> T.call(q, "peek"));
            T.expect("peek() must not remove the item: size() should still be 2", 2, () -> T.call(q, "size"));
            T.expect("enqueue after peek must add at the back: [10, 20, 30]", "[10, 20, 30]", () -> {
                T.call(q, "enqueue", 30);
                return T.call(q, "toString");
            });
        });

        T.test("LinkedQueue", "test_LinkedQueue_invalid", "invalid operations", () -> {
            T.expectThrows("enqueue(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(T.make("LinkedQueue"), "enqueue", (Object) null));
            T.expectThrows("dequeue() on an empty queue should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make("LinkedQueue"), "dequeue"));
            T.expectThrows("peek() on an empty queue should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make("LinkedQueue"), "peek"));
            T.expectThrows("dequeuing past the last item should throw NoSuchElementException", NSEE, () -> {
                Object t = linked(1);
                T.call(t, "dequeue");
                T.call(t, "dequeue");
            });
        });

        T.test("LinkedQueue", "test_LinkedQueue_reuse", "reusing an emptied queue", () -> {
            Object q = linked(1);
            T.call(q, "dequeue");
            T.expectNull("after the last item is dequeued, tail must be null again", () -> T.field(q, "tail"));
            T.expectNull("after the last item is dequeued, head must be null again", () -> T.field(q, "head"));
            T.call(q, "enqueue", 2);
            T.call(q, "enqueue", 3);
            T.expect("an emptied queue must work again: toString() should be [2, 3]", "[2, 3]", () -> T.call(q, "toString"));
            T.expect("an emptied queue must work again: size() should be 2", 2, () -> T.call(q, "size"));
            T.expect("an emptied queue must work again: dequeue() should return 2", 2, () -> T.call(q, "dequeue"));
            T.expect("an emptied queue must work again: peek() should now be 3", 3, () -> T.call(q, "peek"));
        });

        T.test("LinkedQueue", "test_LinkedQueue_toString", "toString", () -> {
            T.expect("toString() of a new LinkedQueue should be []", "[]", () -> T.call(T.make("LinkedQueue"), "toString"));
            T.expect("toString() of a queue with one item", "[5]", () -> T.call(linked(5), "toString"));
            T.expect("toString() lists items front to back", "[1, 2, 3]", () -> T.call(linked(1, 2, 3), "toString"));
            T.expect("a long queue must keep every item in order", "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]",
                    () -> T.call(linked(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), "toString"));
        });

        // ================================================================ hotPotato
        T.test("hotPotato", "test_hotPotato_basic", "hotPotato winners", () -> {
            T.expect("hotPotato([A, B, C, D], 3) should be A", "A",
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", "B", "C", "D"}, 3));
            T.expect("hotPotato([A, B, C, D, E], 2) should be C", "C",
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", "B", "C", "D", "E"}, 2));
            T.expect("hotPotato([A, B, C], 5) should be A (k larger than the number of players)", "A",
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", "B", "C"}, 5));
        });

        T.test("hotPotato", "test_hotPotato_edges", "hotPotato edge cases", () -> {
            T.expect("hotPotato([A, B, C], 1) should be C (the front player is eliminated every round)", "C",
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", "B", "C"}, 1));
            T.expect("hotPotato([Solo], 4) should be Solo (one player wins immediately)", "Solo",
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"Solo"}, 4));
            T.expect("hotPotato([A, B], 2) should be A", "A",
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", "B"}, 2));
            String[] players = {"A", "B", "C", "D"};
            T.callStatic("Lab05", "hotPotato", (Object) players, 3);
            T.expect("hotPotato must leave the players array unchanged", java.util.Arrays.asList("A", "B", "C", "D"),
                    () -> players);
        });

        T.test("hotPotato", "test_hotPotato_invalid", "hotPotato invalid input", () -> {
            T.expectThrows("hotPotato(null, 2) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab05", "hotPotato", null, 2));
            T.expectThrows("hotPotato([], 2) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[0], 2));
            T.expectThrows("hotPotato([A, B], 0) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", "B"}, 0));
            T.expectThrows("hotPotato([A, B], -1) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", "B"}, -1));
            T.expectThrows("hotPotato([A, null], 2) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", null}, 2));
        });

        // ================================================================ generateBinary
        T.test("generateBinary", "test_generateBinary_basic", "generateBinary values", () -> {
            T.expect("generateBinary(1) should be [1]", java.util.Arrays.asList("1"),
                    () -> T.callStatic("Lab05", "generateBinary", 1));
            T.expect("generateBinary(5) should be [1, 10, 11, 100, 101]",
                    java.util.Arrays.asList("1", "10", "11", "100", "101"),
                    () -> T.callStatic("Lab05", "generateBinary", 5));
            T.expect("generateBinary(10) should end with 1000, 1001, 1010",
                    java.util.Arrays.asList("1", "10", "11", "100", "101", "110", "111", "1000", "1001", "1010"),
                    () -> T.callStatic("Lab05", "generateBinary", 10));
        });

        T.test("generateBinary", "test_generateBinary_size", "generateBinary size and invalid input", () -> {
            T.expect("generateBinary(20) should return an array of length 20", 20,
                    () -> ((String[]) T.callStatic("Lab05", "generateBinary", 20)).length);
            T.expect("the last value of generateBinary(20) should be 10100", "10100",
                    () -> ((String[]) T.callStatic("Lab05", "generateBinary", 20))[19]);
            T.expectThrows("generateBinary(0) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab05", "generateBinary", 0));
            T.expectThrows("generateBinary(-3) should throw IllegalArgumentException", IAE,
                    () -> T.callStatic("Lab05", "generateBinary", -3));
        });

        // ================================================================ rules
        T.test("(code rules)", "test_Lab05_rules", "Lab05 code rules", () -> {
            T.expectFalse("only Lab05 may be public", T.isPublic("CircularQueue") || T.isPublic("LinkedQueue"));
            T.expectFalse("Lab05.java must not use a java.util collection (ArrayList, LinkedList, Queue, Deque, ArrayDeque, Stack, Vector, PriorityQueue)",
                    T.uses(F, "ArrayList") || T.uses(F, "LinkedList") || T.uses(F, "Queue") || T.uses(F, "Deque")
                            || T.uses(F, "ArrayDeque") || T.uses(F, "Stack") || T.uses(F, "Vector")
                            || T.uses(F, "PriorityQueue"));
            T.expectFalse("Lab05.java must not use java.util.Arrays: grow the array with your own loop",
                    T.uses(F, "Arrays"));
            T.expectFalse("generateBinary must not use Integer.toBinaryString or Integer.parseInt: build the strings with a queue",
                    T.uses(F, "toBinaryString") || T.uses(F, "parseInt"));
            T.expectSilent("the required methods must not print", () -> {
                try {
                    Object c = circular(2, 1, 2, 3);
                    T.call(c, "dequeue");
                    T.call(c, "peek");
                    T.call(c, "toString");
                    Object l = linked(1, 2, 3);
                    T.call(l, "dequeue");
                    T.call(l, "peek");
                    T.call(l, "toString");
                    T.callStatic("Lab05", "hotPotato", (Object) new String[] {"A", "B", "C"}, 2);
                    T.callStatic("Lab05", "generateBinary", 4);
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
