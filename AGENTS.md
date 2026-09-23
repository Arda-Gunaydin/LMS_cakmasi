# Instructions for AI assistants working in this repository

This repository is **VPL Lab**: a local copy of the Moodle VPL editor used in a university
course (CSE201 Data Structures, Java). Students open it in the browser, solve an assignment,
and press **Evaluate** to get an automatic grade in exactly the format the course uses.

Students will talk to you in short sentences, usually in Turkish, and usually only name a topic:

> "LinkedList ödevi hazırla" · "stack konusunda bir lab yap" · "recursion postlab'ı hazırla"
> "make an assignment about hash maps"

**That sentence is the whole specification.** Do not ask the student how the assignment should
look, how to grade it, or where to put files — everything is defined here. Only ask a question
if the topic itself is unclear (for example a single letter). Then do the full workflow below
without stopping, and answer in the student's language (Turkish if they wrote Turkish).

## What you produce

A new folder `assignments/<id>/` that matches the existing ones:

```
assignments/<id>/
  assignment.json        title, course, subtitle, order
  description.html       the assignment text in the school's style (English)
  starter/               the file(s) the student edits
  provided/              (Postlab only) read-only files: the application that uses the student's code
  tests/Tests.java       hidden tests, written only with lib/T.java
  tests/optional/*.java  (optional) extra classes compiled against the student's code, e.g. an unseen subclass
```

Read these before writing anything, every time:

1. `ASSIGNMENT_FORMAT.md` — the folder format and the `T` test API.
2. `lib/T.java` — the only API the tests may use (reflection helpers + soft assertions).
3. `assignments/lab02-arrays/` — a complete, validated example (description, starter, tests).
4. If it exists locally, `assignments/lab01-library/` — an OOP example rebuilt from the real course.

Never edit anything under `work/` (the student's own code), other assignments, `VplServer.java`,
`lib/T.java` or `web/` unless the student explicitly asks for that.

## Choosing the kind of assignment

| Student says | Make | Starter file | Size |
|---|---|---|---|
| "ödev", "lab", "assignment", a topic only | **Lab** | `starter/LabNN.java` with every required declaration and bodies that `throw new UnsupportedOperationException("Not implemented");`, plus a working `main()` demo (below) | 6–10 methods or 3–5 small classes, 15–22 checks |
| "postlab", "post-lab", "homework app", "uygulamalı" | **Postlab** | `starter/<Name>.java`, same stub pattern, plus `provided/` (below) | an app scenario, 18–25 checks |

`NN` is the next free two-digit number (look at the `order` values of the existing assignments;
`order` = NN). The folder id is `labNN-<topic-in-kebab-case>` or `postlabNN-<topic>`.

**A Lab's starter is never a bare empty class.** Every method and constructor the description
requires is already declared, with a body that only throws
`new UnsupportedOperationException("Not implemented")`; the student fills in the bodies, never
adds signatures. The one file also ends with a working `public static void main(String[] args)`
that calls the student's own methods with example values and prints the results (wrap it in
`try { ... } catch (UnsupportedOperationException e) { print which method stopped it }`, the way
`assignments/lab02-arrays/starter/Lab02.java` does — read that file once as the exact template).
This is what makes the Run button useful before anything is implemented, and it is how the real
course ships its labs; do not fall back to `public class LabNN {\n    \n}` for a normal lab. (The
one exception is a literal open-ended "sandbox" assignment with no fixed methods at all, which a
student would ask for by name, not by naming a topic.) Static-method labs describe each method in
a `<h3>` + table like `assignments/lab02-arrays/description.html`'s Part 1. OOP labs (interfaces,
abstract classes, generics) describe each type with Fields / Constructor / Methods tables, the way
`assignments/lab03-stack/` (ArrayStack) and `assignments/lab04-bst/` (BST) do.

