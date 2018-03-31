package teammates.test.cases.action;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEnrollSaveAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorCourseEnrollPageData;
import teammates.ui.pagedata.InstructorCourseEnrollResultPageData;
import teammates.ui.template.EnrollResultPanel;

/**
 * SUT: {@link InstructorCourseEnrollSaveAction}.
 */
public class InstructorCourseEnrollSaveActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_SAVE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String enrollString = "";

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;

        gaeSimulation.loginAsInstructor(instructorId);

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
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        InstructorCourseEnrollSaveAction enrollAction = getAction(submissionParams);

        ShowPageResult pageResult = getShowPageResult(enrollAction);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL_RESULT, false, "idOfInstructor1OfCourse1"),
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        // there are 6 sessions in this course
        verifySpecifiedTasksAdded(enrollAction, Const.TaskQueue.FEEDBACK_RESPONSE_ADJUSTMENT_QUEUE_NAME, 6);

        List<TaskWrapper> tasksAdded = enrollAction.getTaskQueuer().getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            Map<String, String[]> paramMap = task.getParamMap();
            assertEquals(courseId, paramMap.get(ParamsNames.COURSE_ID)[0]);
        }

        InstructorCourseEnrollResultPageData pageData = (InstructorCourseEnrollResultPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());

        StudentAttributes newStudent = StudentAttributes
                .builder(courseId, "Jean Wong", "jean@email.tmt")
                .withSection("Section 3")
                .withTeam("Team 1")
                .withComments("Exchange student")
                .withGoogleId("jean")
                .build();
        newStudent.updateStatus = StudentUpdateStatus.NEW;
        verifyStudentEnrollmentStatus(newStudent, pageData.getEnrollResultPanelList());

        StudentAttributes newStudentWithExtraSpaces = StudentAttributes
                .builder(courseId, "student with extra spaces", "studentWithExtraSpaces@gmail.tmt")
                .withSection("Section 3")
                .withTeam("Team 1")
                .withComments("")
                .withGoogleId("student")
                .build();
        newStudentWithExtraSpaces.updateStatus = StudentUpdateStatus.NEW;
        verifyStudentEnrollmentStatus(newStudentWithExtraSpaces, pageData.getEnrollResultPanelList());

        StudentAttributes modifiedStudent = typicalBundle.students.get("student1InCourse1");
        modifiedStudent.comments = "New comment added";
        modifiedStudent.section = "Section 2";
        modifiedStudent.team = "Team 1.3";
        modifiedStudent.updateStatus = StudentUpdateStatus.MODIFIED;
        verifyStudentEnrollmentStatus(modifiedStudent, pageData.getEnrollResultPanelList());

        StudentAttributes unmodifiedStudent = typicalBundle.students.get("student2InCourse1");
        unmodifiedStudent.updateStatus = StudentUpdateStatus.UNMODIFIED;
        verifyStudentEnrollmentStatus(unmodifiedStudent, pageData.getEnrollResultPanelList());

        StudentAttributes unmodifiedStudentWithExtraSpaces = typicalBundle.students.get("student3InCourse1");
        unmodifiedStudentWithExtraSpaces.updateStatus = StudentUpdateStatus.UNMODIFIED;
        verifyStudentEnrollmentStatus(unmodifiedStudentWithExtraSpaces, pageData.getEnrollResultPanelList());

        String expectedLogSegment = "Students Enrolled in Course <span class=\"bold\">[" + courseId + "]"
                                    + ":</span><br>"
                                    + SanitizationHelper.sanitizeForHtml(enrollString).replace("\n", "<br>");
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());

        ______TS("Masquerade mode, enrollment into empty course");

        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }

        courseId = "new-course";
        CoursesLogic.inst().createCourseAndInstructor(instructorId, courseId, "New course", "UTC");

        gaeSimulation.loginAsAdmin("admin.user");

        String headerRow = "Name\tEmail\tTeam\tComment";
        String studentsInfo = "Jean Wong\tjean@email.tmt\tTeam 1\tExchange student"
                              + System.lineSeparator() + "James Tan\tjames@email.tmt\tTeam 2\t";
        enrollString = headerRow + System.lineSeparator() + studentsInfo;

        submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);

        pageResult = getShowPageResult(enrollAction);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL_RESULT, false, "idOfInstructor1OfCourse1"),
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        verifyNoTasksAdded(enrollAction);

        pageData = (InstructorCourseEnrollResultPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());

        StudentAttributes student1 = StudentAttributes
                .builder(courseId, "Jean Wong", "jean@email.tmt")
                .withSection("None")
                .withTeam("Team 1")
                .withComments("Exchange student")
                .withGoogleId("jean")
                .build();
        student1.updateStatus = StudentUpdateStatus.NEW;
        verifyStudentEnrollmentStatus(student1, pageData.getEnrollResultPanelList());

        StudentAttributes student2 = StudentAttributes
                .builder(courseId, "James Tan", "james@email.tmt")
                .withSection("None")
                .withTeam("Team 2")
                .withComments("")
                .withGoogleId("james")
                .build();
        student2.updateStatus = StudentUpdateStatus.NEW;
        verifyStudentEnrollmentStatus(student2, pageData.getEnrollResultPanelList());

        expectedLogSegment = "Students Enrolled in Course <span class=\"bold\">[" + courseId + "]:</span>"
                             + "<br>" + enrollString.replace("\n", "<br>");
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());

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
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);

        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageResult.destination);
        assertTrue(pageResult.isError);
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
        assertEquals(expectedStatusMessage, pageResult.getStatusMessage());
        verifyNoTasksAdded(enrollAction);

        InstructorCourseEnrollPageData enrollPageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, enrollPageData.getCourseId());
        assertEquals(enrollString, enrollPageData.getEnrollStudents());

        expectedLogSegment = expectedStatusMessage + "<br>Enrollment string entered by user:<br>"
                             + enrollString.replace("\n", "<br>");
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());

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
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollStringBuilder.toString()
        };
        enrollAction = getAction(submissionParams);
        pageResult = getShowPageResult(enrollAction);
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        verifyNoTasksAdded(enrollAction);

        //fail to enroll, if exceed the range
        enrollStringBuilder.append(System.lineSeparator()).append(
                "section" + Const.SIZE_LIMIT_PER_ENROLLMENT + "\tteam" + Const.SIZE_LIMIT_PER_ENROLLMENT
                 + "\tname" + Const.SIZE_LIMIT_PER_ENROLLMENT + "\temail" + Const.SIZE_LIMIT_PER_ENROLLMENT
                 + "@nonexistemail.nonexist");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollStringBuilder.toString()
        };
        enrollAction = getAction(submissionParams);
        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageResult.destination);
        assertTrue(pageResult.isError);
        assertEquals(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED, pageResult.getStatusMessage());
        verifyNoTasksAdded(enrollAction);

        ______TS("Failure case: empty input");

        enrollString = "";

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);

        pageResult = getShowPageResult(enrollAction);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, true, "idOfInstructor1OfCourse1"),
                pageResult.getDestinationWithParams());
        assertTrue(pageResult.isError);
        assertEquals(Const.StatusMessages.ENROLL_LINE_EMPTY, pageResult.getStatusMessage());
        verifyNoTasksAdded(enrollAction);

        enrollPageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, enrollPageData.getCourseId());
        assertEquals(enrollString, enrollPageData.getEnrollStudents());

        AssertHelper.assertContains(Const.StatusMessages.ENROLL_LINE_EMPTY, enrollAction.getLogMessage());

        CoursesLogic.inst().deleteCourseCascade("new-course");
        StudentsLogic.inst().deleteStudentsForCourseWithoutDocument(instructor1OfCourse1.courseId);
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
    protected InstructorCourseEnrollSaveAction getAction(String... params) {
        return (InstructorCourseEnrollSaveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, ""
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }

}
