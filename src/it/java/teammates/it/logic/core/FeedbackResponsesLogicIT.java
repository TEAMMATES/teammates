package teammates.it.logic.core;

import org.junit.jupiter.api.Assertions;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class FeedbackResponsesLogicIT extends BaseTestCaseWithDatabaseAccess {
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();

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
        UUID frcId = fr1.getFeedbackResponseComments().iterator().next().getId();
        Assertions.assertNotNull(fr1);
        Assertions.assertFalse(fr1.getFeedbackResponseComments().isEmpty());

        frLogic.deleteFeedbackResponsesAndCommentsCascade(fr1);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();

        Assertions.assertNull(frLogic.getFeedbackResponse(fr1.getId()));
        Assertions.assertNull(frcLogic.getFeedbackResponseComment(frcId));
    }

    @Test
    public void testUpdatedFeedbackResponsesAndCommentsCascade() throws Exception {
        ______TS("success: feedbackresponse and feedbackresponsecomment has been updated");
        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        fr = frLogic.getFeedbackResponse(fr.getId());

        Student newGiver = typicalDataBundle.students.get("student2InCourse1");
        Student newRecipient = typicalDataBundle.students.get("student4InCourse1");

        Assertions.assertNotEquals(fr.getGiver().getIdentifier(), newGiver.getEmail());
        Assertions.assertNotEquals(fr.getRecipient().getIdentifier(), newRecipient.getEmail());

        fr.setGiver(new ResponseGiver(newGiver));
        fr.setRecipient(new ResponseRecipient(newRecipient));

        fr = frLogic.updateFeedbackResponse(fr);

        fr = frLogic.getFeedbackResponse(fr.getId());
        Assertions.assertEquals(fr.getGiver().getIdentifier(), newGiver.getEmail());
        Assertions.assertEquals(fr.getRecipient().getIdentifier(), newRecipient.getEmail());
    }
}
