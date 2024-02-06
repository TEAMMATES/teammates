package teammates.it.sqllogic.core;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.FeedbackResponseCommentsLogic;
import teammates.sqllogic.core.FeedbackResponsesLogic;
import teammates.storage.sqlentity.FeedbackResponse;

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
}
