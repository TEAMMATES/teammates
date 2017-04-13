# Maintainer Guide

This document details the tasks that need to be done by core team (afterwards "team") members (project maintainers) in order to keep the project moving.

It is assumed that the team members are familiar with the [development workflow](process.md), [individual development process](development.md), and the terms used in the project as listed in those documents, [project glossary](glossary.md), [issue labels](issues.md), and beyond.

* Issue tracker management
  * [Triaging an issue](#triaging-an-issue)
  * [Closing an issue](#closing-an-issue)

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
