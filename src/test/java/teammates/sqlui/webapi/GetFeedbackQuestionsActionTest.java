package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.FeedbackQuestionsData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetFeedbackQuestionsAction;

/**
 * SUT: {@link GetFeedbackQuestionsAction}.
 */
public class GetFeedbackQuestionsActionTest extends BaseActionTest<GetFeedbackQuestionsAction> {

    Course course;
    FeedbackSession feedbackSession;
    List<FeedbackQuestion> feedbackQuestions;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        feedbackSession = generateSession1InCourse(course);
        feedbackQuestions = generateFeedbackQuestionsInSession(feedbackSession);
    }

    @Test
    void testExecute_noParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_invalidCourseId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, null,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSession.getName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_invalidSessionName_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, null,
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_success() {
        when(mockLogic.getFeedbackSession(feedbackSession.getName(), course.getId())).thenReturn(feedbackSession);
        when(mockLogic.getFeedbackQuestionsForSession(feedbackSession)).thenReturn(feedbackQuestions);
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSession.getName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        GetFeedbackQuestionsAction getFeedbackQuestionsAction = getAction(params);
        FeedbackQuestionsData actionOutput = (FeedbackQuestionsData) getJsonResult(getFeedbackQuestionsAction).getOutput();
        assertEquals(JsonUtils.toJson(
                                    FeedbackQuestionsData.makeFeedbackQuestionsData(feedbackQuestions)),
                                    JsonUtils.toJson(actionOutput));
    }

    private FeedbackSession generateSession1InCourse(Course course) {
        FeedbackSession fs = new FeedbackSession("feedbacksession-1", course,
                "instructor1@gmail.com", "generic instructions",
                Instant.parse("2012-04-01T22:00:00Z"), Instant.parse("2027-04-30T22:00:00Z"),
                Instant.parse("2012-03-28T22:00:00Z"), Instant.parse("2027-05-01T22:00:00Z"),
                Duration.ofHours(10), true, true, true);
        fs.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        fs.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));

        return fs;
    }

    private List<FeedbackQuestion> generateFeedbackQuestionsInSession(FeedbackSession feedbackSession) {
        List<FeedbackParticipantType> feedbackQuestionParticipantTypes =
                List.of(FeedbackParticipantType.INSTRUCTORS);

        FeedbackTextQuestionDetails fq1Details =
                new FeedbackTextQuestionDetails("What is the best selling point of your product?");
        FeedbackQuestion fq1 = FeedbackQuestion.makeQuestion(
                                    feedbackSession, 1, "This is a text question.",
                                    FeedbackParticipantType.STUDENTS, FeedbackParticipantType.SELF, 1,
                                    feedbackQuestionParticipantTypes, feedbackQuestionParticipantTypes,
                                    feedbackQuestionParticipantTypes, fq1Details);

        FeedbackTextQuestionDetails fq2Details =
                new FeedbackTextQuestionDetails("Rate 1 other student's product");
        fq2Details.setRecommendedLength(0);
        FeedbackQuestion fq2 = FeedbackQuestion.makeQuestion(
                feedbackSession, 2, "This is a text question.",
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.STUDENTS_EXCLUDING_SELF, 1,
                feedbackQuestionParticipantTypes, feedbackQuestionParticipantTypes, feedbackQuestionParticipantTypes,
                fq2Details);
        return List.of(fq1, fq2);
    }

}
