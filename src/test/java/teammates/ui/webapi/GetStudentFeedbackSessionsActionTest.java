package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionsData;

/**
 * Tests for {@link GetStudentFeedbackSessionsAction}.
 */
public class GetStudentFeedbackSessionsActionTest
        extends BaseActionTest<GetStudentFeedbackSessionsAction, FeedbackSessionsData> {

    @Test(groups = GroupNames.ACTION)
    public void getStudentFeedbackSessionsAction_validCourse_returnsVisibleSessionsWithoutHiddenData() {
        var account = given.account("account");
        var course = given.course("course");
        given.student("student", s -> s.course(course.alias()).account(account.alias()));
        var openedSession = given.feedbackSession("opened-session", fs -> fs.course(course.alias()).opened());
        given.feedbackSession("not-visible-session", fs -> fs.course(course.alias()).notVisible());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        FeedbackSessionsData result = execute(request);

        assertEquals(1, result.getFeedbackSessions().size());

        FeedbackSessionData sessionData = result.getFeedbackSessions().get(0).getFeedbackSession();
        assertEquals(openedSession.id(), sessionData.getFeedbackSessionId());
        assertEquals(course.id(), sessionData.getCourseId());
        assertNull(sessionData.getGracePeriod());
        assertNull(sessionData.getResultVisibleFromTimestamp());
        assertNull(sessionData.getResponseVisibleSetting());
        assertNull(sessionData.getCustomResponseVisibleTimestamp());
        assertNull(sessionData.getIsClosingSoonEmailEnabled());
        assertNull(sessionData.getIsPublishedEmailEnabled());
        assertEquals(0, sessionData.getCreatedAtTimestamp());
    }

    @Test(groups = GroupNames.ACTION)
    public void getStudentFeedbackSessionsAction_missingCourseId_throwsInvalidHttpParameterException() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id());

        assertActionThrows(InvalidHttpParameterException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getStudentFeedbackSessionsAction_studentNotInCourse_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var course = given.course("course");
        var otherCourse = given.course("other-course");
        given.student("student", s -> s.course(otherCourse.alias()).account(account.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
