package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackQuestionsData;
import teammates.ui.output.FeedbackVisibilityType;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetFeedbackQuestionsAction}.
 */
public class GetFeedbackQuestionsActionTest extends BaseActionTest<GetFeedbackQuestionsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes feedbackSessionAttributes = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.googleId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId());
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_SESSION_NAME,
                feedbackSessionAttributes.getFeedbackSessionName());
        verifyHttpParameterFailure(Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName());

        ______TS("typical success case");

        String[] params = {
                Const.ParamsNames.COURSE_ID, feedbackSessionAttributes.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionAttributes.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        GetFeedbackQuestionsAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        FeedbackQuestionsData feedbackQuestionsResponse = (FeedbackQuestionsData) r.getOutput();

        List<FeedbackQuestionData> questions = feedbackQuestionsResponse.getQuestions();
        assertEquals(5, questions.size());

        FeedbackQuestionData typicalResponse = questions.get(0);
        FeedbackQuestionAttributes expected =
                logic.getFeedbackQuestionsForSession(feedbackSessionAttributes.getFeedbackSessionName(),
                        feedbackSessionAttributes.getCourseId()).get(0);

        assertNotNull(typicalResponse.getFeedbackQuestionId());
        assertEquals(expected.getFeedbackQuestionId(), typicalResponse.getFeedbackQuestionId());
        assertEquals(expected.getQuestionNumber(), typicalResponse.getQuestionNumber());
        assertEquals(expected.getQuestionDetails().getQuestionText(), typicalResponse.getQuestionBrief());
        assertEquals(expected.getQuestionDescription(), typicalResponse.getQuestionDescription());

        assertEquals(JsonUtils.toJson(expected.getQuestionDetails()),
                JsonUtils.toJson(typicalResponse.getQuestionDetails()));

        assertEquals(expected.getQuestionType(), typicalResponse.getQuestionType());
        assertEquals(expected.getGiverType(), typicalResponse.getGiverType());
        assertEquals(expected.getRecipientType(), typicalResponse.getRecipientType());

        assertEquals(NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
                typicalResponse.getNumberOfEntitiesToGiveFeedbackToSetting());
        assertEquals(1, typicalResponse.getCustomNumberOfEntitiesToGiveFeedbackTo().intValue());

        assertEquals(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                typicalResponse.getShowResponsesTo());
        assertEquals(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                typicalResponse.getShowGiverNameTo());
        assertEquals(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                typicalResponse.getShowRecipientNameTo());
    }

    @Test
    public void testExecute_studentFeedbackSubmissionMcqGenerateOptionsForTeams_shouldReturnGeneratedFields()
            throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(studentAttributes.getGoogleId());

        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        logic.createFeedbackQuestion(FeedbackQuestionAttributes.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withQuestionDetails(feedbackMcqQuestionDetails)
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build());

        String[] params = {
                Const.ParamsNames.COURSE_ID, fsa.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        GetFeedbackQuestionsAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        FeedbackQuestionsData feedbackQuestionsResponse = (FeedbackQuestionsData) r.getOutput();

        assertEquals(Arrays.asList("Team 1.1</td></div>'\"", "Team 1.2"),
                ((FeedbackMcqQuestionDetails)
                        feedbackQuestionsResponse.getQuestions().get(0).getQuestionDetails()).getMcqChoices());
    }

    @Test
    public void testExecute_instructorFeedbackSubmissionMcqGenerateOptionsForTeams_shouldReturnGeneratedFields()
            throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        logic.createFeedbackQuestion(FeedbackQuestionAttributes.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.INSTRUCTORS)
                .withQuestionDetails(feedbackMcqQuestionDetails)
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build());

        String[] params = {
                Const.ParamsNames.COURSE_ID, fsa.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackQuestionsAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        FeedbackQuestionsData feedbackQuestionsResponse = (FeedbackQuestionsData) r.getOutput();

        assertEquals(Arrays.asList("Team 1.1</td></div>'\"", "Team 1.2"),
                ((FeedbackMcqQuestionDetails)
                        feedbackQuestionsResponse.getQuestions().get(0).getQuestionDetails()).getMcqChoices());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName for a session",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        loginAsInstructor(instructor1OfCourse1.googleId);
        verifyEntityNotFound(params);

        ______TS("only instructors of the same course can access");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(params);

        ______TS("observers of course can access result");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, params);
    }

}
