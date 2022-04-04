<frontmatter>
  title: "Getting Started: Documentation"
</frontmatter>

# MarkBind Documentation

This project uses [MarkBind](https://markbind.org/) for developer documentation.

## Quickstart

MarkBind is used to create a static site for developer documentation, and can parse markdown, git flavoured markdown and more. Generally, edits to documentation files in docs will be reflected on the website with no further action, due to auto-deployment by Github actions.

To preview changes to documentation on the markbind:

1. Install `npm` (version 7 or higher) and `Node.js` (version 12 or higher)
1. Navigate to the `/docs` folder in your fork of the TEAMMATES repository
1. Run `npm ci` to install the necessary tools to build documentation, such as MarkBind.
1. Preview the website with the command `npm serve` to view the site locally. The live preview in the should update automatically to reflect changes you make to the docs.

Editing the docs is similar to how you edit any Markdown file. For most changes, knowledge of MarkBind is sufficient. However, MarkBind also supports:

1. [Content reuse: reusing and including portions of documents in other documents](https://markbind.org/userGuide/reusingContents.html)
1. [Expandable panels](https://markbind.org/userGuide/components/presentation.html#panels)
1. [Support for PUML diagrams](https://markbind.org/userGuide/components/imagesAndDiagrams.html#diagrams)
1. [Additional text formatting](https://markbind.org/userGuide/markBindSyntaxOverview.html)

Read the [MarkBind user guide](https://markbind.org/userGuide) if you need more information.

## Adding pages to the developer guide

1. Create the page in the docs folder as a `.md` file.
1. Add content as desired.
    <box type="tip" light>

    You may want to include the following code (or a variant of it)

    ```markdown
    <frontmatter>
    title: "YOUR TITLE HERE"
    </frontmatter>
    ```

    The `<frontmatter>` block assigns the name of the file to title (allowing it to be searched by this name in the dev docs and changes the title of the html file).

    </box>
1. Add the page to the site navigation, by including the link at the appropriate location in <code>_markbind/layouts/default.md</code>.

## Deploying to Github pages

Note that deployment is done automatically upon pushing to master, due to the setup Github Action. See `dev-docs.yml` for more details, and [this section of the MarkBind user guide](https://markbind.org/userGuide/deployingTheSite.html).
