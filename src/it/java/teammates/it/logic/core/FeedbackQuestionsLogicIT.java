package teammates.it.logic.core;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.output.FeedbackVisibilityType;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionUpdateRequest;

/**
 * SUT: {@link FeedbackQuestionsLogic}.
 */
public class FeedbackQuestionsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    private DataBundle typicalDataBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Test
    public void testCreateFeedbackQuestion() throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackSession fs = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackTextQuestionDetails newQuestionDetails = new FeedbackTextQuestionDetails("New question text.");
        List<ViewerType> showTos = new ArrayList<>();
        showTos.add(ViewerType.INSTRUCTORS);
        FeedbackQuestion newQuestion = FeedbackQuestion.makeQuestion(6, "This is a new text question",
                QuestionGiverType.STUDENTS, QuestionRecipientType.OWN_TEAM_MEMBERS, -100,
                showTos, showTos, showTos, newQuestionDetails);
        fs.addFeedbackQuestion(newQuestion);

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
        FeedbackQuestion fq6 = typicalDataBundle.feedbackQuestions.get("qn6InSession1InCourse1NoResponses");
        FeedbackQuestion fq7 = typicalDataBundle.feedbackQuestions.get("qn7InSession1InCourse1");
        FeedbackQuestion fq8 = typicalDataBundle.feedbackQuestions.get("qn8InSession1InCourse1");
        FeedbackQuestion fq9 = typicalDataBundle.feedbackQuestions.get("qn9InSession1InCourse1");

        List<FeedbackQuestion> expectedQuestions = List.of(fq1, fq2, fq3, fq4, fq5, fq6, fq7, fq8, fq9);

        List<FeedbackQuestion> actualQuestions = fqLogic.getFeedbackQuestionsForSession(fs);

        assertEquals(expectedQuestions.size(), actualQuestions.size());
        assertTrue(expectedQuestions.containsAll(actualQuestions));
    }

    @Test
    public void testUpdateFeedbackQuestionCascade() throws InvalidParametersException, EntityDoesNotExistException {
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

        fqLogic.updateFeedbackQuestionCascade(fq1.getId(), updateRequest);

        FeedbackQuestion actualFeedbackQuestion = fqLogic.getFeedbackQuestion(fq1.getId());

        verifyEquals(fq1, actualFeedbackQuestion);
    }

    private FeedbackQuestionUpdateRequest generateFeedbackQuestionUpdateRequest(
            int questionNumber,
            String questionDescription,
            FeedbackQuestionDetails questionDetails,
            FeedbackQuestionType questionType,
            QuestionGiverType giverType,
            QuestionRecipientType recipientType,
            Integer customNumberOfEntitiesToGiveFeedbackTo,
            List<ViewerType> showResponsesTo,
            List<ViewerType> showGiverNameTo,
            List<ViewerType> showRecipientNameTo
    ) {
        FeedbackQuestionUpdateRequest updateRequest = new FeedbackQuestionUpdateRequest();

        updateRequest.setQuestionNumber(questionNumber);
        updateRequest.setQuestionDescription(questionDescription);
        updateRequest.setQuestionDetails(questionDetails);
        updateRequest.setQuestionType(questionType);
        updateRequest.setGiverType(giverType);
        updateRequest.setRecipientType(recipientType);
        updateRequest.setCustomNumberOfEntitiesToGiveFeedbackTo(customNumberOfEntitiesToGiveFeedbackTo);
        updateRequest.setShowResponsesTo(convertToFeedbackVisibilityType(showResponsesTo));
        updateRequest.setShowGiverNameTo(convertToFeedbackVisibilityType(showGiverNameTo));
        updateRequest.setShowRecipientNameTo(convertToFeedbackVisibilityType(showRecipientNameTo));

        return updateRequest;
    }

    private List<FeedbackVisibilityType> convertToFeedbackVisibilityType(
            List<ViewerType> viewerTypes) {
        return viewerTypes.stream().map(viewerType -> {
            switch (viewerType) {
            case STUDENTS:
                return FeedbackVisibilityType.STUDENTS;
            case INSTRUCTORS:
                return FeedbackVisibilityType.INSTRUCTORS;
            case RECEIVER:
                return FeedbackVisibilityType.RECIPIENT;
            case OWN_TEAM_MEMBERS:
                return FeedbackVisibilityType.GIVER_TEAM_MEMBERS;
            case RECEIVER_TEAM_MEMBERS:
                return FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS;
            default:
                assert false : "Unknown viewerType" + viewerType;
                break;
            }
            return null;
        }).toList();
    }
}
