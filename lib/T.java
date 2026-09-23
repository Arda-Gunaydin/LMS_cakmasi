import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * VPL-Lab test helper. It imitates the school's JUnit based evaluation:
 * every T.test(...) is one "check" (one JUnit test method). Inside a test the
 * expect... calls are soft assertions (like assertAll): all of them run, and
 * every failing one is reported. Tests run in JUnit 4's default order.
 *
 *   T.test("Catalog", "test_Catalog_add", "add", () -> {
 *       Object cat = T.make("Catalog");
 *       Object b = T.make("Book", "B1", "Title", "Author");
 *       T.expect("add(book) should return true", true, () -> T.call(cat, "add", b));
 *       T.expect("size() should be 1 after one add", 1, () -> T.call(cat, "size"));
 *   });
 *   T.done();
 *
 * A lookup that fails outside an expect (for example T.call(cat, "add", b) at
 * the top of the test) fails the whole test with that single message, the way
 * "No method named like add was found in Catalog" does at school.
 */
public final class T {

    private T() {
    }

    /** Code that may throw anything. */
    public interface Body {
        void run() throws Throwable;
    }

    /** Code that produces a value and may throw anything. */
    public interface Value {
        Object get() throws Throwable;
    }

    /** A hard assertion failure (fails the whole test). */
    public static final class Fail extends RuntimeException {
        private static final long serialVersionUID = 1L;
        final boolean replace;

        Fail(String message, boolean replace) {
            super(message, null, false, false);
            this.replace = replace;
        }
    }

    private static final PrintStream OUT = System.out;
    private static final InputStream IN = System.in;
    private static final PrintStream SILENT = new PrintStream(OutputStream.nullOutputStream());
    private static final long TIMEOUT_MS = Long.getLong("vpl.checkTimeout", 3000L);

    static {
        System.setOut(SILENT);
        System.setErr(SILENT);
    }

    // ================================================================== tests

    private static final class Case {
        String group;
        String method;
        String title;
        Body body;
    }

    /** One recorded soft failure: exception class name, message, and student stack frames. */
    private static final class Failure {
        String type;
        String message;
        String cause = "";

        Failure(String type, String message) {
            this.type = type;
            this.message = message;
        }
    }

    private static final List<Case> CASES = new ArrayList<Case>();
    private static final ThreadLocal<List<Failure>> SOFT = new ThreadLocal<List<Failure>>();

    /** Registers a test. It runs when done() is called. */
    public static void test(String group, String method, String title, Body body) {
        Case c = new Case();
        c.group = group;
        c.method = method;
        c.title = title;
        c.body = body;
        CASES.add(c);
    }

    /** Runs every registered test in JUnit 4 default order and reports. Must be last. */
    public static void done() {
        List<Case> order = new ArrayList<Case>(CASES);
        order.sort(Comparator.comparingInt((Case c) -> c.method.hashCode()).thenComparing(c -> c.method));
        long start = System.currentTimeMillis();
        for (Case c : order) {
            runCase(c);
        }
        OUT.println("@@VPL@@TIME\t" + (System.currentTimeMillis() - start));
        OUT.println("@@VPL@@END");
        OUT.flush();
        Runtime.getRuntime().halt(0);
    }

