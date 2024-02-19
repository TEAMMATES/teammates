package teammates.it.sqllogic.core;

import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.FeedbackResponseCommentsLogic;
import teammates.sqllogic.core.FeedbackResponsesLogic;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Section;

/**
 * SUT: {@link FeedbackResponsesLogic}.
 */
public class FeedbackResponsesLogicIT extends BaseTestCaseWithSqlDatabaseAccess {
    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();

    private SqlDataBundle typicalDataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalDataBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testDeleteFeedbackResponsesAndCommentsCascade() {
        ______TS("success: typical case");
        FeedbackResponse fr1 = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        fr1 = frLogic.getFeedbackResponse(fr1.getId());
        assertNotNull(fr1);
        assertFalse(frcLogic.getFeedbackResponseCommentsForResponse(fr1.getId()).isEmpty());

        frLogic.deleteFeedbackResponsesAndCommentsCascade(fr1);

        assertNull(frLogic.getFeedbackResponse(fr1.getId()));
        assertTrue(frcLogic.getFeedbackResponseCommentsForResponse(fr1.getId()).isEmpty());
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

    // TODO: Enable test after fixing automatic persist cascade of feedbackResponse to feedbackResponseComments
    @Test(enabled = false)
    public void testUpdatedFeedbackResponsesAndCommentsCascade_noChangeToResponseSection_shouldNotUpdateComments()
            throws Exception {
        ______TS("Cascading to feedbackResponseComments should not trigger");
        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        fr = frLogic.getFeedbackResponse(fr.getId());
        List<FeedbackResponseComment> oldComments = fr.getFeedbackResponseComments();

        Section newGiverSection = typicalDataBundle.sections.get("section2InCourse1");
        Section newRecipientSection = typicalDataBundle.sections.get("section2InCourse1");
        String newGiver = "new test giver";

        for (FeedbackResponseComment oldFrc : oldComments) {
            assertNotEquals(oldFrc.getGiverSection(), newGiverSection);
            assertNotEquals(oldFrc.getRecipientSection(), newRecipientSection);
        }
        assertNotEquals(fr.getGiver(), newGiver);

        // feedbackResponseComments were changed, but sections on feedbackResponse not changed
        for (FeedbackResponseComment frc : oldComments) {
            frc.setGiverSection(newGiverSection);
            frc.setRecipientSection(newRecipientSection);
        }
        fr.setUpdatedAt(Instant.now());

        fr = frLogic.updateFeedbackResponseCascade(fr);
        fr = frLogic.getFeedbackResponse(fr.getId());

        List<FeedbackResponseComment> updatedComments = fr.getFeedbackResponseComments();
        for (FeedbackResponseComment updatedFrc : updatedComments) {
            assertNotEquals(updatedFrc.getGiverSection(), newGiverSection);
            assertNotEquals(updatedFrc.getRecipientSection(), newRecipientSection);
        }
        assertEquals(fr.getGiver(), newGiver);
    }
}
