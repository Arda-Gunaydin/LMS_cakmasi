/** Hidden tests for last year's Lab 3: Singly, Circular and Doubly Linked Lists (one test = one check). */
public class Tests {

    static final String F = "Lab03.java";
    static final String[] LISTS = {"SinglyLinkedList", "CircularLinkedList", "DoublyLinkedList"};

    static Object listOf(String cls, Object... items) {
        Object l = T.make(cls);
        for (Object x : items) {
            T.call(l, "addLast", x);
        }
        return l;
    }

    /**
     * Walks the real nodes (not get(i)) and returns the elements as "[a, b]".
     * Broken links are returned as a description, so the failure message explains what is wrong.
     */
    static String nodes(Object l) {
        String cls = l.getClass().getSimpleName();
        int size = (Integer) T.call(l, "size");
        StringBuilder sb = new StringBuilder("[");
        if (cls.equals("CircularLinkedList")) {
            Object tail = T.call(l, "getTail");
            if (size == 0) {
                return tail == null ? "[]" : "(size is 0 but tail is not null)";
            }
            if (tail == null) {
                return "(size is " + size + " but tail is null)";
            }
            Object first = T.field(tail, "next");
            Object n = first;
            for (int i = 0; i < size; i++) {
                if (n == null) {
                    return "(a next reference is null: a circular list never has null links)";
                }
                if (i == size - 1 && n != tail) {
                    return "(the node at index size-1 is not tail)";
                }
                sb.append(i > 0 ? ", " : "").append(T.field(n, "item"));
                n = T.field(n, "next");
            }
            if (n != first) {
                return "(after size steps from tail.next the walk did not come back to tail.next)";
            }
            return sb.append(']').toString();
        }
        boolean dbl = cls.equals("DoublyLinkedList");
        Object head = T.call(l, "getHead");
        Object tail = T.call(l, "getTail");
        if (size == 0) {
            return head == null && tail == null ? "[]" : "(size is 0 but head or tail is not null)";
        }
        if (dbl && head != null && T.field(head, "prev") != null) {
            return "(head.prev must be null)";
        }
        Object n = head;
        Object prev = null;
        for (int i = 0; i < size; i++) {
            if (n == null) {
                return "(only " + i + " nodes can be reached from head, but size is " + size + ")";
            }
            if (dbl && T.field(n, "prev") != prev) {
                return "(the prev link of the node at index " + i + " is wrong)";
            }
            sb.append(i > 0 ? ", " : "").append(T.field(n, "item"));
            prev = n;
            n = T.field(n, "next");
        }
        if (n != null) {
            return "(more than size nodes can be reached from head, or tail.next is not null)";
        }
        if (prev != tail) {
            return "(the last node reached from head is not tail)";
        }
        return sb.append(']').toString();
    }

