/** Hidden tests for Lab 4: Linked Lists (school style: one test = one check). */
public class Tests {

    static final String F = "Lab04.java";
    static final String L = "SinglyLinkedList";
    static final Class<IllegalArgumentException> IAE = IllegalArgumentException.class;
    static final Class<IndexOutOfBoundsException> IOOBE = IndexOutOfBoundsException.class;
    static final Class<java.util.NoSuchElementException> NSEE = java.util.NoSuchElementException.class;

    static Object listOf(Object... items) {
        Object l = T.make(L);
        for (Object item : items) {
            T.call(l, "addLast", item);
        }
        return l;
    }

    static String str(Object l) {
        return String.valueOf(T.call(l, "toString"));
    }

    static Object merge(Object a, Object b) {
        return T.callStatic("Lab04", "mergeSorted", a, b);
    }

    public static void main(String[] args) {

        T.test("SinglyLinkedList", "test_SinglyLinkedList_structure", "SinglyLinkedList structure", () -> {
            T.expectTrue("all SinglyLinkedList fields must be private", T.fieldsPrivate(L));
            T.expect("a new list should have size 0", 0, () -> T.call(T.make(L), "size"));
            T.expectTrue("a new list must be empty", () -> T.call(T.make(L), "isEmpty"));
            T.expect("toString() of a new list should be []", "[]", () -> str(T.make(L)));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_addFirst", "addFirst", () -> {
            T.method(L, "addFirst", 1);
            Object l = T.make(L);
            T.call(l, "addFirst", 3);
            T.call(l, "addFirst", 2);
            T.call(l, "addFirst", 1);
            T.expect("after addFirst(3), addFirst(2), addFirst(1) the list should be [1, 2, 3]", "[1, 2, 3]", () -> str(l));
            T.expect("size() should be 3", 3, () -> T.call(l, "size"));
            T.expect("getFirst() should be 1", 1, () -> T.call(l, "getFirst"));
            T.expectThrows("addFirst(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(T.make(L), "addFirst", (Object) null));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_addLast", "addLast", () -> {
            T.method(L, "addLast", 1);
            Object l = T.make(L);
            T.call(l, "addLast", "a");
            T.expect("after addLast(\"a\") on an empty list, getFirst() should be a", "a", () -> T.call(l, "getFirst"));
            T.call(l, "addLast", "b");
            T.call(l, "addLast", "c");
            T.expect("after addLast a, b, c the list should be [a, b, c]", "[a, b, c]", () -> str(l));
            T.expect("size() should be 3", 3, () -> T.call(l, "size"));
            T.expectThrows("addLast(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(T.make(L), "addLast", (Object) null));
            T.expectTrue("addLast must keep tail as the last node",
                    () -> T.field(T.field(l, "tail"), "item").equals("c") && T.field(T.field(l, "tail"), "next") == null);
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_addAtIndex", "add(index, item)", () -> {
            T.method(L, "add", 2);
            Object l = listOf(10, 30);
            T.call(l, "add", 1, 20);
            T.expect("add(1, 20) on [10, 30] should give [10, 20, 30]", "[10, 20, 30]", () -> str(l));
            T.call(l, "add", 0, 5);
            T.expect("add(0, 5) should insert at the front: [5, 10, 20, 30]", "[5, 10, 20, 30]", () -> str(l));
            T.call(l, "add", 4, 40);
            T.expect("add(size, 40) should append: [5, 10, 20, 30, 40]", "[5, 10, 20, 30, 40]", () -> str(l));
            T.call(l, "addLast", 50);
            T.expect("addLast after add(size, item) should still append: [5, 10, 20, 30, 40, 50]",
                    "[5, 10, 20, 30, 40, 50]", () -> str(l));
            T.expect("size() should be 6", 6, () -> T.call(l, "size"));
            Object e = T.make(L);
            T.call(e, "add", 0, 1);
            T.expect("add(0, 1) on an empty list should give [1]", "[1]", () -> str(e));
            T.expectThrows("add(-1, x) should throw IndexOutOfBoundsException", IOOBE, () -> T.call(listOf(1, 2), "add", -1, 9));
            T.expectThrows("add(3, x) on a list of size 2 should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(listOf(1, 2), "add", 3, 9));
            T.expectThrows("add(1, null) should throw IllegalArgumentException", IAE,
                    () -> T.call(listOf(1, 2), "add", 1, (Object) null));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_get", "get and getFirst", () -> {
            T.method(L, "get", 1);
            Object l = listOf(10, 20, 30);
            T.expect("get(0) should be 10", 10, () -> T.call(l, "get", 0));
            T.expect("get(1) should be 20", 20, () -> T.call(l, "get", 1));
            T.expect("get(2) should be 30", 30, () -> T.call(l, "get", 2));
            T.expect("get must not change the list", "[10, 20, 30]", () -> str(l));
            T.expectThrows("get(-1) should throw IndexOutOfBoundsException", IOOBE, () -> T.call(l, "get", -1));
            T.expectThrows("get(3) on a list of size 3 should throw IndexOutOfBoundsException", IOOBE, () -> T.call(l, "get", 3));
            T.expectThrows("get(0) on an empty list should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(T.make(L), "get", 0));
            T.expectThrows("getFirst() on an empty list should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make(L), "getFirst"));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_set", "set", () -> {
            T.method(L, "set", 2);
            Object l = listOf(10, 20, 30);
            T.expect("set(1, 99) should return the old item 20", 20, () -> T.call(l, "set", 1, 99));
            T.expect("after set(1, 99) the list should be [10, 99, 30]", "[10, 99, 30]", () -> str(l));
            T.expect("set(2, 77) on the last index should return 30", 30, () -> T.call(l, "set", 2, 77));
            T.expect("size() must not change after set", 3, () -> T.call(l, "size"));
            T.expectThrows("set(3, 1) on a list of size 3 should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(l, "set", 3, 1));
            T.expectThrows("set(-1, 1) should throw IndexOutOfBoundsException", IOOBE, () -> T.call(l, "set", -1, 1));
            T.expectThrows("set(0, null) should throw IllegalArgumentException", IAE, () -> T.call(l, "set", 0, (Object) null));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_removeFirst", "removeFirst", () -> {
            T.method(L, "removeFirst", 0);
            Object l = listOf(1, 2, 3);
            T.expect("removeFirst() on [1, 2, 3] should return 1", 1, () -> T.call(l, "removeFirst"));
            T.expect("after removeFirst the list should be [2, 3]", "[2, 3]", () -> str(l));
            T.expect("size() should be 2", 2, () -> T.call(l, "size"));
            T.call(l, "removeFirst");
            T.expect("removing the last remaining item should return 3", 3, () -> T.call(l, "removeFirst"));
            T.expectTrue("the list must be empty after removing every item", () -> T.call(l, "isEmpty"));
            T.expectNull("tail must be null when the list becomes empty", () -> T.field(l, "tail"));
            T.call(l, "addLast", 8);
            T.expect("addLast after the list became empty should give [8]", "[8]", () -> str(l));
            T.expectThrows("removeFirst() on an empty list should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make(L), "removeFirst"));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_removeLast", "removeLast", () -> {
            T.method(L, "removeLast", 0);
            Object l = listOf(1, 2, 3);
            T.expect("removeLast() on [1, 2, 3] should return 3", 3, () -> T.call(l, "removeLast"));
            T.expect("after removeLast the list should be [1, 2]", "[1, 2]", () -> str(l));
            T.call(l, "addLast", 4);
            T.expect("addLast after removeLast should give [1, 2, 4] (tail must be updated)", "[1, 2, 4]", () -> str(l));
            Object one = listOf(7);
            T.expect("removeLast() on a one-item list should return 7", 7, () -> T.call(one, "removeLast"));
            T.expectTrue("a one-item list must be empty after removeLast", () -> T.call(one, "isEmpty"));
            T.call(one, "addFirst", 9);
            T.expect("addFirst on the emptied list should give [9]", "[9]", () -> str(one));
            T.expect("getFirst() should be 9", 9, () -> T.call(one, "getFirst"));
            T.expectThrows("removeLast() on an empty list should throw NoSuchElementException", NSEE,
                    () -> T.call(T.make(L), "removeLast"));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_removeAtIndex", "remove(index)", () -> {
            T.method(L, "remove", 1);
            Object l = listOf(10, 20, 30, 40);
            T.expect("remove(1) on [10, 20, 30, 40] should return 20", 20, () -> T.call(l, "remove", 1));
            T.expect("after remove(1) the list should be [10, 30, 40]", "[10, 30, 40]", () -> str(l));
            T.expect("remove(0) should return 10", 10, () -> T.call(l, "remove", 0));
            T.expect("remove(size - 1) should return 40", 40, () -> T.call(l, "remove", 1));
            T.expect("the list should now be [30]", "[30]", () -> str(l));
            T.call(l, "addLast", 50);
            T.expect("addLast after removing the last index should give [30, 50]", "[30, 50]", () -> str(l));
            T.expect("size() should be 2", 2, () -> T.call(l, "size"));
            T.expectThrows("remove(2) on a list of size 2 should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(l, "remove", 2));
            T.expectThrows("remove(-1) should throw IndexOutOfBoundsException", IOOBE, () -> T.call(l, "remove", -1));
            T.expectThrows("remove(0) on an empty list should throw IndexOutOfBoundsException", IOOBE,
                    () -> T.call(T.make(L), "remove", 0));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_indexOf", "indexOf and contains", () -> {
            T.method(L, "indexOf", 1);
            Object l = listOf(5, 7, 5, 9);
            T.expect("indexOf(5) should be 0 (the first match)", 0, () -> T.call(l, "indexOf", 5));
            T.expect("indexOf(9) should be 3", 3, () -> T.call(l, "indexOf", 9));
            T.expect("indexOf(4) should be -1", -1, () -> T.call(l, "indexOf", 4));
            T.expect("indexOf(null) should be -1", -1, () -> T.call(l, "indexOf", (Object) null));
            T.expect("indexOf on an empty list should be -1", -1, () -> T.call(T.make(L), "indexOf", 1));
            T.expect("contains(7) should be true", true, () -> T.call(l, "contains", 7));
            T.expect("contains(8) should be false", false, () -> T.call(l, "contains", 8));
            Object s = listOf("ab", "cd");
            T.expect("indexOf must compare with equals: indexOf(new String(\"cd\")) should be 1", 1,
                    () -> T.call(s, "indexOf", new String("cd")));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_reverse", "reverse", () -> {
            T.method(L, "reverse", 0);
            Object l = listOf(1, 2, 3, 4);
            Object oldHead = T.field(l, "head");
            Object oldTail = T.field(l, "tail");
            T.call(l, "reverse");
            T.expect("reverse() on [1, 2, 3, 4] should give [4, 3, 2, 1]", "[4, 3, 2, 1]", () -> str(l));
            T.expect("size() must not change after reverse", 4, () -> T.call(l, "size"));
            T.expectSame("reverse must re-link the existing nodes: the new head is the old tail node", oldTail,
                    () -> T.field(l, "head"));
            T.expectSame("reverse must re-link the existing nodes: the new tail is the old head node", oldHead,
                    () -> T.field(l, "tail"));
            T.call(l, "addLast", 0);
            T.expect("addLast after reverse should give [4, 3, 2, 1, 0]", "[4, 3, 2, 1, 0]", () -> str(l));
            T.expect("removeLast after reverse should return 0", 0, () -> T.call(l, "removeLast"));
            T.expect("removeLast after reverse should then return 1", 1, () -> T.call(l, "removeLast"));
            Object e = T.make(L);
            T.expectNoThrow("reverse() on an empty list should not throw", () -> T.call(e, "reverse"));
            Object one = listOf(5);
            T.call(one, "reverse");
            T.expect("reverse() on a one-item list should leave [5]", "[5]", () -> str(one));
            T.call(one, "addLast", 6);
            T.expect("addLast after reversing a one-item list should give [5, 6]", "[5, 6]", () -> str(one));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_removeAll", "removeAll", () -> {
            T.method(L, "removeAll", 1);
            Object l = listOf(1, 2, 1, 3, 1);
            T.expect("removeAll(1) on [1, 2, 1, 3, 1] should return 3", 3, () -> T.call(l, "removeAll", 1));
            T.expect("after removeAll(1) the list should be [2, 3]", "[2, 3]", () -> str(l));
            T.expect("size() should be 2", 2, () -> T.call(l, "size"));
            T.call(l, "addLast", 4);
            T.expect("addLast after removeAll should give [2, 3, 4] (tail must be updated)", "[2, 3, 4]", () -> str(l));
            Object adj = listOf(5, 5, 5, 6, 7, 7);
            T.expect("removeAll(5) on [5, 5, 5, 6, 7, 7] should return 3", 3, () -> T.call(adj, "removeAll", 5));
            T.expect("removeAll(7) should remove the two items at the end and return 2", 2, () -> T.call(adj, "removeAll", 7));
            T.expect("the list should now be [6]", "[6]", () -> str(adj));
            T.call(adj, "addLast", 8);
            T.expect("addLast after removing at the end should give [6, 8]", "[6, 8]", () -> str(adj));
            Object all = listOf(2, 2, 2);
            T.expect("removeAll(2) on [2, 2, 2] should return 3", 3, () -> T.call(all, "removeAll", 2));
            T.expectTrue("the list must be empty when every item was removed", () -> T.call(all, "isEmpty"));
            T.call(all, "addLast", 1);
            T.expect("addLast on the emptied list should give [1]", "[1]", () -> str(all));
            T.expect("removeAll(9) with no match should return 0", 0, () -> T.call(listOf(1, 2), "removeAll", 9));
            T.expect("removeAll on an empty list should return 0", 0, () -> T.call(T.make(L), "removeAll", 1));
            T.expectThrows("removeAll(null) should throw IllegalArgumentException", IAE,
                    () -> T.call(listOf(1), "removeAll", (Object) null));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_toString", "toString", () -> {
            T.expect("toString() of a one-item list should be [5]", "[5]", () -> str(listOf(5)));
            T.expect("toString() lists items first to last: [1, 2, 3]", "[1, 2, 3]", () -> str(listOf(1, 2, 3)));
            T.expect("toString() of strings should be [x, y]", "[x, y]", () -> str(listOf("x", "y")));
        });

        T.test("SinglyLinkedList", "test_SinglyLinkedList_mixed", "mixed operations", () -> {
            Object l = T.make(L);
            for (int i = 1; i <= 5; i++) {
                T.call(l, "addLast", i);
            }
            T.call(l, "removeFirst");
            T.call(l, "addFirst", 9);
            T.call(l, "remove", 2);
            T.call(l, "add", 2, 7);
            T.call(l, "removeLast");
            T.call(l, "addLast", 6);
            T.expect("after a series of adds and removes the list should be [9, 2, 7, 4, 6]", "[9, 2, 7, 4, 6]", () -> str(l));
            T.expect("size() should be 5", 5, () -> T.call(l, "size"));
            for (int i = 0; i < 5; i++) {
                T.call(l, "removeFirst");
            }
            T.expectTrue("the list must be empty after removing all 5 items", () -> T.call(l, "isEmpty"));
            T.expect("size() should be 0", 0, () -> T.call(l, "size"));
        });

        T.test("mergeSorted", "test_mergeSorted_basic", "mergeSorted", () -> {
            T.expect("mergeSorted([1, 4, 9], [2, 3, 10]) should be [1, 2, 3, 4, 9, 10]", "[1, 2, 3, 4, 9, 10]",
                    () -> str(merge(listOf(1, 4, 9), listOf(2, 3, 10))));
            T.expect("mergeSorted([1, 2], [3, 4]) should be [1, 2, 3, 4]", "[1, 2, 3, 4]",
                    () -> str(merge(listOf(1, 2), listOf(3, 4))));
            T.expect("mergeSorted([3, 4], [1, 2]) should be [1, 2, 3, 4]", "[1, 2, 3, 4]",
                    () -> str(merge(listOf(3, 4), listOf(1, 2))));
            T.expect("mergeSorted([2, 2], [2]) should be [2, 2, 2]", "[2, 2, 2]", () -> str(merge(listOf(2, 2), listOf(2))));
            T.expect("mergeSorted([-5, 0], [-3, 7]) should be [-5, -3, 0, 7]", "[-5, -3, 0, 7]",
                    () -> str(merge(listOf(-5, 0), listOf(-3, 7))));
        });

        T.test("mergeSorted", "test_mergeSorted_empty", "mergeSorted with empty lists", () -> {
            T.expect("mergeSorted([], [5, 6]) should be [5, 6]", "[5, 6]", () -> str(merge(listOf(), listOf(5, 6))));
            T.expect("mergeSorted([5, 6], []) should be [5, 6]", "[5, 6]", () -> str(merge(listOf(5, 6), listOf())));
            T.expect("mergeSorted([], []) should be []", "[]", () -> str(merge(listOf(), listOf())));
            Object r = merge(listOf(1), listOf());
            T.call(r, "addLast", 2);
            T.expect("the merged list must be a working list: addLast(2) should give [1, 2]", "[1, 2]", () -> str(r));
        });

        T.test("mergeSorted", "test_mergeSorted_newList", "mergeSorted leaves inputs unchanged", () -> {
            Object a = listOf(1, 4);
            Object b = listOf(2, 3);
            Object r = merge(a, b);
            T.expect("the merged list should have size 4", 4, () -> T.call(r, "size"));
            T.expect("mergeSorted must not change its first argument", "[1, 4]", () -> str(a));
            T.expect("mergeSorted must not change its second argument", "[2, 3]", () -> str(b));
            T.expectTrue("mergeSorted must return a new list, not one of its arguments", () -> r != a && r != b);
            T.call(r, "removeFirst");
            T.expect("changing the result must not change the inputs", "[1, 4]", () -> str(a));
        });

        T.test("mergeSorted", "test_mergeSorted_null", "mergeSorted(null, ...)", () -> {
            T.expectThrows("mergeSorted(null, list) should throw IllegalArgumentException", IAE,
                    () -> merge(null, listOf(1)));
            T.expectThrows("mergeSorted(list, null) should throw IllegalArgumentException", IAE,
                    () -> merge(listOf(1), null));
        });

        T.test("(code rules)", "test_Lab04_rules", "Lab04 code rules", () -> {
            T.expectFalse("only Lab04 may be public", T.isPublic(L));
            T.expectFalse("Lab04.java must not use a java.util collection (ArrayList, LinkedList, List, Vector, Stack, Deque, ArrayDeque)",
                    T.uses(F, "ArrayList") || T.uses(F, "LinkedList") || T.uses(F, "List") || T.uses(F, "Vector")
                            || T.uses(F, "Stack") || T.uses(F, "Deque") || T.uses(F, "ArrayDeque"));
            T.expectFalse("SinglyLinkedList must not use an array (no new T[...] or Object[])",
                    T.matches(F, "new\\s+\\w+(<[^>]*>)?\\s*\\[") || T.matches(F, "\\bObject\\s*\\[\\s*\\]"));
            T.expectSilent("the required methods must not print", () -> {
                try {
                    Object l = listOf(3, 1, 2);
                    T.call(l, "add", 1, 5);
                    T.call(l, "get", 0);
                    T.call(l, "set", 0, 4);
                    T.call(l, "indexOf", 1);
                    T.call(l, "reverse");
                    T.call(l, "removeAll", 1);
                    T.call(l, "removeLast");
                    T.call(l, "removeFirst");
                    T.call(l, "toString");
                    merge(listOf(1, 3), listOf(2));
                } catch (Throwable ignored) {
                    // printing is what matters here
                }
            });
        });

        T.done();
    }
}
