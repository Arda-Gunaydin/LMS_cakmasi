# VPL Lab – assignment format (for whoever writes new assignments)

The evaluation imitates the school's VPL exactly: the school runs a JUnit test class
(`Lab_TestTemplate`) through the JUnit console launcher. One **check** = one JUnit test
method, which may contain many assertions (assertAll). The grade is
`round(100 × passing tests / tests)`. The Compilation section shows the JUnit tree,
Yorumlar shows the `CSE201 - AUTOMATIC EVALUATION` report. Verified against the school:
the same student code gives a byte-identical Yorumlar report.

## Validate every assignment

```
java VplServer.java check <id> --solution <folder with a reference solution>
java VplServer.java check --all
```

It checks the format, the school-style description, that the starter compiles, that the tests use
reflection only, that the unfinished starter scores low and that the reference solution scores 100.
AI assistants: follow `AGENTS.md`.

## Folder

```
assignments/lab03-linkedlist/
  assignment.json          settings (below)
  description.html         assignment text, school style (see below)
  starter/                 requested files, e.g. Lab03.java
  provided/                read-only files shown in the editor and compiled with the student's code
  tests/Tests.java         hidden tests (main class "Tests"), written with lib/T.java
  tests/optional/*.java    extra classes compiled against the student's code one by one
                           (e.g. an unseen subclass); if one does not compile, T.hasClass is false
  tests/*                  other test resources, copied next to the code
```

A leading `_` in the folder name hides the assignment. Student code lives in `work/<id>/`.

## assignment.json

```json
{
  "title": "Lab03 Sandbox",
  "course": "CSE201 DATA STRUCTURES",
  "subtitle": "Lab 3: Linked Lists",
  "order": 3,
  "testClassName": "Lab_TestTemplate",
  "checkTimeoutMs": 3000,
  "evaluateTimeLimit": 90
}
```

Run starts the first class that has a `main` method.

## description.html (school style)

```html
<div class="vd">
<div class="vd-banner"><div class="vd-kicker">CSE201 Data Structures</div>
<div class="vd-title">Lab 3: Linked Lists</div></div>
<h2>Objective</h2><p>...</p>
<h3>Methods</h3>
<div class="vd-table"><table><thead><tr><th>Method</th><th>Return type</th><th>Required behavior</th></tr></thead>
<tbody><tr><td><code>add(T item)</code></td><td><code>boolean</code></td><td>...</td></tr></tbody></table></div>
<div class="vd-box"><h2>Before Submission</h2><ul><li>...</li></ul></div>
</div>
```

## tests/Tests.java

```java
public class Tests {
    public static void main(String[] args) {
        T.test("Catalog", "test_Catalog_add", "add", () -> {
            T.method("Catalog", "add", 1);            // hard: "No method named like add was found in Catalog"
            Object cat = T.make("Catalog");
            Object b = T.make("Book", "B1", "Title", "Author");
            T.expect("add(item) should return true for a new id", true, () -> T.call(cat, "add", b));
            T.expectThrows("add(null) should throw IllegalArgumentException",
                    IllegalArgumentException.class, () -> T.call(cat, "add", (Object) null));
        });
        T.done();   // runs every test in JUnit 4 default order (method-name hash), always last
    }
}
```

* `T.test(group, method, title, body)` – one check. `group` is the line in RESULTS BY METHOD
  (class names like `Book`, or `calculateFees`, or `(code rules)`); `method` is the JUnit
  method name shown in the tree (`test_<Group>_<what>`); `title` is shown for failing tests.
* Soft assertions (all run, each failure is one line in WHAT WENT WRONG):
  `expect(desc, expected, () -> actual)`, `expectNear`, `expectSame`, `expectNull`,
  `expectTrue(desc, bool | () -> bool)`, `expectFalse`, `expectThrows(desc, Ex.class, body)`,
  `expectNoThrow`, `expectSilent(desc, body)` (nothing may be printed).
* Messages follow JUnit: `desc expected:<1.5> but was:<1.0>`, strings as
  `expected:<[New Title]> but was:<[   ]>`, `desc, but it threw java.lang.X`
  (Yorumlar shows `desc   [threw X]`), `desc, but it returned without throwing`.
* Anything that throws outside an expect fails the whole test with one line
  (lookups: `No method named like add was found in Catalog`).
* Reflection helpers: `make`, `call`, `callStatic` (names are matched "like" the school does:
  exact, else ignoring case, else a small typo), `method`, `declared`, `fieldOf`, `field`,
  `isPrivate/isFinal/isAbstract`, `isInterface`, `isA`, `extendsDirectly`,
  `typeParameterBoundedBy`, `fieldsPrivate`, `array(elemClass, items...)`, `hasClass`.
* Source rules: `uses(file, word)`, `matches(file, regex)` (comments/strings ignored),
  `captureOut`, `runMain(cls, stdin)`.
* Wrap single array arguments: `T.callStatic("ArrayTools", "sum", (Object) new int[] {1, 2})`.
