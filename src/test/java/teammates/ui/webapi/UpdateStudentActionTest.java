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
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Tests for {@link UpdateStudentAction}.
 */
public class UpdateStudentActionTest extends BaseActionTest<UpdateStudentAction, MessageOutput> {

    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    public void setUpData() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = GroupNames.ACTION)
    public void updateStudentAction_sendSummaryEmailTrue_updatesStudentAndQueuesSummaryEmail() {
        Student student = typicalBundle.students.get("student1InCourse1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentUpdateRequest requestBody = new StudentUpdateRequest(
                student.getName(),
                "updated.student@teammates.tmt",
                student.getTeamName(),
                student.getSectionName(),
                "Updated comments",
                true);

        MessageOutput result = execute(new RequestContext()
                .withAccountAuth(instructor.getAccount().getId())
                .withParam(Const.ParamsNames.USER_ID, student.getId().toString())
                .withRequest(requestBody));

        assertEquals(UpdateStudentAction.SUCCESSFUL_UPDATE_WITH_EMAIL, result.getMessage());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.STUDENT_EMAIL_CHANGED, queuedEmails.get(0).getType());
        assertEquals("updated.student@teammates.tmt", queuedEmails.get(0).getRecipient());
    }

    @Test(groups = GroupNames.ACTION)
    public void updateStudentAction_sendSummaryEmailFalse_updatesStudentWithoutQueueingEmail() {
        Student student = typicalBundle.students.get("student1InCourse1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentUpdateRequest requestBody = new StudentUpdateRequest(
                student.getName(),
                student.getEmail(),
                student.getTeamName(),
                student.getSectionName(),
                student.getComments(),
                false);

        MessageOutput result = execute(new RequestContext()
                .withAccountAuth(instructor.getAccount().getId())
                .withParam(Const.ParamsNames.USER_ID, student.getId().toString())
                .withRequest(requestBody));

        assertEquals(UpdateStudentAction.SUCCESSFUL_UPDATE, result.getMessage());
        assertEquals(0, getQueuedEmails().size());
    }

    private List<EmailWrapper> getQueuedEmails() {
        return mockTaskQueuer.getTasksAdded().stream()
                .map(TaskWrapper::getRequestBody)
                .map(SendEmailRequest.class::cast)
                .map(SendEmailRequest::getEmail)
                .toList();
    }
}
