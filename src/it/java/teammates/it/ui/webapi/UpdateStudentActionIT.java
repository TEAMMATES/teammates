package teammates.it.ui.webapi;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelperExtension;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.StudentUpdateRequest;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UpdateStudentAction;

/**
 * SUT: {@link UpdateStudentAction}.
 */
public class UpdateStudentActionIT extends BaseActionIT<UpdateStudentAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
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
    @Test
    public void testExecute() throws Exception {
        assert true;
    }

    @Test
    public void testExecute_invalidParameters_failure() throws Exception {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        ______TS("no parameters");
        verifyHttpParameterFailure();

        ______TS("null student email");
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1.getCourseId(),
        };
        verifyHttpParameterFailure(invalidParams);

        ______TS("null course id");
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };
        verifyHttpParameterFailure(invalidParams);
        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_typicalCase_success() throws Exception {
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
                Const.ParamsNames.COURSE_ID, student1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };

        UpdateStudentAction updateAction = getAction(updateRequest, submissionParams);
        JsonResult actionOutput = getJsonResult(updateAction);

        MessageOutput msgOutput = (MessageOutput) actionOutput.getOutput();
        assertEquals("Student has been updated and email sent", msgOutput.getMessage());
        verifyNumberOfEmailsSent(1);

        Student updatedStudent = logic.getStudent(student1.getId());
        assertEquals(updatedStudent.getEmail(), newStudentEmail);
        assertEquals(updatedStudent.getTeamName(), newStudentTeam);
        assertEquals(updatedStudent.getComments(), newStudentComments);

        EmailWrapper email = getEmailsSent().get(0);
        String courseName = logic.getCourse(student1.getCourseId()).getName();
        assertEquals(String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), courseName,
                student1.getCourseId()), email.getSubject());
        assertEquals(newStudentEmail, email.getRecipient());

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        resetStudent(student1.getId(), originalEmail, originalTeam, originalComments);
    }

    @Test
    public void testExecute_studentDetailsWithWhitespace_success() throws Exception {
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
                Const.ParamsNames.COURSE_ID, student1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };

        UpdateStudentAction actionToBeTrimmed = getAction(updateRequest, submissionParamsToBeTrimmed);
        JsonResult outputToBeTrimmed = getJsonResult(actionToBeTrimmed);

        MessageOutput msgTrimmedOutput = (MessageOutput) outputToBeTrimmed.getOutput();
        assertEquals("Student has been updated", msgTrimmedOutput.getMessage());
        verifyNoEmailsSent();

        resetStudent(student1.getId(), originalEmail, originalTeam, originalComments);
    }

    @Test
    public void testExecute_emailHasTooManyCharacters_failure() throws Exception {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        String invalidStudentEmail = StringHelperExtension.generateStringOfLength(255 - "@gmail.tmt".length())
                + "@gmail.tmt";
        assertEquals(FieldValidator.EMAIL_MAX_LENGTH + 1, invalidStudentEmail.length());

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), invalidStudentEmail,
                student1.getTeamName(), student1.getSectionName(), student1.getComments(), false);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(updateRequest, submissionParams);

        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.EMAIL_MAX_LENGTH),
                ihrbe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_emailTakenByOthers_failure() {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        Student student2 = typicalBundle.students.get("student2InCourse1");
        String takenStudentEmail = student2.getEmail();

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), takenStudentEmail,
                student1.getTeamName(), student1.getSectionName(), student1.getComments(), false);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        assertEquals("Trying to update to an email that is already in use", ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_studentDoesNotExist_failure() {
        Student student1 = typicalBundle.students.get("student1InCourse1");

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), student1.getEmail(),
                student1.getTeamName(), student1.getSectionName(), student1.getComments(), false);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, "notinuseemail@gmail.tmt",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(updateRequest, submissionParams);
        assertEquals("The student you tried to edit does not exist. "
                + "If the student was created during the last few minutes, "
                + "try again in a few more minutes as the student may still be being saved.",
                enfe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_studentTeamExistsInAnotherSection_failure() throws Exception {
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Student student4 = typicalBundle.students.get("student4InCourse1");

        assertNotEquals(student1.getSection(), student4.getSection());

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1.getName(), student1.getEmail(),
                student4.getTeamName(), student1.getSectionName(), student1.getComments(), true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        String expectedErrorMessage = String.format("Team \"%s\" is detected in both Section \"%s\" and Section \"%s\"."
                + " Please use different team names in different sections.", student4.getTeamName(),
                student1.getSectionName(), student4.getSectionName());
        assertEquals(expectedErrorMessage, ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_sectionFull_failure() throws Exception {
        Student studentToJoinMaxSection = typicalBundle.students.get("student1InCourse1");

        Course course = typicalBundle.courses.get("course1");
        String courseId = studentToJoinMaxSection.getCourseId();
        String sectionInMaxCapacity = "sectionInMaxCapacity";
        Section section = logic.getSectionOrCreate(courseId, sectionInMaxCapacity);
        Team team = logic.getTeamOrCreate(section, "randomTeamName");

        for (int i = 0; i < Const.SECTION_SIZE_LIMIT; i++) {
            Student addedStudent = new Student(course, "Name " + i, i + "email@test.com", "cmt" + i, team);

            logic.createStudent(addedStudent);
        }

        List<Student> studentList = logic.getStudentsForCourse(courseId);

        assertEquals(Const.SECTION_SIZE_LIMIT,
                studentList.stream().filter(student -> student.getSectionName().equals(sectionInMaxCapacity)).count());
        assertEquals(courseId, studentToJoinMaxSection.getCourseId());

        StudentUpdateRequest updateRequest =
                new StudentUpdateRequest(studentToJoinMaxSection.getName(), studentToJoinMaxSection.getEmail(),
                        "randomTeamName", sectionInMaxCapacity,
                        studentToJoinMaxSection.getComments(), true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, studentToJoinMaxSection.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, studentToJoinMaxSection.getEmail(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        String expectedErrorMessage = String.format("You are trying enroll more than %d students in section \"%s\". "
                + "To avoid performance problems, please do not enroll more than %d students in a single section.",
                Const.SECTION_SIZE_LIMIT, sectionInMaxCapacity, Const.SECTION_SIZE_LIMIT);
        assertEquals(expectedErrorMessage, ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_renameEmptySectionNameToDefault_success() {
        Student student4 = typicalBundle.students.get("student4InCourse1");

        Team originalTeam = student4.getTeam();

        StudentUpdateRequest emptySectionUpdateRequest = new StudentUpdateRequest(student4.getName(), student4.getEmail(),
                student4.getTeamName(), "", student4.getComments(), true);

        String[] emptySectionSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student4.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student4.getEmail(),
        };

        UpdateStudentAction updateEmptySectionAction = getAction(emptySectionUpdateRequest, emptySectionSubmissionParams);
        JsonResult emptySectionActionOutput = getJsonResult(updateEmptySectionAction);

        MessageOutput emptySectionMsgOutput = (MessageOutput) emptySectionActionOutput.getOutput();
        assertEquals("Student has been updated", emptySectionMsgOutput.getMessage());
        verifyNoEmailsSent();

        // verify student in database
        Student actualStudent =
                logic.getStudentForEmail(student4.getCourseId(), student4.getEmail());
        assertEquals(student4.getCourse(), actualStudent.getCourse());
        assertEquals(student4.getName(), actualStudent.getName());
        assertEquals(student4.getEmail(), actualStudent.getEmail());
        assertEquals(student4.getTeam(), actualStudent.getTeam());
        assertEquals(Const.DEFAULT_SECTION, actualStudent.getSectionName());
        assertEquals(student4.getComments(), actualStudent.getComments());

        resetStudent(student4.getId(), student4.getEmail(), originalTeam, student4.getComments());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Course course = typicalBundle.courses.get("course1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);
    }

    private void resetStudent(UUID studentId, String originalEmail, Team originalTeam, String originalComments) {
        Student updatedStudent = logic.getStudent(studentId);
        updatedStudent.setEmail(originalEmail);
        updatedStudent.setTeam(originalTeam);
        updatedStudent.setComments(originalComments);
    }

}
