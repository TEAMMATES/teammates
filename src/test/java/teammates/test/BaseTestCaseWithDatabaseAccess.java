package teammates.test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.JsonUtils;

/**
 * Base class for all test cases which are allowed to access the database.
 */
public abstract class BaseTestCaseWithDatabaseAccess extends BaseTestCase {

    private static final int VERIFICATION_RETRY_COUNT = 5;
    private static final int VERIFICATION_RETRY_DELAY_IN_MS = 1000;
    private static final int OPERATION_RETRY_COUNT = 5;
    private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;

    protected void verifyPresentInDatabase(DataBundle data) {
        data.accounts.values().forEach(this::verifyPresentInDatabase);

        data.instructors.values().forEach(this::verifyPresentInDatabase);

        data.courses.values().stream()
                .filter(course -> !course.isCourseDeleted())
                .forEach(this::verifyPresentInDatabase);

        data.students.values().forEach(this::verifyPresentInDatabase);
    }

    protected void verifyPresentInDatabase(EntityAttributes<?> expected) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        EntityAttributes<?> actual = getEntity(expected);
        while (actual == null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(expected);
        }
        verifyEquals(expected, actual);
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

        } else if (expected instanceof AccountRequestAttributes) {
            return getAccountRequest((AccountRequestAttributes) expected);

        } else if (expected instanceof DeadlineExtensionAttributes) {
            return getDeadlineExtension((DeadlineExtensionAttributes) expected);

        } else if (expected instanceof NotificationAttributes) {
            return getNotification((NotificationAttributes) expected);

        } else {
            throw new RuntimeException("Unknown entity type!");
        }
    }

    protected void verifyAbsentInDatabase(EntityAttributes<?> entity) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        EntityAttributes<?> actual = getEntity(entity);
        while (actual != null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(entity);
        }
        assertNull(actual);
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
            assertEquals(expectedFrc.getCourseId(), actualFrc.getCourseId());
            assertEquals(expectedFrc.getCommentGiver(), actualFrc.getCommentGiver());
            assertEquals(expectedFrc.getFeedbackSessionName(), actualFrc.getFeedbackSessionName());
            assertEquals(expectedFrc.getCommentText(), actualFrc.getCommentText());

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
            assertEquals(JsonUtils.toJson(expectedInstructor), JsonUtils.toJson(actualInstructor));

        } else if (expected instanceof StudentAttributes) {
            StudentAttributes expectedStudent = ((StudentAttributes) expected).getCopy();
            StudentAttributes actualStudent = (StudentAttributes) actual;
            equalizeIrrelevantData(expectedStudent, actualStudent);
            assertEquals(JsonUtils.toJson(expectedStudent), JsonUtils.toJson(actualStudent));

        } else if (expected instanceof AccountRequestAttributes) {
            AccountRequestAttributes expectedAccountRequest = (AccountRequestAttributes) expected;
            AccountRequestAttributes actualAccountRequest = (AccountRequestAttributes) actual;
            assertEquals(JsonUtils.toJson(expectedAccountRequest), JsonUtils.toJson(actualAccountRequest));

        } else if (expected instanceof DeadlineExtensionAttributes) {
            DeadlineExtensionAttributes expectedDeadlineExtension = (DeadlineExtensionAttributes) expected;
            DeadlineExtensionAttributes actualDeadlineExtension = (DeadlineExtensionAttributes) actual;
            equalizeIrrelevantData(expectedDeadlineExtension, actualDeadlineExtension);
            assertEquals(JsonUtils.toJson(expectedDeadlineExtension), JsonUtils.toJson(actualDeadlineExtension));

        } else if (expected instanceof NotificationAttributes) {
            NotificationAttributes expectedNotification = (NotificationAttributes) expected;
            NotificationAttributes actualNotification = (NotificationAttributes) actual;
            equalizeIrrelevantData(expectedNotification, actualNotification);
            assertEquals(JsonUtils.toJson(expectedNotification), JsonUtils.toJson(actualNotification));

        } else {
            throw new RuntimeException("Unknown entity type!");
        }
    }

    protected abstract AccountAttributes getAccount(AccountAttributes account);

    private void equalizeIrrelevantData(AccountAttributes expected, AccountAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
    }

    private void equalizeIrrelevantData(CourseAttributes expected, CourseAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
    }

    private void equalizeIrrelevantData(FeedbackQuestionAttributes expected, FeedbackQuestionAttributes actual) {
        expected.setId(actual.getId());
    }

    private void equalizeIrrelevantData(FeedbackResponseAttributes expected, FeedbackResponseAttributes actual) {
        expected.setId(actual.getId());
    }

    private void equalizeIrrelevantData(FeedbackSessionAttributes expected, FeedbackSessionAttributes actual) {
        expected.setCreatedTime(actual.getCreatedTime());
        // Not available in FeedbackSessionData and thus ignored
        expected.setCreatorEmail(actual.getCreatorEmail());
    }

    private void equalizeIrrelevantData(InstructorAttributes expected, InstructorAttributes actual) {
        // pretend keys match because the key is generated only before storing into database
        if (actual.getKey() != null) {
            expected.setKey(actual.getKey());
        }
    }

    private void equalizeIrrelevantData(StudentAttributes expected, StudentAttributes actual) {
        // For these fields, we consider null and "" equivalent.
        if (expected.getGoogleId() == null && actual.getGoogleId().isEmpty()) {
            expected.setGoogleId("");
        }
        if (expected.getTeam() == null && actual.getTeam().isEmpty()) {
            expected.setTeam("");
        }
        if (expected.getComments() == null && actual.getComments().isEmpty()) {
            expected.setComments("");
        }

        // pretend keys match because the key is generated only before storing into database
        if (actual.getKey() != null) {
            expected.setKey(actual.getKey());
        }
    }

    private void equalizeIrrelevantData(DeadlineExtensionAttributes expected, DeadlineExtensionAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    private void equalizeIrrelevantData(NotificationAttributes expected, NotificationAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
        expected.setUpdatedAt(actual.getUpdatedAt());
    }

    protected abstract CourseAttributes getCourse(CourseAttributes course);

    protected abstract FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq);

    protected abstract FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc);

    protected abstract FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr);

    protected abstract FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs);

    protected abstract InstructorAttributes getInstructor(InstructorAttributes instructor);

    protected abstract StudentAttributes getStudent(StudentAttributes student);

    protected abstract AccountRequestAttributes getAccountRequest(AccountRequestAttributes accountRequest);

    protected abstract DeadlineExtensionAttributes getDeadlineExtension(DeadlineExtensionAttributes accountRequest);

    protected abstract NotificationAttributes getNotification(NotificationAttributes notification);

    protected void removeAndRestoreDataBundle(DataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        boolean isOperationSuccess = doRemoveAndRestoreDataBundle(testData);
        while (!isOperationSuccess && retryLimit > 0) {
            retryLimit--;
            print("Re-trying removeAndRestoreDataBundle");
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            isOperationSuccess = doRemoveAndRestoreDataBundle(testData);
        }
        assertTrue(isOperationSuccess);
    }

    protected abstract boolean doRemoveAndRestoreDataBundle(DataBundle testData);

    protected void putDocuments(DataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        boolean isOperationSuccess = doPutDocuments(testData);
        while (!isOperationSuccess && retryLimit > 0) {
            retryLimit--;
            print("Re-trying putDocuments");
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            isOperationSuccess = doPutDocuments(testData);
        }
        assertTrue(isOperationSuccess);
    }

    protected abstract boolean doPutDocuments(DataBundle testData);

}
