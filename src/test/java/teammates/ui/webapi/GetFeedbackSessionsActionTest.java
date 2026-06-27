package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.FeedbackSessionsData;

/**
 * Tests for {@link GetFeedbackSessionsAction}.
 */
public class GetFeedbackSessionsActionTest
        extends BaseActionTest<GetFeedbackSessionsAction, FeedbackSessionsData> {

    private static final Instant EXTENDED_DEADLINE = Instant.parse("2027-01-15T00:00:00Z");

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_studentAcrossCourses_returnsSessionsWithDeadlines() {
        var account = given.account("account");
        var firstCourse = given.course("first-course");
        var secondCourse = given.course("second-course");
        var firstStudent = given.student("first-student",
                student -> student.course(firstCourse.alias()).account(account.alias()));
        given.student("second-student",
                student -> student.course(secondCourse.alias()).account(account.alias()));
        var firstSession = given.feedbackSession("first-session",
                session -> session.course(firstCourse.alias()).opened());
        given.feedbackSession("second-session",
                session -> session.course(secondCourse.alias()).opened());
        given.deadlineExtension("deadline-extension", extension -> extension
                .student(firstStudent.alias())
                .feedbackSession(firstSession.alias())
                .endTime(EXTENDED_DEADLINE));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT)
                .withAccountAuth(account.id());

        FeedbackSessionsData result = execute(request);

        assertEquals(2, result.getFeedbackSessions().size());
        assertEquals(EXTENDED_DEADLINE.toEpochMilli(),
                getSession(result, firstSession.id()).getUserDeadlineExtension());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_instructorAcrossCourses_returnsSessionsWithDeadlinesAndPermissions() {
        var account = given.account("account");
        var firstCourse = given.course("first-course");
        var secondCourse = given.course("second-course");
        var firstInstructor = given.instructor("first-instructor",
                instructor -> instructor.course(firstCourse.alias()).account(account.alias()).coOwner());
        given.instructor("second-instructor",
                instructor -> instructor.course(secondCourse.alias()).account(account.alias()).coOwner());
        var firstSession = given.feedbackSession("first-session",
                session -> session.course(firstCourse.alias()).opened());
        given.feedbackSession("second-session",
                session -> session.course(secondCourse.alias()).opened());
        given.deadlineExtension("deadline-extension", extension -> extension
                .instructor(firstInstructor.alias())
                .feedbackSession(firstSession.alias())
                .endTime(EXTENDED_DEADLINE));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR)
                .withParam(Const.ParamsNames.IS_IN_RECYCLE_BIN, "false")
                .withAccountAuth(account.id());

        FeedbackSessionsData result = execute(request);

        assertEquals(2, result.getFeedbackSessions().size());
        assertEquals(EXTENDED_DEADLINE.toEpochMilli(),
                getSession(result, firstSession.id()).getUserDeadlineExtension());
        assertTrue(getSession(result, firstSession.id()).getInstructorPermissions().getCanModifySession());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_unsupportedEntityType_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN)
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_studentNotInCourse_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var enrolledCourse = given.course("enrolled-course");
        var otherCourse = given.course("other-course");
        given.student("student", student -> student.course(enrolledCourse.alias()).account(account.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, otherCourse.id())
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT)
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    private FeedbackSessionViewData getSession(FeedbackSessionsData result, UUID sessionId) {
        return result.getFeedbackSessions().stream()
                .filter(session -> session.getFeedbackSession().getFeedbackSessionId().equals(sessionId))
                .findFirst()
                .orElseThrow();
    }
}
