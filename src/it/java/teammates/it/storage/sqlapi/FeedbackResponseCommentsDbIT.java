package teammates.it.storage.sqlapi;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackResponseCommentsDb;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Section;

/**
 * SUT: {@link FeedbackResponseCommentsDb}.
 */
public class FeedbackResponseCommentsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackResponseCommentsDb frcDb = FeedbackResponseCommentsDb.inst();

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
    }

    @Test
    public void testGetFeedbackResponseCommentForResponseFromParticipant() {
        ______TS("success: typical case");
        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");

        FeedbackResponseComment expectedComment = typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        FeedbackResponseComment actualComment = frcDb.getFeedbackResponseCommentForResponseFromParticipant(fr.getId());

        assertEquals(expectedComment, actualComment);
    }

    @Test
    public void testUpdateFeedbackResponseComment_noChangeToComment_shouldNotIssueSaveRequest() throws Exception {
        FeedbackResponseComment frc = typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");

        ______TS("OPTIMIZED_SAVING_POLICY_APPLIED should trigger on original");
        // please verify the log message manually to ensure that saving request is not issued
        FeedbackResponseComment updatedComment = frcDb.updateFeedbackResponseComment(frc);
        assertEquals(JsonUtils.toJson(frc), JsonUtils.toJson(updatedComment));

        updatedComment = new FeedbackResponseComment(
            frc.getFeedbackResponse(),
            frc.getGiver(),
            frc.getGiverType(),
            frc.getGiverSection(),
            frc.getRecipientSection(),
            frc.getCommentText(),
            frc.getIsVisibilityFollowingFeedbackQuestion(),
            frc.getIsCommentFromFeedbackParticipant(),
            frc.getShowCommentTo(),
            frc.getShowGiverNameTo(),
            frc.getLastEditorEmail()
        );
        updatedComment.setId(frc.getId());
        updatedComment.setCreatedAt(frc.getCreatedAt());
        updatedComment.setUpdatedAt(frc.getUpdatedAt());

        ______TS("OPTIMIZED_SAVING_POLICY_APPLIED should trigger on copy");
        // please verify the log message manually to ensure that saving request is not issued
        frcDb.updateFeedbackResponseComment(updatedComment);
        assertEquals(JsonUtils.toJson(frc), JsonUtils.toJson(updatedComment));
    }

    @Test
    private void testUpdateFeedbackResponseComment_shouldThrowAndReturnErrors() throws Exception {
        ______TS("null parameter");

        assertThrows(AssertionError.class, () -> frcDb.updateFeedbackResponseComment(null));

        ______TS("non-existent comment");

        FeedbackResponseComment feedbackResponseCommentNotPersisted = new FeedbackResponseComment(
                null, getTestDataFolder(), null, null, null, getTestDataFolder(), false, false, null, null, getTestDataFolder());
        feedbackResponseCommentNotPersisted.setId(null);

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcDb.updateFeedbackResponseComment(feedbackResponseCommentNotPersisted));
        assertEquals(ERROR_UPDATE_NON_EXISTENT + feedbackResponseCommentNotPersisted, ednee.getMessage());
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateFeedbackResponseComment_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        FeedbackResponseComment typicalComment =
                typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");

        ______TS("Change and update ID field");
        UUID newIdToSet = UUID.randomUUID();
        assertNotEquals(newIdToSet, typicalComment.getFeedbackResponse().getId());
        typicalComment.getFeedbackResponse().setId(newIdToSet);
        FeedbackResponseComment updatedComment = frcDb.updateFeedbackResponseComment(typicalComment);
        FeedbackResponseComment actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(newIdToSet, updatedComment.getFeedbackResponse().getId());
        assertEquals(newIdToSet, actualComment.getFeedbackResponse().getId());

        ______TS("Change and update commentText field");
        String newCommentToSet = "This is new Text";
        assertNotEquals(newCommentToSet, typicalComment.getCommentText());
        typicalComment.setCommentText(newCommentToSet);
        updatedComment = frcDb.updateFeedbackResponseComment(typicalComment);
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(newCommentToSet, updatedComment.getCommentText());
        assertEquals(newCommentToSet, actualComment.getCommentText());

        ______TS("Change and update showCommentTo field");
        List<FeedbackParticipantType> newShowCommentToToSet = Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS);
        assertNotEquals(newShowCommentToToSet, typicalComment.getShowCommentTo());
        typicalComment.setShowCommentTo(newShowCommentToToSet);
        updatedComment = frcDb.updateFeedbackResponseComment(typicalComment);
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(newShowCommentToToSet, updatedComment.getShowCommentTo());
        assertEquals(newShowCommentToToSet, actualComment.getShowCommentTo());

        ______TS("Change and update showGiverNameTo field");
        List<FeedbackParticipantType> newShowGiverNameToToSet = Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS);
        assertNotEquals(newShowGiverNameToToSet, typicalComment.getShowGiverNameTo());
        typicalComment.setShowGiverNameTo(newShowGiverNameToToSet);
        updatedComment = frcDb.updateFeedbackResponseComment(typicalComment);
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(newShowGiverNameToToSet, updatedComment.getShowGiverNameTo());
        assertEquals(newShowGiverNameToToSet, actualComment.getShowGiverNameTo());

        ______TS("Change and update getLastEditorEmail field");
        String newLastEditorEmailToSet = "editor1@email.com";
        assertNotEquals(newLastEditorEmailToSet, typicalComment.getLastEditorEmail());
        typicalComment.setLastEditorEmail(newLastEditorEmailToSet);
        updatedComment = frcDb.updateFeedbackResponseComment(typicalComment);
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(newLastEditorEmailToSet, updatedComment.getLastEditorEmail());
        assertEquals(newLastEditorEmailToSet, actualComment.getLastEditorEmail());

        ______TS("Change and update getUpdatedAt field");
        Instant newUpdatedAtToSet = Instant.ofEpochMilli(1000);
        assertNotEquals(newUpdatedAtToSet, typicalComment.getUpdatedAt());
        typicalComment.setUpdatedAt(newUpdatedAtToSet);
        updatedComment = frcDb.updateFeedbackResponseComment(typicalComment);
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(newUpdatedAtToSet, updatedComment.getUpdatedAt());
        assertEquals(newUpdatedAtToSet, actualComment.getUpdatedAt());

        ______TS("Change and update giverSection field");
        Section newGiverSectionToSet = typicalDataBundle.sections.get("course1");
        assertNotEquals(newGiverSectionToSet, typicalComment.getGiverSection());
        typicalComment.setGiverSection(newGiverSectionToSet);
        updatedComment = frcDb.updateFeedbackResponseComment(typicalComment);
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(newGiverSectionToSet, updatedComment.getGiverSection());
        assertEquals(newGiverSectionToSet, actualComment.getGiverSection());

        ______TS("Change and update recipientSection field");
        Section newRecipientSectionToSet = typicalDataBundle.sections.get("course1");
        assertNotEquals(newRecipientSectionToSet, typicalComment.getRecipientSection());
        typicalComment.setRecipientSection(newRecipientSectionToSet);
        updatedComment = frcDb.updateFeedbackResponseComment(typicalComment);
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(newRecipientSectionToSet, updatedComment.getRecipientSection());
        assertEquals(newRecipientSectionToSet, actualComment.getRecipientSection());
    }
}
