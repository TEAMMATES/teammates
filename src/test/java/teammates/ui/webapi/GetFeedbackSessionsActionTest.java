package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.FeedbackSessionsData;

/**
 * Tests for {@link GetFeedbackSessionsAction}.
 */
public class GetFeedbackSessionsActionTest extends BaseActionTest<GetFeedbackSessionsAction, FeedbackSessionsData> {

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_adminWithoutCourseId_returnsAllFeedbackSessionsInActiveCourses() {
        var course1 = given.course("course-1");
        var course2 = given.course("course-2");
        var softDeletedCourse = given.course("soft-deleted-course", c -> c.softDeleted());
        var session1 = given.feedbackSession("session-1", fs -> fs.course(course1.alias()).opened());
        var session2 = given.feedbackSession("session-2", fs -> fs.course(course2.alias()).opened());
        given.feedbackSession("session-in-deleted-course", fs -> fs.course(softDeletedCourse.alias()).opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAdminAuth();

        FeedbackSessionsData result = execute(request);

        assertEquals(Set.of(session1.id(), session2.id()), getFeedbackSessionIds(result));
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_adminWithCourseIds_returnsInstructorPermissions() {
        var adminAccount = given.account("admin-account", a -> a.admin());
        var course = given.course("course");
        given.instructor("admin-instructor", i -> i.account(adminAccount.alias()).course(course.alias()).manager());
        var session = given.feedbackSession("session", fs -> fs.course(course.alias()).opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(adminAccount.id());

        FeedbackSessionsData result = execute(request);

        assertEquals(Set.of(session.id()), getFeedbackSessionIds(result));
        assertNotNull(result.getFeedbackSessions().get(0).getInstructorPermissions());
        assertTrue(result.getFeedbackSessions().get(0).getInstructorPermissions().getCanModifySession());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_instructorWithMultipleCourseIds_returnsSessionsFromAllCourses() {
        var requesterAccount = given.account("requester-account");
        var course1 = given.course("course-1");
        var course2 = given.course("course-2");
        var otherCourse = given.course("other-course");
        given.instructor("requester-1", i -> i.account(requesterAccount.alias()).course(course1.alias()).coOwner());
        given.instructor("requester-2", i -> i.account(requesterAccount.alias()).course(course2.alias()).coOwner());
        var session1 = given.feedbackSession("session-1", fs -> fs.course(course1.alias()).opened());
        var session2 = given.feedbackSession("session-2", fs -> fs.course(course2.alias()).opened());
        given.feedbackSession("other-session", fs -> fs.course(otherCourse.alias()).opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course1.id())
                .withParam(Const.ParamsNames.COURSE_ID, course2.id())
                .withAccountAuth(requesterAccount.id());

        FeedbackSessionsData result = execute(request);

        assertEquals(Set.of(session1.id(), session2.id()), getFeedbackSessionIds(result));
        result.getFeedbackSessions().forEach(session -> assertNotNull(session.getInstructorPermissions()));
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_instructorWithoutCourseId_throwsInvalidHttpParameterException() {
        var requesterAccount = given.account("requester-account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(InvalidHttpParameterException.class, request);
    }

    private Set<UUID> getFeedbackSessionIds(FeedbackSessionsData data) {
        return data.getFeedbackSessions().stream()
                .map(session -> session.getFeedbackSession().getFeedbackSessionId())
                .collect(Collectors.toSet());
    }
}
