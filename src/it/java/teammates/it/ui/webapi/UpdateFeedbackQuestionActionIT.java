package teammates.it.ui.webapi;

import java.util.ArrayList;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UpdateFeedbackQuestionAction;

/**
 * SUT: {@link UpdateFeedbackQuestionAction}.
 */
public class UpdateFeedbackQuestionActionIT extends BaseActionIT<UpdateFeedbackQuestionAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        Instructor instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestion fq1 = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion typicalQuestion = logic.getFeedbackQuestion(fq1.getId());
        assertEquals(FeedbackQuestionType.TEXT, typicalQuestion.getQuestionDetailsCopy().getQuestionType());

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("success: Typical case");

        String[] param = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getId().toString(),
        };
        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();

        UpdateFeedbackQuestionAction a = getAction(updateRequest, param);
        JsonResult r = getJsonResult(a);

        FeedbackQuestionData response = (FeedbackQuestionData) r.getOutput();

        typicalQuestion = logic.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals(typicalQuestion.getQuestionNumber().intValue(), response.getQuestionNumber());
        assertEquals(2, typicalQuestion.getQuestionNumber().intValue());

        assertEquals(typicalQuestion.getQuestionDetailsCopy().getQuestionText(), response.getQuestionBrief());
        assertEquals("this is the brief", typicalQuestion.getQuestionDetailsCopy().getQuestionText());

        assertEquals(typicalQuestion.getDescription(), response.getQuestionDescription());
        assertEquals("this is the description", typicalQuestion.getDescription());

        assertEquals(typicalQuestion.getQuestionDetailsCopy().getQuestionType(), response.getQuestionType());
        assertEquals(FeedbackQuestionType.TEXT, typicalQuestion.getQuestionDetailsCopy().getQuestionType());

        assertEquals(JsonUtils.toJson(typicalQuestion.getQuestionDetailsCopy()),
                JsonUtils.toJson(response.getQuestionDetails()));
        assertEquals(800, ((FeedbackTextQuestionDetails)
                typicalQuestion.getQuestionDetailsCopy()).getRecommendedLength().intValue());

        assertEquals(typicalQuestion.getGiverType(), typicalQuestion.getGiverType());
        assertEquals(FeedbackParticipantType.STUDENTS, typicalQuestion.getGiverType());

        assertEquals(typicalQuestion.getRecipientType(), typicalQuestion.getRecipientType());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, typicalQuestion.getRecipientType());

        assertEquals(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
                response.getNumberOfEntitiesToGiveFeedbackToSetting());
        assertEquals(Const.MAX_POSSIBLE_RECIPIENTS, typicalQuestion.getNumOfEntitiesToGiveFeedbackTo().intValue());

        assertNull(response.getCustomNumberOfEntitiesToGiveFeedbackTo());

        assertTrue(response.getShowResponsesTo().isEmpty());
        assertTrue(typicalQuestion.getShowResponsesTo().isEmpty());
        assertTrue(response.getShowGiverNameTo().isEmpty());
        assertTrue(typicalQuestion.getShowGiverNameTo().isEmpty());
        assertTrue(response.getShowRecipientNameTo().isEmpty());
        assertTrue(typicalQuestion.getShowRecipientNameTo().isEmpty());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSession fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion fq1 = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestion typicalQuestion =
                logic.getFeedbackQuestion(fq1.getId());

        ______TS("non-existent feedback question");

        loginAsInstructor(instructor1OfCourse1.getAccount().getGoogleId());

        verifyEntityNotFoundAcl(Const.ParamsNames.FEEDBACK_QUESTION_ID, "random");

        ______TS("accessible only for instructor with ModifySessionPrivilege");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getId().toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                fs.getCourse(), Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

    private FeedbackQuestionUpdateRequest getTypicalTextQuestionUpdateRequest() {
        FeedbackQuestionUpdateRequest updateRequest = new FeedbackQuestionUpdateRequest();
        updateRequest.setQuestionNumber(2);
        updateRequest.setQuestionBrief("this is the brief");
        updateRequest.setQuestionDescription("this is the description");
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        textQuestionDetails.setRecommendedLength(800);
        updateRequest.setQuestionDetails(textQuestionDetails);
        updateRequest.setQuestionType(FeedbackQuestionType.TEXT);
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.INSTRUCTORS);
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        updateRequest.setShowResponsesTo(new ArrayList<>());
        updateRequest.setShowGiverNameTo(new ArrayList<>());
        updateRequest.setShowRecipientNameTo(new ArrayList<>());

        return updateRequest;
    }
}
