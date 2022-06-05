<img src="src/web/assets/images/teammateslogo-black.png" width="400">

## TEAMMATES Developer Web Site

[![GitHub Actions Build Status Component Tests](https://github.com/TEAMMATES/teammates/workflows/Component%20Tests/badge.svg)](https://github.com/TEAMMATES/teammates/actions)
[![GitHub Actions Build Status E2E Tests](https://github.com/TEAMMATES/teammates/workflows/E2E%20Tests/badge.svg)](https://github.com/TEAMMATES/teammates/actions)
[![Codecov Coverage Status](https://codecov.io/gh/TEAMMATES/teammates/branch/master/graph/badge.svg)](https://codecov.io/gh/TEAMMATES/teammates)
[![License](https://img.shields.io/badge/license-GPLv2-blue.svg)](LICENSE)

TEAMMATES is a free online tool for managing peer evaluations and other feedback paths of your students.
It is provided as a cloud-based service for educators/students and is currently used by hundreds of universities across the world.

<img src="src/web/assets/images/overview.png" width="600">

This is the developer web site for TEAMMATES. **Click [here](http://teammatesv4.appspot.com/) to go to the TEAMMATES product website.**

## Documentation

### Documentation for:
* [**Developers** :book:](https://teammates.github.io/teammates)
* [**Instructors** :book:](https://teammatesv4.appspot.com/web/front/help/instructor)
* [**Students** :book:](https://teammatesv4.appspot.com/web/front/help/student)

### Other Documentation:
* [Version History](https://github.com/TEAMMATES/teammates/milestones?direction=desc&sort=due_date&state=closed)
* [Project Stats](https://www.openhub.net/p/teammatesonline)
* [Design](https://github.com/TEAMMATES/teammates/blob/master/docs/design.md)

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

## Contributing

We welcome contributions from developers, especially students. Here are some resources on how to get started:

### Contribution Guidelines

* [**Contributor Orientation Guide**](https://teammates.github.io/teammates/contributing-doc.html): This document describes what you need to know/do to become a contributor.
* [**Project ideas page**](https://github.com/TEAMMATES/teammates/wiki): These are for those who would like to do a relatively bigger projects with TEAMMATES (e.g. summer internships).
* [**Report an issue**](https://github.com/TEAMMATES/teammates/issues/new): Report issues here by posting a message to our issues tracker.

### Code of Conduct

* [**Contributor Covenant Code of Conduct**](https://github.com/TEAMMATES/teammates/blob/master/.github/CODE_OF_CONDUCT.md)

### Best Practices

* [**UI Design**](https://teammates.github.io/teammates/best-practices/ui-design.html)
* [**Coding**](https://github.com/TEAMMATES/teammates/blob/master/docs/best-practices/coding.md)
* [**Testing**](https://github.com/TEAMMATES/teammates/blob/master/docs/best-practices/testing.md)
* [**Data Migration**](https://github.com/TEAMMATES/teammates/blob/master/docs/best-practices/data-migration.md)

## Acknowledgements

TEAMMATES team wishes to thank the following invaluable contributions:
* [**School of Computing, National University of Singapore (NUS)**](http://www.comp.nus.edu.sg), for providing us with the infrastructure support to run the project.
* [**Centre for Development of Teaching and Learning (CDTL)**](https://nus.edu.sg/cdtl) of NUS, for supporting us with several *Teaching Enhancement Grants* over the years.
* **Learning Innovation Fund-Technology (LIF-T)** initiative of NUS, for funding us for the 2015-2018 period.
* **Google Summer of Code** Program, for including TEAMMATES as a mentor organization in *GSoC2014*, *GSoC2015*, *GSoC2016*, *GSoC2017* and *GSoC2018* editions.
* **Facebook Open Academy** Program, for including TEAMMATES as a mentor organization in FBOA 2016.
* **Jet Brains**, for the [Intellij IDEA](https://www.jetbrains.com/idea/) licences
* <img src="src/web/assets/images/yklogo.png" width="100"> [**YourKit LLC**](https://www.yourkit.com), for providing us with free licenses for the [YourKit Java Profiler](https://www.yourkit.com/java/profiler) (an industry leading profiler tool for Java applications).
* <img src="src/web/assets/images/saucelabs.png" width="100"> [**SauceLabs**](https://saucelabs.com), for providing us with a free [Open Sauce account](https://saucelabs.com/open-source) for cross-browser testing.

## Contacting us

The best way to contact us is to [post a message in our issue tracker](https://github.com/TEAMMATES/teammates/issues/new). Our issue tracker doubles as a discussion forum. You can use it for things like asking questions about the project or requesting technical help.

Alternatively (less preferred), you can email us at **teammates@comp.nus.edu.sg**.
