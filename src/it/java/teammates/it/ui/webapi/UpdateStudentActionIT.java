package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelperExtension;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
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
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String instructorId = instructor1OfCourse1.getGoogleId();
        loginAsInstructor(instructorId);

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null student email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        verifyHttpParameterFailure(invalidParams);
        verifyNoTasksAdded();

        ______TS("Typical case, successful edit and save student detail");
        String newStudentEmail = "newemail@gmail.tmt";
        String newStudentTeam = "new student's team";
        String newStudentComments = "this is new comment after editing";
        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1InCourse1.getName(), newStudentEmail,
                newStudentTeam, student1InCourse1.getSectionName(), newStudentComments, true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        UpdateStudentAction updateAction = getAction(updateRequest, submissionParams);
        JsonResult actionOutput = getJsonResult(updateAction);

        MessageOutput msgOutput = (MessageOutput) actionOutput.getOutput();
        assertEquals("Student has been updated and email sent", msgOutput.getMessage());
        verifyNumberOfEmailsSent(1);

        EmailWrapper email = getEmailsSent().get(0);
        String courseName = logic.getCourse(instructor1OfCourse1.getCourseId()).getName();
        assertEquals(String.format(EmailType.STUDENT_EMAIL_CHANGED.getSubject(), courseName,
                instructor1OfCourse1.getCourseId()), email.getSubject());
        assertEquals(newStudentEmail, email.getRecipient());

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("Typical case, successful edit and save student detail with spaces to be trimmed");
        String newStudentEmailToBeTrimmed = "  newemail@gmail.tmt   "; // after trim, this is equal to newStudentEmail
        String newStudentTeamToBeTrimmed = "  New team   ";
        String newStudentCommentsToBeTrimmed = "  this is new comment after editing   ";
        updateRequest = new StudentUpdateRequest(student1InCourse1.getName(), newStudentEmailToBeTrimmed,
                newStudentTeamToBeTrimmed, student1InCourse1.getSectionName(), newStudentCommentsToBeTrimmed, true);

        String[] submissionParamsToBeTrimmed = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail,
        };

        UpdateStudentAction actionToBeTrimmed = getAction(updateRequest, submissionParamsToBeTrimmed);
        JsonResult outputToBeTrimmed = getJsonResult(actionToBeTrimmed);

        MessageOutput msgTrimmedOutput = (MessageOutput) outputToBeTrimmed.getOutput();
        assertEquals("Student has been updated", msgTrimmedOutput.getMessage());
        verifyNoEmailsSent();

        ______TS("Error case, invalid email parameter (email has too many characters)");

        String invalidStudentEmail = StringHelperExtension.generateStringOfLength(255 - "@gmail.tmt".length())
                + "@gmail.tmt";
        assertEquals(FieldValidator.EMAIL_MAX_LENGTH + 1, invalidStudentEmail.length());

        updateRequest = new StudentUpdateRequest(student1InCourse1.getName(), invalidStudentEmail,
                student1InCourse1.getTeamName(), student1InCourse1.getSectionName(), student1InCourse1.getComments(), false);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail,
        };

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(updateRequest, submissionParams);

        assertEquals(getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, invalidStudentEmail,
                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.EMAIL_MAX_LENGTH),
                ihrbe.getMessage());

        verifyNoTasksAdded();

        ______TS("Error case, invalid email parameter (email already taken by others)");

        Student student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        String takenStudentEmail = student2InCourse1.getEmail();

        updateRequest = new StudentUpdateRequest(student1InCourse1.getName(), takenStudentEmail,
                student1InCourse1.getTeamName(), student1InCourse1.getSectionName(), student1InCourse1.getComments(), false);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, newStudentEmail,
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        assertEquals("Trying to update to an email that is already in use", ioe.getMessage());

        verifyNoTasksAdded();

        // deleting edited student
        logic.deleteAccountCascade(student2InCourse1.getGoogleId());
        logic.deleteAccountCascade(student1InCourse1.getGoogleId());

        ______TS("Error case, student does not exist");

        String nonExistentEmailForStudent = "notinuseemail@gmail.tmt";
        updateRequest = new StudentUpdateRequest(student1InCourse1.getName(), student1InCourse1.getEmail(),
                student1InCourse1.getTeamName(), student1InCourse1.getSectionName(), student1InCourse1.getComments(), false);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, nonExistentEmailForStudent,
        };

        EntityNotFoundException enfe = verifyEntityNotFound(updateRequest, submissionParams);
        assertEquals("The student you tried to edit does not exist. "
                + "If the student was created during the last few minutes, "
                + "try again in a few more minutes as the student may still be being saved.",
                enfe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_withTeamNameAlreadyExistsInAnotherSection_shouldFail() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        Student student4InCourse1 = typicalBundle.students.get("student4InCourse1");

        assertNotEquals(student1InCourse1.getSection(), student4InCourse1.getSection());

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(student1InCourse1.getName(),
                student1InCourse1.getEmail(), student4InCourse1.getTeamName(), student1InCourse1.getSectionName(),
                student1InCourse1.getComments(), true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        InvalidOperationException ioe = verifyInvalidOperation(updateRequest, submissionParams);
        String expectedErrorMessage = String.format("Team \"%s\" is detected in both Section \"%s\" and Section \"%s\"."
                + " Please use different team names in different sections.", student4InCourse1.getTeamName(),
                student1InCourse1.getSectionName(), student4InCourse1.getSectionName());
        assertEquals(expectedErrorMessage, ioe.getMessage());

        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_withSectionAlreadyHasMaxNumberOfStudents_shouldFail() throws Exception {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Course course = typicalBundle.courses.get("course1");
        String courseId = instructor1OfCourse1.getCourseId();
        String sectionInMaxCapacity = "sectionInMaxCapacity";
        Section section = logic.getSectionOrCreate(courseId, sectionInMaxCapacity);
        Team team = logic.getTeamOrCreate(section, "randomTeamName");

        for (int i = 0; i < Const.SECTION_SIZE_LIMIT; i++) {
            Student addedStudent = new Student(course, "Name " + i, i + "email@test.com", "cmt" + i, team);

            logic.createStudent(addedStudent);
        }

        List<Student> studentList = logic.getStudentsForCourse(courseId);

        Student studentToJoinMaxSection = typicalBundle.students.get("student1InCourse1");
        assertEquals(Const.SECTION_SIZE_LIMIT,
                studentList.stream().filter(student -> student.getSectionName().equals(sectionInMaxCapacity)).count());
        assertEquals(courseId, studentToJoinMaxSection.getCourseId());

        StudentUpdateRequest updateRequest =
                new StudentUpdateRequest(studentToJoinMaxSection.getName(), studentToJoinMaxSection.getEmail(),
                        "randomTeamName", sectionInMaxCapacity,
                        studentToJoinMaxSection.getComments(), true);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
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
    public void testExecute_withEmptySectionName_shouldBeUpdatedWithDefaultSectionName() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student4InCourse1 = typicalBundle.students.get("student4InCourse1");

        StudentUpdateRequest emptySectionUpdateRequest =
                new StudentUpdateRequest(student4InCourse1.getName(), student4InCourse1.getEmail(),
                        student4InCourse1.getTeamName(), "", student4InCourse1.getComments(), true);

        String[] emptySectionSubmissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student4InCourse1.getEmail(),
        };

        UpdateStudentAction updateEmptySectionAction =
                getAction(emptySectionUpdateRequest, emptySectionSubmissionParams);
        JsonResult emptySectionActionOutput = getJsonResult(updateEmptySectionAction);

        MessageOutput emptySectionMsgOutput = (MessageOutput) emptySectionActionOutput.getOutput();
        assertEquals("Student has been updated", emptySectionMsgOutput.getMessage());
        verifyNoEmailsSent();

        // verify student in database
        Student actualStudent =
                logic.getStudentForEmail(student4InCourse1.getCourseId(), student4InCourse1.getEmail());
        assertEquals(student4InCourse1.getCourse(), actualStudent.getCourse());
        assertEquals(student4InCourse1.getName(), actualStudent.getName());
        assertEquals(student4InCourse1.getEmail(), actualStudent.getEmail());
        assertEquals(student4InCourse1.getTeam(), actualStudent.getTeam());
        assertEquals(Const.DEFAULT_SECTION, actualStudent.getSectionName());
        assertEquals(student4InCourse1.getComments(), actualStudent.getComments());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Student student1InCourse1 = typicalBundle.students.get("student3InCourse1");
        Course course = typicalBundle.courses.get("course1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                course, Const.InstructorPermissions.CAN_MODIFY_STUDENT, submissionParams);
    }
}
