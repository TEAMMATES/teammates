package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionUpdateRequest;

/**
 * SUT: {@link FeedbackQuestionsLogic}.
 */
public class FeedbackQuestionsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    private DataBundle typicalDataBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testCreateFeedbackQuestion() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackTextQuestionDetails newQuestionDetails = new FeedbackTextQuestionDetails("New question text.");
        List<FeedbackVisibilityType> showTos = new ArrayList<>();
        showTos.add(FeedbackVisibilityType.INSTRUCTORS);
        FeedbackQuestion newQuestion = FeedbackQuestion.makeQuestion(6, "This is a new text question",
                QuestionGiverType.STUDENTS, QuestionRecipientType.OWN_TEAM_MEMBERS, -100,
                showTos, showTos, showTos, newQuestionDetails);
        fs.addFeedbackQuestion(newQuestion);

        FeedbackQuestion createdQuestion = inTransaction(() -> fqLogic.createFeedbackQuestion(newQuestion));

        FeedbackQuestion actualQuestion = inTransaction(() -> fqLogic.getFeedbackQuestion(createdQuestion.getId()));

        assertEquals(createdQuestion, actualQuestion);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetFeedbackQuestionsForSession() {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion fq2 = typicalDataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion fq3 = typicalDataBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        FeedbackQuestion fq4 = typicalDataBundle.feedbackQuestions.get("qn4InSession1InCourse1");
        FeedbackQuestion fq5 = typicalDataBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        FeedbackQuestion fq6 = typicalDataBundle.feedbackQuestions.get("qn6InSession1InCourse1NoResponses");
        FeedbackQuestion fq7 = typicalDataBundle.feedbackQuestions.get("qn7InSession1InCourse1");
        FeedbackQuestion fq8 = typicalDataBundle.feedbackQuestions.get("qn8InSession1InCourse1");
        FeedbackQuestion fq9 = typicalDataBundle.feedbackQuestions.get("qn9InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4, fq5, fq6, fq7, fq8, fq9);

        List<FeedbackQuestion> actualQuestions = inTransaction(() -> fqLogic.getFeedbackQuestionsForSession(fs.getId()));

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testUpdateFeedbackQuestionCascade() {
        FeedbackQuestion fq1 = typicalDataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        fq1.setDescription("New question description");
        FeedbackQuestionUpdateRequest updateRequest = generateFeedbackQuestionUpdateRequest(
                fq1.getQuestionNumber(),
                fq1.getDescription(),
                fq1.getQuestionDetailsCopy(),
                fq1.getQuestionType(),
                fq1.getGiverType(),
                fq1.getRecipientType(),
                fq1.getNumOfEntitiesToGiveFeedbackTo(),
                fq1.getShowResponsesTo(),
                fq1.getShowGiverNameTo(),
                fq1.getShowRecipientNameTo()
        );
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM);

        inTransaction(() -> fqLogic.updateFeedbackQuestionCascade(fq1.getId(), updateRequest));

        FeedbackQuestion actualFeedbackQuestion = inTransaction(() -> fqLogic.getFeedbackQuestion(fq1.getId()));

        assertEquals(fq1, actualFeedbackQuestion);
    }

    private FeedbackQuestionUpdateRequest generateFeedbackQuestionUpdateRequest(
            int questionNumber,
            String questionDescription,
            FeedbackQuestionDetails questionDetails,
            FeedbackQuestionType questionType,
            QuestionGiverType giverType,
            QuestionRecipientType recipientType,
            Integer customNumberOfEntitiesToGiveFeedbackTo,
            List<FeedbackVisibilityType> showResponsesTo,
            List<FeedbackVisibilityType> showGiverNameTo,
            List<FeedbackVisibilityType> showRecipientNameTo
    ) {
        FeedbackQuestionUpdateRequest updateRequest = new FeedbackQuestionUpdateRequest();

        updateRequest.setQuestionNumber(questionNumber);
        updateRequest.setQuestionDescription(questionDescription);
        updateRequest.setQuestionDetails(questionDetails);
        updateRequest.setQuestionType(questionType);
        updateRequest.setGiverType(giverType);
        updateRequest.setRecipientType(recipientType);
        updateRequest.setCustomNumberOfEntitiesToGiveFeedbackTo(customNumberOfEntitiesToGiveFeedbackTo);
        updateRequest.setShowResponsesTo(showResponsesTo);
        updateRequest.setShowGiverNameTo(showGiverNameTo);
        updateRequest.setShowRecipientNameTo(showRecipientNameTo);

        return updateRequest;
    }

}
