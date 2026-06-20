package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.output.ResponseOutput;
import teammates.ui.output.UserSessionResultsData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetUserSessionResultsAction}.
 */
public class GetUserSessionResultsActionIT extends BaseActionIT<GetUserSessionResultsAction> {
    private DataBundle typicalBundle;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.USER_SESSION_RESULTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        logoutUser();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor);

        FeedbackSession accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, accessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetUserSessionResultsAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        UserSessionResultsData output = (UserSessionResultsData) result.getOutput();
        UserSessionResultsData expectedResults = inTransaction(() -> UserSessionResultsData.initForUser(
                logic.getSessionResultsForUser(accessibleFeedbackSession, instructor, false),
                instructor));

        assertTrue(isSessionResultsDataEqual(expectedResults, output));

        Student student = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student);

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, accessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        action = getAction(params);
        result = getJsonResult(action);

        output = (UserSessionResultsData) result.getOutput();
        expectedResults = inTransaction(() -> UserSessionResultsData.initForUser(
                logic.getSessionResultsForUser(accessibleFeedbackSession, student, false),
                student));

        assertTrue(isSessionResultsDataEqual(expectedResults, output));
    }

    @Override
    protected void testAccessControl() throws Exception {
        String[] params;
        FeedbackSession publishedFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Course course = typicalBundle.courses.get("course1");
        FeedbackSession inaccessibleFeedbackSession = typicalBundle.feedbackSessions.get(
                "unpublishedSession1InTypicalCourse");

        ______TS("Inaccessible for authenticated instructor when unpublished");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, inaccessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        verifyCannotAccess(params);

        ______TS("Inaccessible for authenticated student when unpublished");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, inaccessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1);
        verifyCannotAccess(params);

        ______TS("Accessible for authenticated instructor when published");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, publishedFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        verifyAccessibleForInstructorsOfTheSameCourse(course, params);
        verifyInaccessibleForInstructorsOfOtherCourses(course, params);

        ______TS("Accessible for authenticated student when published");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, publishedFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        verifyAccessibleForStudentsOfTheSameCourse(course, params);
        verifyInaccessibleForStudentsOfOtherCourse(course, params);

        ______TS("Invalid intent");
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, publishedFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.name(),
        };
        verifyHttpParameterFailure(params);
    }

    private boolean isSessionResultsDataEqual(UserSessionResultsData self, UserSessionResultsData other) {
        List<UserSessionResultsData.UserQuestionOutput> thisQuestions = self.getQuestions();
        List<UserSessionResultsData.UserQuestionOutput> otherQuestions = other.getQuestions();
        if (thisQuestions.size() != otherQuestions.size()) {
            return false;
        }
        for (int i = 0; i < thisQuestions.size(); i++) {
            UserSessionResultsData.UserQuestionOutput thisQuestion = thisQuestions.get(i);
            UserSessionResultsData.UserQuestionOutput otherQuestion = otherQuestions.get(i);
            if (!isQuestionOutputEqual(thisQuestion, otherQuestion)) {
                return false;
            }
        }
        return true;
    }

    private boolean isQuestionOutputEqual(UserSessionResultsData.UserQuestionOutput self,
            UserSessionResultsData.UserQuestionOutput other) {
        if (!JsonUtils.toJson(self.getFeedbackQuestion()).equals(JsonUtils.toJson(other.getFeedbackQuestion()))
                || !JsonUtils.toJson(self.getQuestionStatistics()).equals(JsonUtils.toJson(other.getQuestionStatistics()))
                || self.getHasResponseButNotVisibleForPreview() != other.getHasResponseButNotVisibleForPreview()
                || self.getHasCommentNotVisibleForPreview() != other.getHasCommentNotVisibleForPreview()) {
            return false;
        }
        List<ResponseOutput> thisResponses;
        List<ResponseOutput> otherResponses;
        thisResponses = self.getAllResponses();
        otherResponses = other.getAllResponses();
        if (thisResponses.size() != otherResponses.size()) {
            return false;
        }
        for (int j = 0; j < thisResponses.size(); j++) {
            if (!isResponseOutputEqual(thisResponses.get(j), otherResponses.get(j))) {
                return false;
            }
        }
        return true;
    }

    private boolean isResponseOutputEqual(ResponseOutput self,
            ResponseOutput other) {
        return self.getGiver().equals(other.getGiver())
                && self.getGiverTeam().equals(other.getGiverTeam())
                && self.getGiverSection().equals(other.getGiverSection())
                && self.getRecipient().equals(other.getRecipient())
                && self.getRecipientTeam().equals(other.getRecipientTeam())
                && self.getRecipientSection().equals(other.getRecipientSection())
                && self.getResponseDetails().getJsonString().equals(other.getResponseDetails().getJsonString());
    }
}
