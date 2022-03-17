<frontmatter>
  title: "Getting Started: Documentation"
</frontmatter>

# MarkBind Documentation

This project uses [MarkBind](https://markbind.org/) for developer documentation. Follow [this tutorial](https://markbind.org/userGuide/gettingStarted.html) to get started with using MarkBind for updating project documentation.

## Quickstart

MarkBind is used to create a static site for developer documentation, and can parse markdown, git flavoured markdown and more. Generally, edits to documentation files in docs will be reflected on the website with no further action, due to auto-deployment by Github actions.

To preview changes to documentation on the markbind, follow the above [tutorial](https://markbind.org/userGuide/gettingStarted.html) to install Markbind.

1. Install `npm`
1. Install MarkBind with the command `npm install -g markbind-cli`
1. Navigate to the `/docs` folder in your fork of the Teammates repository
1. Preview the website with the command `markbind serve` to view the site locally. The live preview in the should update automatically to reflect changes you make to the docs.

Editing the docs is similar to how you edit any Markdown file. For most changes, knowledge of MarkBind is sufficient. However, MarkBind also supports:

1. [Content reuse: reusing and including portions of documents in other documents](https://markbind.org/userGuide/reusingContents.html)
1. [Expandable panels](https://markbind.org/userGuide/components/presentation.html#panels)
1. [Support for PUML diagrams](https://markbind.org/userGuide/components/imagesAndDiagrams.html#diagrams)
1. [Additional text formatting](https://markbind.org/userGuide/markBindSyntaxOverview.html)

Read the [MarkBind user guide](https://markbind.org/userGuide/components/imagesAndDiagrams.html#diagrams) if you need more information.

## Adding pages to the developer guide

1. Create the page in the docs folder as a `.md` file.
1. Add content as desired.
    <box type="tip" light>

    You may want to include the following code (or a variant of it)

    ```markdown
    {% raw %}{% set title= "your title here" %}{% endraw %}
    <span id="title" class="d-none">YOUR TITLE HERE</span>

    <frontmatter>
    title: "YOUR TITLE HERE"
    pageNav: 3
    pageNavTitle: "Chapters of This Page"
    </frontmatter>
    ```

    The code uses Nunjucks variables to assign a `title`, which is replaced with the value at all instances of `{{ title }}`. This allows the page to have a custom title and to appear by this title in the search, instead of using the file name.

    The `<frontmatter>` block assigns the name of the file to title (allowing it to be searched by this name in the dev docs), and creates a page navigation titled _Chapters of This Page_ based on the headers, indexing the headers `3` levels deep.

    </box>
1. Add the page to the site navigation, by including the link at the appropriate location in <code>_markbind/layouts/default.md</code>.

## Deploying to Github pages

Note that deployment is done automatically upon pushing to master, due to the setup Github Action. See `deploy.yml` for more details, and [this section of the MarkBind user guide](https://markbind.org/userGuide/deployingTheSite.html).