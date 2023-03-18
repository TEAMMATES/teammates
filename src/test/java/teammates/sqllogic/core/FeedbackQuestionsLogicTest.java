package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackQuestionsLogic}.
 */
public class FeedbackQuestionsLogicTest extends BaseTestCase {

    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private CoursesLogic coursesLogic;
    private UsersLogic usersLogic;

    private FeedbackQuestionsDb fqDb;

    private SqlDataBundle typicalDataBundle;

    @BeforeClass
    public void setUpClass() {
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @BeforeMethod
    public void setUpMethod() {
        fqDb = mock(FeedbackQuestionsDb.class);
        coursesLogic = mock(CoursesLogic.class);
        usersLogic = mock(UsersLogic.class);
        fqLogic.initLogicDependencies(fqDb, coursesLogic, usersLogic);
    }

    @Test(enabled = false)
    public void testGetFeedbackQuestionsForSession_questionNumbersInOrder_success() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");

        List<FeedbackQuestion> questions = List.of(fq1, fq2, fq3, fq4, fq5);
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questions);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForSession(fs);

        assertEquals(questions.size(), actualQuestions.size());
        assertTrue(questions.containsAll(actualQuestions));
    }

    @Test(enabled = false)
    public void testGetFeedbackQuestionsForSession_questionNumbersOutOfOrder_success() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");

        List<FeedbackQuestion> questions = List.of(fq2, fq4, fq3, fq1, fq5);
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questions);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForSession(fs);

        assertEquals(questions.size(), actualQuestions.size());
        assertTrue(questions.containsAll(actualQuestions));
    }

    @Test(enabled = false)
    public void testCreateFeedbackQuestion_questionNumbersAreConsistent_canCreateFeedbackQuestion()
            throws InvalidParametersException {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");

        List<FeedbackQuestion> questionsBefore = List.of(fq1, fq2, fq3, fq4);
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questionsBefore);

        FeedbackQuestion createdQuestion = fqLogic.createFeedbackQuestion(fq5);

        assertEquals(fq5, createdQuestion);
    }

    @Test(enabled = false)
    public void testCreateFeedbackQuestion_questionNumbersAreInconsistent_canCreateFeedbackQuestion()
            throws InvalidParametersException {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        fq1.setQuestionNumber(2);
        fq2.setQuestionNumber(3);
        fq3.setQuestionNumber(4);
        fq4.setQuestionNumber(5);

        List<FeedbackQuestion> questionsBefore = List.of(fq1, fq2, fq3, fq4);
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questionsBefore);

        FeedbackQuestion createdQuestion = fqLogic.createFeedbackQuestion(fq5);

        assertEquals(fq5, createdQuestion);
    }

    @Test(enabled = false)
    public void testCreateFeedbackQuestion_oldQuestionNumberLargerThanNewQuestionNumber_adjustQuestionNumberCorrectly()
            throws InvalidParametersException {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        fq1.setQuestionNumber(2);
        fq2.setQuestionNumber(3);
        fq3.setQuestionNumber(4);
        fq4.setQuestionNumber(5);

        List<FeedbackQuestion> questionsBefore = List.of(fq1, fq2, fq3, fq4);
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questionsBefore);

        fqLogic.createFeedbackQuestion(fq5);

        assertEquals(1, fq1.getQuestionNumber().intValue());
        assertEquals(2, fq2.getQuestionNumber().intValue());
        assertEquals(3, fq3.getQuestionNumber().intValue());
        assertEquals(4, fq4.getQuestionNumber().intValue());
    }

    @Test(enabled = false)
    public void testCreateFeedbackQuestion_oldQuestionNumberSmallerThanNewQuestionNumber_adjustQuestionNumberCorrectly()
            throws InvalidParametersException {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        fq1.setQuestionNumber(0);
        fq2.setQuestionNumber(1);
        fq3.setQuestionNumber(2);
        fq4.setQuestionNumber(3);

        List<FeedbackQuestion> questionsBefore = List.of(fq1, fq2, fq3, fq4);
        fs.setId(UUID.randomUUID());
        when(fqDb.getFeedbackQuestionsForSession(fs.getId())).thenReturn(questionsBefore);

        fqLogic.createFeedbackQuestion(fq5);

        assertEquals(1, fq1.getQuestionNumber().intValue());
        assertEquals(2, fq2.getQuestionNumber().intValue());
        assertEquals(3, fq3.getQuestionNumber().intValue());
        assertEquals(4, fq4.getQuestionNumber().intValue());
    }

    @Test(enabled = false)
    public void testGetFeedbackQuestionsForStudents() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForStudents(fs);
        
        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(actualQuestions.containsAll(actualQuestions));
    }

    @Test(enabled = false)
    public void testGetFeedbackQuestionsForInstructors() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq3, fq4, fq5);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForInstructors(fs, "instr1@teammates.tmt");
        
        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(actualQuestions.containsAll(actualQuestions));
    }
}
