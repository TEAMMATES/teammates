<frontmatter>
  title: "Issues"
</frontmatter>

# Issue tracker

This document describes how the issue tracker is used by the TEAMMATES project.

## Issue lifecycle

<img src="images/IssueLifecycle.png" width="600">

Given above is an illustration of the issue lifecycle.
Colors indicate which roles are involved in which states/transitions.

## Issue labels

This portion will only describe the purpose of each *label group* briefly.
The full description of each individual label can be viewed under the [labels page](https://github.com/TEAMMATES/teammates/labels).

Grouped Labels

* **Status (`s.*`)**: Classifies PRs based on **status**
  * No `s.*` label: PR is yet to be triaged
* **Category (`c.*`)**: Classifies issues and PRs based on **type of work done**
* **Priority (`p.*`)**: Classifies issues based on **importance**, as determined by the project maintainers
* **Aspect (`a-*`)**: Classifies issues based on the **aspect**
  * No `a-*` label: no specific aspect tackled, usually the case for enhancements or new features
* **Technology (`t-*`)**: Classifies issues based on the **technology/tool stack** involved
  * No `t-*` label: usually documentation update, or mixture of many languages
  
Standalone Labels

* `enhancement`: Indicates new feature requests that have been accepted
* `good first issue`: Indicates a good issue for first-time contributors
* `help wanted`: Issues that should be tackled by project contributors
* `committers only`: Issues that should only be tackled by committers
