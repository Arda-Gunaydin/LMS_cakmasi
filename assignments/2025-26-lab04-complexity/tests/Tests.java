import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Hidden tests for last year's Lab 4: Algorithm Analysis (school style: one test = one check). */
public class Tests {

    static final String C = "Lab04";
    static final int[] SAMPLE = {3, 1, 4, 1, 5, 9, 2, 6};

    /** Lower case, no spaces or '*', and without a wrapping O(...). */
    static String norm(String s) {
        String x = s.toLowerCase().replaceAll("[\\s*]", "");
        while (x.startsWith("o(") && x.endsWith(")")) {
            x = x.substring(2, x.length() - 1);
        }
        return x;
    }

    static String printed(String method) throws Throwable {
        return T.captureOut(() -> {
            if (method.startsWith("question")) {
                T.callStatic(C, method);
            } else {
                T.callStatic(C, method, (Object) SAMPLE.clone());
            }
        });
    }

    /** The answer after "label: O(" in the printed text, or null when there is no such line. */
    static String answer(String out, String label) {
        Matcher m = Pattern.compile(Pattern.quote(label) + "[^\\n]*?: O\\((.*)\\)\\s*$", Pattern.MULTILINE).matcher(out);
        return m.find() ? m.group(1) : null;
    }

    static void method(String name, String correct) throws Throwable {
        T.method(C, name, 1);
        String out = printed(name);
        String a = answer(out, "Your answer");
        T.expectTrue(name + " must print one line \"Your answer: O(...)\"", a != null);
        if (a != null) {
            T.expectTrue(name + ": the answer O(" + a + ") is not the tightest bound", norm(a).equals(correct));
        }
    }

    static void question(String name, String array, String sll) throws Throwable {
        T.method(C, name, 0);
        String out = printed(name);
        String a = answer(out, "Your answer for Array");
        String s = answer(out, "Your answer for SLL");
        T.expectTrue(name + " must print the Array and the SLL answer lines", a != null && s != null);
        if (a != null) {
            T.expectTrue(name + ": the Array answer O(" + a + ") is not correct", norm(a).equals(array));
        }
        if (s != null) {
            T.expectTrue(name + ": the SLL answer O(" + s + ") is not correct", norm(s).equals(sll));
        }
    }

    // Reference versions of the given computations (the student must not change them).
    static long cube(int[] a) {
        long s = 0;
        int n = a.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    s += a[(i + j + k) % n];
                }
            }
        }
        return s;
    }

    static long halving(int[] a) {
        long s = 0;
        int n = a.length;
        int m = Math.max(1, n);
        while (m > 1) {
            s += a[m % n];
            m = m / 2;
        }
        return s;
    }

    static long xor(int[] a, int idx, long acc) {
        if (idx == a.length) {
            return acc;
        }
        return xor(a, idx + 1, acc + a[idx]) ^ xor(a, idx + 1, acc - a[idx]);
    }

    static long triangle(int[] a) {
        long s = 0;
        int n = a.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                s += a[(i + j) % n];
            }
        }
        return s;
    }

    static long few(int[] a) {
        long s = 0;
        int n = a.length;
        if (n > 0) {
            s += a[0];
        }
        if (n > 1) {
            s += a[n - 1];
        }
        if (n > 2) {
            s += a[n / 2];
        }
        return s;
    }

    static long phased(int[] a) {
        long s = 0;
        int n = a.length;
        for (int stride = 1; stride <= Math.max(1, n); stride = stride * 2) {
            for (int i = 0; i < n; i++) {
                s += a[i];
            }
        }
        return s;
    }

    static long scan(int[] a) {
        long s = 0;
        for (int x : a) {
            s += x;
        }
        return s;
    }

    public static void main(String[] args) {

        T.test("cubeWalk", "test_cubeWalk_answer", "cubeWalk answer", () -> method("cubeWalk", "n^3"));
        T.test("halvingProbe", "test_halvingProbe_answer", "halvingProbe answer", () -> method("halvingProbe", "logn"));
        T.test("subsetXor", "test_subsetXor_answer", "subsetXor answer", () -> method("subsetXor", "2^n"));
        T.test("triangularPairs", "test_triangularPairs_answer", "triangularPairs answer", () -> method("triangularPairs", "n^2"));
        T.test("touchFew", "test_touchFew_answer", "touchFew answer", () -> method("touchFew", "1"));
        T.test("phasedSweeps", "test_phasedSweeps_answer", "phasedSweeps answer", () -> method("phasedSweeps", "nlogn"));
        T.test("singleScan", "test_singleScan_answer", "singleScan answer", () -> method("singleScan", "n"));

        T.test("question1", "test_question1_answer", "Q1 access by index", () -> question("question1", "1", "n"));
        T.test("question2", "test_question2_answer", "Q2 search in an unsorted structure", () -> question("question2", "n", "n"));
        T.test("question3", "test_question3_answer", "Q3 insert at the end", () -> question("question3", "1", "n"));
        T.test("question4", "test_question4_answer", "Q4 delete the first element", () -> question("question4", "n", "1"));
        T.test("question5", "test_question5_answer", "Q5 concatenate (SLL with tail)", () -> question("question5", "n+m", "1"));

        T.test("(code rules)", "test_Lab04_unchanged", "the given computations were not changed", () -> {
            int[][] inputs = {SAMPLE, {7, -2, 5, 11, 0}, {4}};
            for (int[] in : inputs) {
                String arr = java.util.Arrays.toString(in);
                T.expect("cubeWalk(" + arr + ") must still return its original result", cube(in),
                        () -> T.callStatic(C, "cubeWalk", (Object) in.clone()));
                T.expect("halvingProbe(" + arr + ") must still return its original result", halving(in),
                        () -> T.callStatic(C, "halvingProbe", (Object) in.clone()));
                T.expect("subsetXor(" + arr + ") must still return its original result", xor(in, 0, 0L),
                        () -> T.callStatic(C, "subsetXor", (Object) in.clone()));
                T.expect("triangularPairs(" + arr + ") must still return its original result", triangle(in),
                        () -> T.callStatic(C, "triangularPairs", (Object) in.clone()));
                T.expect("touchFew(" + arr + ") must still return its original result", few(in),
                        () -> T.callStatic(C, "touchFew", (Object) in.clone()));
                T.expect("phasedSweeps(" + arr + ") must still return its original result", phased(in),
                        () -> T.callStatic(C, "phasedSweeps", (Object) in.clone()));
                T.expect("singleScan(" + arr + ") must still return its original result", scan(in),
                        () -> T.callStatic(C, "singleScan", (Object) in.clone()));
            }
            T.expectTrue("every method must print exactly one answer line", () -> {
                for (String m : new String[] {"cubeWalk", "halvingProbe", "subsetXor", "triangularPairs", "touchFew",
                        "phasedSweeps", "singleScan"}) {
                    if (printed(m).trim().split("\n").length != 1) {
                        return false;
                    }
                }
                return true;
            });
        });

        T.done();
    }
}
