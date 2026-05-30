package teammates.it.logic.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.ResponseInstructorCommentsLogic;
import teammates.storage.entity.FeedbackResponse;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class FeedbackResponsesLogicIT extends BaseTestCaseWithDatabaseAccess {
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final ResponseInstructorCommentsLogic frcLogic = ResponseInstructorCommentsLogic.inst();

    private DataBundle typicalDataBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testDeleteFeedbackResponsesAndCommentsCascade() {
        ______TS("success: typical case");
        FeedbackResponse fr1 = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        fr1 = frLogic.getFeedbackResponse(fr1.getId());
        UUID frcId = fr1.getResponseInstructorComments().iterator().next().getId();
        assertNotNull(fr1);
        assertFalse(fr1.getResponseInstructorComments().isEmpty());

        frLogic.deleteFeedbackResponsesAndCommentsCascade(fr1);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();

        assertNull(frLogic.getFeedbackResponse(fr1.getId()));
        assertNull(frcLogic.getResponseInstructorComment(frcId));
    }
}
