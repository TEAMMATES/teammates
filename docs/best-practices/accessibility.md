<frontmatter>
  title: "Best Practices: Accessibility"
</frontmatter>

# Accessibility Best Practices

TEAMMATES aims to comply with the [Web Content Accessibility Guidelines (WCAG)](https://www.w3.org/WAI/standards-guidelines/wcag/).

## Content

- Use plain, easy-to-understand language.
- Ensure content flows logically.
- Use headings in a logical sequence (no skipping levels) to structure the page, not for visual styling.

## Images & Icons

- Meaningful images should have a descriptive `alt` attribute.
- Decorative images should have an empty `alt` attribute.
- Icons in controls should have a descriptive label if they convey meaning, or `aria-hidden="true"` if purely decorative.

## Keyboard

- All interactive elements (buttons, links, inputs) should be keyboard-focusable.
- Tab order should match the visual layout of the page.
- Elements hidden from the current view should be removed from the focus order.

## Controls & Inputs

- All inputs should have a corresponding label.
- Button and link text should be descriptive.
- Custom controls should have an appropriate ARIA role assigned.