    private static void runCase(Case c) {
        final List<Failure> soft = new ArrayList<Failure>();
        final Throwable[] error = new Throwable[1];
        Thread worker = new Thread(() -> {
            SOFT.set(soft);
            try {
                c.body.run();
            } catch (Throwable t) {
                error[0] = t;
            }
        }, "vpl-" + c.method);
        worker.setDaemon(true);
        worker.start();
        try {
            worker.join(TIMEOUT_MS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        String kind;
        List<Failure> fails;
        if (worker.isAlive()) {
            System.setOut(SILENT);
            System.setIn(IN);
            kind = "hard";
            fails = new ArrayList<Failure>();
            fails.add(new Failure("org.junit.runners.model.TestTimedOutException",
                    c.title + ", but it timed out after " + TIMEOUT_MS + " milliseconds"));
        } else if (error[0] != null) {
            Throwable t = unwrap(error[0]);
            kind = "hard";
            fails = new ArrayList<Failure>();
            if (t instanceof Fail) {
                Fail f = (Fail) t;
                String m = f.getMessage() == null ? "" : f.getMessage();
                fails.add(new Failure("java.lang.AssertionError", f.replace ? m : (c.title + " " + m).trim()));
            } else {
                Failure f = new Failure("java.lang.AssertionError", c.title + ", but it threw " + t.getClass().getName());
                f.cause = causeOf(t);
                fails.add(f);
            }
        } else if (!soft.isEmpty()) {
            kind = "multi";
            fails = soft;
        } else {
            kind = "ok";
            fails = soft;
        }
        OUT.println("@@VPL@@T\t" + clean(c.group) + "\t" + clean(c.method) + "\t" + (kind.equals("ok") ? 1 : 0)
                + "\t" + clean(c.title) + "\t" + kind);
        for (Failure f : fails) {
            OUT.println("@@VPL@@F\t" + clean(f.type) + "\t" + clean(f.message) + "\t" + clean(f.cause));
        }
        OUT.flush();
    }

    private static String clean(String s) {
        return s == null ? "" : s.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
    }

    private static List<Failure> soft() {
        List<Failure> l = SOFT.get();
        if (l == null) {
            l = new ArrayList<Failure>();
            SOFT.set(l);
        }
        return l;
    }

    /** Student stack frames of an exception, joined with \u0001. */
    private static String causeOf(Throwable t) {
        Set<String> student = studentFiles();
        StringBuilder sb = new StringBuilder("Caused by: " + t);
        int n = 0;
        for (StackTraceElement e : t.getStackTrace()) {
            if (e.getFileName() != null && student.contains(e.getFileName())) {
                sb.append('\u0001').append(e.toString());
                if (++n == 4) {
                    break;
                }
            }
        }
        return sb.toString();
    }

    private static Set<String> studentFiles() {
        Set<String> s = new HashSet<String>();
        String list = System.getProperty("vpl.studentFiles", "");
        for (String f : list.split(",")) {
            if (!f.isEmpty()) {
                s.add(f);
            }
        }
        return s;
    }

    // ================================================================== soft assertions

    private static void record(Failure f) {
        soft().add(f);
    }

    /** Evaluates the value; records a failure when it throws. Returns NOTHING on failure. */
    private static final Object NOTHING = new Object();

    private static Object eval(String desc, Value v) {
        try {
            return v.get();
        } catch (Throwable x) {
            Throwable t = unwrap(x);
            if (t instanceof Fail) {
                Fail f = (Fail) t;
                record(new Failure("java.lang.AssertionError", f.replace ? f.getMessage() : desc + " " + f.getMessage()));
            } else {
                Failure f = new Failure("java.lang.AssertionError", desc + ", but it threw " + t.getClass().getName());
                f.cause = causeOf(t);
                record(f);
            }
            return NOTHING;
        }
    }

    /** desc expected:&lt;expected&gt; but was:&lt;actual&gt; (JUnit assertEquals). */
    public static void expect(String desc, Object expected, Value actual) {
        Object a = eval(desc, actual);
        if (a == NOTHING) {
            return;
        }
        if (!same0(expected, a)) {
            if (expected instanceof String && a instanceof String) {
                record(new Failure("org.junit.ComparisonFailure",
                        desc + " " + compact((String) expected, (String) a)));
            } else {
                record(new Failure("java.lang.AssertionError",
                        desc + " expected:<" + text(expected) + "> but was:<" + text(a) + ">"));
            }
        }
    }

    /** Like expect for doubles, with a tolerance. */
    public static void expectNear(String desc, double expected, Value actual, double delta) {
        Object a = eval(desc, actual);
        if (a == NOTHING) {
            return;
        }
        if (!(a instanceof Number) || Math.abs(((Number) a).doubleValue() - expected) > delta) {
            record(new Failure("java.lang.AssertionError",
                    desc + " expected:<" + expected + "> but was:<" + text(a) + ">"));
        }
    }

    /** The value must be exactly this object (==). */
    public static void expectSame(String desc, Object expected, Value actual) {
        Object a = eval(desc, actual);
        if (a != NOTHING && a != expected) {
            record(new Failure("java.lang.AssertionError",
                    desc + " expected same:<" + text(expected) + "> was not:<" + text(a) + ">"));
        }
    }

    public static void expectNull(String desc, Value actual) {
        Object a = eval(desc, actual);
        if (a != NOTHING && a != null) {
            record(new Failure("java.lang.AssertionError", desc + " expected null, but was:<" + text(a) + ">"));
        }
    }

    public static void expectTrue(String desc, boolean condition) {
        if (!condition) {
            record(new Failure("java.lang.AssertionError", desc));
        }
    }

    public static void expectTrue(String desc, Value condition) {
        Object a = eval(desc, condition);
        if (a != NOTHING && !Boolean.TRUE.equals(a)) {
            record(new Failure("java.lang.AssertionError", desc));
        }
    }

    public static void expectFalse(String desc, boolean condition) {
        expectTrue(desc, !condition);
    }

    /** The body must throw the type (or a subclass). */
    public static void expectThrows(String desc, Class<? extends Throwable> type, Body body) {
        try {
            body.run();
        } catch (Throwable x) {
            Throwable t = unwrap(x);
            if (t instanceof Fail) {
                Fail f = (Fail) t;
                record(new Failure("java.lang.AssertionError", f.replace ? f.getMessage() : desc + " " + f.getMessage()));
                return;
            }
            if (!type.isInstance(t)) {
                Failure f = new Failure("java.lang.AssertionError", desc + ", but it threw " + t.getClass().getName());
                f.cause = causeOf(t);
                record(f);
            }
            return;
        }
        record(new Failure("java.lang.AssertionError", desc + ", but it returned without throwing"));
    }

    /** The body must finish without throwing. */
    public static void expectNoThrow(String desc, Body body) {
        eval(desc, () -> {
            body.run();
            return null;
        });
    }

    /** Runs the body; nothing may be printed to System.out or System.err. */
    public static void expectSilent(String desc, Body body) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos, true);
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        System.setOut(ps);
        System.setErr(ps);
        try {
            body.run();
        } catch (Throwable ignored) {
            // only printing matters here
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }
        String printed = new String(bos.toByteArray(), StandardCharsets.UTF_8);
        if (!printed.isEmpty()) {
            String shown = printed.trim().replace("\n", "\\n");
            if (shown.length() > 60) {
                shown = shown.substring(0, 57) + "...";
            }
            record(new Failure("java.lang.AssertionError", desc + ", but it printed \"" + shown + "\""));
        }
    }

