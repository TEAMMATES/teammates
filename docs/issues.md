# Issue tracker

This document describes how the issue tracker is used by the TEAMMATES project.

* [Issue lifecycle](#issue-lifecycle)
* [Issue labels](#issue-labels)

## Issue lifecycle

<img src="images/IssueLifecycle.png" width="600">

Given above is an illustration of the issue lifecycle.
Colors indicate which roles are involved in which states/transitions.

## Issue labels

### Status (`s.*`)

This label classifies the issue based on its **status**.

* No `s.*` label and no other labels: issue is yet to be triaged
* `s.NeedsInfo`: more information is needed from the issue reporter
* `s.ToInvestigate`: the issue needs to be validated by a core team member
* `s.OnHold`: the issue's validity has been put on hold pending some other event
* No `s.*` label and other labels present: issue is accepted

### Category (`c.*`)

This label classifies the issue based on its **type**.

* `c.Bug`: a bug report
* `c.Enhancement`: an enhancement to an existing functionality (not big enough to be considered as a user story)
* `c.Story`: a user story, e.g. a feature request
* `c.Epic`: a feature that is worth many user stories
* `c.DevOps`: workflow-related or build-related improvements and additions
* `c.Task`: other work items such as refactoring or updating documentation
* `c.Release`: release-related issues; this is reserved for core team members working on managing release
* `c.Message`: a means of communication with the dev team, e.g. help requests; while this is not an issue to be worked on, any contributor is welcome to chip in their opinions

### Priority (`p.*`)

This label classifies the issue based on its **importance**, as determined by the project maintainers.

* `p.Critical`: would like to fix it ASAP and release as a hot patch
* `p.Urgent`: would like to handle in the very next release
* `p.High`: enhances user experience, software design, or development process significantly, would like to do in the next few releases
* `p.Medium`: marginal impact on user experience, software design, or development process
* `p.Low`: very little impact, unlikely to do in the near future
* `p.Zero`: unlikely to do, ever

### Difficulty Level (`d.*`)

This label classifies the issue based on its **difficulty level**.

* `d.FirstTimers`: easy; to do as the first issue for new developers (one developer should not do more than one of these)
* `d.Contributors`: moderate difficulty, small localized change; suitable for contributors (new developers who are not first timers)
* `d.Committers`: more difficult issues that are better left for committers or more senior developers
* No `d.*` label: variable difficulty level, typically between `d.Contributors` and `d.Committers` level

### Aspect (`a-*`)

This label classifies the issue based on the **non-functional aspect** it tackles.

* `a-AccessControl`: controlling access to user groups, authentication, privacy, anonymity
* `a-CodeQuality`: code/design quality-related issues, static analysis
* `a-Concurrency`: things related to concurrent access, session control
* `a-Build`: IDE support, CI, task automation
* `a-Docs`: website, user docs, dev docs
* `a-FaultTolerance`: resilience to user errors, environmental problems
* `a-Performance`: speed of operation
* `a-Persistence`: database layer, GAE datastore
* `a-Process`: workflow management, release management
* `a-Scalability`: related to behavior at increasing loads
* `a-Security`: protection from security threats
* `a-Testing`: testing-related traits such as efficiency, robustness, and coverage
* `a-UIX`: User Interface, User eXperience, responsiveness
* No `a-*` label: no specific aspect tackled, usually the case for enhancements or new features

### Feature (`f-*`)

This label classifies the issue based on the **functional aspect** it tackles.

* `f-Admin`: features used by system administrators
* `f-Comments`: comments
* `f-Courses`: courses, instructors, students, home page
* `f-Email`: code related to sending emails
* `f-Profiles`: user profiles
* `f-Results`: session results, moderation, download
* `f-Search`: search
* `f-Submissions`: session creation, editing, submissions
* No `f-*` label: no specific feature tackled, usually the case for refactoring

### Technology (`t-*`)

This label classifies the issue based on the **technology/tool stack** it involves.

* `t-CI`: Gradle, NPM, static analysis, build script, CI
* `t-CSS`: CSS, Bootstrap
* `t-GAE`: Google App Engine-related technologies
* `t-HTML`: HTML, Browsers
* `t-JS`: JavaScript, jQuery, related frameworks such as Node.js
* `t-JSTL`: JSP, JSTL, Servlets
* No `t-*` label: usually only Java, or documentation update
