package teammates.it.storage.sqlapi;

import java.util.ArrayList;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
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

    private FeedbackResponseComment prepareSqlInjectionTest() {
        FeedbackResponseComment frc = typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        assertNotNull(frcDb.getFeedbackResponseComment(frc.getId()));

        return frc;
    }

    private void checkSqlInjectionFailed(FeedbackResponseComment frc) {
        assertNotNull(frcDb.getFeedbackResponseComment(frc.getId()));
    }

    @Test
    public void testSqlInjectionInUpdateGiverEmailOfFeedbackResponseComments() {
        FeedbackResponseComment frc = prepareSqlInjectionTest();

        String sqli = "'; DELETE FROM feedback_response_comments;--";
        frcDb.updateGiverEmailOfFeedbackResponseComments(sqli, "", "");

        checkSqlInjectionFailed(frc);
    }

    @Test
    public void testSqlInjectionInUpdateLastEditorEmailOfFeedbackResponseComments() {
        FeedbackResponseComment frc = prepareSqlInjectionTest();

        String sqli = "'; DELETE FROM feedback_response_comments;--";
        frcDb.updateLastEditorEmailOfFeedbackResponseComments(sqli, "", "");

        checkSqlInjectionFailed(frc);
    }

    @Test
    public void testSqlInjectionInCreateFeedbackResponseComment() throws Exception {
        FeedbackResponseComment frc = prepareSqlInjectionTest();

        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        Section s = typicalDataBundle.sections.get("section2InCourse1");

        String sqli = "'');/**/DELETE/**/FROM/**/feedback_response_comments;--@gmail.com";
        FeedbackResponseComment newFrc = new FeedbackResponseComment(
                fr, "", FeedbackParticipantType.INSTRUCTORS, s, s, "",
                false, false,
                new ArrayList<FeedbackParticipantType>(), new ArrayList<FeedbackParticipantType>(), sqli);

        frcDb.createFeedbackResponseComment(newFrc);

        checkSqlInjectionFailed(frc);
    }

    @Test
    public void testSqlInjectionInUpdateFeedbackResponseComment() throws Exception {
        FeedbackResponseComment frc = prepareSqlInjectionTest();

        String sqli = "'');/**/DELETE/**/FROM/**/feedback_response_comments;--@gmail.com";
        frc.setLastEditorEmail(sqli);
        frcDb.updateFeedbackResponseComment(frc);

        checkSqlInjectionFailed(frc);
    }
}