    /** JUnit 4 ComparisonCompactor output: expected:<[New Title]> but was:<[   ]>. */
    static String compact(String expected, String actual) {
        final int ctx = 20;
        int prefix = 0;
        int end = Math.min(expected.length(), actual.length());
        while (prefix < end && expected.charAt(prefix) == actual.charAt(prefix)) {
            prefix++;
        }
        int eEnd = expected.length() - 1;
        int aEnd = actual.length() - 1;
        while (aEnd >= prefix && eEnd >= prefix && expected.charAt(eEnd) == actual.charAt(aEnd)) {
            aEnd--;
            eEnd--;
        }
        int suffix = expected.length() - eEnd - 1;
        String pre = prefix > 0
                ? (prefix > ctx ? "..." : "") + expected.substring(Math.max(0, prefix - ctx), prefix)
                : "";
        String post = "";
        if (suffix > 0) {
            int s = expected.length() - suffix;
            int e = Math.min(s + ctx, expected.length());
            post = expected.substring(s, e) + (expected.length() - suffix < expected.length() - ctx ? "..." : "");
        }
        String ex = pre + "[" + expected.substring(prefix, expected.length() - suffix) + "]" + post;
        String ac = pre + "[" + actual.substring(prefix, actual.length() - suffix) + "]" + post;
        return "expected:<" + ex + "> but was:<" + ac + ">";
    }

