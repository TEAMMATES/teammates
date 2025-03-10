<frontmatter>
  title: "Unit Testing"
</frontmatter>

# Unit Testing

## What is Unit Testing?

Unit testing is a testing methodology where the objective is to test components in isolation.

- It aims to ensure all components of the application work as expected, assuming its dependencies are working.
- This is done in TEAMMATES by using mocks to simulate a component's dependencies.

Frontend unit tests in TEAMMATES are located in `*.spec.ts` files and configured in `src/web/jest.config.js`.

Backend unit tests in TEAMMATES are located in the package `teammates.test` and configured in `src/test/resources/testng-component.xml`.

## Running Unit Tests

### Frontend tests

To run all frontend component tests in watch mode (i.e. any change to source code will automatically reload the tests), run the following command:

```sh
npm run test
```

Most frontend component tests use [Snapshot Testing](snapshot-testing.md). To update snapshots, run the following command:

```sh
npm run test
```

Followed by `a` to run all the test cases. Check through the snapshots to make sure that the changes are as expected, and press `u` to update them.

To run all frontend component tests once and generate coverage data afterwards, run the following command:

```sh
npm run coverage
```

To run an individual test in a test file, change `it` in the `*.spec.ts` file to `fit`.

To run all tests in a test file (or all test files matching a pattern), you can use Jest's watch mode and filter by filename pattern.

### Backend tests

To run the backend tests, ensure that a database instance and a full-text search service instance are running. Details on the backend pre-requisites can be found [here](development.md#pre-requisites). The tests may take some time to complete, typically around 10 minutes.

Backend component tests follow this configuration:

| Test suite                    | Command                                          | Results can be viewed in                                         |
| ----------------------------- | ------------------------------------------------ | ---------------------------------------------------------------- |
| Component tests               | `./gradlew componentTests --continue`            | `{project folder}/build/reports/tests/componentTests/index.html` |
| Any individual component test | `./gradlew componentTests --tests TestClassName` | `{project folder}/build/reports/tests/componentTests/index.html` |

You can generate the coverage data with `jacocoReport` task after running tests, e.g.:

```sh
./gradlew componentTests jacocoReport
```

The report can be found in the `build/reports/jacoco/jacocoReport/` directory.

## Creating Unit Tests

### General guidelines

#### Include only relevant details in tests

When writing unit tests, reduce the amount of noise in the code to make it easier for future developers to follow.

The code below has a lot of noise in creation of the `studentModel`:

```javascript
it('displayInviteButton: should display "Send Invite" button when a student has not joined the course', () => {
  component.studentModels = [
    {
      student: {
        name: "tester",
        teamName: "Team 1",
        email: "tester@tester.com",
        joinState: JoinState.NOT_JOINED,
        sectionName: "Tutorial Group 1",
        courseId: "text-exa.demo",
      },
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
    },
  ];

  expect(sendInviteButton).toBeTruthy();
});
```

However, what is important is only the student joinState. We should thus reduce the noise by including only the relevant details:

```javascript
it('displayInviteButton: should display "Send Invite" button when a student has not joined the course', () => {
  component.studentModels = [
    studentModelBuilder.joinState(JoinState.NOT_JOINED).build(),
  ];

  expect(sendInviteButton).toBeTruthy();
});
```

Including only the relevant details in tests makes it easier for future developers to read and understand the purpose of the test.

#### Favor readability over uniqueness

Since tests don't have tests, it should be easy for developers to manually inspect them for correctness, even at the expense of greater code duplication.

Take the following test for example:

```java
@BeforeMethod
public void setUp() {
    users = new User[]{new User("alice"), new User("bob")};
}

@Test
public void test_register_canRegisterMultipleUsers() {
    registerAllUsers();
    for (User user : users) {
        assertTrue(forum.hasRegisteredUser(user));
    }
}

private void registerAllUsers() {
    for (User user : users) {
        forum.register(user);
    }
}
```

While the code reduces duplication, it is not as straightforward for a developer to follow.

A more readable way to write this test would be:

```java
@Test
public void test_register_canRegisterMultipleUsers() {
    User user1 = new User("alice");
    User user2 = new User("bob");

    forum.register(user1);
    forum.register(user2);

    assertTrue(forum.hasRegisteredUser(user1));
    assertTrue(forum.hasRegisteredUser(user2));
}
```

By choosing readability over uniqueness in writing unit tests, there is code duplication, but the test flow is easier for a reader to follow.

#### Inline mocks in test code

Inlining mock return values in the unit test itself improves readability:

```javascript
it("getStudentCourseJoinStatus: should return true if student has joined the course", () => {
  jest
    .spyOn(courseService, "getJoinCourseStatus")
    .mockReturnValue(of({ hasJoined: true }));

  expect(student.getJoinCourseStatus).toBeTruthy();
});
```

By injecting the values in the test right before they are used, developers are able to more easily trace the code and understand the test.

### Frontend tests

#### Naming

Unit tests for a function should follow the format:

`"<function-name>: should ... when/if ..."`

Example:

```javascript
it("hasSection: should return false when there are no sections in the course");
```

#### Creating test data

To aid with [including only relevant details in tests](#include-only-relevant-details-in-tests), use the builder in `src/web/test-helpers/generic-builder.ts`

Usage:

```javascript
const instructorModelBuilder =
  createBuilder <
  InstructorListInfoTableRowModel >
  {
    email: "instructor@gmail.com",
    name: "Instructor",
    hasSubmittedSession: false,
    isSelected: false,
  };

it("isAllInstructorsSelected: should return false if at least one instructor !isSelected", () => {
  component.instructorListInfoTableRowModels = [
    instructorModelBuilder.isSelected(true).build(),
    instructorModelBuilder.isSelected(false).build(),
    instructorModelBuilder.isSelected(true).build(),
  ];

  expect(component.isAllInstructorsSelected).toBeFalsy();
});
```

#### Testing event emission

In Angular, child components emit events. To test for event emissions, we've provided a utility function in `src/test-helpers/test-event-emitter`

Usage:

```javascript
@Output()
deleteCommentEvent: EventEmitter<number> = new EventEmitter();

triggerDeleteCommentEvent(index: number): void {
  this.deleteCommentEvent.emit(index);
}

it('triggerDeleteCommentEvent: should emit the correct index to deleteCommentEvent', () => {
  let emittedIndex: number | undefined;
  testEventEmission(component.deleteCommentEvent, (index) => { emittedIndex = index; });

  component.triggerDeleteCommentEvent(5);
  expect(emittedIndex).toBe(5);
});
```

### Backend tests

#### Naming

Unit test names should follow the format: `test<functionName>_<scenario>_<outcome>`

Examples:

```java
public void testGetComment_commentDoesNotExist_returnsNull()
public void testCreateComment_commentDoesNotExist_success()
public void testCreateComment_commentAlreadyExists_throwsEntityAlreadyExistsException()
```

#### Creating test data

To aid with [including only relevant details in tests](#include-only-relevant-details-in-tests), use the `getTypicalX` functions in `BaseTestCase`, where X represents an entity.

Example:

```java
Account account = getTypicalAccount();
account.setEmail("newemail@teammates.com");

Student student = getTypicalStudent();
student.setName("New Student Name");
```
