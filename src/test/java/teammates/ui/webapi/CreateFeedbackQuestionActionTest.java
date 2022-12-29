package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackVisibilityType;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionCreateRequest;

/**
 * SUT: {@link CreateFeedbackQuestionAction}.
 */
public class CreateFeedbackQuestionActionTest extends BaseActionTest<CreateFeedbackQuestionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, session.getCourseId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName());

        String[] params = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        ______TS("null question type");

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionType(null);
        verifyHttpRequestBodyFailure(createRequest, params);

        ______TS("Invalid questionNumber");

        createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setQuestionNumber(0);
        verifyHttpRequestBodyFailure(createRequest, params);

        ______TS("Failure: Invalid giverType");

        createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setGiverType(FeedbackParticipantType.NONE);
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
        FeedbackQuestionAttributes questionAttributes =
                logic.getFeedbackQuestion(session.getFeedbackSessionName(),
                        session.getCourseId(), createRequest.getQuestionNumber());
        // verify question is created
        assertEquals("this is the description", questionAttributes.getQuestionDescription());

        ______TS("Custom number of entity to give feedback to");

        createRequest = getTypicalTextQuestionCreateRequest();
        createRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM);
        createRequest.setCustomNumberOfEntitiesToGiveFeedbackTo(100);
        createRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        createRequest.setRecipientType(FeedbackParticipantType.STUDENTS);
        a = getAction(createRequest, params);
        r = getJsonResult(a);

        questionResponse = (FeedbackQuestionData) r.getOutput();

        assertEquals(100, questionResponse.getCustomNumberOfEntitiesToGiveFeedbackTo().intValue());
        assertNotNull(questionResponse.getFeedbackQuestionId());
        questionAttributes =
                logic.getFeedbackQuestion(session.getFeedbackSessionName(),
                        session.getCourseId(), createRequest.getQuestionNumber());
        // verify question is created
        assertEquals(100, questionAttributes.getNumberOfEntitiesToGiveFeedbackTo());

    }

    @Test
    protected void testExecute_masqueradeMode_shouldCreateQuestionSuccessfully() {
        loginAsAdmin();

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        params = addUserIdToParams(instructor1OfCourse1.getGoogleId(), params);

        FeedbackQuestionCreateRequest createRequest = getTypicalTextQuestionCreateRequest();
        CreateFeedbackQuestionAction a = getAction(createRequest, params);
        JsonResult r = getJsonResult(a);

        FeedbackQuestionData questionResponse = (FeedbackQuestionData) r.getOutput();

        assertEquals("this is the description", questionResponse.getQuestionDescription());
        assertNotNull(questionResponse.getFeedbackQuestionId());
        FeedbackQuestionAttributes questionAttributes =
                logic.getFeedbackQuestion(session.getFeedbackSessionName(),
                        session.getCourseId(), createRequest.getQuestionNumber());
        // verify question is created
        assertEquals("this is the description", questionAttributes.getQuestionDescription());
    }

    @Test
    public void testExecute_contributionQuestion_shouldCreateQuestionSuccessfully() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        ______TS("Failure: Invalid feedback path");

        // contribution question cannot have students -> students feedback path
        FeedbackQuestionCreateRequest createRequest = getTypicalContributionQuestionCreateRequest();
        createRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        createRequest.setRecipientType(FeedbackParticipantType.STUDENTS);
        verifyHttpRequestBodyFailure(createRequest, params);

        ______TS("Typical case");

        createRequest = getTypicalContributionQuestionCreateRequest();
        CreateFeedbackQuestionAction a = getAction(createRequest, params);
        JsonResult r = getJsonResult(a);

        FeedbackQuestionData questionResponse = (FeedbackQuestionData) r.getOutput();
        assertEquals(FeedbackQuestionType.CONTRIB, questionResponse.getQuestionType());
        assertNotNull(questionResponse.getFeedbackQuestionId());
        FeedbackQuestionAttributes questionAttributes =
                logic.getFeedbackQuestion(session.getFeedbackSessionName(),
                        session.getCourseId(), createRequest.getQuestionNumber());
        // verify question is created
        assertEquals(FeedbackQuestionType.CONTRIB, questionAttributes.getQuestionType());
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
        createRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        createRequest.setRecipientType(FeedbackParticipantType.INSTRUCTORS);
        createRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        createRequest.setShowResponsesTo(new ArrayList<>());
        createRequest.setShowGiverNameTo(new ArrayList<>());
        createRequest.setShowRecipientNameTo(new ArrayList<>());

        return createRequest;
    }

    private FeedbackQuestionCreateRequest getTypicalContributionQuestionCreateRequest() {
        FeedbackQuestionCreateRequest createRequest = new FeedbackQuestionCreateRequest();
        createRequest.setQuestionNumber(1);
        createRequest.setQuestionBrief("this is the brief for contribution question");
        createRequest.setQuestionDescription("this is the description for contribution question");
        FeedbackContributionQuestionDetails textQuestionDetails = new FeedbackContributionQuestionDetails();
        createRequest.setQuestionDetails(textQuestionDetails);
        createRequest.setQuestionType(FeedbackQuestionType.CONTRIB);
        createRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        createRequest.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        createRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        createRequest.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        createRequest.setShowGiverNameTo(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        createRequest.setShowRecipientNameTo(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));

        return createRequest;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "abcRandomSession",
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyEntityNotFoundAcl(submissionParams);

        ______TS("inaccessible without ModifySessionPrivilege");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(submissionParams);

        ______TS("only instructors of the same course with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

}
