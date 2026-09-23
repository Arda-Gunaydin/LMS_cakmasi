---
name: new-assignment
description: Create a new VPL Lab assignment (lab or postlab) from just a topic name, in the CSE201 course format, and validate it. Use when the student asks for an assignment, lab, postlab or "ödev" on any topic.
---

# New VPL Lab assignment

1. Read `AGENTS.md`, `ASSIGNMENT_FORMAT.md`, `lib/T.java` and `assignments/lab02-arrays/` (and
   `assignments/lab01-library/` if present).
2. Decide Lab or Postlab (AGENTS.md table) and the next number from the existing `order` values.
3. Create `assignments/<id>/`: `assignment.json`, `description.html` (school style), `starter/`,
   `provided/` (Postlab only), `tests/Tests.java` (15–25 `T.test` checks, reflection only, one `(code rules)` test).
4. Write a reference solution in `.scratch/<id>/` and run
   `java VplServer.java check <id> --solution .scratch/<id>` until `RESULT: PASS`.
5. Break the solution in two typical ways and confirm the grade drops; restore it.
6. Delete `.scratch/<id>/`. Reply in the student's language: title, what it practices, number of
   checks, "refresh the page". Never reveal the solution.
