package teammates.it.storage.sqlapi;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;

/**
 * SUT: {@link FeedbackResponsesDb}.
 */
public class FeedbackResponsesDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackResponsesDb frDb = FeedbackResponsesDb.inst();

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
    public void testGetFeedbackResponsesFromGiverForQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion fq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackResponse fr = typicalDataBundle.feedbackResponses.get("response1ForQ1");

        List<FeedbackResponse> expectedQuestions = List.of(fr);

        List<FeedbackResponse> actualQuestions =
                frDb.getFeedbackResponsesFromGiverForQuestion(fq.getId(), "student1@teammates.tmt");

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

    @Test
    public void testDeleteFeedbackResponsesForQuestionCascade() {
        ______TS("success: typical case");
        FeedbackQuestion fq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackResponse fr1 = typicalDataBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponse fr2 = typicalDataBundle.feedbackResponses.get("response2ForQ1");

        frDb.deleteFeedbackResponsesForQuestionCascade(fq.getId());

        assertNull(frDb.getFeedbackResponse(fr1.getId()));
        assertNull(frDb.getFeedbackResponse(fr2.getId()));
    }
}
