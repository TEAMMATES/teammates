<frontmatter>
  title: "Best Practices: Coding"
</frontmatter>

# Coding Best Practices

## Quality

Maintainability is our top priority. Less is more. Prefer simple over complex.

## Style

The goal is to make the code as if it was written by one person.

* Prefer standard way of doing something over creating special cases.
* Try to be consistent with the existing code in naming, layout etc.
* Follow coding style guides as much as possible. Most of the static analysis tools will enforce those styles, but not all.

## Refactoring

* Follow the [boy scout rule](https://medium.com/@biratkirat/step-8-the-boy-scout-rule-robert-c-martin-uncle-bob-9ac839778385) (i.e. always leave the campground cleaner than you found it). Whenever you touch some code, e.g. to fix a bug, try to do some clean up of that code as well.
  But do the extra refactoring as a separate issue. One issue should fix only one thing.

## Code Comments

* Use comments ONLY when the code is not-self explanatory. Avoid redundant comments. Even comment elements such as `@return` and `@param` can be omitted if they do not add value.
* Comments should be well-written. They should not be written as 'note to self' of the developer, but as explanations to help the reader.
* Write header comments for all classes.
* Write header comments for all non-trivial public methods.

## User-visible text

Make sure the terminology used is understandable to users.

## Issue tracking

* Everything you do for the project, even non-coding tasks, should be recorded in the issue tracker.
* Use a descriptive issue name with a well-defined scope. `Improve DevMan` is not good because the scope is not clear (improve in what way?). `Add issue name convention to the DevMan` is a better name.
* All ongoing work should be reflected in the tracker correctly. The issue tracker should clearly show which issues are being handled at the moment, which are ready for review, etc. Use status labels (`s.*`) appropriately.
* Resist the urge to do more than one task under one issue/PR unless they are tightly coupled and one cannot be done without the other. Specifically, do not do refactorings unrelated to the fix. Instead, create a new issue and do as a separate PR.

  Reason: Occasionally we have to 'undo' fixes done previously, e.g. when we discover latent regressions introduced by that fix. Therefore, we should be able to undo any specific fix by reverting the merge commit of the relevant branch. If one branch merge brings in multiple fixes, we cannot undo one without the other.
* Small issues are better than big ones. Big issues take longer to finish and have a higher risk of merge conflicts.

## Revision control

Go through your change set before pushing it. Ensure all changes are as intended.

## Security

* When coding the UI (web pages), any user-entered value (e.g. course name) should be sanitized before displaying it back. This is to minimize cross-site scripting.
