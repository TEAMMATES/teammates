package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.output.FeedbackSessionViewData;
import teammates.ui.output.FeedbackSessionsData;

/**
 * Tests for {@link GetFeedbackSessionsAction}.
 */
public class GetFeedbackSessionsActionTest
        extends BaseActionTest<GetFeedbackSessionsAction, FeedbackSessionsData> {

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_studentAcrossCourses_returnsVisibleSessionsWithDeadlines() {
        Instant firstSessionEndTime = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant secondSessionEndTime = Instant.now().plus(2, ChronoUnit.HOURS);
        Instant extendedDeadline = Instant.now().plus(3, ChronoUnit.HOURS);

        var account = given.account("account");
        var firstCourse = given.course("first-course");
        var secondCourse = given.course("second-course");
        var firstStudent = given.student("first-student",
                student -> student.course(firstCourse.alias()).account(account.alias()));
        given.student("second-student",
                student -> student.course(secondCourse.alias()).account(account.alias()));
        var firstSession = given.feedbackSession("first-session",
                session -> session.course(firstCourse.alias()).opened().endTime(firstSessionEndTime));
        var secondSession = given.feedbackSession("second-session",
                session -> session.course(secondCourse.alias()).opened().endTime(secondSessionEndTime));
        given.feedbackSession("invisible-session",
                session -> session.course(secondCourse.alias()).notVisible());
        given.deadlineExtension("deadline-extension", extension -> extension
                .student(firstStudent.alias())
                .feedbackSession(firstSession.alias())
                .endTime(extendedDeadline));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT)
                .withAccountAuth(account.id());

        FeedbackSessionsData result = execute(request);

        Map<UUID, FeedbackSessionViewData> sessionsById = result.getFeedbackSessions().stream()
                .collect(Collectors.toMap(
                        session -> session.getFeedbackSession().getFeedbackSessionId(),
                        Function.identity()));
        assertEquals(2, sessionsById.size());
        assertEquals(extendedDeadline.toEpochMilli(),
                sessionsById.get(firstSession.id()).getUserDeadlineExtension());
        assertEquals(secondSessionEndTime.toEpochMilli(),
                sessionsById.get(secondSession.id()).getUserDeadlineExtension());
        assertNull(sessionsById.get(firstSession.id()).getFeedbackSession().getSessionVisibleFromTimestamp());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackSessionsAction_instructorAcrossCourses_returnsSessionsWithDeadlinesAndPermissions() {
        Instant extendedDeadline = Instant.now().plus(3, ChronoUnit.HOURS);

        var account = given.account("account");
        var firstCourse = given.course("first-course");
        var secondCourse = given.course("second-course");
        var firstInstructor = given.instructor("first-instructor",
                instructor -> instructor.course(firstCourse.alias()).account(account.alias()).coOwner());
        given.instructor("second-instructor",
                instructor -> instructor.course(secondCourse.alias()).account(account.alias()).coOwner());
        var firstSession = given.feedbackSession("first-session",
                session -> session.course(firstCourse.alias()).opened());
        var secondSession = given.feedbackSession("second-session",
                session -> session.course(secondCourse.alias()).opened());
        given.deadlineExtension("deadline-extension", extension -> extension
                .instructor(firstInstructor.alias())
                .feedbackSession(firstSession.alias())
                .endTime(extendedDeadline));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR)
                .withParam(Const.ParamsNames.IS_IN_RECYCLE_BIN, "false")
                .withAccountAuth(account.id());

        FeedbackSessionsData result = execute(request);

        Map<UUID, FeedbackSessionViewData> sessionsById = result.getFeedbackSessions().stream()
                .collect(Collectors.toMap(
                        session -> session.getFeedbackSession().getFeedbackSessionId(),
                        Function.identity()));
        assertEquals(2, sessionsById.size());
        assertEquals(extendedDeadline.toEpochMilli(),
                sessionsById.get(firstSession.id()).getUserDeadlineExtension());
        assertEquals(secondSession.id(),
                sessionsById.get(secondSession.id()).getFeedbackSession().getFeedbackSessionId());
        assertTrue(sessionsById.get(firstSession.id())
                .getInstructorPermissions().getCanModifySession());
    }
}
