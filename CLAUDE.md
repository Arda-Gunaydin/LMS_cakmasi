# VPL Lab

@AGENTS.md

When a student names a topic ("X ödevi hazırla", "make an assignment about X"), follow AGENTS.md
completely: create `assignments/<id>/`, validate with
`java VplServer.java check <id> --solution .scratch/<id>` until `RESULT: PASS`, delete `.scratch/<id>`,
and reply briefly in the student's language. Do not ask how the assignment should look.
