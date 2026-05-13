package teammates.it.logic.core;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Section;

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
        assertNotNull(fr1);
        assertFalse(fr1.getFeedbackResponseComments().isEmpty());

        frLogic.deleteFeedbackResponsesAndCommentsCascade(fr1);

        assertNull(frLogic.getFeedbackResponse(fr1.getId()));
        assertNull(frcLogic.getFeedbackResponseComment(frcId));
    }

    @Test
    public void testUpdatedFeedbackResponsesAndCommentsCascade() throws Exception {
        ______TS("success: feedbackresponse and feedbackresponsecomment has been updated");
        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        fr = frLogic.getFeedbackResponse(fr.getId());

        Section newGiverSection = typicalDataBundle.sections.get("section1InCourse2");
        Section newRecipientSection = typicalDataBundle.sections.get("section2InCourse1");
        String newGiver = "new test giver";

        assertNotEquals(fr.getGiver(), newGiver);
        assertNotEquals(fr.getGiverSection(), newGiverSection);
        assertNotEquals(fr.getRecipientSection(), newRecipientSection);

        fr.setGiver(newGiver);
        fr.setGiverSection(newGiverSection);
        fr.setRecipientSection(newRecipientSection);

        fr = frLogic.updateFeedbackResponse(fr);

        fr = frLogic.getFeedbackResponse(fr.getId());
        assertEquals(fr.getGiver(), newGiver);
        assertEquals(fr.getGiverSection(), newGiverSection);
        assertEquals(fr.getRecipientSection(), newRecipientSection);
    }
}