A **Postlab** is an application story (a shop, a bill splitter, a playlist, a parking garage…).
`provided/` holds a complete, runnable application (`<Name>App.java` with `main`: a Swing window
or a text menu) that calls the student's class, plus any data files it reads. The description has
Files, Running the Application and Example sections. The provided app must compile against the
starter stubs and must not crash at start-up when a stub throws (catch the exception and show a
message, like the course's ShopApp does).

## Level and content

* CSE201 Data Structures, second-year Java. Topics the course covers: arrays, ArrayList and
  generics, singly/doubly linked lists, stacks, queues, recursion, sorting and searching,
  complexity, iterators, binary trees, BSTs, heaps/priority queues, hashing/hash maps, graphs.
* For a data structure topic, the student **implements the structure themselves** (nodes, arrays,
  pointers) and the rules forbid the matching `java.util` class. Say exactly what is allowed.
* Every method has an exact contract: return values, `null` handling, empty input, invalid input
  → `IllegalArgumentException` (or `IndexOutOfBoundsException` / `NoSuchElementException` /
  `IllegalStateException` where natural), input arrays/lists left unchanged unless "in place".
* No printing from required methods. `main` is a free playground and is not graded.
* Money, if any, is whole kuruş in `long` (never `double`).

## description.html — the school's style

English, wrapped exactly like this (CSS classes are provided by the app; do not add `<style>`/`<script>`):

```html
<div class="vd">
<div class="vd-banner">
<div class="vd-kicker">CSE201 Data Structures</div>
<div class="vd-title">Lab 3: Singly Linked Lists</div>
</div>

<div class="vd-sigs">                                  <!-- Labs with static methods -->
<div class="vd-sigs-title">The 6 methods you have to write</div>
<div class="vd-sigs-list">
<div>public static int size(Node head)</div>
...
</div>
</div>

<h2>Objective</h2>
<p>...</p><ul><li>...</li></ul>

<h2>The Grading Contract</h2>                          <!-- Labs: copy this section as is -->
<p>The tests never read what your program prints. They call your methods with chosen arguments and compare the returned values with the specification.</p>
<p>Because of that:</p>
<ul>
<li>Method names, parameter types, parameter order, and return types must match this document exactly.</li>
<li>Return values, not printed text, are graded.</li>
<li>Invalid inputs must produce the stated result, which is sometimes a value and sometimes an exception.</li>
<li>A file that does not compile earns no points, so save and evaluate early rather than in the last minute.</li>
</ul>

<h2>Files</h2> ... <h2>Running the Application</h2> ...   <!-- Postlabs only -->

<h2>General Rules</h2>
<ul><li>Complete all work in the provided <code>Lab03.java</code> file. Only <code>Lab03</code> is declared <code>public</code>.</li>
<li>Do not add a package declaration.</li> ... </ul>

<h2>Part 1: ...</h2>
<h3>methodName</h3>
<div class="vd-sig">public static int methodName(int[] a, int x)</div>
<p>Contract in plain sentences.</p>
<ul><li>edge-case rule</li></ul>
<div class="vd-table"><table>
<thead><tr><th>Call</th><th>Result</th></tr></thead>
<tbody><tr><td><code>methodName([1, 2], 3)</code></td><td><code>0</code></td></tr></tbody>
</table></div>

<!-- OOP parts use: <h3>Fields</h3> table (Field | Type | Requirements), <h3>Constructor</h3>,
     <h3>Methods</h3> table (Method | Return type | Required behavior), <h3>... Rules</h3> list -->

<h2>Example</h2>                                       <!-- worked example with exact numbers -->
<h2>Required Edge Cases</h2>
<p>Your implementation must correctly handle:</p><ul>...</ul>
<h2>Suggested Order</h2><ol>...</ol>

<div class="vd-box">
<h2>Before Submission</h2>
<p>Confirm that:</p>
<ul><li>The file is named <code>Lab03.java</code> and compiles.</li> ... </ul>
</div>
</div>
```

Write like the course: short, precise sentences; `code` for every identifier and value;
concrete examples with exact results; every rule the tests check must appear in the text.

## assignment.json

```json
{
  "title": "Lab03",
  "course": "CSE201 DATA STRUCTURES",
  "subtitle": "Lab 3: Singly Linked Lists",
  "topic": "linked lists",
  "order": 3,
  "testClassName": "Lab_TestTemplate"
}
```

Postlabs use `"title": "Postlab03"`.

## tests/Tests.java — how the grade is computed

The course grades with JUnit: one **check** = one test method that may contain many assertions.
`grade = round(100 × passing tests / tests)`. `T` reproduces this exactly. Rules:

* Tests talk to student code **only** through `T` (`T.make`, `T.call`, `T.callStatic`,
  `T.method`, `T.fieldOf`, …). Never write `new Lab03()` or a student type name in Java code:
  the starter of a Lab is an empty class, and the tests must compile without any student code
  (the checker enforces this).
* One `T.test(group, method, title, body)` per behaviour, 15–25 of them.
  * `group`: the class name for OOP (`Book`, `Catalog`), the method name for static labs
    (`countOccurrences`), and `(code rules)` for the rules check. The report shows one line per group.
  * `method`: `test_<Group>_<aspect>` (e.g. `test_LinkedList_removeFirst`), unique.
  * `title`: short, shown when the test fails ("removeFirst and removeLast").
* Inside a test use soft assertions, each with a full English sentence that states the expected
  result, like the course:
  `T.expect("indexOf(list, 7) should be 2", 2, () -> T.callStatic("Lab03", "indexOf", head, 7));`
  `T.expectThrows("get(-1) should throw IndexOutOfBoundsException", IndexOutOfBoundsException.class, () -> ...);`
  `T.expectTrue("Node.next must be a private field", () -> T.isPrivate(T.fieldOf("Node", "next")));`
* Put a lookup that the rest of the test depends on at the top, outside any expect
  (e.g. `T.method("MyList", "add", 1);`) so a missing method fails the test with one line.
* Always include one `(code rules)` test: `T.expectSilent(...)` around calls of the required
  methods (no printing) plus the forbidden-construct checks (`T.uses("Lab03.java", "ArrayList")`,
  `T.matches(...)`), one sentence per rule.
* Test the edge cases listed in the description: null, empty, one element, duplicates, negative,
  boundaries, invalid input, "input unchanged", "returns a new object".
* Wrap single array arguments: `T.callStatic("Lab03", "sum", (Object) new int[] {1, 2})`.
* End `main` with `T.done();`.

## Mandatory validation (do not skip)

1. Write a complete reference solution in `.scratch/<id>/` (this folder is git-ignored and hidden
   from the app). Use the same file names as `starter/`.
2. Run, from the repository root:

   ```
   java VplServer.java check <id> --solution .scratch/<id>
   ```

3. It must print `RESULT: PASS`: description format, compiling starter, reflection-only tests,
   starter scoring ≤ 40, reference solution scoring 100. If it fails, fix the assignment (or your
   solution, if the tests are right) and run it again until it passes.
4. Also make sure the tests are strict: temporarily break your solution in two typical ways
   (off-by-one, missing null check, wrong exception) and run the check again; the grade must drop.
   Restore it afterwards.
5. Delete `.scratch/<id>/` when done. Never put a solution inside `assignments/`, never show the
   solution in chat unless the student asks for it after trying.

If `java` is not on the PATH, use the JDK the student uses (for example
`~/Library/Java/JavaVirtualMachines/*/Contents/Home/bin/java` on macOS, or `/usr/libexec/java_home`).

## When you are done

Tell the student, in their language, in a few lines: the assignment's title, what it practices,
how many checks it has, and that they only need to refresh the page (the server does not need a
restart). Do not explain how to solve it.

## Other requests

* "Daha zor / daha kolay yap": change the same assignment (more/fewer parts and checks), then validate again.
* "Testler yanlış" / a check seems wrong: reread the description, fix the test or the text so they
  agree, validate again. Never weaken a test just so the student's code passes.
* "İpucu ver": give a hint about the failing check from the Yorumlar report, not the full code.
* Questions about the app itself: see `README.md`.
