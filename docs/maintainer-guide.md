# Maintainer Guide

This document details the tasks that need to be done by core team (afterwards "team") members (project maintainers) in order to keep the project moving.

It is assumed that the team members are familiar with the [development workflow](process.md), [individual development process](development.md), and the terms used in the project as listed in those documents, [project glossary](glossary.md), [issue labels](issues.md), and beyond.

* Issue tracker management
  * [Triaging an issue](#triaging-an-issue)
  * [Closing an issue](#closing-an-issue)
* PR management
  * [Choosing a reviewer](#choosing-a-reviewer)
  * [Closing a PR](#closing-a-pr)
  * [Reverting a PR](#reverting-a-pr)
* Release management
  * [Making a release](#making-a-release)
  * [Making a hot patch](#making-a-hot-patch)
* Other tasks
  * [Security vulnerabilities](#security-vulnerabilities)
  * [Dependencies update](#dependencies-update)
  * [Branch management](#branch-management)
    * [Using a long-lived feature branch](#using-a-long-lived-feature-branch)
  * [Community membership](#community-membership)
  * [Beginner-level issues](#beginner-level-issues)

## Issue tracker management

### Triaging an issue

A new issue needs to be triaged by a team member. Here is the process:

1. Is the issue a duplicate of an existing issue?
   * Yes: close the issue and add a reference to the original issue (which the new issue is a duplicate of). Alternatively, close the older issue and add a reference to the newly opened issue.
   * No: continue to the next step.
1. Does the issue provide enough information?
   * Yes: continue to the next step.
   * No: add the `s.NeedsInfo` label and request the issue reporter to provide more information. Additionally, if the issue does not follow the given templates (which is likely the case if the information is lacking), encourage the issue reporter to make use of an appropriate template. There is no need to comment any further until the issue reporter provides more details.
1. Is the issue valid? (e.g. if it is a bug report, is it reproducible? If it is a feature/enhancement request, is the requested feature/enhancement absent?)
   * Yes: continue to the next step.
   * No: add the `s.ToInvestigate` label and assign a team member (can be yourself) to the issue. This assignee will be tasked to confirm the issue's validity, not to resolve the issue. There is no need to comment any further until the assignee confirms the validity of the issue.
     * An issue should not be left as `s.ToInvestigate` for too long without valid reason. If the assignee does not show any activity for at least **3 days**, assign it to somebody else.
1. Is the issue suitable? Not all issues are equal; sometimes, a valid issue does not fit well into the project's best interest.
   * Yes: continue to the next step.
   * No: to be dealt on a case-by-case basis. Possible actions include closing the issue, applying `s.OnHold` label and revisiting the issue in the future, or simply accepting the issue with low priority. In any case, leave a comment to explain the rationale of such action.
1. Accept the issue by categorizing:
   * For messages directed to the team, add the label `c.Message`, and post a comment if you can respond or know someone who can. If the message is about help requests, add the label `a-DevHelp` as well.
   * For other types of issues, add the category labels as appropriate. Do NOT add `e.*` label.
     * Issues marked as `c.Message` and `c.Release` are exempted from all other labels.
     * If an issue is at priority `p.High` or higher, labels `d.FirstTimers` and `d.Contributors` cannot be applied to it.

### Closing an issue

An issue can be closed without a resolution if:

* The issue is a message and either has been resolved with no loose ends left, or has no more activity for at least **7 days** after the last post in the issue thread and not pending any response from the team.
* The issue is a duplicate of an existing issue.
* The issue was opened in the past and is no longer relevant in the present.
* The team has decided that the issue is not suitable to be worked on, e.g. not in line with the project's interest.

In any case, leave a comment to explain why the issue is closed without resolution.

## PR management

### Choosing a reviewer

* When a new PR comes in, assign a reviewer for the PR based on the related issue's labels and team members' expertise. Try to load-balance when assigning reviewers.
* If a reviewer does not show any activity in **2 days**, post a reminder. If the reviewer still does not show any activity for the next **1 day**, assign another reviewer.

### Closing a PR

A PR can be closed without merging if:
* The PR addresses something that has been fixed.
* The PR addresses something that needs not be fixed.
* The PR addresses an issue labelled `d.FirstTimers` and is authored by a contributor who has committed code to the main repository before.
* The author does not address the review comments after **7 days**.
* The author is not acting in the project's best interest, e.g. resisting review comments, not following project guidelines.

In any case, leave a comment to explain why the PR is closed without merging.

### Reverting a PR

There may be situations where a merged PR needs to be reverted, e.g. when the PR has an unintended side effect that is difficult to fix or the PR was incomplete but accidentally merged.

For example, to revert the PR `#3944` (`Remove unnecessary System.out.printlns from Java files #3942`):
* No issue needs to be opened for this.
* There should only be one commit, which can be auto-generated with `git revert 1234567` (replace `1234567` with the appropriate commit SHA). A conflict resolution may be necessary.
* PR title: Duplicate the first line of the reversion commit message. (e.g. `Revert "[#3942] Remove unnecessary System.out.printlns from Java files (#3944)"`).
* PR description: `Reverts #...` (e.g. `Reverts #3944`).
* Merge with "Rebase and merge" option.
* Re-open the issue once the reversion is merged.
* The reverted PR and the reversion PR should not be included in any milestone if the reverted PR does not belong in any released version.

## Release management

**Roles: Release Lead (RL), Project Manager (PM)**

### Making a release

**Role: RL**

New releases are made every set period of time (typically every week), in which new set of features and bug fixes are deployed for the users.

* Before release day:
  * Create an issue for the release to announce the scheduled release time.
  * Update `about.jsp` with the names of new contributors, if any.
* Release day:
  * Ensure all issues and PRs included in the release are tagged with the correct milestone, correct assignee(s), and appropriate `e.*` labels.
  * Merge `release` branch with `master` branch and tag the release with format `V{major}.{minor}.0` (e.g. `V6.0.0`).
  * Close the current milestone and create a new milestone for the next + 1 release.
  * Announce the release via GitHub release feature as well as the release issue in the issue tracker. Be sure to credit all who contributed to the release in one way or another.
  * Assign PM to the "Release" issue.

**Role: PM**

* Pull the latest `release` branch.
* Deploy to the live server.
* Get live green, or otherwise all test failures need to be accounted for.
* Make the version default.
* Close the "Release" issue.

## Making a hot patch

Hot patches are releases containing fix for critical issues that severely affect the application's functionality.
It is released on a necessity basis, typically few days after latest release.

**Role: RL**

* Tag the release with format `V{major}.{minor}.{patch}` (e.g. `V6.0.1`).
* Close the milestone for the patch release and announce via GitHub release feature only. Be sure to credit all who contributed in one way or another.
* Inform PM the hot patch is ready for deployment.
* After the last hot patch of the proper release, merge the `release` branch back to the current `master` branch.

**Role: PM**

The PM's actions are the same as when [making a release](#making-a-release), minus the "Closing the release issue" part.

## Other tasks

### Security vulnerabilities

Security vulnerabilities, once reported and confirmed, should be treated as a candidate for hot patch (i.e. fixed in the soonest possible time directly on the `release` branch).

Since the detail of such vulnerability cannot be disclosed until it is fixed, an issue can be created just before a PR for the fix is submitted, with minimal information (e.g. simply "Security vulnerability" as an issue with no further description).
The complete details can be filled in just before merging and/or after the fix is deployed.

### Dependencies update

The third-party dependencies/libraries should be updated periodically (e.g. once every 3-6 months) in order to benefit from fixes developed by the library developers.

The steps to update dependencies can be found in the [dependencies document](dependencies.md).
To find which dependencies need update, you can use libraries like [`Gradle Versions Plugin`](https://plugins.gradle.org/plugin/com.github.ben-manes.versions) and [`npm-check-updates`](https://www.npmjs.com/package/npm-check-updates).

* Not all updates are important/relevant; it is up to the team's discretion on what needs to be updated and what not, and when to update.
* Only stable versions (i.e. non-beta and non-alpha) should be considered.
* Updates with little to no breaking changes should be included in the periodic mass update; otherwise, an issue to update a specific dependency should be created.

### Branch management

Ideally, only two branches should exist in the main repository:

* `master` to contain the latest stable code.
* `release` to contain the copy of the code running on the live server.

The usage of any other branch should be accounted for, and the branches should be deleted as soon as they are no longer needed.

#### Using a long-lived feature branch

There may be times where a major feature development/refactoring necessitates a long-lived branch to be used to contain all the changes before merging everything to `master` branch in one go.

For the usage of such a branch, the following practices should be observed:

* There should be at least one team member in charge of the branch.
* The first commit of the branch should be allowing CI to run on that branch. This can be done by modifying `.travis.yml` and `appveyor.yml`.
* Keep this long-lived branch in sync with `master` periodically. Syncing should be done strictly by rebasing in order to preserve all the individual commits and to keep the commit history linear.
  * The team member(s) in charge will be responsible for syncing with the `master` branch, including resolving conflicts.
* When the long-lived branch is ready to be merged to the `master` branch:
  * Rebase with the latest `master` branch and get rid of the commit which explicitly allows CI run.
  * Submit a PR and get it merged as per the usual procedure.
    * The PR title and issue number can be a dummy, but keep the PR title as informative as possible.
    * Reviews can be skipped if the individual commits/PRs are sufficiently reviewed.

### Community membership

To welcome a new committer:

* Add the GitHub user to the `Committers` team.
* Add the committer's name and photo to `about.jsp`.

Subsequent promotions are done by moving the member's name to the appropriate section in `about.jsp`.

To welcome a new project lead:

* Add the GitHub user to the `Team-leads` team.
* Set the GitHub user to have the "Owner" role for the TEAMMATES organization.

When someone's tenure as committer or team member has passed:

* Do NOT revoke the team membership, unless voluntarily done by the past member him/herself.
* Move the past member's name to the appropriate section in `about.jsp`.

### Beginner-level issues

Ensure that there is a healthy supply of `d.FirstTimers`-labelled issues and `d.Contributors`-labelled issues to last for at least **7 days** considering the activity level at that point of time.

Possible `d.FirstTimers` issues:

* Documentation-only changes
* Fixing typo
* Removing unused methods or classes

Possible `d.Contributors` issues:

* Minor refactoring
* Adding missing tests
* Adding minor new feature or enhancement
