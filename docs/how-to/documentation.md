<frontmatter>
  title: "Documentation"
</frontmatter>

# Documentation

TEAMMATES uses [MarkBind](https://markbind.org/) to build the developer documentation site.

## Installation

Run `npm ci` to install MarkBind and other dependencies.

## Previewing Locally

```sh
npm run serve
```

The site will be available at `http://localhost:8080` and updates automatically as you make changes. To use a different port:

```sh
npm run serve -- -p 8090
```

## Writing Documentation

MarkBind pages work like standard Markdown with a few additions:

- **Front matter**: Add a `<frontmatter>` block at the top of each page to set the page title:

```markdown
<frontmatter>
  title: "YOUR TITLE HERE"
</frontmatter>
```

- **Navigation**: To add a new page to the sidebar, include it in `_markbind/layouts/default.md`.
- **MarkBind features**: You can use [panels](https://markbind.org/userGuide/components/presentation.html#panels), [PUML diagrams](https://markbind.org/userGuide/components/imagesAndDiagrams.html#diagrams), [content reuse](https://markbind.org/userGuide/reusingContents.html), and [additional formatting](https://markbind.org/userGuide/markBindSyntaxOverview.html).

Refer to the [MarkBind user guide](https://markbind.org/userGuide) for full documentation.

## Deployment

Documentation is automatically deployed to GitHub Pages on every push to `master`, as configured in `.github/workflows/dev-docs.yml`.
