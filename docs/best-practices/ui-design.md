<frontmatter>
  title: "Best Practices: UI Design"
</frontmatter>

# UI Design Best Practices

The goal is to make the user guide unnecessary.

## Be Forgiving

- Forgive extra whitespace and blank lines in input.
- Auto-correct where possible.
- Allow cancellation at any step where it makes sense.

## Be Descriptive

- Explain errors in detail, including how to correct them.
- After an action, describe what happened (e.g. after a bulk enroll, show how many students were added, modified, or unchanged).
- Keep users informed of background actions (e.g. when emails are being sent).
- Disable rather than hide unavailable functions so users know they exist.

## Minimise User Effort

User convenience is far more important than developer convenience.

- After submitting a form, take the user to the next logical page while showing feedback about the previous action.
- Prefer undo over asking for confirmation on every action.
- Use sensible defaults so users are not forced to fill every field.
- If only one option is available, select it by default.

## Use the User's Language

- Use terms from the user's domain, not technical terms.
- Use intent-based language rather than mechanism-based, e.g. "leave this course" instead of "delete".

## Be Consistent

- Maintain a neutral colour scheme across the site.
- Use Bootstrap CSS classes instead of custom CSS where possible.
- Use Bootstrap colour utility classes meaningfully (e.g. `warning` for user warnings).
- Use consistent naming for links, page names, and titles.
- Use consistent styles for components with the same behaviour.
