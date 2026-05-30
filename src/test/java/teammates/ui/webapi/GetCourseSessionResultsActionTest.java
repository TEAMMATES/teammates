package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.output.SessionResultsData;

/**
 * SUT: {@link GetCourseSessionResultsAction}.
 */
public class GetCourseSessionResultsActionTest extends BaseActionTest<GetCourseSessionResultsAction> {
    private String googleId = "user-googleId";
    private Course course;
    private FeedbackSession session;
    private SessionResultsBundle resultsStub;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_SESSION_RESULTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        session = getTypicalFeedbackSessionForCourse(course);
        session.setStartTime(Instant.parse("2020-01-01T00:00:00.000Z"));
        session.setEndTime(Instant.parse("2020-10-01T00:00:00.000Z"));
        session.setResultsVisibleFromTime(Instant.MIN);

        FeedbackQuestion questionStub = getTypicalFeedbackQuestionForSession(session);
        ArrayList<FeedbackQuestion> questionsStub = new ArrayList<>();
        questionsStub.add(questionStub);
        resultsStub = new SessionResultsBundle(
            questionsStub,
                new HashSet<>(), new HashSet<>(), new ArrayList<>(),
                new ArrayList<>(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new CourseRoster(new ArrayList<>(), new ArrayList<>()));
        reset(mockLogic);
    }

    @Test
    void testExecute_fullDetailIntent_success() {
        loginAsInstructor(googleId);
        Instructor instructorStub = getTypicalInstructor();

        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);
        when(mockLogic.getInstructorByGoogleId(session.getCourseId(), googleId)).thenReturn(instructorStub);
        when(mockLogic.getSessionResults(argThat(
                        argument -> Objects.equals(argument.getName(), session.getName())),
                eq(instructorStub.getEmail()), isNull(), isNull(),
                eq(FeedbackResultFetchType.BOTH))).thenReturn(resultsStub);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
        };

        GetCourseSessionResultsAction action = getAction(params);
        SessionResultsData output = (SessionResultsData) getJsonResult(action).getOutput();

        assertEquals(1, output.getQuestions().size());
    }

    @Test
    void testExecute_userIntent_throwsInvalidHttpParameterException() {
        loginAsInstructor(googleId);
        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testGetSessionResult_notLoggedInUser_cannotAccess() {
        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);
        verifyWithoutLoginCannotAccess(buildParams());
    }

    @Test
    void testGetSessionResult_fullDetailIntent_canAccess() {
        when(mockLogic.getFeedbackSession(session.getId())).thenReturn(session);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(course, buildParams());
    }

    private String[] buildParams() {
        return new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, session.getId().toString(),
        };
    }
}
