<frontmatter>
  title: "Best Practices: UI Design"
</frontmatter>

# UI Design Best Practices

The goal is to make the user guide unnecessary.

## Be forgiving in processing input

* Forgive extra blank lines and extra white space.
* Auto-correct if possible.
* Allow 'cancel' at any step where it makes sense.

## Be descriptive

* Errors should be explained in as much details as possible. Also explain how to correct it.
* After an action, give a description of what happened, e.g. after a mass enroll operation, you can explain which students were added, which were modified, and how many remained the same.
* Keep user informed of what is happening, e.g. inform user about sending emails to users.
* Disable (rather than hide) unavailable functions so that users know they exist.

## Minimize work (to be done by the user)

User convenience is **far** more important than developer convenience, e.g.

* Submitting a page should take the user to the next logical page while showing feedback about the previous action at the same time.
* When possible, give an 'undo' instead of asking to confirm every action.
* Use defaults so that users are not forced to enter values for each field. This applies to sorting as well (i.e. sort by default using the most likely sorting order).
* If only one option is available, choose it by default.

## Take the user's point of view

* Use terms from user domain.
* Use terms that show 'user intent' rather than mechanism, e.g. 'leave this course' instead of 'delete'.

## Be consistent

* Use consistent naming guidelines for links, page names and page titles.
* Use consistent styles for components (e.g. buttons, links) that have the same behavior.
