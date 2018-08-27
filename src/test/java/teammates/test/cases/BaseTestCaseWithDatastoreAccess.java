package teammates.test.cases;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.retry.RetryManager;
import teammates.test.driver.TestProperties;

/**
 * Base class for all test cases which are allowed to access the Datastore.
 */
public abstract class BaseTestCaseWithDatastoreAccess extends BaseTestCaseWithObjectifyAccess {

    private static final int VERIFICATION_RETRY_COUNT = 5;
    private static final int VERIFICATION_RETRY_DELAY_IN_MS = 1000;
    private static final int OPERATION_RETRY_COUNT = 5;
    private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;

    protected RetryManager persistenceRetryManager = new RetryManager(TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2);

    protected void verifyPresentInDatastore(DataBundle data) {
        data.accounts.values().forEach(this::verifyPresentInDatastore);

        data.instructors.values().forEach(this::verifyPresentInDatastore);

        data.courses.values().stream()
                .filter(course -> !course.isCourseDeleted())
                .forEach(this::verifyPresentInDatastore);

        data.students.values().forEach(this::verifyPresentInDatastore);
    }

    private EntityAttributes<?> getEntity(EntityAttributes<?> expected) {
        if (expected instanceof AccountAttributes) {
            return getAccount((AccountAttributes) expected);

        } else if (expected instanceof CourseAttributes) {
            return getCourse((CourseAttributes) expected);

        } else if (expected instanceof FeedbackQuestionAttributes) {
            return getFeedbackQuestion((FeedbackQuestionAttributes) expected);

        } else if (expected instanceof FeedbackResponseCommentAttributes) {
            return getFeedbackResponseComment((FeedbackResponseCommentAttributes) expected);

        } else if (expected instanceof FeedbackResponseAttributes) {
            return getFeedbackResponse((FeedbackResponseAttributes) expected);

        } else if (expected instanceof FeedbackSessionAttributes) {
            return getFeedbackSession((FeedbackSessionAttributes) expected);

        } else if (expected instanceof InstructorAttributes) {
            return getInstructor((InstructorAttributes) expected);

        } else if (expected instanceof StudentAttributes) {
            return getStudent((StudentAttributes) expected);

        } else {
            throw new RuntimeException("Unknown entity type!");
        }
    }

