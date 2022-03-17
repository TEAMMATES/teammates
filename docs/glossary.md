<frontmatter>
  title : "Glossary"
</frontmatter>

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

+ [**Main repo**](https://github.com/TEAMMATES/teammates): The repository containing the latest stable code.
+ [**Ops repo**](https://github.com/TEAMMATES/teammates-ops): The repository containing maintenance and operations-specific information.
+ **Specs**: System Specification.

### Servers

+ **Dev server**: The development server running on your computer.
+ **CI server**: Continuous Integration server used for building/testing patches. We use GitHub Actions.
+ **Production server**: A server on Google App Engine. Can be a staging server or the live server.
+ **Staging server**: The server instance you created on Google App Engine for testing purpose.
+ **Live server**: The server running the released public version of the app (i.e. https://teammatesv4.appspot.com).

### Tests

+ **All tests**: The full test suite.
+ **Component tests**: Unit and integration tests, i.e. white-box tests. There are separate component test suites for front-end and back-end.
+ **E2E tests**: End-to-end system tests, i.e. black-box tests. Also used for product acceptance.
+ **CI tests**: Tests to be run on the CI server. Consists of components tests and E2E tests.
