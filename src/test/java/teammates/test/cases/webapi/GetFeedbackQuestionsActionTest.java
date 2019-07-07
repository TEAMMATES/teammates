package teammates.test.cases.webapi;

import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.webapi.action.GetFeedbackQuestionsAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackQuestionData;
import teammates.ui.webapi.output.FeedbackQuestionsData;
import teammates.ui.webapi.output.FeedbackVisibilityType;
import teammates.ui.webapi.output.NumberOfEntitiesToGiveFeedbackToSetting;

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
        verifyCannotAccess(params);

        ______TS("only instructors of the same course can access");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(params);
    }

}
