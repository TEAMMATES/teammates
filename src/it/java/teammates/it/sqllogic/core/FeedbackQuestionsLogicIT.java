package teammates.it.sqllogic.core;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.FeedbackQuestionsLogic;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.questions.FeedbackTextQuestion;

/**
 * SUT: {@link FeedbackQuestionsLogic}.
 */
public class FeedbackQuestionsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

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
    public void testCreateFeedbackQuestion() throws InvalidParametersException {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackTextQuestionDetails newQuestionDetails = new FeedbackTextQuestionDetails("New question text.");
        List<FeedbackParticipantType> showTos = new ArrayList<>();
        showTos.add(FeedbackParticipantType.INSTRUCTORS);
        FeedbackQuestion newQuestion = new FeedbackTextQuestion(fs, 6, "This is a new text question",
                FeedbackQuestionType.TEXT, FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, -100, showTos, showTos, showTos, newQuestionDetails);

        newQuestion = fqLogic.createFeedbackQuestion(newQuestion);

        FeedbackQuestion actualQuestion = fqLogic.getFeedbackQuestion(newQuestion.getId());

        verifyEquals(newQuestion, actualQuestion);
    }

    @Test
    public void testGetFeedbackQuestionsForSession() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4, fq5);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForSession(fs);

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        for (int i = 0; i < expectedQuestions.size(); i++) {
            verifyEquals(expectedQuestions.get(i), actualQuestions.get(i));
        }
    }

}
