# TEAMMATES Developer Community

This document describes the organization structure of the TEAMMATES developers community, in the order of increasing seniority.

* [Contributor](#contributor)
* [Committer](#committer)
* [Core team member](#core-team-member)
  * [Snr Developer](#snr-developer)
  * [Area Lead](#area-lead)
  * [Project Lead](#project-lead)
  * [Project Mentor](#project-mentor)

Refer to [this page](https://teammatesv4.appspot.com/about.jsp) for the current lineup of the community.

## Contributor

Contributors are community members who give small but noteworthy contributions (typically bug reports, bug fixes, enhancements, documentation updates) to the project, on a no-strings-attached basis.
TEAMMATES welcome any developer (especially student) to be a contributor to the project, with no expectation of commitment or skill set.

Contributors:
* Have "pull" access to the main repository and submit their fixes via pull requests (PRs) from their own forked repository.
  The detailed workflow is given in [this document](process.md).
* Can open issues.
* Can close issues and PRs opened by themselves.
* Can comment on any issue and PR.

## Committer

Committers are community members who have shown significant contributions sustained over a long period of time.

On top of contributor's privileges, committers:
* Have "push" access to the main repository and has the option to submit their fixes via PRs from the main repository.
* Can label and close any issue as they see fit.
* Can label PRs opened by themselves.
* Can assign themselves to issues.
* Can assign reviewers to PRs authored by themselves.
* Can merge approved PRs authored by themselves.

## Core team member

Core team members are community members who have shown significant and sustained contributions in recent times,
and are trusted to be responsible for determining the direction of the project for a set period of time.
They are expected to be familiar with the development workflow.

On top of committer's privileges, core team members:
* Can label and close any PR as they see fit.
* Can be assigned or assign themselves to review PRs.
* Can merge approved PRs reviewed by themselves.

Core team members are expected to:
* Contribute (i.e. pledge at least one issue) to almost every release cycle, thereby helping to maintain the project velocity.
  They are also strongly encouraged to pick at least one high priority issue for each release cycle.
* Respond to community members trying to reach the project team members, e.g. requesting for help or introducing themselves.
* Cover all of [maintainer's duties](maintainer-guide.md).

Core team members can progress through various ranks, with different duties involved in those ranks.

> Core team members are synonymous with *project maintainers*, a term more commonly used in other software projects.

### Snr Developer

Snr developers are the most junior members of the core team.
They do not have additional duties other than that common to all core team members.

### Area Lead

Area leads are team members in charge of particular aspects of the project.

Area leads have the following duties:
* Act as the first point of contact for an issue requiring triage or a PR requiring review, if the issue falls in their portfolio (as determined by the issue labels).
* Perform maintenance tasks regularly in their portfolio area. For example:
  * For feature-related areas: fixing bugs, testing for correctness and performance, paying off code-related technical debts.
  * For DevOps: researching new development tools or alternative workflow to be adopted, updating third-party dependencies.
  * For documentation: making sure they are updated and relevant.

### Project Lead

> This title is used interchangeably with *Team Lead*; they both refer to the same position.

Project leads are team members who oversee the entire project velocity and ensure its continuity and maintainability.

Project leads have the following additional privileges:
* Have "admin" access to the main repository.
* Can assign reviewers to any PR.
* Can merge any approved PR.

Project leads have the following duties:
* Manage weekly release (i.e. act as Release Lead). Can also be delegated to other team members.
* Make sure that all of maintainer's duties are covered by the team.

### Project Mentor

Project mentors are experienced past team members or domain/industry experts who take on advisory role for current team members.
They are not obliged to actively contribute code to the project or perform maintenance tasks.
