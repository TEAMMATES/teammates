package teammates.test.cases.webapi;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.template.EnrollResultPanel;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.PostCourseEnrollSaveAction;
import teammates.ui.webapi.action.PostCourseEnrollSaveAction.EnrollResults;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link PostCourseEnrollSaveAction}.
 */
public class PostCourseEnrollSaveActionTest extends BaseActionTest<PostCourseEnrollSaveAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_ENROLL_SAVE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        String enrollString;

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        loginAsInstructor(instructorId);

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null course id
        String[] invalidParams = new String[] {};
        verifyHttpParameterFailure(invalidParams);

        ______TS("Typical case: add and edit students for non-empty course");

        enrollString = "Section | Team | Name | Email | Comment" + System.lineSeparator()
                // A new student
                + "Section 3 \t Team 1\tJean Wong\tjean@email.tmt\tExchange student" + System.lineSeparator()
                // A new student with extra spaces in the team and name
                + "Section 3 \t Team   1\tstudent  with   extra  spaces  \t"
                + "studentWithExtraSpaces@gmail.tmt\t" + System.lineSeparator()
                // A student to be modified
                + "Section 2 \t Team 1.3\tstudent1 In Course1</td></div>'\"\tstudent1InCourse1@gmail.tmt\t"
                + "New comment added" + System.lineSeparator()
                // An existing student with no modification
                + "Section 1 \t Team 1.1</td></div>'\"\tstudent2 In Course1\tstudent2InCourse1@gmail.tmt\t"
                + System.lineSeparator()
                // An existing student, now with extra spaces, should cause no modification
                + "Section 1 \t Team   1.1</td></div>'\"\tstudent3  In   Course1  \tstudent3InCourse1@gmail.tmt\t";

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        PostCourseEnrollSaveAction a = getAction(enrollString, submissionParams);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        EnrollResults output = (EnrollResults) r.getOutput();

        List<TaskWrapper> tasksAdded = a.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            assertEquals(courseId, paramMap.get(Const.ParamsNames.COURSE_ID)[0]);
        }

        StudentAttributes newStudent = StudentAttributes
                .builder(courseId, "jean@email.tmt")
                .withName("Jean Wong")
                .withSectionName("Section 3")
                .withTeamName("Team 1")
                .withComment("Exchange student")
                .withGoogleId("jean")
                .build();
        newStudent.updateStatus = StudentUpdateStatus.NEW;
        verifyStudentEnrollmentStatus(newStudent, output.getEnrollResultPanelList());

        StudentAttributes newStudentWithExtraSpaces = StudentAttributes
                .builder(courseId, "studentWithExtraSpaces@gmail.tmt")
                .withName("student with extra spaces")
                .withSectionName("Section 3")
                .withTeamName("Team 1")
                .withComment("")
                .withGoogleId("student")
                .build();
        newStudentWithExtraSpaces.updateStatus = StudentUpdateStatus.NEW;
        verifyStudentEnrollmentStatus(newStudentWithExtraSpaces, output.getEnrollResultPanelList());

        StudentAttributes modifiedStudent = typicalBundle.students.get("student1InCourse1");
        modifiedStudent.comments = "New comment added";
        modifiedStudent.section = "Section 2";
        modifiedStudent.team = "Team 1.3";
        modifiedStudent.updateStatus = StudentUpdateStatus.MODIFIED;
        verifyStudentEnrollmentStatus(modifiedStudent, output.getEnrollResultPanelList());

        StudentAttributes unmodifiedStudent = typicalBundle.students.get("student2InCourse1");
        unmodifiedStudent.updateStatus = StudentUpdateStatus.UNMODIFIED;
        verifyStudentEnrollmentStatus(unmodifiedStudent, output.getEnrollResultPanelList());

        StudentAttributes unmodifiedStudentWithExtraSpaces = typicalBundle.students.get("student3InCourse1");
        unmodifiedStudentWithExtraSpaces.updateStatus = StudentUpdateStatus.UNMODIFIED;
        verifyStudentEnrollmentStatus(unmodifiedStudentWithExtraSpaces, output.getEnrollResultPanelList());

        ______TS("Masquerade mode, enrollment into empty course");

        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        courseId = "new-course";
        CoursesLogic.inst().createCourseAndInstructor(instructorId,
                CourseAttributes.builder(courseId)
                        .withName("New course")
                        .withTimezone(ZoneId.of("UTC"))
                        .build());

        loginAsAdmin();

        String headerRow = "Name\tEmail\tTeam\tComment";
        String studentsInfo = "Jean Wong\tjean@email.tmt\tTeam 1\tExchange student"
                + System.lineSeparator() + "James Tan\tjames@email.tmt\tTeam 2\t";
        enrollString = headerRow + System.lineSeparator() + studentsInfo;

        submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
        };
        a = getAction(enrollString, submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        output = (EnrollResults) r.getOutput();
        verifyNoTasksAdded(a);

        StudentAttributes student1 = StudentAttributes
                .builder(courseId, "jean@email.tmt")
                .withName("Jean Wong")
                .withSectionName("None")
                .withTeamName("Team 1")
                .withComment("Exchange student")
                .withGoogleId("jean")
                .build();
        student1.updateStatus = StudentUpdateStatus.NEW;
        verifyStudentEnrollmentStatus(student1, output.getEnrollResultPanelList());

        StudentAttributes student2 = StudentAttributes
                .builder(courseId, "james@email.tmt")
                .withName("James Tan")
                .withSectionName("None")
                .withTeamName("Team 2")
                .withComment("")
                .withGoogleId("james")
                .build();
        student2.updateStatus = StudentUpdateStatus.NEW;
        verifyStudentEnrollmentStatus(student2, output.getEnrollResultPanelList());

        ______TS("Failure case: enrollment failed due to invalid lines");

        gaeSimulation.loginAsInstructor(instructorId);

        String studentWithoutEnoughParam = "Team 1\tStudentWithNoEmailInput";
        String studentWithInvalidEmail = "Team 2\tBenjamin Tan\tinvalid.email.tmt";
        String invalidEmail = "invalid.email.tmt";
        enrollString = "Team | Name | Email" + System.lineSeparator()
                + studentWithoutEnoughParam + System.lineSeparator()
                + studentWithInvalidEmail;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        a = getAction(enrollString, submissionParams);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        String expectedStatusMessage = "<p>"
                + "<span class=\"bold\">Problem in line : "
                + "<span class=\"invalidLine\">"
                + SanitizationHelper.sanitizeForHtml(studentWithoutEnoughParam)
                + "</span>"
                + "</span>"
                + "<br>"
                + "<span class=\"problemDetail\">&bull; "
                + StudentAttributesFactory.ERROR_ENROLL_LINE_TOOFEWPARTS
                + "</span>"
                + "</p>"
                + "<br>"
                + "<p>"
                + "<span class=\"bold\">Problem in line : "
                + "<span class=\"invalidLine\">"
                + SanitizationHelper.sanitizeForHtml(studentWithInvalidEmail)
                + "</span>"
                + "</span>"
                + "<br>"
                + "<span class=\"problemDetail\">&bull; "
                + SanitizationHelper.sanitizeForHtml(
                getPopulatedErrorMessage(
                        FieldValidator.EMAIL_ERROR_MESSAGE,
                        invalidEmail,
                        FieldValidator.EMAIL_FIELD_NAME,
                        FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH))
                + "</span>"
                + "</p>";
        MessageOutput statusMessage = (MessageOutput) r.getOutput();
        assertEquals(expectedStatusMessage, statusMessage.getMessage());
        verifyNoTasksAdded(a);

        ______TS("Boundary test for size limit per enrollment");

        //can enroll, if within the size limit
        StringBuilder enrollStringBuilder = new StringBuilder(200);
        enrollStringBuilder.append("Section\tTeam\tName\tEmail");
        for (int i = 0; i < Const.SIZE_LIMIT_PER_ENROLLMENT; i++) {
            enrollStringBuilder.append(System.lineSeparator()).append("section" + i + "\tteam" + i + "\tname" + i
                    + "\temail" + i + "@nonexistemail.nonexist");
        }
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        a = getAction(enrollStringBuilder.toString(), submissionParams);
        r = getJsonResult(a);
        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        verifyNoTasksAdded(a);

        //fail to enroll, if exceed the range
        enrollStringBuilder.append(System.lineSeparator()).append(
                "section" + Const.SIZE_LIMIT_PER_ENROLLMENT + "\tteam" + Const.SIZE_LIMIT_PER_ENROLLMENT
                        + "\tname" + Const.SIZE_LIMIT_PER_ENROLLMENT + "\temail" + Const.SIZE_LIMIT_PER_ENROLLMENT
                        + "@nonexistemail.nonexist");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        a = getAction(enrollStringBuilder.toString(), submissionParams);
        r = getJsonResult(a);
        MessageOutput msgOutput = (MessageOutput) r.getOutput();

        assertEquals(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED, msgOutput.getMessage());
        verifyNoTasksAdded(a);

        ______TS("Failure case: empty input");

        enrollString = "";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString,
        };
        a = getAction(submissionParams);
        r = getJsonResult(a);
        msgOutput = (MessageOutput) r.getOutput();
        assertEquals(Const.StatusMessages.ENROLL_LINE_EMPTY, msgOutput.getMessage());
        verifyNoTasksAdded(a);

        CoursesLogic.inst().deleteCourseCascade("new-course");
        StudentsLogic.inst().deleteStudents(
                AttributesDeletionQuery.builder()
                        .withCourseId(instructor1OfCourse1.courseId)
                        .build());
    }

    /**
     * Verify if {@code student} exists in the {@code panelList}.
     */
    private void verifyStudentEnrollmentStatus(StudentAttributes student, List<EnrollResultPanel> panelList) {
        boolean result = false;

        StudentUpdateStatus status = student.updateStatus;
        for (StudentAttributes s : panelList.get(status.numericRepresentation).getStudentList()) {
            if (s.isEnrollInfoSameAs(student)) {
                result = true;
                break;
            }
        }

        assertTrue(result);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, "",
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
}
