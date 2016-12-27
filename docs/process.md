# Development Workflow

* [Overview](#overview)
* [Fixing issues](#fixing-issues)
* [Reviewing a PR](#reviewing-a-pr)
* [Merging a PR](#merging-a-pr)
* [Making a release](#making-a-release)
* [Making a hot patch](#making-a-hot-patch)

## Overview

<img src="images/workflow.png" width="600">

* This workflow is an adaptation of the [GitHub flow](https://guides.github.com/introduction/flow/).
* If you need any help regarding the workflow, please [post a new issue in our issue tracker](https://github.com/TEAMMATES/teammates/issues/new).
* It is assumed that the development environment has been correctly set up. If this step has not been completed, refer to [this document](settingUp.md).<br>
  You are also encouraged to be reasonably familiar with how to [work with your own local copy of TEAMMATES](development.md).

The following are the roles involved:
* **Dev**: fixing issues
* **Reviewer**: reviewing pull requests (PRs); usually a core team member
* **Release Lead (RL)**: release management; usually one of the project leads
* **Project Manager (PM)**: general project coordination, deploying to the live server, approving PRs

> *Roles* are related to the development process and they are different from *Positions*, which relate to the organization structure of the TEAMMATES developer community.

<img src="images/IssueLifecycle.png" width="600">

Given above is an illustration of the issue lifecycle.
Colors indicate which roles are involved in which states/transitions.

## Fixing issues

**Role: Dev**

This instruction set will use the issue `Remove unnecessary System.out.printlns from Java files #3942` as an example.

### Step 1: Pick an issue to fix

Our issue tracker contains bug reports, feature requests, as well as suggestions for enhancements.
You are free to work on any of the issues listed there.

* If you are a contributor, there is no need to get the issue assigned to you.
* If you are a core team member, assign the issue to yourself and assign it a milestone.
  You are expected to open a PR for this issue within a week; inactivity for a longer time may result in the issue being un-assigned so that someone else can work on it.
* While not required, the following gestures are appreciated:
  * Indicating your interest in working on any particular issue by commenting on the issue thread itself.
  * Refraining from working on issues that are assigned to someone else or have open PRs.
* (Optional) You can discuss, via the issue tracker, the alternative solutions before choosing one to implement.
  Such a discussion reduces the chance of a rejected fix or a misunderstood issue.

Here are the labels that may help you in choosing which issue to fix:

**Category**: classifies the issue based on its type.

* `c.Bug`: a bug report
* `c.Enhancement`: an enhancement to an existing functionality (not big enough to be considered as a user story)
* `c.Story`: a user story, e.g. a feature request
* `c.Epic`: a feature that is worth many user stories
* `c.Task`: other work items such as updating documentation
* `c.Release`: release-related issues; this is reserved for core team members working on managing release
* `c.Message`: a means of communication with the dev team, e.g. help requests; while this is not an issue to be worked on, you are welcome to chip in your opinions

**Difficulty Level**: classifies the issue based on its difficulty level.

* `d.FirstTimers`: easy; to do as the first issue for new developers (one developer should not do more than one of these)
* `d.Contributors`: moderate difficulty, small localized change; suitable for contributors (new developers who are not first timers)
* `d.Committers`: more difficult issues that are better left for committers or more senior developers
* No `d.*` label: variable difficulty level

**Aspect**: classifies the issue based on the non-functional aspect it tackles.

* `a-AccessControl`: controlling access to user groups, authentication, privacy, anonymity
* `a-CodeQuality`: refactorings that are mainly to improve code/design quality
* `a-Concurrency`: things related to concurrent access, session control
* `a-DevOps`: CI, release management, version control, dev docs
* `a-Docs`: website, user docs
* `a-FaultTolerance`: resilience to user errors, environmental problems
* `a-Performance`: speed of operation
* `a-Persistence`: database layer, GAE datastore
* `a-Scalability`: related to behavior at increasing loads
* `a-Security`: protection from security threats
* `a-Testing`: testing efficiency and robustness (as opposed to testing a specific feature)
* `a-UIX`: User Interface, User eXperience, responsiveness

**Feature**: classifies the issue based on the functional aspect it tackles.

* `f-Admin`: features used by system administrators
* `f-Comments`: comments
* `f-Courses`: courses, Instructors, Students, Home page
* `f-Email`: code related to sending emails
* `f-Profiles`: user profiles
* `f-Results`: session results, moderation, download
* `f-Search`: search
* `f-Submissions`: session creation, editing, submissions

**Tech**: classifies the issue based on the technology it involves.

* `t-CI`: Gradle, static analysis, build script, CI
* `t-CSS`: CSS, Bootstrap
* `t-GAE`: Google App Engine-related technologies
* `t-HTML`: HTML, Browsers
* `t-JS`: Javascript, JQuery
* `t-JSTL`: JSTL, JSP, Servlets
* No `t-*` label: usually only Java

### <a name="creating-a-branch"></a> Step 2: Start clean from a new branch

1. Start off from your `master` branch and make sure it is up-to-date with the latest version of the committer repo's `master` branch.
   ```sh
   git checkout master
   git pull upstream master
   ```

1. Create a new branch named `{IssueNumber}-{some-keywords}`,
   where `some-keywords` are representative keywords taken from the issue title.
   ```sh
   git checkout -b 3942-remove-unnecessary-println
   ```

**Do not** combine fixes for multiple issues in one branch, unless they are tightly related.

### Step 3: Fix the issue

> If this is your first issue, you may want to look at our coding and testing best practices as well as coding conventions (links given [here](README.md)).

Make the changes to the code, tests, and documentations as needed by the issue.

1. Commit the changes to your branch.
   ```sh
   git add -A
   git commit
   ```
   * You may commit as many times as you wish while you are making the changes.
     It is, however, a good idea to commit at meaningful points to keep your branch reasonably clean.
   * Use meaningful commit messages (e.g. `Add tests for the truncate method`).
     [Here](http://chris.beams.io/posts/git-commit/) is a good reference.

1. Sync with the committer repo frequently. While you were fixing the issue, others might have pushed new code to the committer repo.
   * Update your repo's `master` branch with any new changes from committer repo.

     ```sh
     git checkout master
     git pull upstream master
     ```
   * Option 1: merge those updates to the branch you are working on.

     ```sh
     git checkout -b 3942-remove-unnecessary-println
     git merge master
     ```
   * Option 2: if you are confident with rebasing, rebase your changes over the latest `master` branch.

     ```sh
     git rebase master
     ```
   * If there are updates to the dependencies on the build configuration, you should update your local copies accordingly.
     The details on the steps can be found on [this document](dependencies.md).

1. <a name="things-to-check"></a>Before submitting your work for review, here are some things to check (non-exhaustive):
   * The code is **properly formatted for readability**.<br>
     Select the code segments you modified and apply the code formatting function of Eclipse (`Source → Format`).
     You may tweak the code further to improve readability as auto-format does not always result in a good layout.
   * The code base passes **static analysis** (i.e. code quality check).<br>
     The details on how to run static analysis locally is given on [this document](staticAnalysis.md).
   * **Dev green**, i.e. all *local* tests are passing on your dev server. Local tests can be run using the "Local Tests" run configuration in Eclipse.<br>
     You are more than welcome to also ensure all *Travis* tests are passing on your dev server.
   * **Staging-tested (if need be)**: If your new code might behave differently on a remote server than how it behaves on the dev server,
     ensure that the affected tests are passing against the updated app running on your own GAE staging server.
   * **No unrelated changes** are introduced in the branch. This includes unnecessary formatting changes.
   * All changes or additions to functional code are **accompanied by changes or additions in tests**, even if they are absent before.
   * All new public APIs (methods, classes) are **documented with header comments**.
   * **Documentations are updated** when necessary, particularly when there are changes or additions to software design as well as user-facing features.

1. Push your branch to your fork, or to the committer repo if you have push access.
   ```sh
   git push {remote-name} 3942-remove-unnecessary-println
   ```
   If the above command does not work e.g. because of rebasing, do a forced-push:
   ```sh
   git push -f {remote-name} 3942-remove-unnecessary-println
   ```

### <a name="creating-a-pull-request"></a> Step 4: Submit a PR

[Create a PR](https://help.github.com/articles/creating-a-pull-request/) with the following configuration:
* The base branch is the committer repo's `master` branch (except for hot patches in which it will be the `release` branch).
* PR name: copy-and-paste the relevant issue name and include the issue number as well,
  e.g. `Remove unnecessary System.out.printlns from Java files #3942`.
* PR description: mention the issue number in this format: `Fixes #3942`.
  Doing so will [automatically close the related issue once the PR is merged](https://github.com/blog/1506-closing-issues-via-pull-requests).<br>
  You are also welcome to describe the changes you have made in your branch and how they resolve the issue.

It is not required that you submit a PR only when your work is ready for review;
make it clear in the PR (e.g. in the description, in a comment, or as an `s.*` label) whether it is still a work-in-progress or is ready for review.

### Step 5: Following up

Once a PR is opened, try and complete it within 2 weeks, or at least stay actively working on it.
Inactivity for a long period may necessitate a closure of the PR.

The following labels are used to indicate status of PRs:
* `s.Ongoing`: the PR is being worked on
* `s.ToReview`: the PR is waiting for review
* `s.ToMerge`: reviewer has accepted the changes
* `s.MergeApproved`: PM has approved the merge; PR ready to be merged
* `s.OnHold`: the work on the PR has been put on hold pending some other event; this label is to be used as needed

#### Code review

Your code will be reviewed, in this sequence, by:
* Travis CI: by running static analysis.<br>
  If there are problems found, the build will terminate without proceeding to testing.<br>
  Most of the tools will display the cause of the failures in the console;
  if this is not the case, you can run any of the static analysis tools and obtain the reports locally.<br>
  Ensure that the static analysis passes before triggering another build.
* Travis CI: by building and running tests. If there are failed tests, the build will be marked as a failure.<br>
  You can consult the CI log to find which tests.<br>
  Ensure that all tests pass before triggering another build.
  * The CI log will also contain the command that will enable running the failed tests locally.
* Reviewer: a core team member will be assigned to the PR as its reviewer, who will approve your PR (`s.ToMerge`) or suggest changes (`s.Ongoing`).
  Feel free to add a comment if:
  * a reviewer is not assigned within 24 hours.
  * the PR does not get any review within 48 hours of review request.
  * you want to clarify or discuss about the suggestions given by your reviewer.
* PM: final review for maintainability and style.

#### Updating the PR

If you are tasked to update your PR either by Travis CI or by your reviewer, there is no need to close the PR and open a new one.
You will simply make and push the updates to the same branch used in the PR, essentially repeating [step 3](#step-3-fix-the-issue).

Remember to add a comment to indicate the PR is ready for review again, e.g. `ready for review` or `changes made`.
If you have permission to change labels you may additionally change the `s.*` PR label as appropriate.

The cycle of "code review" - "updating the PR" will be repeated until your PR is by all the parties involved (`s.MergeApproved`).

### Step 6: Prepare for merging

The core team member responsible for merging your PR might contact you for reasons such as syncing your PR with the latest `master` branch or resolving merge conflicts.
Depending on the situation, this may necessitate more changes to be made in your PR (e.g. if your PR is functionally conflicting with a recent change), however this rarely happens.

Your work on the issue is done when your PR is successfully merged to the committter repo's `master` branch.

## Reviewing a PR

**Role: Reviewer**

> - The reviewer of a PR is the assignee of it.
> - To remove whitespace-only changes from being shown, append `?w=1` to url of the `/files` page of the PR (the "Files changed" tab).

[GitHub's review feature](https://github.com/blog/2256-a-whole-new-github-universe-announcing-new-tools-forums-and-features#code-better-with-reviews) is to be used in this task.

* Ensure that the Travis CI build is successful and the developer has local dev green.
* Ensure the following:
  * Naming conventions for PR and branch are followed, and `Fixes #....` or similar keyword is present in the PR description.
  * The items in [this list](#things-to-check) are all satisfied.
  * The solution is the best possible solution to the problem under the circumstances.
  * The code is up-to-date with the latest `master` branch, or at least no conflict.
    If this is not the case, ask the dev to sync with it with the latest `master` branch.
* If any of the above are not OK:
  * Add comments in the diff to suggest changes.
    Bundle the review comments with the "Start a review" and "Add review comment" features, and finish it with "Request changes", preferably with the review summary.
  * Change the status of the PR to `s.Ongoing`.
* If the code is OK in all aspects, change the PR status to `s.ToMerge` and "Approve" the PR.

**Role: PM**

* Review the code for maintainability and style.
* The follow-up action is the same as that of reviewers, with the only difference being the label to be applied is `s.MergeApproved`.

## Merging a PR

**Role: dev (with push permission), or reviewer**

This instruction set will use the issue `Remove unnecessary System.out.printlns from Java files #3942` as an example.

* Merging can be done anytime as long as the `s.MergeApproved` label is present and GitHub gives a green light for merging.
  There are a few scenarios where GitHub can prevent merging from proceeding:
  * **Merge conflict**: the PR is conflicting with the current `master` branch; the author will need to resolve the conflicts before proceeding
  * **Outdated branch**: the PR is not in sync with the current `master` branch; the author will need to sync it before proceeding

  The dev will need to resolve them before merging can proceed. It is up to the dev/reviewer's discretion on whether the merge conflict or outdated branch necessitates another review.<br>
  In general, unless the changeset is functionally conflicting, there is no need for another review.
* When ready for merging,
  * Checkout to the PR branch and test the code locally by running the "Local tests".

    ```sh
    git checkout -b 3942-remove-unnecessary-println {remote-name}/3942-remove-unnecessary-println
    ```
  * If green,
    * Merge with ["Squash and merge"](https://github.com/blog/2141-squash-your-commits) option (preferable). Format of the commit message:

      ```
      [Issue number] Issue title as given in the original issue
      ```
      e.g. `[3942] Remove unnecessary System.out.printlns from Java files`.
    * Apply an `e.*` label to the issue (not the PR) to indicate the estimated effort required to fix the issue,
      and another `e.*` label to the PR to indicate the estimated effort required to review the PR.<br>
      `e.1` is roughly equal to an hour of work, `e.2` is two hours of work, and so on.
  * If not green,
    * Change the PR status back to `s.Ongoing`.
    * Add a comment to mention the test failure(s).

## Making a release

New releases are made every set period of time (typically every week), in which new set of features and bug fixes are deployed for the users.

**Role: RL**

* Release day - 3:
  * Post a comment on ongoing PRs of the current milestone to remind the dev/reviewer to finish by the release date.
* Release day:
  * Get Travis green and dev green for `master`.
  * Merge to `release` branch and tag the release with format `V{major}.{minor}` (e.g. `V5.55`).
  * Inform PM the new version is ready for deployment.
  * Ensure all issues and PRs included in the release are tagged with the correct milestone and the correct assignee.
  * Extend the milestone of issues that slipped the current milestone and post a comment asking to finish by next milestone.<br>
    If a PR is not making progress, close the PR and un-assign the issue.
  * Add/revise `e.*` labels for the issues/PRs in the release.
  * Ensure all branches merged in the milestone have been deleted.
  * Announce the release on Slack and issue tracker.
  * Update `about.jsp` with names of new contributors, if any. Alternatively, create an issue to update `about.jsp`.
  * Close the current milestone and create a new milestone for the next + 1 release.
  * Create an issue for the next release. Post a comment in that issue to remind active members who do not have issues scheduled for next release.
* Release day + 1:
  * Ensure all pending `p.Urgent` issues are assigned and scheduled for next milestone.
  * Ensure all active members have committed at least one issue for the next milestone.

**Role: PM**

* Pull the latest `release` branch.
* Get Travis green and dev green.
* Deploy to the live server.
* Get live green.
* Make the version default.
* Close the "Release" issue.

## Making a hot patch

Hot patches are releases containing fix for critical issues that severely affect the application's functionality.
It is released on a necessity basis, typically few days after latest release.

**Role: RL**

* Get Travis green and dev green for `release`.
* Tag the release with format `V{major}.{minor}.{patch}` (e.g. `V5.55.1`).
* Inform PM the hot patch is ready for deployment.
* Merge the `release` branch back to the current `master` branch.

**Role: PM**

The PM's actions are the same as when [making a release](#making-a-release), minus the "Closing the release issue" part.
