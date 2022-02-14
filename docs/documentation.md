<frontmatter>
  pageNav: 2
  pageNavTitle: "Chapters of This Page"
</frontmatter>

# Markbind Documentation

This project uses [MarkBind](https://markbind.org/) for developer documentation. Follow [this tutorial](https://se-education.org/guides/tutorials/markbind.html) to get started with using MarkBind for updating project documentation.

## Quickstart

Markbind is used to create a static site for developer documentation, and can parse markdown, git flavoured markdown and more. Generally, edits to documentation files in docs will be reflected on the website with no further action, due to auto-deployment by Github actions.

To preview changes to documentation on the markbind, follow the above [tutorial](https://se-education.org/guides/tutorials/markbind.html#installation) to install Markbind. Navigate to the `/docs` folder and serve with `markbind serve` to view the site locally.

To add new pages to the site navigation, include the link at the appropriate location in `_markbind/layouts/default.md`.
