package teammates.it.logic.core;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.entity.FeedbackResponse;
import teammates.logic.entity.FeedbackResponseComment;
import teammates.logic.entity.Section;

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
        UUID frcId = fr1.getFeedbackResponseComments().get(0).getId();
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
        List<FeedbackResponseComment> oldComments = fr.getFeedbackResponseComments();

        Section newGiverSection = typicalDataBundle.sections.get("section1InCourse2");
        Section newRecipientSection = typicalDataBundle.sections.get("section2InCourse1");
        String newGiver = "new test giver";

        for (FeedbackResponseComment frc : oldComments) {
            assertNotEquals(frc.getGiverSection(), newGiverSection);
            assertNotEquals(frc.getRecipientSection(), newRecipientSection);
        }
        assertNotEquals(fr.getGiver(), newGiver);
        assertNotEquals(fr.getGiverSection(), newGiverSection);
        assertNotEquals(fr.getRecipientSection(), newRecipientSection);

        for (FeedbackResponseComment frc : oldComments) {
            frc.setGiverSection(newGiverSection);
            frc.setRecipientSection(newRecipientSection);
        }
        fr.setGiver(newGiver);
        fr.setGiverSection(newGiverSection);
        fr.setRecipientSection(newRecipientSection);

        fr = frLogic.updateFeedbackResponseCascade(fr);

        fr = frLogic.getFeedbackResponse(fr.getId());
        List<FeedbackResponseComment> updatedComments = fr.getFeedbackResponseComments();
        for (FeedbackResponseComment frc : updatedComments) {
            assertEquals(frc.getGiverSection(), newGiverSection);
            assertEquals(frc.getRecipientSection(), newRecipientSection);
        }
        assertEquals(fr.getGiver(), newGiver);
        assertEquals(fr.getGiverSection(), newGiverSection);
        assertEquals(fr.getRecipientSection(), newRecipientSection);
    }
}
