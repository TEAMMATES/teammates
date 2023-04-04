package teammates.it.storage.sqlapi;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.FeedbackResponseCommentsDb;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * SUT: {@link FeedbackResponsesDb}.
 */
public class FeedbackResponsesDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final FeedbackResponsesDb frDb = FeedbackResponsesDb.inst();
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
        HibernateUtil.clearSession();
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion fq = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackResponse fr1 = typicalDataBundle.feedbackResponses.get("response1ForQ1");

        List<FeedbackResponse> expectedQuestions = List.of(fr1);

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
        FeedbackResponseComment frc1 = typicalDataBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");

        frDb.deleteFeedbackResponsesForQuestionCascade(fq.getId());

        assertNull(frDb.getFeedbackResponse(fr1.getId()));
        assertNull(frDb.getFeedbackResponse(fr2.getId()));
        assertNull(frcDb.getFeedbackResponseComment(frc1.getId()));
    }

    @Test
    public void testHasResponsesFromGiverInSession() {
        ______TS("success: typical case");
        Course course = typicalDataBundle.courses.get("course1");
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");

        boolean actualHasReponses1 =
                frDb.hasResponsesFromGiverInSession("student1@teammates.tmt", fs.getName(), course.getId());

        assertTrue(actualHasReponses1);

        ______TS("student with no responses");
        boolean actualHasReponses2 =
                frDb.hasResponsesFromGiverInSession("studentnorespones@teammates.tmt", fs.getName(), course.getId());

        assertFalse(actualHasReponses2);
    }

    @Test
    public void testAreThereResponsesForQuestion() {
        ______TS("success: typical case");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        boolean actualResponse1 =
                frDb.areThereResponsesForQuestion(fq1.getId());

        assertTrue(actualResponse1);

        ______TS("feedback question with no responses");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn6InSession1InCourse1NoResponses");

        boolean actualResponse2 =
                frDb.areThereResponsesForQuestion(fq2.getId());

        assertFalse(actualResponse2);
    }

    @Test
    public void testHasResponsesForCourse() {
        ______TS("success: typical case");
        Course course = typicalDataBundle.courses.get("course1");

        boolean actual =
                frDb.hasResponsesForCourse(course.getId());

        assertTrue(actual);
    }
}
