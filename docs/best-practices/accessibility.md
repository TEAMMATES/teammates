<frontmatter>
  title: "Best Practices: Accessibility"
</frontmatter>

# Accessibility Best Practices

TEAMMATES aims to be compliant to the [Web Content Accessibility Guidelines (WCAG)](https://www.w3.org/WAI/standards-guidelines/wcag/). On top of the full list of WCAG guidelines, we have compiled some general guidelines below.

## Content

* Language used should be plain and easy to understand. Avoid using complicated figures of speech.
* Content flow should be logical.
* Headings should be used in a logical sequence (e.g. descending order, no skipping levels) to split the page into sections, and not used purely for visual design.

## Images/Icons

* Images that convey meaning should have a descriptive `alt` attribute.
* Decorative images should have an empty `alt` attribute.
* Icons used in controls (e.g. buttons) should have a descriptive label if they convey meaning, and should be hidden from assistive technologies (e.g. `aria-hidden` attribute set to `true`) if they are purely descriptive.

## Keyboard

* All interactive elements (e.g. buttons, links, inputs) should be focusable by the keyboard (e.g. when tabbing).
* The keyboard focus/tabbing order should match the visual layout of the page.
* Focusable elements that are hidden from the current view should be removed from the focus order.

## Controls/Inputs

* All inputs should have a corresponding label.
* The contents of controls (e.g. buttons, links) and input labels should be descriptive.
* Custom controls/inputs should have an appropriate role assigned (e.g. dropdown, button).
