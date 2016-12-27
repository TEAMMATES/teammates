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

## Committer

Committers are community members who have shown significant contributions sustained over a long period of time.

On top of contributor's privileges, committers:
* Have "push" access to the main repository and submit their fixes via PRs from the main repository.
* Can label and close issues and PRs as they see fit.
* Can assign themselves to issues.
* Can assign reviewers for PRs authored by themselves.
* Can merge PRs authored by themselves.

## Core team member

Core team members are community members who have shown significant and sustained contributions in recent times,
and are trusted to be responsible for determining the direction of the project for a set period of time.
They are expected to be familiar with the development workflow.

On top of committer's privileges, core team members:
* Can be assigned or assign themselves to review PRs.
* Can merge PRs reviewed by themselves.

Core team members are expected to contribute (i.e. pledge at least one issue) to almost every release cycle, thereby helping to maintain the project velocity.
They are also strongly encouraged to pick at least one high priority issue for each release cycle.

Core team members can progress through various ranks, with different duties involved in those ranks.

### Snr Developer

* Chow Yuan Bin ([@chowyb](https://github.com/chowyb))
* Khoo Yong Jie ([@YongJieYongJie](https://github.com/YongJieYongJie))

Snr developers are the most junior members of the core team.
They do not have additional duties other than that common to all core team members.

### Area Lead

* Josephine Kwa ([@JosephineKwa](https://github.com/JosephineKwa))
* Wilson Kurniawan ([@wkurniawan07](https://github.com/wkurniawan07))
* Yap Jun Hao ([@junhaoyap](https://github.com/junhaoyap))

Area leads are the team members in charge of particular aspects of the project.

Area leads have the following duties:
* Act as the first point of contact for an issue requiring triaging or a PR requiring review,
  if the issue falls in their portfolio (as determined by the issue labels).
* Perform maintenance tasks regularly in their portfolio area. For example:
  * For feature-related areas: fixing bugs, testing for correctness and performance, paying off code-related technical debts.
  * For DevOps: researching new development tools or alternative workflow to be adopted, updating third-party dependencies.
  * For documentation: making sure they are updated and relevant.

### Project Lead

* Wilson Kurniawan ([@wkurniawan07](https://github.com/wkurniawan07))

Project leads are the team members who oversee the entire project velocity and ensure its continuity and maintainability.

Project leads have the following additional privileges:
* Have "admin" access to the main repository
* Can merge any approved PRs reviewed by anyone as they see fit.

Project leads have the following duties, which can also be spread with other team members:
* Choose reviewers for PRs (if nobody volunteers to be a reviewer), based on the issue label.
* Manage issue and PR tracker, e.g. close PRs that are abandoned for long.
* Manage weekly release (i.e. act as Release Lead).
* Invite new committers/core team members to the relevant teams in GitHub.
* Ensure no unnecessary branches remain in the main repository.
* Ensure that there are sufficient beginner-level issues (`d.FirstTimers` and `d.Contributors`) to last for few days at any time.

### Project Mentor

* Damith C. Rajapakse ([@damithc](https://github.com/damithc))
* Kang Hong Jin ([@kanghj](https://github.com/kanghj))
* Thyagesh Manikandan ([@thyageshm](https://github.com/thyageshm))

Project mentors are experienced past team members who take on advisory role for the current team members.
They are not obliged to actively contribute code to the project or perform maintenance tasks.
