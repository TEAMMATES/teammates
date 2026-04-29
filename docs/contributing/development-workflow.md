<frontmatter>
  title: "Development Workflow"
  pageNav: 3
</frontmatter>

# Development Workflow

This document describes the workflow for contributing code to TEAMMATES. It is assumed that you have already [set up your development environment](../getting-started.md) and are familiar with the [Development Guide](development-guide.md).

## Step 1: Pick an Issue

Browse the [issue tracker](https://github.com/TEAMMATES/teammates/issues) and pick an issue to work on. A few things to note:

- You do not need to be assigned to an issue to work on it. We only assign issues to core team members.
- Indicate your interest by commenting on the issue thread to avoid duplicated effort.
- Avoid working on issues that are:
  - Already assigned
  - Labelled as on hold or core team only
  - Have open PRs
- You may discuss alternative solutions on the issue thread before starting work. This reduces the chance of a rejected fix.

### Issue Labels

The following labels can help you find suitable issues to work on.

| Label              | Purpose                                                           |
| ------------------ | ----------------------------------------------------------------- |
| `good first issue` | Good starting point for first-time contributors.                  |
| `help wanted`      | Moderate difficulty, localized scope.                             |
| `committers only`  | Difficult or broad in scope, not open to first-time contributors. |
| `core team only`   | Reserved for core team, not open to external contributions.       |
| `p.*`              | Priority labels, set by maintainers.                              |
| `a-*`              | Aspect labels, indicating the area of the codebase affected.      |

## Step 2: Create Branch

Start from an up-to-date `master` branch and create a new branch for your fix:

```sh
git checkout master
git pull
git checkout -b {your-branch-name}
```

For example: `3942-remove-unnecessary-println`.

- Do not combine fixes for multiple unrelated issues in one branch.
- Your `master` branch should never be ahead of the main repository's `master` branch.

## Step 3: Fix and Commit

Make your changes and commit them:

```sh
git add -A
git commit
```

A few things to keep in mind:

- Use meaningful commit messages, e.g. `Add tests for the truncate method`. [This guide](http://chris.beams.io/posts/git-commit/) is a good reference.
- Sync with the main repository frequently to avoid large merge conflicts:

```sh
git checkout master
git pull upstream master
git checkout {your-branch-name}
git merge master
```

Before submitting, make sure:

- Code passes static analysis:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew lint --continue
npm run format
npm run lint
```

</tab>
<tab header="Windows">

```sh
gradlew.bat lint --continue
npm run format
npm run lint
```

</tab>
</tabs>

- All affected tests are passing.
- No unrelated changes are introduced.
- No unrelated changes, new code has tests, and documentation is updated where necessary.

## Step 4: Submit a PR

[Open a pull request](https://help.github.com/articles/creating-a-pull-request/) with the following:

- **Base branch**: `master`
- **Title**: `[#issue-number] Issue title`, e.g. `[#3942] Remove unnecessary System.out.printlns from Java files`
- **Description**: `Fixes #3942` — this automatically closes the issue when the PR is merged
- **Allow edits from maintainers**: enabled

If the PR only partially addresses the issue, use `Part of #3942` in the description instead.

## Step 5: Follow Up

Once your PR is open:

- Aim to complete it within 2 weeks. Inactive PRs may be closed.
- Ensure all GitHub Actions checks pass.
- Push updates to the same branch when asked to make changes, and comment when ready for re-review.
