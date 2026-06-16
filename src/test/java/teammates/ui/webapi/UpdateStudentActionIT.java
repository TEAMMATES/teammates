package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.storage.entity.Course;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.StudentUpdateRequest;

/**
 * SUT: {@link UpdateStudentAction}.
 */
public class UpdateStudentActionIT extends BaseActionIT<UpdateStudentAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute() throws Exception {
        assert true;
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute_invalidParameters_failure() {
        ______TS("no parameters");
        verifyHttpParameterFailure();
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute_typicalCase_success() {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        String originalEmail = student1.getEmail();
        Team originalTeam = student1.getTeam();
        String originalComments = student1.getComments();

        String newStudentEmail = "newemail@gmail.tmt";
        String newStudentTeam = "new student's team";
        String newStudentComments = "this is new comment after editing";
        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), newStudentEmail,
                newStudentTeam, student1.getSectionName(), newStudentComments, true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, student1.getId().toString(),
        };

        UpdateStudentAction updateAction = getAction(updateRequest, submissionParams);
        JsonResult actionOutput = getJsonResult(updateAction);

        MessageOutput msgOutput = (MessageOutput) actionOutput.getOutput();
        assertEquals("Student has been updated and email sent", msgOutput.getMessage());
        verifyNumberOfEmailsQueued(1);

        Student updatedStudent = inTransaction(() -> logic.getStudent(student1.getId()));
        assertEquals(updatedStudent.getEmail(), newStudentEmail);
        assertEquals(updatedStudent.getTeamName(), newStudentTeam);
        assertEquals(updatedStudent.getComments(), newStudentComments);

        EmailWrapper email = getQueuedEmails().get(0);
        String courseName = inTransaction(() -> logic.getCourse(student1.getCourseId()).getName());
        assertEquals(String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), courseName,
                student1.getCourseId()), email.getSubject());
        assertEquals(newStudentEmail, email.getRecipient());

        resetStudent(student1.getId(), originalEmail, originalTeam, originalComments);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute_studentDetailsWithWhitespace_success() {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        String originalEmail = student1.getEmail();
        Team originalTeam = student1.getTeam();
        String originalComments = student1.getComments();

        String newStudentEmailToBeTrimmed = "  student1@teammates.tmt   "; // after trim, this is equal to originalEmail
        String newStudentTeamToBeTrimmed = "  New team   ";
        String newStudentCommentsToBeTrimmed = "  this is new comment after editing   ";
        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), newStudentEmailToBeTrimmed,
                newStudentTeamToBeTrimmed, student1.getSectionName(), newStudentCommentsToBeTrimmed, true);

        String[] submissionParamsToBeTrimmed = new String[] {
                Const.ParamsNames.USER_ID, student1.getId().toString(),
        };

        UpdateStudentAction actionToBeTrimmed = getAction(updateRequest, submissionParamsToBeTrimmed);
        JsonResult outputToBeTrimmed = getJsonResult(actionToBeTrimmed);

        MessageOutput msgTrimmedOutput = (MessageOutput) outputToBeTrimmed.getOutput();
        assertEquals("Student has been updated and email sent", msgTrimmedOutput.getMessage());

        resetStudent(student1.getId(), originalEmail, originalTeam, originalComments);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute_emailHasTooManyCharacters_failure() throws Exception {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        String invalidStudentEmail = StringHelperExtension.generateStringOfLength(255 - "@gmail.tmt".length())
                + "@gmail.tmt";
        assertEquals(FieldValidator.EMAIL_MAX_LENGTH + 1, invalidStudentEmail.length());

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), invalidStudentEmail,
                student1.getTeamName(), student1.getSectionName(), student1.getComments(), false);

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, student1.getId().toString(),
        };

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(updateRequest, submissionParams);

        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.EMAIL_MAX_LENGTH),
                ihrbe.getMessage());

        verifyNoTasksAdded();
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute_emailTakenByOthers_failure() {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        Student student2 = typicalBundle.students.get("student2InCourse1");
        String takenStudentEmail = student2.getEmail();

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), takenStudentEmail,
                student1.getTeamName(), student1.getSectionName(), student1.getComments(), false);

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, student1.getId().toString(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        assertEquals("Trying to update to an email that is already in use", ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute_studentDoesNotExist_failure() {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), student1.getEmail(),
                student1.getTeamName(), student1.getSectionName(), student1.getComments(), false);

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, UUID.randomUUID().toString(),
        };

        EntityNotFoundException enfe = verifyEntityNotFound(updateRequest, submissionParams);
        assertEquals("The student you tried to edit does not exist.",
                enfe.getMessage());

        verifyNoTasksAdded();
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute_studentTeamExistsInAnotherSection_failure() {
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Student student4 = typicalBundle.students.get("student4InCourse1");

        assertNotEquals(student1.getSection(), student4.getSection());

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), student1.getEmail(),
                student4.getTeamName(), student1.getSectionName(), student1.getComments(), true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, student1.getId().toString(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        String expectedErrorMessage = "Team \"Team 4\" is detected in Sections \"Section 1\", \"Section 3\"."
                + " Please use different team names in different sections.";
        assertEquals(expectedErrorMessage, ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute_sectionFull_failure() {
        Student studentToJoinMaxSection = typicalBundle.students.get("student1InCourse1");

        Course course = typicalBundle.courses.get("course1");
        String courseId = studentToJoinMaxSection.getCourseId();
        String sectionInMaxCapacity = "sectionInMaxCapacity";
        inTransaction(() -> {
            Section section = logic.createSection(course, sectionInMaxCapacity);
            Team team = logic.createTeam(section, "randomTeamName");

            for (int i = 0; i < Const.SECTION_SIZE_LIMIT; i++) {
                logic.createStudent(course, team, "Name " + i, i + "email@test.com", "cmt" + i);
            }
        });

        List<Student> studentList = inTransaction(() -> logic.getStudentsForCourse(courseId));

        assertEquals(Const.SECTION_SIZE_LIMIT,
                studentList.stream().filter(student -> student.getSectionName().equals(sectionInMaxCapacity)).count());
        assertEquals(courseId, studentToJoinMaxSection.getCourseId());

        StudentUpdateRequest updateRequest =
                new StudentUpdateRequest(studentToJoinMaxSection.getName(), studentToJoinMaxSection.getEmail(),
                        "randomTeamName", sectionInMaxCapacity,
                        studentToJoinMaxSection.getComments(), true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, studentToJoinMaxSection.getId().toString(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        String expectedErrorMessage = String.format("You are trying enroll more than %d students in section \"%s\". "
                + "To avoid performance problems, please do not enroll more than %d students in a single section.",
                Const.SECTION_SIZE_LIMIT, sectionInMaxCapacity, Const.SECTION_SIZE_LIMIT);
        assertEquals(expectedErrorMessage, ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testAccessControl() throws Exception {
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Course course = typicalBundle.courses.get("course1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, student1.getId().toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);
    }

    private void resetStudent(UUID studentId, String originalEmail, Team originalTeam, String originalComments) {
        inTransaction(() -> {
            Student updatedStudent = logic.getStudent(studentId);
            updatedStudent.setEmail(originalEmail);
            updatedStudent.setTeam(originalTeam);
            updatedStudent.setComments(originalComments);
        });
    }

}