    static void add(String L) {
            T.method(L, "addFirst", 1);
            Object l = T.make(L);
            T.call(l, "addLast", 20);
            T.expect("addLast(20) on an empty list should give [20]", "[20]", () -> nodes(l));
            T.call(l, "addFirst", 10);
            T.expect("addFirst(10) should give [10, 20]", "[10, 20]", () -> nodes(l));
            T.call(l, "insertAt", 2, 40);
            T.expect("insertAt(2, 40) (at size) should give [10, 20, 40]", "[10, 20, 40]", () -> nodes(l));
            T.call(l, "insertAt", 2, 30);
            T.expect("insertAt(2, 30) should give [10, 20, 30, 40]", "[10, 20, 30, 40]", () -> nodes(l));
            T.call(l, "insertAt", 0, 5);
            T.expect("insertAt(0, 5) should give [5, 10, 20, 30, 40]", "[5, 10, 20, 30, 40]", () -> nodes(l));
            T.call(l, "addLast", 50);
            T.expect("addLast(50) should give [5, 10, 20, 30, 40, 50]", "[5, 10, 20, 30, 40, 50]", () -> nodes(l));
            T.expect("size() should be 6", 6, () -> T.call(l, "size"));
            Object e = T.make(L);
            T.call(e, "addFirst", "x");
            T.expect("addFirst on an empty list should give [x]", "[x]", () -> nodes(e));
            Object z = T.make(L);
            T.call(z, "insertAt", 0, 7);
            T.expect("insertAt(0, 7) on an empty list should give [7]", "[7]", () -> nodes(z));
    }
    static void insertInvalid(String L) {
            Object l = listOf(L, 1, 2);
            T.expectNoThrow("insertAt(-1, 9) must not throw", () -> T.call(l, "insertAt", -1, 9));
            T.expectNoThrow("insertAt(3, 9) on a list of size 2 must not throw", () -> T.call(l, "insertAt", 3, 9));
            T.expect("invalid insertAt calls must change nothing: [1, 2]", "[1, 2]", () -> nodes(l));
            T.expect("size() should stay 2", 2, () -> T.call(l, "size"));
    }
    static void removeEnds(String L) {
            T.method(L, "removeLast", 0);
            Object l = listOf(L, 1, 2, 3, 4);
            T.expect("removeFirst() on [1, 2, 3, 4] should return 1", 1, () -> T.call(l, "removeFirst"));
            T.expect("removeLast() should return 4", 4, () -> T.call(l, "removeLast"));
            T.expect("the list should be [2, 3]", "[2, 3]", () -> nodes(l));
            T.call(l, "addLast", 5);
            T.expect("addLast(5) after removeLast should give [2, 3, 5]", "[2, 3, 5]", () -> nodes(l));
            T.expect("removeLast() should return 5", 5, () -> T.call(l, "removeLast"));
            T.expect("removeLast() should return 3", 3, () -> T.call(l, "removeLast"));
            T.expect("removeLast() on a one-element list should return 2", 2, () -> T.call(l, "removeLast"));
            T.expect("the list must be empty (head/tail null)", "[]", () -> nodes(l));
            T.expectNull("removeLast() on an empty list should return null", () -> T.call(l, "removeLast"));
            T.expectNull("removeFirst() on an empty list should return null", () -> T.call(l, "removeFirst"));
            T.expect("size() should still be 0", 0, () -> T.call(l, "size"));
            T.call(l, "addFirst", 8);
            T.call(l, "addLast", 9);
            T.expect("adding after emptying should give [8, 9]", "[8, 9]", () -> nodes(l));
            Object one = listOf(L, "a");
            T.expect("removeFirst() on [a] should return a", "a", () -> T.call(one, "removeFirst"));
            T.expect("removing the only element with removeFirst should leave []", "[]", () -> nodes(one));
    }
    static void indexed(String L) {
            T.method(L, "removeAt", 1);
            Object l = listOf(L, 10, 20, 30, 40);
            T.expect("get(0) should be 10", 10, () -> T.call(l, "get", 0));
            T.expect("get(3) should be 40", 40, () -> T.call(l, "get", 3));
            T.expectNull("get(4) on a list of size 4 should be null", () -> T.call(l, "get", 4));
            T.expectNull("get(-1) should be null", () -> T.call(l, "get", -1));
            T.expect("set(1, 25) should return the old element 20", 20, () -> T.call(l, "set", 1, 25));
            T.expectNull("set(4, 1) should return null", () -> T.call(l, "set", 4, 1));
            T.expect("after set the list should be [10, 25, 30, 40]", "[10, 25, 30, 40]", () -> nodes(l));
            T.expect("removeAt(1) should return 25", 25, () -> T.call(l, "removeAt", 1));
            T.expect("removeAt(2) (the last index) should return 40", 40, () -> T.call(l, "removeAt", 2));
            T.expect("the list should be [10, 30]", "[10, 30]", () -> nodes(l));
            T.call(l, "addLast", 50);
            T.expect("addLast after removeAt(last) should give [10, 30, 50]", "[10, 30, 50]", () -> nodes(l));
            T.expect("removeAt(0) should return 10", 10, () -> T.call(l, "removeAt", 0));
            T.expectNull("removeAt(2) on a list of size 2 should return null", () -> T.call(l, "removeAt", 2));
            T.expectNull("removeAt(-1) should return null", () -> T.call(l, "removeAt", -1));
            T.expect("invalid removeAt must change nothing: [30, 50]", "[30, 50]", () -> nodes(l));
            T.expectNull("get(0) on an empty list should be null", () -> T.call(T.make(L), "get", 0));
    }
    static void search(String L) {
            T.method(L, "indexOf", 1);
            Object l = listOf(L, "a", "b", "a", "c");
            T.expect("indexOf(a) should be 0 (first match)", 0, () -> T.call(l, "indexOf", "a"));
            T.expect("indexOf(c) should be 3", 3, () -> T.call(l, "indexOf", "c"));
            T.expect("indexOf(z) should be -1", -1, () -> T.call(l, "indexOf", "z"));
            T.expect("indexOf must use equals: indexOf(new String(\"b\")) should be 1", 1,
                    () -> T.call(l, "indexOf", new String("b")));
            T.expect("indexOf on an empty list should be -1", -1, () -> T.call(T.make(L), "indexOf", "a"));
            T.expect("remove(a) should return true", true, () -> T.call(l, "remove", "a"));
            T.expect("remove(a) removes only the first a: [b, a, c]", "[b, a, c]", () -> nodes(l));
            T.expect("remove(c) (the last element) should return true", true, () -> T.call(l, "remove", "c"));
            T.expect("the list should be [b, a]", "[b, a]", () -> nodes(l));
            T.call(l, "addLast", "d");
            T.expect("addLast after removing the last element should give [b, a, d]", "[b, a, d]", () -> nodes(l));
            T.expect("remove(z) should return false", false, () -> T.call(l, "remove", "z"));
            T.expect("remove on an empty list should return false", false, () -> T.call(T.make(L), "remove", "a"));
            Object one = listOf(L, "q");
            T.call(one, "remove", "q");
            T.expect("removing the only element with remove(o) should leave []", "[]", () -> nodes(one));
    }
    static void clear(String L) {
            Object l = listOf(L, 1, 2, 3);
            T.call(l, "clear");
            T.expect("clear() should make the list empty (head/tail null)", "[]", () -> nodes(l));
            T.expectTrue("isEmpty() should be true after clear()", () -> T.call(l, "isEmpty"));
            T.call(l, "addLast", 6);
            T.call(l, "addFirst", 5);
            T.expect("the list must work after clear(): [5, 6]", "[5, 6]", () -> nodes(l));
    }
    static void mixed(String L) {
            Object l = T.make(L);
            for (int i = 1; i <= 6; i++) {
                T.call(l, "addLast", i);
            }
            T.call(l, "removeFirst");
            T.call(l, "insertAt", 2, 9);
            T.call(l, "removeLast");
            T.call(l, "remove", 4);
            T.call(l, "addFirst", 0);
            T.call(l, "removeAt", 3);
            T.call(l, "addLast", 7);
            T.expect("after a series of operations the list should be [0, 2, 3, 5, 7]", "[0, 2, 3, 5, 7]",
                    () -> nodes(l));
            T.expect("size() should be 5", 5, () -> T.call(l, "size"));
            T.expect("get(4) should be 7", 7, () -> T.call(l, "get", 4));
    }

