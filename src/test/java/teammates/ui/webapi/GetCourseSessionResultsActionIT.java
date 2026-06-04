package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.test.TestGroups;
import teammates.ui.output.SessionResultsData;

/**
 * SUT: {@link GetCourseSessionResultsAction}.
 */
public class GetCourseSessionResultsActionIT extends BaseActionIT<GetCourseSessionResultsAction> {
    private DataBundle typicalBundle;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_SESSION_RESULTS;
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
    @Test(groups = TestGroups.INTEGRATION)
    protected void testExecute() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
        };

        GetCourseSessionResultsAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        SessionResultsData output = (SessionResultsData) result.getOutput();

        SessionResultsData expected = inTransaction(() -> SessionResultsData.init(logic.getSessionResults(
                feedbackSession, instructor, null, null)));

        assertEquals(expected.getQuestions().size(), output.getQuestions().size());
    }

    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Course course = typicalBundle.courses.get("course1");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(course, params);
        verifyInaccessibleForInstructorsOfOtherCourses(course, params);

        loginAsStudent(typicalBundle.students.get("student1InCourse1").getGoogleId());
        verifyCannotAccess(params);

        verifyInaccessibleForStudentsOfOtherCourse(course, params);
    }
}
