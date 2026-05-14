package teammates.it.ui.webapi;

import java.util.ArrayList;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionCreateRequest;
import teammates.ui.webapi.CreateFeedbackQuestionAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateFeedbackQuestionAction}.
 */
public class CreateFeedbackQuestionActionIT extends BaseActionIT<CreateFeedbackQuestionAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.getAccount().getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
        };

        ______TS("null question type");

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionType(null);
        verifyHttpRequestBodyFailure(createRequest, params);

        ______TS("Invalid questionNumber");

        createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionNumber(0);
        verifyHttpRequestBodyFailure(createRequest, params);

        ______TS("Failure: empty question brief");

        createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionBrief("");
        verifyHttpRequestBodyFailure(createRequest, params);

        ______TS("Typical case");

        createRequest = getTypicalTextQuestionCreateRequest();
        CreateFeedbackQuestionAction a = getAction(createRequest, params);
        JsonResult r = getJsonResult(a);

        FeedbackQuestionData questionResponse = (FeedbackQuestionData) r.getOutput();

        assertEquals("this is the description", questionResponse.getQuestionDescription());
        assertNotNull(questionResponse.getFeedbackQuestionId());
        FeedbackQuestion question = logic.getFeedbackQuestion(questionResponse.getFeedbackQuestionId());
        // verify question is created
        assertEquals("this is the description", question.getDescription());

        ______TS("Custom number of entity to give feedback to");

        createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM);
        createRequest.setCustomNumberOfEntitiesToGiveFeedbackTo(100);
        createRequest.setGiverType(QuestionGiverType.STUDENTS);
        createRequest.setRecipientType(QuestionRecipientType.STUDENTS);
        a = getAction(createRequest, params);
        r = getJsonResult(a);

        questionResponse = (FeedbackQuestionData) r.getOutput();

        assertEquals(100, questionResponse.getCustomNumberOfEntitiesToGiveFeedbackTo().intValue());
        assertNotNull(questionResponse.getFeedbackQuestionId());
        question = logic.getFeedbackQuestion(questionResponse.getFeedbackQuestionId());
        // verify question is created
        assertEquals(100, question.getNumOfEntitiesToGiveFeedbackTo().intValue());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSession fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, "00000000-0000-4000-8000-000000000001",
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyEntityNotFoundAcl(submissionParams);

        ______TS("inaccessible without ModifySessionPrivilege");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs.getId().toString(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(fs.getCourse(), submissionParams);

        ______TS("only instructors of the same course with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                fs.getCourse(), Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

    private FeedbackQuestionCreateRequest getTypicalTextQuestionCreateRequest() {
        FeedbackQuestionCreateRequest createRequest = new FeedbackQuestionCreateRequest();
        createRequest.setQuestionNumber(2);
        createRequest.setQuestionBrief("this is the brief");
        createRequest.setQuestionDescription("this is the description");
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        textQuestionDetails.setRecommendedLength(800);
        createRequest.setQuestionDetails(textQuestionDetails);
        createRequest.setQuestionType(FeedbackQuestionType.TEXT);
        createRequest.setGiverType(QuestionGiverType.STUDENTS);
        createRequest.setRecipientType(QuestionRecipientType.INSTRUCTORS);
        createRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        createRequest.setShowResponsesTo(new ArrayList<>());
        createRequest.setShowGiverNameTo(new ArrayList<>());
        createRequest.setShowRecipientNameTo(new ArrayList<>());

        return createRequest;
    }
}
