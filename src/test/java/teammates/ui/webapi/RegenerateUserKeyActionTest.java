package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.storage.entity.User;
import teammates.test.GroupNames;
import teammates.ui.output.RegenerateKeyData;
import teammates.ui.request.SendEmailRequest;

/**
 * Tests for {@link RegenerateUserKeyAction}.
 */
public class RegenerateUserKeyActionTest extends BaseActionTest<RegenerateUserKeyAction, RegenerateKeyData> {

    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    public void setUpData() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = GroupNames.ACTION)
    public void regenerateUserKeyAction_student_queuesStudentSummaryEmail() {
        User student = typicalBundle.students.get("student1InCourse1");

        RegenerateKeyData result = execute(new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.USER_ID, student.getId().toString()));

        assertEquals(RegenerateUserKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_QUEUED, result.getMessage());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.STUDENT_COURSE_LINKS_REGENERATED, queuedEmails.get(0).getType());
        assertEquals(student.getEmail(), queuedEmails.get(0).getRecipient());
    }

    @Test(groups = GroupNames.ACTION)
    public void regenerateUserKeyAction_instructor_queuesInstructorSummaryEmail() {
        User instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        RegenerateKeyData result = execute(new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.USER_ID, instructor.getId().toString()));

        assertEquals(RegenerateUserKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_QUEUED, result.getMessage());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED, queuedEmails.get(0).getType());
        assertEquals(instructor.getEmail(), queuedEmails.get(0).getRecipient());
    }

    private List<EmailWrapper> getQueuedEmails() {
        return mockTaskQueuer.getTasksAdded().stream()
                .map(TaskWrapper::getRequestBody)
                .map(SendEmailRequest.class::cast)
                .map(SendEmailRequest::getEmail)
                .toList();
    }
}
