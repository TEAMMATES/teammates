package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionSubmittedGiverSet;
import teammates.ui.webapi.GetFeedbackSessionSubmittedGiverSetAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackSessionSubmittedGiverSetAction}.
 */
public class GetFeedbackSessionSubmittedGiverSetActionTest
        extends BaseActionTest<GetFeedbackSessionSubmittedGiverSetAction> {

    private Instructor typicalInstructor;
    private Course typicalCourse;
    private FeedbackSession typicalFeedbackSession;
    private FeedbackResponse typicalFeedbackResponse;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_SUBMITTED_GIVER_SET;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        typicalInstructor = getTypicalInstructor();
        typicalCourse = getTypicalCourse();
        typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
        FeedbackQuestion typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(typicalFeedbackQuestion);
    }

    @AfterMethod
    void tearDown() {
        logoutUser();
    }

    @Test
    void testExecute_missingParameters_throwsInvalidHttpParameterException() {
        verifyHttpParameterFailure();
    }

    @Test
    void testExecute_missingCourseId_throwsInvalidHttpParameterException() {
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_missingFeedbackSessionName_throwsInvalidHttpParameterException() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_typicalCase_success() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getGiverSetThatAnsweredFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(new HashSet<>(Arrays.asList(typicalFeedbackResponse.getGiver())));

        GetFeedbackSessionSubmittedGiverSetAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        FeedbackSessionSubmittedGiverSet output = (FeedbackSessionSubmittedGiverSet) result.getOutput();

        assertEquals(new HashSet<>(Arrays.asList(typicalFeedbackResponse.getGiver())), output.getGiverIdentifiers());
    }

    @Test
    void testCheckSpecificAccessControl_withoutLogin_throwsUnauthorizedAccessException() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_unregisteredUser_throwsUnauthorizedAccessException() {
        String googleId = "unregistered-user";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);

        loginAsUnregistered(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_student_throwsUnauthorizedAccessException() {
        String googleId = "student";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);

        loginAsStudent(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_instructorOfOtherCourse_throwsUnauthorizedAccessException() {
        String googleId = "instructor-of-other-course";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), googleId))
                .thenReturn(null);

        loginAsInstructor(googleId);

        verifyCannotAccess(params);
    }

    @Test
    void testCheckSpecificAccessControl_instructorOfSameCourse_success() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, typicalFeedbackSession.getName(),
        };

        when(mockLogic.getFeedbackSession(typicalFeedbackSession.getName(), typicalCourse.getId()))
                .thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        loginAsInstructor(typicalInstructor.getGoogleId());

        verifyCanAccess(params);
    }
}
