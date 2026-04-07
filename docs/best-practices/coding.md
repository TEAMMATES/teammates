<frontmatter>
  title: "Best Practices: Coding"
</frontmatter>

# Coding Best Practices

## Quality

Maintainability is our top priority. Prefer simple over complex, and less over more.

## Style

The goal is to make the code read as if it were written by one person.

- Prefer standard approaches over special cases.
- Be consistent with existing code in naming and layout.
- Follow the coding style guides — most rules are enforced by static analysis, but not all.

## Refactoring

- Follow the [boy scout rule](https://medium.com/@biratkirat/step-8-the-boy-scout-rule-robert-c-martin-uncle-bob-9ac839778385) — always leave code cleaner than you found it.
- However, keep refactoring out of feature or bug fix PRs unless it is trivial. When in doubt, open a separate issue.

## Code Comments

- Use comments only when the code is not self-explanatory. Avoid redundant comments.
  - `@return` and `@param` tags can be omitted if they add no value.
- Write comments as explanations for the reader, not notes to yourself.

## Issue Tracking

- Use descriptive issue names with a clear scope. "Improve DevMan" is too vague; "Add issue naming conventions to DevMan" is better.
- One issue, one fix. Do not combine unrelated changes in a single PR — this makes it impossible to revert a specific fix without reverting others.
- Prefer small issues over large ones. Large issues take longer and have a higher risk of merge conflicts.

## Revision Control

Review your changeset before pushing to ensure all changes are intentional and nothing unrelated is included.
