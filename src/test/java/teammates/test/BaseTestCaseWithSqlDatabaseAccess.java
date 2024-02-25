package teammates.test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.ui.output.ApiOutput;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackResponseData;

/**
 * Base class for all test cases which are allowed to access the database.
 */
public abstract class BaseTestCaseWithSqlDatabaseAccess extends BaseTestCase {

    private static final int VERIFICATION_RETRY_COUNT = 5;
    private static final int VERIFICATION_RETRY_DELAY_IN_MS = 1000;
    private static final int OPERATION_RETRY_COUNT = 5;
    private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;

    /**
     * Removes and restores the databundle, with retries.
     */
    protected SqlDataBundle removeAndRestoreDataBundle(SqlDataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        SqlDataBundle dataBundle = doRemoveAndRestoreDataBundle(testData);
        while (dataBundle == null && retryLimit > 0) {
            retryLimit--;
            print("Re-trying removeAndRestoreDataBundle");
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            dataBundle = doRemoveAndRestoreDataBundle(testData);
        }
        assertNotNull(dataBundle);
        return dataBundle;
    }

    protected abstract SqlDataBundle doRemoveAndRestoreDataBundle(SqlDataBundle testData);

    /**
     * Verifies that two entities are equal.
     */
    protected void verifyEquals(BaseEntity expected, ApiOutput actual) {
        if (expected instanceof FeedbackQuestion) {
            FeedbackQuestion expectedQuestion = (FeedbackQuestion) expected;
            FeedbackQuestionDetails expectedQuestionDetails = expectedQuestion.getQuestionDetailsCopy();
            FeedbackQuestionData actualQuestion = (FeedbackQuestionData) actual;
            assertEquals(expectedQuestion.getQuestionNumber(), (Integer) actualQuestion.getQuestionNumber());
            assertEquals(expectedQuestionDetails.getQuestionText(), actualQuestion.getQuestionBrief());
            assertEquals(expectedQuestion.getDescription(), actualQuestion.getQuestionDescription());
            assertEquals(expectedQuestionDetails.getQuestionType(), actualQuestion.getQuestionType());
            assertEquals(expectedQuestion.getGiverType(), actualQuestion.getGiverType());
            assertEquals(expectedQuestion.getRecipientType(), actualQuestion.getRecipientType());
            // TODO: compare the rest of the attributes D:
        } else if (expected instanceof FeedbackResponse) {
            FeedbackResponse expectedResponse = (FeedbackResponse) expected;
            FeedbackResponseDetails expectedResponseDetails = expectedResponse.getFeedbackResponseDetailsCopy();
            FeedbackResponseData actualResponse = (FeedbackResponseData) actual;
            assertEquals(expectedResponse.getGiver(), actualResponse.getGiverIdentifier());
            assertEquals(expectedResponse.getRecipient(), actualResponse.getRecipientIdentifier());
            assertEquals(expectedResponseDetails.getAnswerString(), actualResponse.getResponseDetails().getAnswerString());
            // TODO: compare the rest of the attributes D:
        } else {
            fail("Unknown entity");
        }
    }

    /**
     * Verifies that the given entity is present in the database.
     */
    protected void verifyPresentInDatabase(BaseEntity expected) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        ApiOutput actual = getEntity(expected);
        while (actual == null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(expected);
        }
        verifyEquals(expected, actual);
    }

    private ApiOutput getEntity(BaseEntity entity) {
        if (entity instanceof FeedbackQuestion) {
            return getFeedbackQuestion((FeedbackQuestion) entity);
        } else if (entity instanceof FeedbackResponse) {
            return getFeedbackResponse((FeedbackResponse) entity);
        } else {
            throw new RuntimeException("Unknown entity type");
        }
    }

    protected abstract FeedbackQuestionData getFeedbackQuestion(FeedbackQuestion fq);

    protected abstract FeedbackResponseData getFeedbackResponse(FeedbackResponse fq);

}
