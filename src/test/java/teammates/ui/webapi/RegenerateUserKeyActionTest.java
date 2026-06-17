package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.logic.api.Logic;
import teammates.test.GroupNames;
import teammates.ui.output.RegenerateKeyData;
import teammates.ui.request.SendEmailRequest;

/**
 * Tests for {@link RegenerateUserKeyAction}.
 */
public class RegenerateUserKeyActionTest extends BaseActionTest<RegenerateUserKeyAction, RegenerateKeyData> {

    @Test(groups = GroupNames.ACTION)
    public void regenerateUserKeyAction_student_queuesStudentSummaryEmail() {
        var course = given.course("course");
        var studentAccount = given.account("student-account");
        given.student("student", s -> s.course(course.alias()).account(studentAccount.alias()));
        given.instructor("instructor", i -> i.course(course.alias()).coOwner());
        persistGivenData(given);

        String studentEmail = inTransaction(() -> Logic.inst().getStudent(given.uuid("student"))).getEmail();

        RegenerateKeyData result = execute(new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.USER_ID, given.uuid("student").toString()));

        assertEquals(RegenerateUserKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_QUEUED, result.getMessage());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.STUDENT_COURSE_LINKS_REGENERATED, queuedEmails.get(0).getType());
        assertEquals(studentEmail, queuedEmails.get(0).getRecipient());
    }

    @Test(groups = GroupNames.ACTION)
    public void regenerateUserKeyAction_instructor_queuesInstructorSummaryEmail() {
        var course = given.course("course");
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.course(course.alias()).account(instructorAccount.alias()).coOwner());
        persistGivenData(given);

        String instructorEmail = inTransaction(() -> Logic.inst().getInstructor(given.uuid("instructor"))).getEmail();

        RegenerateKeyData result = execute(new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.USER_ID, given.uuid("instructor").toString()));

        assertEquals(RegenerateUserKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_QUEUED, result.getMessage());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED, queuedEmails.get(0).getType());
        assertEquals(instructorEmail, queuedEmails.get(0).getRecipient());
    }

    private List<EmailWrapper> getQueuedEmails() {
        return mockTaskQueuer.getTasksAdded().stream()
                .map(TaskWrapper::getRequestBody)
                .map(SendEmailRequest.class::cast)
                .map(SendEmailRequest::getEmail)
                .toList();
    }
}
