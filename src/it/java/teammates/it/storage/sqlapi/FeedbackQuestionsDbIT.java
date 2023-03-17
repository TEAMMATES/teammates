package teammates.it.storage.sqlapi;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * SUT: {@link FeedbackQuestionsDb}.
 */
public class FeedbackQuestionsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackQuestionsDb fqDb = FeedbackQuestionsDb.inst();

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
    public void testGetFeedbackQuestionsForSession() {
        ______TS("success: typical case");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4, fq5);

        List<FeedbackQuestion> actualQuestions = fqDb.getFeedbackQuestionsForSession(fs.getId());

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

}