    protected void verifyAbsentInDatastore(EntityAttributes<?> entity) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        EntityAttributes<?> actual = getEntity(entity);
        while (actual != null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(entity);
        }
        assertNull(actual);
    }

    protected void verifyPresentInDatastore(EntityAttributes<?> expected) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        EntityAttributes<?> actual = getEntity(expected);
        while (actual == null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(expected);
        }
        verifyEquals(expected, actual);
    }

    private void verifyEquals(EntityAttributes<?> expected, EntityAttributes<?> actual) {
        if (expected instanceof AccountAttributes) {
            AccountAttributes expectedAccount = ((AccountAttributes) expected).getCopy();
            AccountAttributes actualAccount = (AccountAttributes) actual;
            equalizeIrrelevantData(expectedAccount, actualAccount);
            assertEquals(JsonUtils.toJson(expectedAccount), JsonUtils.toJson(actualAccount));

        } else if (expected instanceof CourseAttributes) {
            CourseAttributes expectedCourse = (CourseAttributes) expected;
            CourseAttributes actualCourse = (CourseAttributes) actual;
            equalizeIrrelevantData(expectedCourse, actualCourse);
            assertEquals(JsonUtils.toJson(expectedCourse), JsonUtils.toJson(actualCourse));

        } else if (expected instanceof FeedbackQuestionAttributes) {
            FeedbackQuestionAttributes expectedFq = (FeedbackQuestionAttributes) expected;
            FeedbackQuestionAttributes actualFq = (FeedbackQuestionAttributes) actual;
            equalizeIrrelevantData(expectedFq, actualFq);
            assertEquals(JsonUtils.toJson(expectedFq), JsonUtils.toJson(actualFq));

        } else if (expected instanceof FeedbackResponseCommentAttributes) {
            FeedbackResponseCommentAttributes expectedFrc = (FeedbackResponseCommentAttributes) expected;
            FeedbackResponseCommentAttributes actualFrc = (FeedbackResponseCommentAttributes) actual;
            assertEquals(expectedFrc.courseId, actualFrc.courseId);
            assertEquals(expectedFrc.commentGiver, actualFrc.commentGiver);
            assertEquals(expectedFrc.feedbackSessionName, actualFrc.feedbackSessionName);
            assertEquals(expectedFrc.commentText, actualFrc.commentText);

        } else if (expected instanceof FeedbackResponseAttributes) {
            FeedbackResponseAttributes expectedFr = (FeedbackResponseAttributes) expected;
            FeedbackResponseAttributes actualFr = (FeedbackResponseAttributes) actual;
            equalizeIrrelevantData(expectedFr, actualFr);
            assertEquals(JsonUtils.toJson(expectedFr), JsonUtils.toJson(actualFr));

        } else if (expected instanceof FeedbackSessionAttributes) {
            FeedbackSessionAttributes expectedFs = ((FeedbackSessionAttributes) expected).getCopy();
            FeedbackSessionAttributes actualFs = (FeedbackSessionAttributes) actual;
            equalizeIrrelevantData(expectedFs, actualFs);
            assertEquals(JsonUtils.toJson(expectedFs), JsonUtils.toJson(actualFs));

        } else if (expected instanceof InstructorAttributes) {
            InstructorAttributes expectedInstructor = ((InstructorAttributes) expected).getCopy();
            InstructorAttributes actualInstructor = (InstructorAttributes) actual;
            equalizeIrrelevantData(expectedInstructor, actualInstructor);
            assertTrue(expectedInstructor.isEqualToAnotherInstructor(actualInstructor));

        } else if (expected instanceof StudentAttributes) {
            StudentAttributes expectedStudent = ((StudentAttributes) expected).getCopy();
            StudentAttributes actualStudent = (StudentAttributes) actual;
            equalizeIrrelevantData(expectedStudent, actualStudent);
            assertEquals(JsonUtils.toJson(expectedStudent), JsonUtils.toJson(actualStudent));

        } else {
            throw new RuntimeException("Unknown entity type!");
        }
    }

    protected abstract AccountAttributes getAccount(AccountAttributes account);

    private void equalizeIrrelevantData(AccountAttributes expected, AccountAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.createdAt = actual.createdAt;

        if (actual.studentProfile == null) {
            expected.studentProfile = null;
        } else {
            if (expected.studentProfile == null) {
                expected.studentProfile = StudentProfileAttributes.builder(actual.googleId).build();
            }
            expected.studentProfile.modifiedDate = actual.studentProfile.modifiedDate;
        }
    }

    protected abstract CourseAttributes getCourse(CourseAttributes course);

    private void equalizeIrrelevantData(CourseAttributes expected, CourseAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.createdAt = actual.createdAt;
    }

    protected abstract FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq);

    private void equalizeIrrelevantData(FeedbackQuestionAttributes expected, FeedbackQuestionAttributes actual) {
        expected.setId(actual.getId());
    }

    protected abstract FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc);

    protected abstract FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr);

    private void equalizeIrrelevantData(FeedbackResponseAttributes expected, FeedbackResponseAttributes actual) {
        expected.setId(actual.getId());
    }

    protected abstract FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs);

    private void equalizeIrrelevantData(FeedbackSessionAttributes expected, FeedbackSessionAttributes actual) {
        expected.setRespondingInstructorList(actual.getRespondingInstructorList());
        expected.setRespondingStudentList(actual.getRespondingStudentList());
    }

    protected abstract InstructorAttributes getInstructor(InstructorAttributes instructor);

    private void equalizeIrrelevantData(InstructorAttributes expected, InstructorAttributes actual) {
        // pretend keys match because the key is generated only before storing into database
        if (actual.key != null) {
            expected.key = actual.key;
        }
    }

    protected abstract StudentAttributes getStudent(StudentAttributes student);

    private void equalizeIrrelevantData(StudentAttributes expected, StudentAttributes actual) {
        // For these fields, we consider null and "" equivalent.
        if (expected.googleId == null && actual.googleId.isEmpty()) {
            expected.googleId = "";
        }
        if (expected.team == null && actual.team.isEmpty()) {
            expected.team = "";
        }
        if (expected.comments == null && actual.comments.isEmpty()) {
            expected.comments = "";
        }

        // pretend keys match because the key is generated only before storing into database
        if (actual.key != null) {
            expected.key = actual.key;
        }

        expected.lastName = StringHelper.splitName(expected.name)[1];
    }

    protected void removeAndRestoreDataBundle(DataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        String backDoorOperationStatus = doRemoveAndRestoreDataBundle(testData);
        while (!backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS) && retryLimit > 0) {
            retryLimit--;
            print("Re-trying removeAndRestoreDataBundle - " + backDoorOperationStatus);
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            backDoorOperationStatus = doRemoveAndRestoreDataBundle(testData);
        }
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
    }

    protected abstract String doRemoveAndRestoreDataBundle(DataBundle testData);

    protected void putDocuments(DataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        String backDoorOperationStatus = doPutDocuments(testData);
        while (!backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS) && retryLimit > 0) {
            retryLimit--;
            print("Re-trying putDocuments - " + backDoorOperationStatus);
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            backDoorOperationStatus = doPutDocuments(testData);
        }
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
    }

    protected abstract String doPutDocuments(DataBundle testData);

}
