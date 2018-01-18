# Measuring the scalability and performance of TEAMMATES
Author: [Samson Tan Min Rong](bit.ly/samsontanresume)
## Problem
TEAMMATES has grown much larger since the [previous technical report on scalability and performance](https://docs.google.com/document/pub?id=1C7fn11fKsgGUx0AT_nH9ZQBi3G7o5zpYqwIIAC40CxU&embedded=true) was written, not just in terms of users, but developers as well. In fact, it has grown past the 100K mark which was the maximum load tested in the aforementioned report (160K unique users as of 4th April 2017). Therefore, we require a mechanism to prevent regression in terms of performance and scalability as our user and developer base continues to expand. Although we currently have extensive tests in place to check for correctness, we lack tests that check for regression in terms of performance and scalability. In this report, I propose some possible ways of implementing these performance tests.

## Proposed solution
There are two main areas where scalability and performance bottlenecks can occur: the UI and the Storage component. Simple tests, like measuring the time taken, can be conducted to measure the combined performance of the UI and the Storage component (reading/writing from/to the Datastore) under increasing load.

If conducted regularly, this will help maintainers catch regressions early.

## Current implementation of solution
I have identified two overload-prone pages, InstructorCourseEnrollPage and InstructorFeedbackResultsPage, that will benefit from performance and scalability tests. The scalability tests are similar to the UI tests TEAMMATES currently has, since our current focus is on measuring end-to-end performance. Accompanying the tests are data generators to create the test data required for the tests to run.

#### Commonalities
The scalability tests for both pages measure the time taken for the action (enrolling students/loading responses) to be completed under increasing load (increasing number of students/responses). These data will need to be generated before running the tests.

#### InstructorCourseEnrollPage test and data generator Implementation
Since the primary action on this page is enrolling students via text input, the test data generator simply generates text files for each of the cases.

#### InstructorFeedbackResultsPage test and data generator Implementation
The test loads a JSON file in a similar manner to the UI tests at each iteration. The test data generator creates JSON files based on the ones used by the UI test using Google.Gson, but with large numbers of students, questions and responses.

## Future direction

#### Convert measures into tests
At the moment, the scalability tests only measure and report the time taken. In the future, these measures can be extended into tests that will fail or warn maintainers when performance has regressed beyond a preset point. These tests need not be part of the continuous integration tests and need only be run before deployment to detect regression.

#### Storage component tests
Since reading and writing from/to the datastore constitutes a major proportion of TEAMMATES’ operations, tests to measure performance and scalability of the storage component would be a good direction to head in as well. Furthermore, there is a time limit of 60 seconds for request operations and some of TEAMMATES operations are close to, or are already occasionally, exceeding this time limit.
