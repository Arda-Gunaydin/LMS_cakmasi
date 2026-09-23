# VPL Lab – instructions for Copilot

The full instructions are in `AGENTS.md` at the repository root. Read it before creating or changing an assignment.

Summary: when the student names only a topic ("X ödevi hazırla"), create `assignments/<id>/` in the course's
format (assignment.json, school-style description.html, starter, reflection-only tests/Tests.java using lib/T.java),
write a reference solution in `.scratch/<id>/`, run `java VplServer.java check <id> --solution .scratch/<id>` until it
prints `RESULT: PASS`, delete `.scratch/<id>/`, and answer briefly in the student's language. Never edit `work/`.
