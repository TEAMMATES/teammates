package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionViewData;

/**
 * Tests for {@link GetFeedbackSessionAction}.
 */
public class GetFeedbackSessionActionTest extends BaseActionTest<GetFeedbackSessionAction, FeedbackSessionViewData> {

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionAction_student_returnsHiddenData() {
        var account = given.account("account");
        given.student("student", s -> s.defaultCourse().account(account.alias()));
        var fs = given.feedbackSession("fs", f -> f.defaultCourse().opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, fs.id().toString())
                .withAccountAuth(account.id());

        FeedbackSessionViewData result = execute(request);

        assertNotNull(result);
        assertNotNull(result.getFeedbackSession());
        assertNull(result.getFeedbackSession().getSessionVisibleFromTimestamp());
        assertNull(result.getInstructorPermissions());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionAction_instructorWithViewPrivilege_returnsFullData() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).coOwner());
        var fs = given.feedbackSession("fs", f -> f.defaultCourse().opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, fs.id().toString())
                .withAccountAuth(account.id());

        FeedbackSessionViewData result = execute(request);

        assertNotNull(result);
        assertNotNull(result.getFeedbackSession().getSessionVisibleFromTimestamp());
        assertNotNull(result.getInstructorPermissions());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionAction_instructorWithoutViewPrivilege_returnsHiddenDataWithPermissions() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).noPrivileges());
        var fs = given.feedbackSession("fs", f -> f.defaultCourse().opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, fs.id().toString())
                .withAccountAuth(account.id());

        FeedbackSessionViewData result = execute(request);

        assertNotNull(result);
        assertNull(result.getFeedbackSession().getSessionVisibleFromTimestamp());
        assertNotNull(result.getInstructorPermissions());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionAction_studentAccessesNotVisibleSession_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        given.student("student", s -> s.defaultCourse().account(account.alias()));
        var fs = given.feedbackSession("fs", f -> f.defaultCourse().notVisible());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, fs.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionAction_instructorWithViewPrivilegeAccessesNotVisibleSession_returnsFullData() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).coOwner());
        var fs = given.feedbackSession("fs", f -> f.defaultCourse().notVisible());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, fs.id().toString())
                .withAccountAuth(account.id());

        FeedbackSessionViewData result = execute(request);

        assertNotNull(result);
        assertNotNull(result.getInstructorPermissions());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionAction_userNotInCourse_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var otherCourse = given.course("other-course");
        given.instructor("instructor", i -> i.course(otherCourse.alias()).account(account.alias()));
        var course = given.course("course");
        var fs = given.feedbackSession("fs", f -> f.course(course.alias()).opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, fs.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
