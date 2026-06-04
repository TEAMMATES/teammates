package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.FeedbackResponse;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class FeedbackResponsesLogicIT extends BaseTestCaseWithDatabaseAccess {
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final ResponseInstructorCommentsLogic frcLogic = ResponseInstructorCommentsLogic.inst();

    private DataBundle typicalDataBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testDeleteFeedbackResponsesAndCommentsCascade() {
        ______TS("success: typical case");
        FeedbackResponse fr1 = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        UUID frId = fr1.getId();
        UUID frcId = inTransaction(() -> {
            FeedbackResponse loadedFr1 = frLogic.getFeedbackResponse(frId);
            assertNotNull(loadedFr1);
            assertFalse(loadedFr1.getResponseInstructorComments().isEmpty());
            UUID responseInstructorCommentId = loadedFr1.getResponseInstructorComments().iterator().next().getId();
            frLogic.deleteFeedbackResponsesAndCommentsCascade(loadedFr1);
            return responseInstructorCommentId;
        });

        assertNull(inTransaction(() -> frLogic.getFeedbackResponse(frId)));
        assertNull(inTransaction(() -> frcLogic.getResponseInstructorComment(frcId)));
    }
}