    /** Value text used in expected:<...> messages. */
    static String text(Object o) {
        try {
            if (o == null) {
                return "null";
            }
            if (o.getClass().isArray() || o instanceof Collection) {
                List<Object> l = o instanceof Collection ? new ArrayList<Object>((Collection<?>) o) : asList(o);
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < l.size(); i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(text(l.get(i)));
                }
                return sb.append(']').toString();
            }
            return String.valueOf(o);
        } catch (Throwable t) {
            return o.getClass().getSimpleName();
        }
    }

    // ================================================================== hard assertions

    public static Fail fail(String detail) {
        throw new Fail(detail == null || detail.isEmpty() ? "" : "[" + detail + "]", false);
    }

    public static Fail failWith(String message) {
        throw new Fail(message, true);
    }

    public static void isTrue(boolean condition) {
        if (!condition) {
            fail("");
        }
    }

    public static void isTrue(boolean condition, String detail) {
        if (!condition) {
            fail(detail);
        }
    }

    public static void isFalse(boolean condition) {
        isTrue(!condition);
    }

    public static void isFalse(boolean condition, String detail) {
        isTrue(!condition, detail);
    }

    public static void eq(Object expected, Object actual) {
        if (!same0(expected, actual)) {
            fail("expected " + text(expected) + ", got " + text(actual));
        }
    }

    static boolean same0(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof Number && b instanceof Number) {
            if (isFloating(a) || isFloating(b)) {
                return Math.abs(((Number) a).doubleValue() - ((Number) b).doubleValue()) < 1e-9;
            }
            return ((Number) a).longValue() == ((Number) b).longValue();
        }
        if (a instanceof Character && b instanceof String) {
            return a.toString().equals(b);
        }
        if (a instanceof String && b instanceof Character) {
            return a.equals(b.toString());
        }
        List<Object> la = asList(a);
        List<Object> lb = asList(b);
        if (la != null && lb != null) {
            if (la.size() != lb.size()) {
                return false;
            }
            for (int i = 0; i < la.size(); i++) {
                if (!same0(la.get(i), lb.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return a.equals(b);
    }

    private static boolean isFloating(Object o) {
        return o instanceof Double || o instanceof Float;
    }

    private static List<Object> asList(Object o) {
        if (o instanceof List) {
            return new ArrayList<Object>((List<?>) o);
        }
        if (o != null && o.getClass().isArray()) {
            List<Object> l = new ArrayList<Object>();
            for (int i = 0; i < Array.getLength(o); i++) {
                l.add(Array.get(o, i));
            }
            return l;
        }
        return null;
    }

    // ================================================================== reflection

    public static Class<?> cls(String name) {
        try {
            return Class.forName(name, true, T.class.getClassLoader());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            throw new Fail("No class named " + name + " was found", true);
        }
    }

    public static boolean hasClass(String name) {
        try {
            Class.forName(name, false, T.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }

    /** new ClassName(args...). */
    public static Object make(String className, Object... args) {
        Class<?> c = cls(className);
        Object[] a = args == null ? new Object[] { null } : args;
        Constructor<?> best = null;
        int bestCost = Integer.MAX_VALUE;
        for (Constructor<?> k : c.getDeclaredConstructors()) {
            int cost = cost(k, a);
            if (cost >= 0 && cost < bestCost) {
                best = k;
                bestCost = cost;
            }
        }
        if (best == null) {
            throw new Fail("No constructor " + className + "(" + types(a) + ") was found", true);
        }
        if (Modifier.isAbstract(c.getModifiers())) {
            throw new Fail(className + " is abstract and cannot be created directly", true);
        }
        try {
            best.setAccessible(true);
        } catch (RuntimeException ignored) {
            // best effort
        }
        try {
            return best.newInstance(convertAll(best, a));
        } catch (InvocationTargetException e) {
            throw sneaky(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw sneaky(e);
        }
    }

    /** target.method(args...). The name may be matched loosely ("named like"). */
    public static Object call(Object target, String method, Object... args) {
        if (target == null) {
            throw new Fail("[called " + method + " on null]", false);
        }
        return invoke(target.getClass(), target, method, args == null ? new Object[] { null } : args, false);
    }

    /** ClassName.method(args...). */
    public static Object callStatic(String className, String method, Object... args) {
        return invoke(cls(className), null, method, args == null ? new Object[] { null } : args, true);
    }

    private static Object invoke(Class<?> c, Object target, String name, Object[] a, boolean wantStatic) {
        List<Method> all = allMethods(c);
        String owner = nameOf(c);
        List<Method> named = named(all, name, a.length);
        if (named.isEmpty()) {
            throw new Fail("No method named like " + name + " was found in " + owner, true);
        }
        Method best = null;
        int bestCost = Integer.MAX_VALUE;
        for (Method m : named) {
            if (wantStatic && !Modifier.isStatic(m.getModifiers())) {
                continue;
            }
            int cost = cost(m, a);
            if (cost >= 0 && cost < bestCost) {
                best = m;
                bestCost = cost;
            }
        }
        if (best == null) {
            throw new Fail("No method " + name + "(" + types(a) + ") was found in " + owner, true);
        }
        try {
            best.setAccessible(true);
        } catch (RuntimeException ignored) {
            // best effort
        }
        try {
            return best.invoke(Modifier.isStatic(best.getModifiers()) ? null : target, convertAll(best, a));
        } catch (InvocationTargetException e) {
            throw sneaky(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw sneaky(e);
        }
    }

    /** Methods whose name is the wanted one, or "like" it: same ignoring case, or a small typo. */
    private static List<Method> named(List<Method> all, String name, int arity) {
        List<Method> exact = new ArrayList<Method>();
        for (Method m : all) {
            if (m.getName().equals(name)) {
                exact.add(m);
            }
        }
        if (!exact.isEmpty()) {
            return exact;
        }
        int limit = likeLimit(name);
        int bestDist = Integer.MAX_VALUE;
        List<Method> best = new ArrayList<Method>();
        for (Method m : all) {
            if (m.isSynthetic() || (arity >= 0 && m.getParameterCount() != arity)) {
                continue;
            }
            int d = m.getName().equalsIgnoreCase(name) ? 0 : distance(m.getName().toLowerCase(), name.toLowerCase());
            if (d <= limit && d < bestDist) {
                bestDist = d;
                best.clear();
            }
            if (d <= limit && d == bestDist) {
                best.add(m);
            }
        }
        return best;
    }

    private static int likeLimit(String name) {
        return name.length() <= 4 ? 0 : name.length() <= 7 ? 1 : 2;
    }

    static int distance(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] cur = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            cur[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int sub = prev[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
                cur[j] = Math.min(sub, Math.min(prev[j] + 1, cur[j - 1] + 1));
            }
            int[] t = prev;
            prev = cur;
            cur = t;
        }
        return prev[b.length()];
    }

    private static String nameOf(Class<?> c) {
        Class<?> k = c;
        while (k != null && (k.isAnonymousClass() || k.isSynthetic() || k.getSimpleName().isEmpty()
                || k.getName().contains("$$Lambda"))) {
            k = k.getSuperclass();
        }
        return k == null ? c.getName() : k.getSimpleName();
    }

    private static List<Method> allMethods(Class<?> c) {
        List<Method> out = new ArrayList<Method>();
        for (Class<?> k = c; k != null; k = k.getSuperclass()) {
            for (Class<?> i : k.getInterfaces()) {
                out.addAll(Arrays.asList(i.getMethods()));
            }
        }
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            out.addAll(Arrays.asList(k.getDeclaredMethods()));
        }
        out.addAll(Arrays.asList(c.getMethods()));
        if (c.isInterface()) {
            out.addAll(Arrays.asList(c.getDeclaredMethods()));
        }
        return out;
    }

    /** The method "named like" name with the given parameter count (-1: any), or a Fail. */
    public static Method method(String className, String name, int paramCount) {
        Class<?> c = cls(className);
        List<Method> l = named(allMethods(c), name, paramCount);
        if (l.isEmpty()) {
            throw new Fail("No method named like " + name + " was found in " + className, true);
        }
        return l.get(0);
    }

    public static boolean hasMethod(String className, String name) {
        return !named(allMethods(cls(className)), name, -1).isEmpty();
    }

    /** The method declared in exactly this class (not inherited), "named like". */
    public static Method declared(String className, String name, int paramCount) {
        Class<?> c = cls(className);
        List<Method> l = named(Arrays.asList(c.getDeclaredMethods()), name, paramCount);
        if (l.isEmpty()) {
            throw new Fail("No method named like " + name + " was found in " + className, true);
        }
        return l.get(0);
    }

    /** The field "named like" name, declared in the class or a superclass, or a Fail. */
    public static Field fieldOf(String className, String name) {
        Class<?> c = cls(className);
        Field f = findField(c, name);
        if (f == null) {
            throw new Fail("No field named like " + name + " was found in " + className, true);
        }
        return f;
    }

    public static Field findField(Class<?> c, String name) {
        for (Class<?> k = c; k != null; k = k.getSuperclass()) {
            for (Field f : k.getDeclaredFields()) {
                if (f.getName().equals(name)) {
                    return f;
                }
            }
        }
        int limit = likeLimit(name);
        for (Class<?> k = c; k != null; k = k.getSuperclass()) {
            for (Field f : k.getDeclaredFields()) {
                if (!f.isSynthetic() && distance(f.getName().toLowerCase(), name.toLowerCase()) <= limit) {
                    return f;
                }
            }
        }
        return null;
    }

    /** Reads a field of any visibility. */
    public static Object field(Object target, String name) {
        Field f = findField(target.getClass(), name);
        if (f == null) {
            throw new Fail("No field named like " + name + " was found in " + nameOf(target.getClass()), true);
        }
        try {
            f.setAccessible(true);
            return f.get(target);
        } catch (ReflectiveOperationException e) {
            throw sneaky(e);
        }
    }

    public static boolean isPrivate(Field f) {
        return Modifier.isPrivate(f.getModifiers());
    }

    public static boolean isFinal(Field f) {
        return Modifier.isFinal(f.getModifiers());
    }

    public static boolean isAbstract(Method m) {
        return Modifier.isAbstract(m.getModifiers());
    }

    public static boolean isAbstract(String className) {
        Class<?> c = cls(className);
        return Modifier.isAbstract(c.getModifiers()) && !c.isInterface();
    }

    public static boolean isInterface(String className) {
        return cls(className).isInterface();
    }

    public static boolean isPublic(String className) {
        return Modifier.isPublic(cls(className).getModifiers());
    }

    /** className implements or extends other (directly or indirectly). */
    public static boolean isA(String className, String other) {
        return cls(other).isAssignableFrom(cls(className));
    }

    /** className directly extends other. */
    public static boolean extendsDirectly(String className, String other) {
        return cls(className).getSuperclass() == cls(other);
    }

    public static int typeParameterCount(String className) {
        return cls(className).getTypeParameters().length;
    }

    /** True when the class's single type parameter is bounded by the given class. */
    public static boolean typeParameterBoundedBy(String className, String bound) {
        TypeVariable<?>[] tv = cls(className).getTypeParameters();
        if (tv.length != 1) {
            return false;
        }
        return Arrays.asList(tv[0].getBounds()).contains(cls(bound));
    }

    /** True when every declared (non-synthetic) field is private. */
    public static boolean fieldsPrivate(String className) {
        for (Field f : cls(className).getDeclaredFields()) {
            if (!f.isSynthetic() && !Modifier.isPrivate(f.getModifiers())) {
                return false;
            }
        }
        return true;
    }

    public static boolean fieldFinal(String className, String field) {
        return Modifier.isFinal(fieldOf(className, field).getModifiers());
    }

    /** Builds a typed array, e.g. T.array("LibraryItem", book, dvd). */
    public static Object array(String elementClass, Object... items) {
        Class<?> c = primitive(elementClass);
        if (c == null) {
            c = cls(elementClass);
        }
        Object arr = Array.newInstance(c, items.length);
        for (int i = 0; i < items.length; i++) {
            Array.set(arr, i, c.isPrimitive() ? convert(items[i], c) : items[i]);
        }
        return arr;
    }

    private static Class<?> primitive(String n) {
        switch (n) {
            case "int": return int.class;
            case "long": return long.class;
            case "double": return double.class;
            case "boolean": return boolean.class;
            case "char": return char.class;
            case "String": return String.class;
            case "Object": return Object.class;
            case "Integer": return Integer.class;
            default: return null;
        }
    }

    /** Converts an Iterable / array into a List. */
    public static List<Object> list(Object o) {
        if (o instanceof Iterable) {
            List<Object> l = new ArrayList<Object>();
            Iterator<?> it = ((Iterable<?>) o).iterator();
            while (it.hasNext()) {
                l.add(it.next());
            }
            return l;
        }
        List<Object> l = asList(o);
        if (l == null) {
            fail("expected a list, got " + text(o));
        }
        return l;
    }

    // ================================================================== argument matching

    private static int cost(Executable e, Object[] a) {
        Class<?>[] p = e.getParameterTypes();
        if (p.length != a.length) {
            return -1;
        }
        int total = 0;
        for (int i = 0; i < p.length; i++) {
            int c = cost(a[i], p[i]);
            if (c < 0) {
                return -1;
            }
            total += c;
        }
        return total;
    }

    private static final List<Class<?>> NUMS = Arrays.<Class<?>>asList(
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class);

    private static int cost(Object arg, Class<?> param) {
        if (arg == null) {
            return param.isPrimitive() ? -1 : 1;
        }
        Class<?> w = wrap(param);
        if (w == arg.getClass()) {
            return 0;
        }
        if (w.isInstance(arg)) {
            return 1;
        }
        int from = NUMS.indexOf(arg.getClass());
        int to = NUMS.indexOf(w);
        if (from >= 0 && to >= 0) {
            return to > from ? 2 + (to - from) : 20 + (from - to);
        }
        if (arg instanceof Character && to >= 2) {
            return 10;
        }
        if (arg instanceof String && ((String) arg).length() == 1 && w == Character.class) {
            return 15;
        }
        return -1;
    }

    private static Object[] convertAll(Executable e, Object[] a) {
        Class<?>[] p = e.getParameterTypes();
        Object[] out = new Object[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = convert(a[i], p[i]);
        }
        return out;
    }

    private static Object convert(Object arg, Class<?> param) {
        if (arg == null) {
            return null;
        }
        Class<?> w = wrap(param);
        if (w.isInstance(arg)) {
            return arg;
        }
        if (arg instanceof String && w == Character.class) {
            return ((String) arg).charAt(0);
        }
        Number n = arg instanceof Character ? Integer.valueOf((Character) arg) : (Number) arg;
        if (w == Integer.class) {
            return n.intValue();
        }
        if (w == Long.class) {
            return n.longValue();
        }
        if (w == Double.class) {
            return n.doubleValue();
        }
        if (w == Float.class) {
            return n.floatValue();
        }
        if (w == Short.class) {
            return n.shortValue();
        }
        if (w == Byte.class) {
            return n.byteValue();
        }
        return arg;
    }

    private static Class<?> wrap(Class<?> c) {
        if (!c.isPrimitive()) {
            return c;
        }
        if (c == int.class) {
            return Integer.class;
        }
        if (c == long.class) {
            return Long.class;
        }
        if (c == double.class) {
            return Double.class;
        }
        if (c == boolean.class) {
            return Boolean.class;
        }
        if (c == char.class) {
            return Character.class;
        }
        if (c == float.class) {
            return Float.class;
        }
        if (c == short.class) {
            return Short.class;
        }
        if (c == byte.class) {
            return Byte.class;
        }
        return Void.class;
    }

    private static String types(Object[] a) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(a[i] == null ? "null" : nameOf(a[i].getClass()));
        }
        return sb.toString();
    }

    // ================================================================== output / input

    /** Runs the body and returns everything it printed to System.out. */
    public static String captureOut(Body body) throws Throwable {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(bos, true, "UTF-8"));
        try {
            body.run();
        } finally {
            System.out.flush();
            System.setOut(old);
        }
        return bos.toString("UTF-8");
    }

    /** Calls ClassName.main(args) with the given standard input; returns its output. */
    public static String runMain(String className, String stdin, String... args) throws Throwable {
        final Class<?> c = cls(className);
        System.setIn(new ByteArrayInputStream((stdin == null ? "" : stdin).getBytes(StandardCharsets.UTF_8)));
        try {
            return captureOut(() -> {
                Method m;
                try {
                    m = c.getDeclaredMethod("main", String[].class);
                } catch (NoSuchMethodException e) {
                    throw new Fail("No main method was found in " + className, true);
                }
                m.setAccessible(true);
                try {
                    m.invoke(null, (Object) args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            });
        } finally {
            System.setIn(IN);
        }
    }

    /** Trims each line and drops trailing blank lines. */
    public static String norm(String s) {
        String[] lines = s.replace("\r\n", "\n").split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (String l : lines) {
            sb.append(l.replaceAll("\\s+$", "")).append('\n');
        }
        return sb.toString().replaceAll("\\n+$", "");
    }

    // ================================================================== source rules

    /** The student's source file, exactly as written. */
    public static String source(String fileName) {
        Path p = Paths.get(System.getProperty("vpl.src", "."), fileName);
        try {
            return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new Fail("No file named " + fileName + " was found", true);
        }
    }

    /** The source with comments, strings and char literals removed. */
    public static String code(String fileName) {
        String s = source(fileName);
        StringBuilder out = new StringBuilder();
        int i = 0;
        int n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (c == '/' && i + 1 < n && s.charAt(i + 1) == '/') {
                while (i < n && s.charAt(i) != '\n') {
                    i++;
                }
            } else if (c == '/' && i + 1 < n && s.charAt(i + 1) == '*') {
                i += 2;
                while (i + 1 < n && !(s.charAt(i) == '*' && s.charAt(i + 1) == '/')) {
                    i++;
                }
                i += 2;
                out.append(' ');
            } else if (c == '"' || c == '\'') {
                char q = c;
                i++;
                while (i < n && s.charAt(i) != q && s.charAt(i) != '\n') {
                    if (s.charAt(i) == '\\') {
                        i++;
                    }
                    i++;
                }
                i++;
                out.append(q).append(q);
            } else {
                out.append(c);
                i++;
            }
        }
        return out.toString();
    }

    /** True when the word appears in the code (comments and strings ignored). */
    public static boolean uses(String fileName, String word) {
        return Pattern.compile("\\b" + Pattern.quote(word) + "\\b").matcher(code(fileName)).find();
    }

    /** True when the regex matches the code (comments and strings ignored). */
    public static boolean matches(String fileName, String regex) {
        return Pattern.compile(regex).matcher(code(fileName)).find();
    }

    // ================================================================== misc

    static Throwable unwrap(Throwable t) {
        Throwable x = t;
        while ((x instanceof InvocationTargetException || x instanceof ExceptionInInitializerError)
                && x.getCause() != null) {
            x = x.getCause();
        }
        return x;
    }

    static RuntimeException sneaky(Throwable t) {
        T.<RuntimeException>sneaky0(t);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <X extends Throwable> void sneaky0(Throwable t) throws X {
        throw (X) t;
    }
}