    public static void main(String[] args) {

        T.test("(structure)", "test_Lab03_structure", "lists implement MyList", () -> {
            for (String c : LISTS) {
                T.expectTrue(c + " must implement MyList", () -> T.isA(c, "MyList"));
                T.expectTrue("all fields of " + c + " must be private", T.fieldsPrivate(c));
                T.expect("a new " + c + " should be empty", "[]", () -> nodes(T.make(c)));
            }
            Object circ = listOf("CircularLinkedList", 1, 2);
            T.expectTrue("in CircularLinkedList, tail.next must be head",
                    () -> T.field(T.field(circ, "tail"), "next") == T.field(circ, "head"));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_add", "SinglyLinkedList addFirst, addLast and insertAt", () -> add("SinglyLinkedList"));
        T.test("SinglyLinkedList", "test_SinglyLinkedList_insertInvalid", "SinglyLinkedList insertAt with an invalid index", () -> insertInvalid("SinglyLinkedList"));
        T.test("SinglyLinkedList", "test_SinglyLinkedList_removeEnds", "SinglyLinkedList removeFirst and removeLast", () -> removeEnds("SinglyLinkedList"));
        T.test("SinglyLinkedList", "test_SinglyLinkedList_indexed", "SinglyLinkedList get, set and removeAt", () -> indexed("SinglyLinkedList"));
        T.test("SinglyLinkedList", "test_SinglyLinkedList_search", "SinglyLinkedList indexOf and remove(o)", () -> search("SinglyLinkedList"));
        T.test("SinglyLinkedList", "test_SinglyLinkedList_clear", "SinglyLinkedList clear", () -> clear("SinglyLinkedList"));
        T.test("SinglyLinkedList", "test_SinglyLinkedList_mixed", "SinglyLinkedList mixed operations", () -> mixed("SinglyLinkedList"));

        T.test("CircularLinkedList", "test_CircularLinkedList_add", "CircularLinkedList addFirst, addLast and insertAt", () -> add("CircularLinkedList"));
        T.test("CircularLinkedList", "test_CircularLinkedList_insertInvalid", "CircularLinkedList insertAt with an invalid index", () -> insertInvalid("CircularLinkedList"));
        T.test("CircularLinkedList", "test_CircularLinkedList_removeEnds", "CircularLinkedList removeFirst and removeLast", () -> removeEnds("CircularLinkedList"));
        T.test("CircularLinkedList", "test_CircularLinkedList_indexed", "CircularLinkedList get, set and removeAt", () -> indexed("CircularLinkedList"));
        T.test("CircularLinkedList", "test_CircularLinkedList_search", "CircularLinkedList indexOf and remove(o)", () -> search("CircularLinkedList"));
        T.test("CircularLinkedList", "test_CircularLinkedList_clear", "CircularLinkedList clear", () -> clear("CircularLinkedList"));
        T.test("CircularLinkedList", "test_CircularLinkedList_mixed", "CircularLinkedList mixed operations", () -> mixed("CircularLinkedList"));

        T.test("DoublyLinkedList", "test_DoublyLinkedList_add", "DoublyLinkedList addFirst, addLast and insertAt", () -> add("DoublyLinkedList"));
        T.test("DoublyLinkedList", "test_DoublyLinkedList_insertInvalid", "DoublyLinkedList insertAt with an invalid index", () -> insertInvalid("DoublyLinkedList"));
        T.test("DoublyLinkedList", "test_DoublyLinkedList_removeEnds", "DoublyLinkedList removeFirst and removeLast", () -> removeEnds("DoublyLinkedList"));
        T.test("DoublyLinkedList", "test_DoublyLinkedList_indexed", "DoublyLinkedList get, set and removeAt", () -> indexed("DoublyLinkedList"));
        T.test("DoublyLinkedList", "test_DoublyLinkedList_search", "DoublyLinkedList indexOf and remove(o)", () -> search("DoublyLinkedList"));
        T.test("DoublyLinkedList", "test_DoublyLinkedList_clear", "DoublyLinkedList clear", () -> clear("DoublyLinkedList"));
        T.test("DoublyLinkedList", "test_DoublyLinkedList_mixed", "DoublyLinkedList mixed operations", () -> mixed("DoublyLinkedList"));

        T.test("(code rules)", "test_Lab03_rules", "Lab03 code rules", () -> {
            T.expectFalse("Lab03.java must not use java.util", T.matches(F, "\\bjava\\s*\\.\\s*util\\b"));
            T.expectFalse("the lists must not use arrays", T.matches(F, "new\\s+\\w+(<[^>]*>)?\\s*\\["));
            T.expectFalse("only Lab03 may be public",
                    T.isPublic("SinglyLinkedList") || T.isPublic("CircularLinkedList") || T.isPublic("DoublyLinkedList"));
            T.expectSilent("the list methods must not print", () -> {
                for (String c : LISTS) {
                    try {
                        Object l = listOf(c, 1, 2, 3);
                        T.call(l, "insertAt", 9, 1);
                        T.call(l, "get", 7);
                        T.call(l, "removeAt", 7);
                        T.call(l, "remove", 2);
                        T.call(l, "removeLast");
                        T.call(l, "removeFirst");
                        T.call(l, "removeFirst");
                    } catch (Throwable ignored) {
                        // printing is what matters here
                    }
                }
            });
        });

        T.done();
    }
}
