<frontmatter>
  title: "Documentation"
</frontmatter>

# Documentation

This project uses [MarkBind](https://markbind.org/) for developer documentation. MarkBind is used to create a static site, and can parse markdown, GitHub Flavoured Markdown, and more.

All the commands in this document are assumed to be run from the `/docs` folder, unless specified otherwise.

## Installation

1. Install `Node.js` (minimum version 12).
1. Run `npm ci` to install the necessary tools to build documentation, including MarkBind.

Install the following additional dependencies required by MarkBind to generate [PlantUML](https://plantuml.com/) diagrams locally:

1. Install Java 8 or later.
1. Install [Graphviz](https://www.graphviz.org/download/) v2.38 (or later).

<box type="tip" light>

You can also use a globally installed MarkBind if you have one. Make sure to use version `3.*.*`.
</box>

## Quickstart

Run the following command to view the site locally:
```sh
npm run serve

# Alternative if you wish to use a globally installed MarkBind
markbind serve
```
The live preview will be available at `localhost:8080` by default and should update automatically to reflect changes you make to the docs. If you wish to use another port (e.g. `8090`), use the `-p` flag as follows:
```sh
npm run serve -- -p 8090
markbind serve -p 8090
```

Working with a MarkBind page is almost exactly the same as working with a standard Markdown page, with the following additional pointers:

1. You may want to add `<frontmatter>` code block at the top of the page. For example, setting `title` allows for the page to be titled as such instead of following the file name. Refer [here](https://markbind.org/userGuide/tweakingThePageStructure.html#front-matter) for more details.
   ```markdown
   <frontmatter>
     title: "YOUR TITLE HERE"
   </frontmatter>
   ```
1. If you are adding a new page and want to include it in the site navigation, you can do so by including the link at the appropriate location in <code>_markbind/layouts/default.md</code>.
1. You can take advantage of MarkBind's additional features such as:
   1. [Content reuse: reusing and including portions of documents in other documents](https://markbind.org/userGuide/reusingContents.html)
   1. [Expandable panels](https://markbind.org/userGuide/components/presentation.html#panels)
   1. [Support for PUML diagrams](https://markbind.org/userGuide/components/imagesAndDiagrams.html#diagrams)
   1. [Additional text formatting](https://markbind.org/userGuide/markBindSyntaxOverview.html)
  
You can refer to [MarkBind user guide](https://markbind.org/userGuide) for more information.

## Deploying to GitHub pages

Documentation is automatically deployed after each push to `master` branch, as configured in `dev-docs.yml`. For more details, refer [here](https://markbind.org/userGuide/deployingTheSite.html).
