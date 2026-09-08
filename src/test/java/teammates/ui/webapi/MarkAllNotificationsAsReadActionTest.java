package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.Test;

import teammates.test.GroupNames;
import teammates.ui.output.ReadNotificationsData;

/**
 * Tests for {@link MarkAllNotificationsAsReadAction}.
 */
public class MarkAllNotificationsAsReadActionTest
        extends BaseActionTest<MarkAllNotificationsAsReadAction, ReadNotificationsData> {

    @Test(groups = GroupNames.ACTION)
    public void markAllNotificationsAsReadAction_studentWithUnreadNotifications_marksGeneralAndStudentNotifications() {
        var account = given.account("account");
        var course = given.course("course");
        given.student("student", s -> s.account(account.alias()).course(course.alias()));
        var generalNotification = given.notification("general-notification", n -> n.forGeneral());
        var studentNotification = given.notification("student-notification", n -> n.forStudent());
        var instructorNotification = given.notification("instructor-notification", n -> n.forInstructor());
        persistGivenData(given);

        ReadNotificationsData result = execute(new RequestContext().withAccountAuth(account.id()));

        assertEquals(2, result.getReadNotifications().size());
        assertTrue(result.getReadNotifications().contains(generalNotification.id()));
        assertTrue(result.getReadNotifications().contains(studentNotification.id()));
        assertFalse(result.getReadNotifications().contains(instructorNotification.id()));
    }

    @Test(groups = GroupNames.ACTION)
    public void markAllNotificationsAsReadAction_instructorWithUnreadNotifications_marksGeneralAndInstructorNotifications() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.account(account.alias()).course(course.alias()));
        var generalNotification = given.notification("general-notification", n -> n.forGeneral());
        var instructorNotification = given.notification("instructor-notification", n -> n.forInstructor());
        var studentNotification = given.notification("student-notification", n -> n.forStudent());
        persistGivenData(given);

        ReadNotificationsData result = execute(new RequestContext().withAccountAuth(account.id()));

        assertEquals(2, result.getReadNotifications().size());
        assertTrue(result.getReadNotifications().contains(generalNotification.id()));
        assertTrue(result.getReadNotifications().contains(instructorNotification.id()));
        assertFalse(result.getReadNotifications().contains(studentNotification.id()));
    }

    @Test(groups = GroupNames.ACTION)
    public void markAllNotificationsAsReadAction_generalUser_marksGeneralNotificationsOnly() {
        var account = given.account("account");
        var generalNotification = given.notification("general-notification", n -> n.forGeneral());
        var studentNotification = given.notification("student-notification", n -> n.forStudent());
        persistGivenData(given);

        ReadNotificationsData result = execute(new RequestContext().withAccountAuth(account.id()));

        assertEquals(1, result.getReadNotifications().size());
        assertTrue(result.getReadNotifications().contains(generalNotification.id()));
        assertFalse(result.getReadNotifications().contains(studentNotification.id()));
    }

    @Test(groups = GroupNames.ACTION)
    public void markAllNotificationsAsReadAction_notificationsAlreadyRead_returnsEmptyList() {
        var account = given.account("account");
        given.notification("notification", n -> n.forGeneral());
        persistGivenData(given);

        RequestContext request = new RequestContext().withAccountAuth(account.id());

        ReadNotificationsData firstResult = execute(request);
        assertEquals(1, firstResult.getReadNotifications().size());

        ReadNotificationsData secondResult = execute(request);
        assertEquals(0, secondResult.getReadNotifications().size());
    }
}
