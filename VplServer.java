import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * VPL-Lab: a local copy of the Moodle VPL editor.
 *
 * Start:  java VplServer.java      (from this folder, Java 11 or newer)
 * Then:   http://127.0.0.1:8080/   opens by itself.
 *
 * Folders:
 *   assignments/<id>/assignment.json   title, course, run settings
 *   assignments/<id>/description.html  the assignment text
 *   assignments/<id>/starter/          files the student edits (requested files)
 *   assignments/<id>/provided/         read-only files compiled with the student's code
 *   assignments/<id>/tests/Tests.java  hidden tests, written with lib/T.java
 *   work/<id>/                         your saved code and last evaluation
 */
public class VplServer {

    static Path ROOT;
    static Path ASSIGN;
    static Path WORK;
    static Path WEB;
    static Path LIB;
    static final Map<String, RunSession> RUNS = new ConcurrentHashMap<>();
    static final Map<String, String> ACTIVE = new ConcurrentHashMap<>();
    static final Pattern SAFE = Pattern.compile("[A-Za-z0-9_][A-Za-z0-9_.\\-]*");

    public static void main(String[] args) throws Exception {
        ROOT = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        if (!Files.isDirectory(ROOT.resolve("web"))) {
            System.out.println("Bu programi VPL-Lab klasorunun icinden baslatin: cd VPL-Lab && java VplServer.java");
            return;
        }
        ASSIGN = ROOT.resolve("assignments");
        WORK = ROOT.resolve("work");
        WEB = ROOT.resolve("web");
        LIB = ROOT.resolve("lib");
        Files.createDirectories(ASSIGN);
        Files.createDirectories(WORK);

        if (args.length > 0 && (args[0].equals("check") || args[0].equals("--check"))) {
            System.exit(Checker.run(args));
        }

        HttpServer server = null;
        int port = Integer.getInteger("port", 8080);
        for (int p = port; p < port + 20 && server == null; p++) {
            // The exact requested port gets a few retries with a short wait: right after an
            // auto-restart (see watchForSelfChanges) the old process may not have released it yet.
            int attempts = p == port ? 15 : 1;
            for (int a = 0; a < attempts && server == null; a++) {
                try {
                    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), p), 0);
                    port = p;
                } catch (BindException e) {
                    if (a < attempts - 1) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        if (server == null) {
            System.out.println("No free port found.");
            return;
        }
        server.createContext("/", VplServer::handle);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (RunSession s : RUNS.values()) {
                s.kill();
            }
        }));
        watchForSelfChanges(port);

        String url = "http://127.0.0.1:" + port + "/";
        System.out.println("==============================================");
        System.out.println("  VPL-Lab calisiyor:  " + url);
        System.out.println("  Kapatmak icin bu pencerede Ctrl+C");
        System.out.println("  VplServer.java degisirse sunucu kendini otomatik yeniden baslatir.");
        System.out.println("==============================================");
        if (!Boolean.getBoolean("noBrowser")) {
            openBrowser(url);
        }
    }

    /**
     * Watches VplServer.java itself for changes (an AI assistant editing the server, a git pull,
     * a manual fix) and restarts the process automatically, on the same port, so a stale server
     * never keeps running mismatched code. Only VplServer.java is watched: assignment files,
     * description.html and lib/T.java are all read fresh on every request/evaluation and never
     * need a restart, and restarting on every one of those edits would interrupt students' active
     * Run sessions for no reason.
     */
    static void watchForSelfChanges(int port) {
        Path self = ROOT.resolve("VplServer.java");
        if (!Files.isRegularFile(self)) {
            return;
        }
        Thread watcher = new Thread(() -> {
            try {
                java.nio.file.WatchService ws = ROOT.getFileSystem().newWatchService();
                ROOT.register(ws, java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY,
                        java.nio.file.StandardWatchEventKinds.ENTRY_CREATE);
                while (true) {
                    java.nio.file.WatchKey key = ws.take();
                    boolean touched = false;
                    for (java.nio.file.WatchEvent<?> ev : key.pollEvents()) {
                        Object ctx = ev.context();
                        if (ctx != null && "VplServer.java".equals(ctx.toString())) {
                            touched = true;
                        }
                    }
                    boolean valid = key.reset();
                    if (!touched) {
                        if (!valid) {
                            return;
                        }
                        continue;
                    }
                    awaitStable(self);
                    restartSelf(port);
                    return;
                }
            } catch (Throwable ignored) {
                // best-effort: if the watcher itself fails, the server just keeps running as before
            }
        }, "vpl-self-watch");
        watcher.setDaemon(true);
        watcher.start();
    }

    /** Waits until the file has stopped changing (an editor may write it in several small steps). */
    static void awaitStable(Path file) {
        long lastSize = -1;
        long lastMod = -1;
        int stable = 0;
        for (int i = 0; i < 30 && stable < 2; i++) {
            try {
                Thread.sleep(150);
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                long size = Files.size(file);
                long mod = Files.getLastModifiedTime(file).toMillis();
                if (size == lastSize && mod == lastMod) {
                    stable++;
                } else {
                    stable = 0;
                }
                lastSize = size;
                lastMod = mod;
            } catch (IOException | InterruptedException ignored) {
                // keep waiting
            }
        }
    }

    /** Re-launches "java VplServer.java" on the same port and exits this process. */
    static void restartSelf(int port) {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("  VplServer.java degisti: sunucu yeniden baslatiliyor...");
        System.out.println("==============================================");
        try {
            String javaBin = ProcessHandle.current().info().command().orElse("java");
            List<String> cmd = new ArrayList<>();
            cmd.add(javaBin);
            cmd.add("-Dport=" + port);
            cmd.add("-DnoBrowser=true");
            cmd.add(ROOT.resolve("VplServer.java").toString());
            new ProcessBuilder(cmd).directory(ROOT.toFile()).inheritIO().start();
        } catch (Exception e) {
            System.out.println("Otomatik yeniden baslatma basarisiz oldu: " + e.getMessage());
            System.out.println("Elle yeniden baslat: bu pencerede Ctrl+C, sonra tekrar calistir.");
            return;
        }
        System.exit(0);
    }

    static void openBrowser(String url) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        try {
            if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else if (os.contains("win")) {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (IOException e) {
            System.out.println("Tarayicida acin: " + url);
        }
    }

    // =================================================================== routing

    static void handle(HttpExchange ex) {
        try {
            String path = URLDecoder.decode(ex.getRequestURI().getRawPath(), "UTF-8");
            String method = ex.getRequestMethod();
            if (path.startsWith("/api/")) {
                api(ex, method, path.substring(5));
            } else {
                serveStatic(ex, path);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                Map<String, Object> err = new LinkedHashMap<>();
                err.put("error", t.getClass().getSimpleName() + ": " + t.getMessage());
                sendJson(ex, 500, err);
            } catch (IOException ignored) {
                // client went away
            }
        } finally {
            ex.close();
        }
    }

    static void api(HttpExchange ex, String method, String path) throws Exception {
        String[] parts = path.split("/");
        if (parts[0].equals("assignments") && method.equals("GET")) {
            sendJson(ex, 200, listAssignments());
            return;
        }
        if (parts[0].equals("settings") && method.equals("GET")) {
            sendJson(ex, 200, settings());
            return;
        }
        if (parts[0].equals("a") && parts.length >= 2) {
            Assignment a = Assignment.load(parts[1]);
            if (a == null) {
                sendJson(ex, 404, Map.of("error", "No assignment " + parts[1]));
                return;
            }
            String action = parts.length >= 3 ? parts[2] : "";
            if (action.isEmpty() && method.equals("GET")) {
                sendJson(ex, 200, a.details());
                return;
            }
            if (method.equals("POST")) {
                Map<String, Object> body = readJsonBody(ex);
                switch (action) {
                    case "save":
                        a.save(body);
                        sendJson(ex, 200, Map.of("ok", true));
                        return;
                    case "evaluate":
                        a.save(body);
                        sendJson(ex, 200, a.evaluate());
                        return;
                    case "run":
                        a.save(body);
                        sendJson(ex, 200, a.run());
                        return;
                    case "reset":
                        a.reset();
                        sendJson(ex, 200, Map.of("ok", true));
                        return;
                    default:
                        break;
                }
            }
        }
        if (parts[0].equals("run") && parts.length >= 2) {
            RunSession s = RUNS.get(parts[1]);
            if (s == null) {
                sendJson(ex, 404, Map.of("error", "No such run"));
                return;
            }
            String action = parts.length >= 3 ? parts[2] : "";
            if (action.isEmpty() && method.equals("GET")) {
                int from = 0;
                String q = ex.getRequestURI().getQuery();
                if (q != null && q.startsWith("from=")) {
                    from = Integer.parseInt(q.substring(5));
                }
                sendJson(ex, 200, s.poll(from));
                return;
            }
            if (action.equals("input") && method.equals("POST")) {
                Map<String, Object> body = readJsonBody(ex);
                s.input(String.valueOf(body.getOrDefault("text", "")));
                sendJson(ex, 200, Map.of("ok", true));
                return;
            }
            if (action.equals("stop") && method.equals("POST")) {
                s.kill();
                sendJson(ex, 200, Map.of("ok", true));
                return;
            }
        }
        sendJson(ex, 404, Map.of("error", "Unknown API " + path));
    }

    static void serveStatic(HttpExchange ex, String path) throws IOException {
        String rel = path.equals("/") ? "index.html" : path.replaceFirst("^/+", "");
        if (rel.startsWith("web/")) {
            rel = rel.substring(4);
        }
        Path f = WEB.resolve(rel).normalize();
        if (!f.startsWith(WEB) || !Files.isRegularFile(f)) {
            if (path.startsWith("/web/")) {
                ex.sendResponseHeaders(404, -1);
                return;
            }
            f = WEB.resolve("index.html");
        }
        byte[] data = Files.readAllBytes(f);
        String name = f.getFileName().toString();
        String type = name.endsWith(".html") ? "text/html; charset=utf-8"
                : name.endsWith(".js") ? "application/javascript; charset=utf-8"
                : name.endsWith(".css") ? "text/css; charset=utf-8"
                : name.endsWith(".svg") ? "image/svg+xml"
                : name.endsWith(".png") ? "image/png"
                : "application/octet-stream";
        ex.getResponseHeaders().set("Content-Type", type);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(200, data.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(data);
        }
    }

    static Map<String, Object> readJsonBody(HttpExchange ex) throws IOException {
        byte[] b = ex.getRequestBody().readAllBytes();
        if (b.length == 0) {
            return new LinkedHashMap<>();
        }
        Object o = Json.parse(new String(b, StandardCharsets.UTF_8));
        if (o instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) o;
            return m;
        }
        return new LinkedHashMap<>();
    }

    static void sendJson(HttpExchange ex, int code, Object body) throws IOException {
        byte[] data = Json.write(body).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(code, data.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(data);
        }
    }

    static Map<String, Object> settings() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("userName", "Student");
        Path f = ROOT.resolve("settings.json");
        if (Files.isRegularFile(f)) {
            try {
                Object o = Json.parse(read(f));
                if (o instanceof Map) {
                    for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                        s.put(String.valueOf(e.getKey()), e.getValue());
                    }
                }
            } catch (Exception ignored) {
                // keep defaults
            }
        }
        return s;
    }

    static List<Object> listAssignments() throws IOException {
        List<Assignment> all = new ArrayList<>();
        try (Stream<Path> s = Files.list(ASSIGN)) {
            for (Path p : s.collect(Collectors.toList())) {
                String name = p.getFileName().toString();
                if (Files.isDirectory(p) && SAFE.matcher(name).matches() && !name.startsWith("_")) {
                    Assignment a = Assignment.load(name);
                    if (a != null) {
                        all.add(a);
                    }
                }
            }
        }
        all.sort(Comparator.comparingDouble((Assignment a) -> a.order).thenComparing(a -> a.id));
        List<Object> out = new ArrayList<>();
        for (Assignment a : all) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.id);
            m.put("title", a.title);
            m.put("course", a.course);
            m.put("subtitle", a.cfg.getOrDefault("subtitle", ""));
            m.put("topic", a.cfg.getOrDefault("topic", ""));
            Map<String, Object> st = a.state();
            m.put("grade", st.get("grade"));
            m.put("evalCount", st.getOrDefault("evalCount", 0));
            m.put("started", Files.isDirectory(a.work.resolve("files")));
            out.add(m);
        }
        return out;
    }

    // =================================================================== assignment

    static final class Assignment {
        String id;
        Path dir;
        Path work;
        Map<String, Object> cfg;
        String title;
        String course;
        double order;

        static Assignment load(String id) {
            if (!SAFE.matcher(id).matches()) {
                return null;
            }
            Path dir = ASSIGN.resolve(id);
            if (!Files.isDirectory(dir)) {
                return null;
            }
            Assignment a = new Assignment();
            a.id = id;
            a.dir = dir;
            a.work = WORK.resolve(id);
            a.cfg = new LinkedHashMap<>();
            Path cfgFile = dir.resolve("assignment.json");
            if (Files.isRegularFile(cfgFile)) {
                try {
                    Object o = Json.parse(read(cfgFile));
                    if (o instanceof Map) {
                        for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                            a.cfg.put(String.valueOf(e.getKey()), e.getValue());
                        }
                    }
                } catch (Exception e) {
                    a.cfg.put("configError", e.getMessage());
                }
            }
            a.title = String.valueOf(a.cfg.getOrDefault("title", id));
            a.course = String.valueOf(a.cfg.getOrDefault("course", ""));
            Object ord = a.cfg.get("order");
            a.order = ord instanceof Number ? ((Number) ord).doubleValue() : 1000;
            return a;
        }

        List<Path> filesIn(String sub) throws IOException {
            Path d = dir.resolve(sub);
            if (!Files.isDirectory(d)) {
                return new ArrayList<>();
            }
            try (Stream<Path> s = Files.list(d)) {
                return s.filter(Files::isRegularFile)
                        .filter(p -> !p.getFileName().toString().startsWith("."))
                        .sorted()
                        .collect(Collectors.toList());
            }
        }

        List<String> requiredNames() throws IOException {
            List<String> out = new ArrayList<>();
            for (Path p : filesIn("starter")) {
                out.add(p.getFileName().toString());
            }
            return out;
        }

        Path workFiles() throws IOException {
            Path wf = work.resolve("files");
            if (!Files.isDirectory(wf)) {
                Files.createDirectories(wf);
                for (Path p : filesIn("starter")) {
                    Files.copy(p, wf.resolve(p.getFileName().toString()));
                }
            }
            // a requested file added to the assignment later
            for (Path p : filesIn("starter")) {
                Path t = wf.resolve(p.getFileName().toString());
                if (!Files.exists(t)) {
                    Files.copy(p, t);
                }
            }
            return wf;
        }

        List<Path> studentFiles() throws IOException {
            Path wf = workFiles();
            List<String> req = requiredNames();
            List<Path> all;
            try (Stream<Path> s = Files.list(wf)) {
                all = s.filter(Files::isRegularFile)
                        .filter(p -> !p.getFileName().toString().startsWith("."))
                        .collect(Collectors.toList());
            }
            all.sort(Comparator.comparing((Path p) -> {
                int i = req.indexOf(p.getFileName().toString());
                return i < 0 ? 1000 : i;
            }).thenComparing(p -> p.getFileName().toString()));
            return all;
        }

        Map<String, Object> details() throws IOException {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", id);
            m.put("title", title);
            m.put("course", course);
            m.put("subtitle", cfg.getOrDefault("subtitle", ""));
            m.put("config", cfg);
            Path desc = dir.resolve("description.html");
            m.put("description", Files.isRegularFile(desc) ? read(desc) : "<p>(No description)</p>");
            m.put("requested", fileList(filesIn("starter"), false, true));
            m.put("provided", fileList(filesIn("provided"), true, false));
            List<String> req = requiredNames();
            List<Object> files = new ArrayList<>();
            for (Path p : studentFiles()) {
                Map<String, Object> f = fileEntry(p);
                f.put("readonly", false);
                f.put("required", req.contains(p.getFileName().toString()));
                files.add(f);
            }
            if (!Boolean.FALSE.equals(cfg.get("showProvidedInEditor"))) {
                for (Object o : fileList(filesIn("provided"), true, false)) {
                    files.add(o);
                }
            }
            m.put("files", files);
            m.put("state", state());
            return m;
        }

        List<Object> fileList(List<Path> paths, boolean readonly, boolean required) throws IOException {
            List<Object> out = new ArrayList<>();
            for (Path p : paths) {
                Map<String, Object> f = fileEntry(p);
                f.put("readonly", readonly);
                f.put("required", required);
                out.add(f);
            }
            return out;
        }

        Map<String, Object> fileEntry(Path p) throws IOException {
            Map<String, Object> f = new LinkedHashMap<>();
            f.put("name", p.getFileName().toString());
            f.put("content", Files.size(p) > 2_000_000 ? "(file too large to show)" : read(p));
            return f;
        }

        Map<String, Object> state() {
            Path f = work.resolve("state.json");
            if (Files.isRegularFile(f)) {
                try {
                    Object o = Json.parse(read(f));
                    if (o instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> m = (Map<String, Object>) o;
                        return m;
                    }
                } catch (Exception ignored) {
                    // start fresh
                }
            }
            return new LinkedHashMap<>();
        }

        void saveState(Map<String, Object> st) throws IOException {
            Files.createDirectories(work);
            write(work.resolve("state.json"), Json.write(st));
        }

        synchronized void save(Map<String, Object> body) throws IOException {
            Object files = body.get("files");
            if (!(files instanceof List)) {
                return;
            }
            Path wf = workFiles();
            Set<String> provided = new LinkedHashSet<>();
            for (Path p : filesIn("provided")) {
                provided.add(p.getFileName().toString());
            }
            List<String> req = requiredNames();
            Set<String> keep = new LinkedHashSet<>(req);
            int count = 0;
            for (Object o : (List<?>) files) {
                if (!(o instanceof Map)) {
                    continue;
                }
                Map<?, ?> f = (Map<?, ?>) o;
                String name = String.valueOf(f.get("name"));
                if (!SAFE.matcher(name).matches() || provided.contains(name) || name.equals("state.json")) {
                    continue;
                }
                if (++count > 60) {
                    break;
                }
                Object content = f.get("content");
                write(wf.resolve(name), content == null ? "" : String.valueOf(content));
                keep.add(name);
            }
            try (Stream<Path> s = Files.list(wf)) {
                for (Path p : s.collect(Collectors.toList())) {
                    if (!keep.contains(p.getFileName().toString())) {
                        Files.deleteIfExists(p);
                    }
                }
            }
        }

        synchronized void reset() throws IOException {
            deleteTree(work.resolve("files"));
            Map<String, Object> st = state();
            st.remove("grade");
            st.remove("report");
            st.remove("compilation");
            st.remove("annotations");
            saveState(st);
        }

        /** Copies provided + student files into a fresh folder. */
        Path prepare(String folder) throws IOException {
            Path d = work.resolve(folder);
            deleteTree(d);
            Files.createDirectories(d);
            for (Path p : filesIn("provided")) {
                Files.copy(p, d.resolve(p.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            }
            for (Path p : studentFiles()) {
                Files.copy(p, d.resolve(p.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            }
            return d;
        }

        List<Path> javaFiles(Path d) throws IOException {
            try (Stream<Path> s = Files.list(d)) {
                return s.filter(p -> p.toString().endsWith(".java")).sorted().collect(Collectors.toList());
            }
        }

        // --------------------------------------------------------------- evaluate

        synchronized Map<String, Object> evaluate() throws Exception {
            Map<String, Object> st = state();
            int evalCount = ((Number) st.getOrDefault("evalCount", 0)).intValue() + 1;
            st.put("evalCount", evalCount);
            st.put("evaluatedAt", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));

            Path d = prepare(".eval");
            Compile c = compile(d, javaFiles(d), d);
            Map<String, Object> res = new LinkedHashMap<>();
            String reportTitle = reportTitle();
            if (!c.ok) {
                st.put("grade", 0);
                st.put("compilation", c.output);
                st.put("annotations", c.annotations);
                st.put("report", compileFailedReport(reportTitle));
                st.put("compiled", false);
                saveState(st);
                deleteTree(d);
                return st;
            }

            Path testDir = dir.resolve("tests");
            if (!Files.isDirectory(testDir) || javaFiles(testDir).isEmpty()) {
                st.put("grade", null);
                st.put("compilation", c.output);
                st.put("annotations", c.annotations);
                st.put("report", "This assignment has no tests yet.");
                st.put("compiled", true);
                saveState(st);
                deleteTree(d);
                return st;
            }
            // other test resources (input files etc.)
            for (Path p : filesIn("tests")) {
                if (!p.toString().endsWith(".java")) {
                    Files.copy(p, d.resolve(p.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            // tests/optional/*.java: extra classes built on the student's code (e.g. an unseen
            // subclass). Each is compiled on its own; if it does not compile, the tests see it missing.
            Path optDir = testDir.resolve("optional");
            if (Files.isDirectory(optDir)) {
                Path od = d.resolve(".optional");
                Files.createDirectories(od);
                for (Path p : javaFiles(optDir)) {
                    Path q = od.resolve(p.getFileName().toString());
                    Files.copy(p, q, StandardCopyOption.REPLACE_EXISTING);
                    compile(d, List.of(q), d);
                }
            }
            Path td = d.resolve(".tests");
            Files.createDirectories(td);
            List<Path> testSources = new ArrayList<>();
            Path t = td.resolve("T.java");
            Files.copy(LIB.resolve("T.java"), t);
            testSources.add(t);
            for (Path p : javaFiles(testDir)) {
                Path q = td.resolve(p.getFileName().toString());
                Files.copy(p, q);
                testSources.add(q);
            }
            Compile ct = compile(d, testSources, d);
            if (!ct.ok) {
                st.put("grade", null);
                st.put("compilation", c.output);
                st.put("annotations", c.annotations);
                st.put("report", "INTERNAL ERROR: the hidden tests could not be compiled.\n\n" + ct.output);
                st.put("compiled", true);
                saveState(st);
                deleteTree(d);
                return st;
            }

            List<String> studentNames = new ArrayList<>();
            for (Path p : studentFiles()) {
                studentNames.add(p.getFileName().toString());
            }
            int limit = cfgInt("evaluateTimeLimit", 90);
            String mainClass = String.valueOf(cfg.getOrDefault("testMain", "Tests"));
            List<String> cmd = new ArrayList<>();
            cmd.add(javaExe());
            cmd.add("-Djava.awt.headless=true");
            cmd.add("-Dfile.encoding=UTF-8");
            cmd.add("-Dvpl.src=" + d.toString());
            cmd.add("-Dvpl.studentFiles=" + String.join(",", studentNames));
            cmd.add("-Dvpl.checkTimeout=" + cfgInt("checkTimeoutMs", 3000));
            cmd.add("-Xss4m");
            cmd.add("-cp");
            cmd.add(d.toString());
            cmd.add(mainClass);
            Proc pr = runProcess(cmd, d, limit * 1000L);

            List<TestResult> tests = new ArrayList<>();
            boolean ended = false;
            long ms = 0;
            for (String line : pr.out.split("\n")) {
                line = line.replace("\r", "");
                if (line.equals("@@VPL@@END")) {
                    ended = true;
                } else if (line.startsWith("@@VPL@@TIME\t")) {
                    try {
                        ms = Long.parseLong(line.substring(12).trim());
                    } catch (NumberFormatException ignored) {
                        // keep 0
                    }
                } else if (line.startsWith("@@VPL@@T\t")) {
                    String[] p = line.split("\t", 6);
                    if (p.length == 6) {
                        TestResult r = new TestResult();
                        r.group = p[1];
                        r.method = p[2];
                        r.pass = p[3].equals("1");
                        r.title = p[4];
                        r.kind = p[5];
                        tests.add(r);
                    }
                } else if (line.startsWith("@@VPL@@F\t") && !tests.isEmpty()) {
                    String[] p = line.split("\t", 4);
                    if (p.length >= 3) {
                        tests.get(tests.size() - 1).failures.add(
                                new String[] { p[1], p[2], p.length == 4 ? p[3] : "" });
                    }
                }
            }
            String problem = null;
            if (ended && tests.isEmpty()) {
                problem = "No checks were run: the test program reported no results. "
                        + "Restart VPL Lab (Ctrl+C, then start it again) so the server and lib/T.java match.";
            }
            if (!ended) {
                if (pr.timedOut) {
                    problem = "The tests did not finish within " + limit + " seconds (infinite loop?)";
                } else {
                    String err = lastLines(pr.err, 3);
                    problem = "The test program stopped unexpectedly (System.exit or a crash, exit code " + pr.exit + ")"
                            + (err.isEmpty() ? "" : ": " + err);
                }
            }
            int passed = 0;
            for (TestResult r : tests) {
                if (r.pass) {
                    passed++;
                }
            }
            int grade = tests.isEmpty() ? 0 : (int) Math.round(100.0 * passed / tests.size());
            if (!ended || tests.isEmpty()) {
                grade = 0;
            }
            String testClass = String.valueOf(cfg.getOrDefault("testClassName", "Lab_TestTemplate"));
            st.put("grade", grade);
            st.put("passed", passed);
            st.put("total", tests.size());
            st.put("compilation", junitTree(testClass, tests, ms, problem));
            st.put("annotations", c.annotations);
            st.put("report", buildReport(reportTitle, grade, passed, tests, problem));
            st.put("compiled", true);
            saveState(st);
            deleteTree(d);
            res.putAll(st);
            return res;
        }

        String reportTitle() {
            Object t = cfg.get("reportTitle");
            if (t != null) {
                return String.valueOf(t);
            }
            String c = course.trim();
            Matcher m = Pattern.compile("^[A-Za-z]+\\s*\\d+").matcher(c);
            String code = m.find() ? m.group().replace(" ", "") : c;
            return (code.isEmpty() ? "" : code + " - ") + "AUTOMATIC EVALUATION";
        }

        int cfgInt(String key, int def) {
            Object o = cfg.get(key);
            return o instanceof Number ? ((Number) o).intValue() : def;
        }

        // --------------------------------------------------------------- run

        synchronized Map<String, Object> run() throws Exception {
            String prev = ACTIVE.get(id);
            if (prev != null && RUNS.containsKey(prev)) {
                RUNS.get(prev).kill();
                RUNS.remove(prev);
            }
            Path d = prepare(".run");
            List<Path> sources = javaFiles(d);
            Map<String, Object> res = new LinkedHashMap<>();
            if (sources.isEmpty()) {
                res.put("ok", false);
                res.put("message", "There are no .java files to run.");
                return res;
            }
            Compile c = compile(d, sources, d);
            res.put("compilation", c.output);
            res.put("annotations", c.annotations);
            if (!c.ok) {
                res.put("ok", false);
                res.put("message", "The code could not be compiled.");
                return res;
            }
            String main = findMain(d);
            if (main == null) {
                res.put("ok", false);
                res.put("message", "No main method was found. Add public static void main(String[] args) to a class to use Run.");
                return res;
            }
            List<String> cmd = new ArrayList<>();
            cmd.add(javaExe());
            cmd.add("-Dfile.encoding=UTF-8");
            cmd.add("-Dstdout.encoding=UTF-8");
            cmd.add("-cp");
            cmd.add(d.toString());
            cmd.add(main);
            RunSession s = new RunSession(cmd, d);
            RUNS.put(s.id, s);
            ACTIVE.put(id, s.id);
            res.put("ok", true);
            res.put("runId", s.id);
            res.put("mainClass", main);
            return res;
        }

        String findMain(Path d) throws IOException {
            Pattern mainP = Pattern.compile("static\\s+(public\\s+)?void\\s+main\\s*\\(");
            Pattern classP = Pattern.compile("(class|enum|record|interface)\\s+([A-Za-z_]\\w*)");
            List<Path> order = new ArrayList<>();
            for (Path p : studentFiles()) {
                if (p.toString().endsWith(".java")) {
                    order.add(d.resolve(p.getFileName().toString()));
                }
            }
            for (Path p : filesIn("provided")) {
                if (p.toString().endsWith(".java")) {
                    order.add(0, d.resolve(p.getFileName().toString()));
                }
            }
            for (Path p : order) {
                String src = read(p);
                Matcher mm = mainP.matcher(src);
                if (mm.find()) {
                    Matcher cm = classP.matcher(src.substring(0, mm.start()));
                    String name = null;
                    while (cm.find()) {
                        name = cm.group(2);
                    }
                    if (name != null) {
                        return name;
                    }
                }
            }
            return null;
        }
    }

    static final class TestResult {
        String group;
        String method;
        String title;
        String kind;
        boolean pass;
        List<String[]> failures = new ArrayList<>();
    }

    static final int WIDTH = 56;

    static String header(String title) {
        return "          " + title;
    }

    static String compileFailedReport(String title) {
        String eq = repeat('=', WIDTH);
        return eq + "\n" + header(title) + "\n" + eq + "\n"
                + "SCORE: 0 / 100\n"
                + "[--------------------]  the code could not be compiled\n"
                + eq + "\n\n"
                + "The compilation or preparation of execution has failed.\n"
                + "Open the Compilation section to see the errors,\n"
                + "fix them, then evaluate again.\n";
    }

    /** Comment text of one failure: "..., but it threw java.lang.X" becomes "...   [threw X]". */
    static String commentLine(String message) {
        Matcher m = Pattern.compile("^(.*), but it threw ([\\w.$]+)$").matcher(message);
        if (m.matches()) {
            String cls = m.group(2);
            return m.group(1) + "   [threw " + cls.substring(cls.lastIndexOf('.') + 1) + "]";
        }
        return message;
    }

    static String buildReport(String title, int grade, int passed, List<TestResult> tests, String problem) {
        String eq = repeat('=', WIDTH);
        String dash = repeat('-', WIDTH);
        StringBuilder sb = new StringBuilder();
        int total = tests.size();
        int filled = total == 0 ? 0 : (int) Math.round(20.0 * passed / total);
        sb.append(eq).append('\n').append(header(title)).append('\n').append(eq).append('\n');
        sb.append("SCORE: ").append(grade).append(" / 100").append(problem != null ? "   (evaluation did not finish)" : "").append('\n');
        sb.append('[').append(repeat('#', filled)).append(repeat('-', 20 - filled)).append("]  ")
                .append(passed).append(" of ").append(total).append(" checks passed\n");
        sb.append(eq).append("\n\n");

        Map<String, List<TestResult>> groups = new LinkedHashMap<>();
        for (TestResult r : tests) {
            groups.computeIfAbsent(r.group, k -> new ArrayList<>()).add(r);
        }
        List<String> names = new ArrayList<>(groups.keySet());
        names.sort(Comparator.comparing((String n) -> n.replaceAll("[()\\[\\]]", "").toLowerCase(Locale.ROOT)));

        sb.append(dash).append("\nRESULTS BY METHOD\n").append(dash).append('\n');
        for (String n : names) {
            List<TestResult> g = groups.get(n);
            int ok = 0;
            for (TestResult r : g) {
                if (r.pass) {
                    ok++;
                }
            }
            StringBuilder dots = new StringBuilder(n + " ");
            while (dots.length() < 31) {
                dots.append('.');
            }
            sb.append(ok == g.size() ? "[OK] " : "[!!] ").append(dots).append(' ').append(ok).append('/')
                    .append(g.size()).append('\n');
        }
        if (names.isEmpty()) {
            sb.append("(no checks were run)\n");
        }
        sb.append('\n');

        boolean anyFail = problem != null || passed < total;
        if (anyFail) {
            sb.append(dash).append("\nWHAT WENT WRONG\n").append(dash).append('\n');
            for (String n : names) {
                List<String> lines = new ArrayList<>();
                for (TestResult r : groups.get(n)) {
                    if (!r.pass) {
                        for (String[] f : r.failures) {
                            lines.add(commentLine(f[1]));
                        }
                    }
                }
                if (lines.isEmpty()) {
                    continue;
                }
                sb.append(n).append('\n');
                for (int i = 0; i < lines.size() && i < 4; i++) {
                    sb.append("  - ").append(lines.get(i)).append('\n');
                }
                if (lines.size() > 4) {
                    sb.append("  - ... and ").append(lines.size() - 4)
                            .append(" more failing check(s) in this method\n");
                }
                sb.append('\n');
            }
            if (problem != null) {
                sb.append("(evaluation)\n  - ").append(problem).append("\n\n");
            }
            sb.append(dash).append('\n')
                    .append("Each line above is one check. Fix the method it names,\n")
                    .append("then evaluate again.\n")
                    .append(dash).append('\n');
        } else {
            sb.append(dash).append('\n')
                    .append("All checks passed. Well done!\n")
                    .append(dash).append('\n');
        }
        return sb.toString();
    }

    /** The JUnit console launcher's tree, as the school's Compilation section shows it. */
    static String junitTree(String cls, List<TestResult> tests, long ms, String problem) {
        StringBuilder sb = new StringBuilder();
        sb.append(".\n+-- JUnit Platform Suite [OK]\n+-- JUnit Jupiter [OK]\n'-- JUnit Vintage [OK]\n");
        sb.append("  '-- ").append(cls).append(" [OK]\n");
        int failed = 0;
        for (int i = 0; i < tests.size(); i++) {
            TestResult r = tests.get(i);
            boolean last = i == tests.size() - 1;
            sb.append(last ? "    '-- " : "    +-- ").append(r.method);
            if (r.pass) {
                sb.append(" [OK]\n");
                continue;
            }
            failed++;
            if (r.kind.equals("multi")) {
                int n = r.failures.size();
                sb.append(" [X] ").append(r.title).append(" (").append(n).append(n == 1 ? " failure)" : " failures)")
                        .append('\n');
                for (String[] f : r.failures) {
                    sb.append(last ? "          " : "    |     ").append('\t').append(f[0]).append(": ").append(f[1])
                            .append('\n');
                }
            } else {
                sb.append(" [X] ").append(r.failures.isEmpty() ? r.title : r.failures.get(0)[1]).append('\n');
            }
        }
        if (failed > 0) {
            sb.append("\nFailures (").append(failed).append("):\n");
            for (TestResult r : tests) {
                if (r.pass) {
                    continue;
                }
                sb.append("  JUnit Vintage:").append(cls).append(':').append(r.method).append('\n');
                sb.append("    MethodSource [className = '").append(cls).append("', methodName = '").append(r.method)
                        .append("', methodParameterTypes = '']\n");
                if (r.kind.equals("multi")) {
                    int n = r.failures.size();
                    sb.append("    => org.opentest4j.MultipleFailuresError: ").append(r.title).append(" (").append(n)
                            .append(n == 1 ? " failure)" : " failures)").append('\n');
                    for (String[] f : r.failures) {
                        sb.append('\t').append(f[0]).append(": ").append(f[1]).append('\n');
                    }
                    for (String[] f : r.failures) {
                        appendCause(sb, f[2]);
                    }
                } else if (!r.failures.isEmpty()) {
                    String[] f = r.failures.get(0);
                    sb.append("    => ").append(f[0]).append(": ").append(f[1]).append('\n');
                    appendCause(sb, f[2]);
                }
            }
        }
        if (problem != null) {
            sb.append("\n").append(problem).append('\n');
        }
        int ok = tests.size() - failed;
        sb.append("\nTest run finished after ").append(ms).append(" ms\n");
        String[][] rows = {
            { "4", "containers found" }, { "0", "containers skipped" }, { "4", "containers started" },
            { "0", "containers aborted" }, { "4", "containers successful" }, { "0", "containers failed" },
            { "" + tests.size(), "tests found" }, { "0", "tests skipped" }, { "" + tests.size(), "tests started" },
            { "0", "tests aborted" }, { "" + ok, "tests successful" }, { "" + failed, "tests failed" } };
        for (String[] row : rows) {
            sb.append(String.format("[%10s %-22s]%n", row[0], row[1]));
        }
        return sb.toString().replace("\r\n", "\n");
    }

    static void appendCause(StringBuilder sb, String cause) {
        if (cause == null || cause.isEmpty()) {
            return;
        }
        String[] parts = cause.split("\u0001");
        sb.append("       ").append(parts[0]).append('\n');
        for (int i = 1; i < parts.length; i++) {
            sb.append("         ").append(parts[i]).append('\n');
        }
    }

    static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    static String lastLines(String s, int n) {
        String[] lines = s.trim().split("\n");
        List<String> out = new ArrayList<>();
        for (int i = 0; i < lines.length && out.size() < n; i++) {
            String l = lines[i].trim();
            if (!l.isEmpty() && !l.startsWith("at ") && !l.startsWith("Picked up")) {
                out.add(l);
            }
        }
        return String.join(" | ", out);
    }

    // =================================================================== compile

    static final class Compile {
        boolean ok;
        String output = "";
        List<Object> annotations = new ArrayList<>();
    }

    static Compile compile(Path outDir, List<Path> sources, Path classPath) throws IOException {
        Compile c = new Compile();
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        if (jc == null) {
            return compileWithJavac(outDir, sources, classPath);
        }
        DiagnosticCollector<JavaFileObject> diags = new DiagnosticCollector<>();
        StandardJavaFileManager fm = jc.getStandardFileManager(diags, Locale.ENGLISH, StandardCharsets.UTF_8);
        List<File> files = new ArrayList<>();
        for (Path p : sources) {
            files.add(p.toFile());
        }
        List<String> opts = List.of("-d", outDir.toString(), "-cp", classPath.toString(), "-encoding", "UTF-8",
                "-nowarn", "-Xlint:none", "-proc:none", "-g", "-Xmaxerrs", "50");
        Locale old = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        try {
            c.ok = jc.getTask(null, fm, diags, opts, null, fm.getJavaFileObjectsFromFiles(files)).call();
        } finally {
            Locale.setDefault(old);
            fm.close();
        }
        StringBuilder sb = new StringBuilder();
        int errors = 0;
        Map<String, List<String>> cache = new ConcurrentHashMap<>();
        for (Diagnostic<? extends JavaFileObject> d : diags.getDiagnostics()) {
            if (d.getKind() != Diagnostic.Kind.ERROR) {
                continue;
            }
            errors++;
            String file = d.getSource() == null ? "" : Paths.get(d.getSource().toUri()).getFileName().toString();
            long line = d.getLineNumber();
            long col = d.getColumnNumber();
            String msg = d.getMessage(Locale.ENGLISH);
            sb.append(file).append(':').append(line).append(": error: ").append(msg).append('\n');
            if (d.getSource() != null && line > 0) {
                List<String> lines = cache.computeIfAbsent(file, k -> {
                    try {
                        return Files.readAllLines(Paths.get(d.getSource().toUri()), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        return new ArrayList<>();
                    }
                });
                if (line <= lines.size()) {
                    String src = lines.get((int) line - 1);
                    sb.append(src).append('\n');
                    StringBuilder caret = new StringBuilder();
                    for (int i = 0; i < col - 1 && i < src.length(); i++) {
                        caret.append(src.charAt(i) == '\t' ? '\t' : ' ');
                    }
                    sb.append(caret).append("^\n");
                }
            }
            Map<String, Object> a = new LinkedHashMap<>();
            a.put("file", file);
            a.put("row", line - 1);
            a.put("column", Math.max(0, col - 1));
            a.put("text", msg);
            a.put("type", "error");
            c.annotations.add(a);
        }
        if (errors > 0) {
            sb.append(errors).append(errors == 1 ? " error" : " errors").append('\n');
        }
        c.output = sb.toString();
        return c;
    }

    static Compile compileWithJavac(Path outDir, List<Path> sources, Path classPath) throws IOException {
        Compile c = new Compile();
        List<String> cmd = new ArrayList<>();
        Path javac = Paths.get(System.getProperty("java.home"), "bin", "javac");
        cmd.add(Files.exists(javac) ? javac.toString() : "javac");
        cmd.addAll(List.of("-J-Duser.language=en", "-d", outDir.toString(), "-cp", classPath.toString(),
                "-encoding", "UTF-8", "-nowarn", "-g"));
        for (Path p : sources) {
            cmd.add(p.toString());
        }
        Proc pr = runProcess(cmd, outDir, 60_000);
        c.ok = pr.exit == 0;
        String out = (pr.out + pr.err).replace(outDir.toString() + File.separator, "");
        StringBuilder kept = new StringBuilder();
        Pattern p = Pattern.compile("^([^:\\s]+\\.java):(\\d+): error: (.*)$");
        for (String line : out.split("\n")) {
            if (line.startsWith("Note:") || line.startsWith("Picked up")) {
                continue;
            }
            kept.append(line).append('\n');
            Matcher m = p.matcher(line);
            if (m.find()) {
                Map<String, Object> a = new LinkedHashMap<>();
                a.put("file", m.group(1));
                a.put("row", Integer.parseInt(m.group(2)) - 1);
                a.put("column", 0);
                a.put("text", m.group(3));
                a.put("type", "error");
                c.annotations.add(a);
            }
        }
        c.output = c.ok ? "" : kept.toString();
        return c;
    }

    static String javaExe() {
        Path p = Paths.get(System.getProperty("java.home"), "bin", "java");
        return Files.exists(p) ? p.toString() : "java";
    }

    // =================================================================== processes

    static final class Proc {
        String out = "";
        String err = "";
        int exit = -1;
        boolean timedOut;
    }

    static Proc runProcess(List<String> cmd, Path dir, long timeoutMs) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(cmd).directory(dir.toFile());
        pb.environment().remove("JAVA_TOOL_OPTIONS");
        Process p = pb.start();
        p.getOutputStream().close();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        Thread t1 = drain(p.getInputStream(), out, 8_000_000);
        Thread t2 = drain(p.getErrorStream(), err, 200_000);
        Proc r = new Proc();
        try {
            if (!p.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
                r.timedOut = true;
                killTree(p);
            }
            t1.join(2000);
            t2.join(2000);
        } catch (InterruptedException e) {
            killTree(p);
            Thread.currentThread().interrupt();
        }
        r.exit = r.timedOut ? -1 : p.exitValue();
        r.out = new String(out.toByteArray(), StandardCharsets.UTF_8);
        r.err = new String(err.toByteArray(), StandardCharsets.UTF_8);
        return r;
    }

    static Thread drain(InputStream in, ByteArrayOutputStream sink, int cap) {
        Thread t = new Thread(() -> {
            byte[] buf = new byte[8192];
            try {
                int n;
                while ((n = in.read(buf)) > 0) {
                    synchronized (sink) {
                        if (sink.size() < cap) {
                            sink.write(buf, 0, Math.min(n, cap - sink.size()));
                        }
                    }
                }
            } catch (IOException ignored) {
                // process ended
            }
        });
        t.setDaemon(true);
        t.start();
        return t;
    }

    static void killTree(Process p) {
        p.descendants().forEach(ProcessHandle::destroyForcibly);
        p.destroyForcibly();
    }

    static final class RunSession {
        final String id = UUID.randomUUID().toString().substring(0, 8);
        final Process process;
        final StringBuilder out = new StringBuilder();
        final Writer stdin;
        volatile boolean done;
        volatile int exit;
        boolean truncated;

        RunSession(List<String> cmd, Path dir) throws IOException {
            ProcessBuilder pb = new ProcessBuilder(cmd).directory(dir.toFile()).redirectErrorStream(true);
            pb.environment().remove("JAVA_TOOL_OPTIONS");
            process = pb.start();
            stdin = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8);
            Thread reader = new Thread(() -> {
                try (InputStream in = process.getInputStream()) {
                    byte[] buf = new byte[4096];
                    int n;
                    java.nio.charset.CharsetDecoder dec = StandardCharsets.UTF_8.newDecoder()
                            .onMalformedInput(java.nio.charset.CodingErrorAction.REPLACE)
                            .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPLACE);
                    java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(8192);
                    java.nio.CharBuffer cb = java.nio.CharBuffer.allocate(8192);
                    while ((n = in.read(buf)) > 0) {
                        bb.put(buf, 0, n);
                        bb.flip();
                        dec.decode(bb, cb, false);
                        bb.compact();
                        cb.flip();
                        append(cb.toString());
                        cb.clear();
                    }
                } catch (IOException ignored) {
                    // ended
                }
                try {
                    exit = process.waitFor();
                } catch (InterruptedException e) {
                    exit = -1;
                }
                done = true;
            });
            reader.setDaemon(true);
            reader.start();
        }

        synchronized void append(String s) {
            if (out.length() > 2_000_000) {
                if (!truncated) {
                    out.append("\n[output truncated]\n");
                    truncated = true;
                }
                return;
            }
            out.append(s);
        }

        synchronized Map<String, Object> poll(int from) {
            Map<String, Object> m = new LinkedHashMap<>();
            int f = Math.max(0, Math.min(from, out.length()));
            m.put("text", out.substring(f));
            m.put("next", out.length());
            m.put("done", done);
            m.put("exit", exit);
            return m;
        }

        void input(String text) {
            try {
                stdin.write(text);
                stdin.write("\n");
                stdin.flush();
            } catch (IOException ignored) {
                // program already ended
            }
        }

        void kill() {
            if (process.isAlive()) {
                killTree(process);
            }
        }
    }

    // =================================================================== assignment checker

    /**
     * java VplServer.java check <id> [--solution <dir>]
     * java VplServer.java check --all
     *
     * Validates an assignment the way an AI assistant must before handing it to a student:
     * format, school-style description, compiling starter, reflection-only tests, the starter
     * scoring low, and (with --solution) a reference solution scoring 100.
     */
    static final class Checker {

        static int run(String[] args) throws Exception {
            List<String> ids = new ArrayList<>();
            Path solution = null;
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("--solution") && i + 1 < args.length) {
                    solution = Paths.get(args[++i]).toAbsolutePath();
                } else if (args[i].equals("--all")) {
                    try (Stream<Path> s = Files.list(ASSIGN)) {
                        for (Path p : s.sorted().collect(Collectors.toList())) {
                            String n = p.getFileName().toString();
                            if (Files.isDirectory(p) && !n.startsWith("_") && !n.startsWith(".")) {
                                ids.add(n);
                            }
                        }
                    }
                } else {
                    ids.add(args[i].replaceFirst("^assignments/", "").replaceAll("/+$", ""));
                }
            }
            if (ids.isEmpty()) {
                System.out.println("Usage: java VplServer.java check <assignment-id> [--solution <dir>]");
                System.out.println("       java VplServer.java check --all");
                return 2;
            }
            boolean all = true;
            for (String id : ids) {
                all &= checkOne(id, ids.size() == 1 ? solution : null);
            }
            return all ? 0 : 1;
        }

        static boolean checkOne(String id, Path solution) throws Exception {
            List<String> problems = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            System.out.println("==============================================================");
            System.out.println(" Checking assignments/" + id);
            System.out.println("==============================================================");
            Assignment a = Assignment.load(id);
            if (a == null) {
                System.out.println("FAIL: assignments/" + id + " does not exist (or the name has illegal characters).");
                return false;
            }
            Path dir = a.dir;

            // ---- assignment.json
            if (!Files.isRegularFile(dir.resolve("assignment.json"))) {
                problems.add("assignment.json is missing");
            } else if (a.cfg.containsKey("configError")) {
                problems.add("assignment.json is not valid JSON: " + a.cfg.get("configError"));
            } else {
                for (String k : new String[] { "title", "course", "subtitle" }) {
                    if (!(a.cfg.get(k) instanceof String) || ((String) a.cfg.get(k)).trim().isEmpty()) {
                        problems.add("assignment.json needs a non-empty \"" + k + "\"");
                    }
                }
                if (!(a.cfg.get("order") instanceof Number)) {
                    warnings.add("assignment.json has no numeric \"order\" (the list order)");
                }
            }

            // ---- description.html
            Path desc = dir.resolve("description.html");
            if (!Files.isRegularFile(desc)) {
                problems.add("description.html is missing");
            } else {
                String h = read(desc);
                String low = h.toLowerCase(Locale.ROOT);
                if (!h.trim().startsWith("<div class=\"vd\">")) {
                    problems.add("description.html must start with <div class=\"vd\"> (school style wrapper)");
                }
                for (String need : new String[] { "vd-banner", "vd-kicker", "vd-title", "vd-box" }) {
                    if (!h.contains(need)) {
                        problems.add("description.html is missing the \"" + need + "\" element");
                    }
                }
                if (!low.contains(">objective</h2>")) {
                    problems.add("description.html needs an <h2>Objective</h2> section");
                }
                if (!low.contains("before submission")) {
                    problems.add("description.html needs a Before Submission box");
                }
                if (!low.contains("<table")) {
                    warnings.add("description.html has no method table; school assignments specify methods in tables");
                }
                if (low.contains("<script") || low.contains("<style")) {
                    problems.add("description.html must not contain <script> or <style>");
                }
                String bal = balance(h);
                if (bal != null) {
                    problems.add("description.html has broken HTML: " + bal);
                }
            }

            // ---- starter
            List<Path> starter = a.filesIn("starter");
            List<Path> starterJava = new ArrayList<>();
            for (Path p : starter) {
                if (p.toString().endsWith(".java")) {
                    starterJava.add(p);
                }
            }
            if (starterJava.isEmpty()) {
                problems.add("starter/ must contain at least one .java file");
            }
            Pattern pub = Pattern.compile("(?m)^\\s*public\\s+(?:(?:final|abstract)\\s+)*(?:class|interface|enum|record)\\s+(\\w+)");
            for (Path p : starterJava) {
                String src = read(p);
                String file = p.getFileName().toString();
                Matcher m = pub.matcher(src);
                int n = 0;
                while (m.find()) {
                    n++;
                    if (!(m.group(1) + ".java").equals(file)) {
                        problems.add("starter/" + file + ": public type " + m.group(1) + " must be named like the file");
                    }
                }
                if (n > 1) {
                    problems.add("starter/" + file + " declares more than one public type");
                }
                if (Pattern.compile("(?m)^\\s*package\\s+").matcher(src).find()) {
                    problems.add("starter/" + file + " must not have a package declaration");
                }
            }
            for (String bad : new String[] { "solution", "solutions", ".solution" }) {
                if (Files.exists(dir.resolve(bad))) {
                    problems.add("remove assignments/" + id + "/" + bad + ": never keep a solution inside the assignment");
                }
            }

            // ---- tests
            Path tests = dir.resolve("tests").resolve("Tests.java");
            int testCount = 0;
            if (!Files.isRegularFile(tests)) {
                problems.add("tests/Tests.java is missing");
            } else {
                String src = read(tests);
                if (!src.contains("T.done()")) {
                    problems.add("tests/Tests.java must end main with T.done()");
                }
                Matcher m = Pattern.compile("T\\.test\\(\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"\\s*,\\s*\"([^\"]*)\"").matcher(src);
                Set<String> methods = new LinkedHashSet<>();
                Set<String> groups = new LinkedHashSet<>();
                while (m.find()) {
                    testCount++;
                    groups.add(m.group(1));
                    String method = m.group(2);
                    if (!method.matches("test_[A-Za-z0-9]+_[A-Za-z0-9_]+")) {
                        problems.add("test method name \"" + method + "\" must look like test_<Group>_<aspect>");
                    }
                    if (!methods.add(method)) {
                        problems.add("duplicate test method name " + method);
                    }
                }
                if (testCount < 8) {
                    problems.add("only " + testCount + " T.test(...) checks; write 12-25 like the school labs");
                } else if (testCount > 30) {
                    warnings.add(testCount + " checks is a lot; the school labs use about 20");
                }
                if (!groups.contains("(code rules)")) {
                    warnings.add("no \"(code rules)\" group (school labs check printing / forbidden constructs there)");
                }
                // Tests must use reflection only: they have to compile without any student code.
                Path iso = Files.createTempDirectory("vpl-iso");
                try {
                    Path t = iso.resolve("T.java");
                    Path q = iso.resolve("Tests.java");
                    Files.copy(LIB.resolve("T.java"), t);
                    Files.copy(tests, q);
                    List<Path> srcs = new ArrayList<>(List.of(t, q));
                    try (Stream<Path> s = Files.list(dir.resolve("tests"))) {
                        for (Path p : s.collect(Collectors.toList())) {
                            String n = p.getFileName().toString();
                            if (n.endsWith(".java") && !n.equals("Tests.java")) {
                                Path c = iso.resolve(n);
                                Files.copy(p, c);
                                srcs.add(c);
                            }
                        }
                    }
                    Compile c = compile(iso, srcs, iso);
                    if (!c.ok) {
                        problems.add("tests must not refer to student classes directly (use T.make / T.call); "
                                + "Tests.java does not compile on its own:\n" + indent(c.output));
                    }
                } finally {
                    deleteTree(iso);
                }
            }

            // ---- evaluate the starter
            Path tmp = Files.createTempDirectory("vpl-check");
            Path realWork = a.work;
            a.work = tmp;
            try {
                if (problems.isEmpty() || testCount > 0) {
                    Map<String, Object> st = a.evaluate();
                    String report = String.valueOf(st.get("report"));
                    if (Boolean.FALSE.equals(st.get("compiled"))) {
                        problems.add("the starter code does not compile:\n" + indent(String.valueOf(st.get("compilation"))));
                    } else if (report.startsWith("INTERNAL ERROR")) {
                        problems.add("the hidden tests do not compile against the starter:\n" + indent(report));
                    } else if (report.contains("(evaluation)")) {
                        problems.add("the tests did not finish on the starter code:\n" + indent(report));
                    } else {
                        Object g = st.get("grade");
                        System.out.println("starter code : " + g + " / 100  (" + st.get("passed") + " of " + st.get("total") + " checks)");
                        if (g instanceof Number && ((Number) g).intValue() > 40) {
                            problems.add("the unfinished starter already scores " + g + "/100; tests are too weak or the starter gives the answer away");
                        }
                    }
                }

                // ---- evaluate the reference solution
                if (solution == null) {
                    warnings.add("no --solution given: the tests were not proven to be passable (a reference solution must score 100)");
                } else if (!Files.isDirectory(solution)) {
                    problems.add("--solution " + solution + " is not a folder");
                } else {
                    deleteTree(tmp.resolve("files"));
                    Path wf = a.workFiles();
                    try (Stream<Path> s = Files.list(solution)) {
                        for (Path p : s.filter(Files::isRegularFile).collect(Collectors.toList())) {
                            Files.copy(p, wf.resolve(p.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    Map<String, Object> st = a.evaluate();
                    Object g = st.get("grade");
                    System.out.println("solution     : " + g + " / 100  (" + st.get("passed") + " of " + st.get("total") + " checks)");
                    if (!(g instanceof Number) || ((Number) g).intValue() != 100) {
                        String comp = String.valueOf(st.get("compilation"));
                        problems.add("the reference solution must score 100 but scored " + g + ":\n"
                                + indent(String.valueOf(st.get("report"))) + "\n" + indent(failingLines(comp)));
                    }
                }
            } finally {
                a.work = realWork;
                deleteTree(tmp);
            }

            for (String w : warnings) {
                System.out.println("warning: " + w);
            }
            for (String p : problems) {
                System.out.println("PROBLEM: " + p);
            }
            System.out.println(problems.isEmpty() ? "RESULT: PASS" : "RESULT: FAIL (" + problems.size() + " problem(s))");
            System.out.println();
            return problems.isEmpty();
        }

        static String failingLines(String tree) {
            StringBuilder sb = new StringBuilder();
            for (String l : tree.split("\n")) {
                if (l.contains("[X]") || l.contains("\tjava.") || l.contains("\torg.") || l.trim().startsWith("Caused by")
                        || l.trim().matches("\\w+\\.\\w+\\(\\w+\\.java:\\d+\\)")) {
                    sb.append(l).append('\n');
                }
                if (l.startsWith("Failures (")) {
                    break;
                }
            }
            return sb.toString();
        }

        static String indent(String s) {
            return "    " + s.trim().replace("\n", "\n    ");
        }

        /** Returns a description of the first nesting error, or null when the tags balance. */
        static String balance(String html) {
            Set<String> voids = Set.of("br", "hr", "img", "input", "meta", "link", "wbr", "col", "area", "source");
            List<String> stack = new ArrayList<>();
            Matcher m = Pattern.compile("<(/?)([a-zA-Z][a-zA-Z0-9]*)[^>]*?(/?)>").matcher(html.replaceAll("(?s)<!--.*?-->", ""));
            while (m.find()) {
                String tag = m.group(2).toLowerCase(Locale.ROOT);
                if (voids.contains(tag) || !m.group(3).isEmpty()) {
                    continue;
                }
                if (m.group(1).isEmpty()) {
                    stack.add(tag);
                } else if (stack.isEmpty() || !stack.get(stack.size() - 1).equals(tag)) {
                    return "unexpected </" + tag + ">" + (stack.isEmpty() ? "" : " while <" + stack.get(stack.size() - 1) + "> is open");
                } else {
                    stack.remove(stack.size() - 1);
                }
            }
            return stack.isEmpty() ? null : "<" + stack.get(stack.size() - 1) + "> is never closed";
        }
    }

    // =================================================================== files

    static String read(Path p) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    static void write(Path p, String s) throws IOException {
        Files.createDirectories(p.getParent());
        Files.write(p, s.getBytes(StandardCharsets.UTF_8));
    }

    static void deleteTree(Path p) throws IOException {
        if (!Files.exists(p)) {
            return;
        }
        try (Stream<Path> s = Files.walk(p)) {
            List<Path> all = s.sorted(Collections.reverseOrder()).collect(Collectors.toList());
            for (Path x : all) {
                try {
                    Files.deleteIfExists(x);
                } catch (IOException ignored) {
                    // a running program may still hold a file
                }
            }
        }
    }

    // =================================================================== JSON

    static final class Json {
        private final String s;
        private int i;

        private Json(String s) {
            this.s = s;
        }

        static Object parse(String text) {
            Json j = new Json(text);
            j.ws();
            Object v = j.value();
            j.ws();
            if (j.i != j.s.length()) {
                throw new IllegalArgumentException("Extra characters in JSON at " + j.i);
            }
            return v;
        }

        private void ws() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
        }

        private Object value() {
            ws();
            if (i >= s.length()) {
                throw new IllegalArgumentException("Unexpected end of JSON");
            }
            char c = s.charAt(i);
            if (c == '{') {
                i++;
                Map<String, Object> m = new LinkedHashMap<>();
                ws();
                if (s.charAt(i) == '}') {
                    i++;
                    return m;
                }
                while (true) {
                    ws();
                    String k = string();
                    ws();
                    expect(':');
                    m.put(k, value());
                    ws();
                    if (s.charAt(i) == ',') {
                        i++;
                    } else {
                        expect('}');
                        return m;
                    }
                }
            }
            if (c == '[') {
                i++;
                List<Object> l = new ArrayList<>();
                ws();
                if (s.charAt(i) == ']') {
                    i++;
                    return l;
                }
                while (true) {
                    l.add(value());
                    ws();
                    if (s.charAt(i) == ',') {
                        i++;
                    } else {
                        expect(']');
                        return l;
                    }
                }
            }
            if (c == '"') {
                return string();
            }
            if (s.startsWith("true", i)) {
                i += 4;
                return Boolean.TRUE;
            }
            if (s.startsWith("false", i)) {
                i += 5;
                return Boolean.FALSE;
            }
            if (s.startsWith("null", i)) {
                i += 4;
                return null;
            }
            int st = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) {
                i++;
            }
            String num = s.substring(st, i);
            if (num.isEmpty()) {
                throw new IllegalArgumentException("Bad JSON at " + st);
            }
            if (num.contains(".") || num.contains("e") || num.contains("E")) {
                return Double.parseDouble(num);
            }
            long v = Long.parseLong(num);
            if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) {
                return (int) v;
            }
            return v;
        }

        private void expect(char c) {
            if (i >= s.length() || s.charAt(i) != c) {
                throw new IllegalArgumentException("Expected '" + c + "' in JSON at " + i);
            }
            i++;
        }

        private String string() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\\') {
                    char e = s.charAt(i++);
                    switch (e) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'u':
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                            i += 4;
                            break;
                        default: sb.append(e);
                    }
                } else {
                    sb.append(c);
                }
            }
        }

        static String write(Object o) {
            StringBuilder sb = new StringBuilder();
            write(o, sb);
            return sb.toString();
        }

        private static void write(Object o, StringBuilder sb) {
            if (o == null) {
                sb.append("null");
            } else if (o instanceof String) {
                quote((String) o, sb);
            } else if (o instanceof Number || o instanceof Boolean) {
                sb.append(o);
            } else if (o instanceof Map) {
                sb.append('{');
                boolean first = true;
                for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                    if (!first) {
                        sb.append(',');
                    }
                    first = false;
                    quote(String.valueOf(e.getKey()), sb);
                    sb.append(':');
                    write(e.getValue(), sb);
                }
                sb.append('}');
            } else if (o instanceof Iterable) {
                sb.append('[');
                boolean first = true;
                for (Object x : (Iterable<?>) o) {
                    if (!first) {
                        sb.append(',');
                    }
                    first = false;
                    write(x, sb);
                }
                sb.append(']');
            } else {
                quote(String.valueOf(o), sb);
            }
        }

        private static void quote(String str, StringBuilder sb) {
            sb.append('"');
            for (int k = 0; k < str.length(); k++) {
                char c = str.charAt(k);
                switch (c) {
                    case '"': sb.append("\\\""); break;
                    case '\\': sb.append("\\\\"); break;
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    default:
                        if (c < 0x20 || c == 0x2028 || c == 0x2029) {
                            sb.append(String.format("\\u%04x", (int) c));
                        } else {
                            sb.append(c);
                        }
                }
            }
            sb.append('"');
        }
    }
}
