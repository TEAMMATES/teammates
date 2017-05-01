# Glossary

This document lines out the common terms used in the project.

## Product-related

+ **Course**: We use "course" to mean "module", "class". A course here means an offering of a course in a particular semester/term. If the course is offered twice in two semesters (i.e. with different students and teaching team), they are considered two courses. There is, however, no time limit on how long a course may run.
+ **Contribution scale**:
```
    [equal share]+100%

    [equal share]+90%

    ...

    [equal share]

    [NOT SURE]

    [equal share]-10%

     ...

    [equal share]-90%

    [0%] (did nothing at all)
```
+ **Enrollment, Enroll** (NOT enrolment, enrol).
+ **Feedback Session**: A collection of questions for students and/or instructors alike to answer; synonymous to a questionnaire, a survey.<br>
  Sometimes, this is referred to as simply "Session"; developers must take extra care so as not to confuse it with HTTP sessions.

## Development-related

+ **Dev site**: [Our developer web site](https://github.com/TEAMMATES/teammates).
+ **Main repo**: The repository containing the latest stable code.
+ **Specs**: System Specification.

`* server`:

+ **Dev server**: The development server running on your computer.
+ **CI server**: Continuous Integration server used for building/testing patches. Can be Travis or AppVeyor.
+ **Production server**: A server on AppSpot. Can be a staging server or the live server.
+ **Staging server**: The server instance you created on AppSpot.
+ **Live server**: The server running the released public version of the app (i.e. http://teammatesv4.appspot.com).

`* tests`:

+ **All tests**: The full test suite.
+ **CI tests**: Tests to be run on the CI server.
+ **Local tests**: Tests to be run locally by developers.

`* green`:

+ **CI green**: **CI** tests are passing on the CI server.
+ **Dev green**: **Local** tests are passing on the dev server.
+ **Local CI green**: **CI** tests are passing on the dev server.
+ **Staging green**: **All** tests are passing against your own staging server.
+ **Live green**: **All** tests are passing against the app running on the live server.
