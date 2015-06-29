package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEnrollPageData;
import teammates.ui.controller.InstructorCourseEnrollResultPageData;
import teammates.ui.controller.InstructorCourseEnrollSaveAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.template.EnrollResultPanel;

public class InstructorCourseEnrollSaveActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_SAVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String enrollString = "";
        String[] submissionParams = new String[]{};
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Typical case: add and edit students for non-empty course");        
        
        enrollString = "Section | Team | Name | Email | Comment" + Const.EOL;
        // A new student
        enrollString += "Section 3 \t Team 1\tJean Wong\tjean@email.tmt\tExchange student" + Const.EOL;
        // A student to be modified
        enrollString += "Section 2 \t Team 1.3\tstudent1 In Course1\tstudent1InCourse1@gmail.tmt\tNew comment added" + Const.EOL;
        // An existing student with no modification
        enrollString += "Section 1 \t Team 1.1\tstudent2 In Course1\tstudent2InCourse1@gmail.tmt\t";
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        InstructorCourseEnrollSaveAction enrollAction = getAction(submissionParams);
        
        ShowPageResult pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL_RESULT + "?error=false&user=idOfInstructor1OfCourse1", 
                     pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        InstructorCourseEnrollResultPageData pageData = (InstructorCourseEnrollResultPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        
        StudentAttributes newStudent = new StudentAttributes("jean", "jean@email.tmt", "Jean Wong", "Exchange student", courseId, "Team 1", "Section 3");
        newStudent.updateStatus = StudentAttributes.UpdateStatus.NEW;
        verifyStudentEnrollmentStatus(newStudent, pageData.getEnrollResultPanelList());
        
        StudentAttributes modifiedStudent = dataBundle.students.get("student1InCourse1");
        modifiedStudent.comments = "New comment added";
        modifiedStudent.section  = "Section 2";
        modifiedStudent.team = "Team 1.3";
        modifiedStudent.updateStatus = StudentAttributes.UpdateStatus.MODIFIED;
        verifyStudentEnrollmentStatus(modifiedStudent, pageData.getEnrollResultPanelList());
        
        StudentAttributes unmodifiedStudent = dataBundle.students.get("student2InCourse1");
        unmodifiedStudent.updateStatus = StudentAttributes.UpdateStatus.UNMODIFIED;
        verifyStudentEnrollmentStatus(unmodifiedStudent, pageData.getEnrollResultPanelList());
        
        String expectedLogSegment = "Students Enrolled in Course <span class=\"bold\">[" + courseId + "]"
                                    + ":</span><br>" + enrollString.replace("\n", "<br>"); 
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());
        
        ______TS("Masquerade mode, enrollment into empty course");
        
        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }
                    
        courseId = "new-course";
        CoursesLogic.inst().createCourseAndInstructor(instructorId, courseId, "New course");
        
        gaeSimulation.loginAsAdmin("admin.user");
        
        String headerRow = "Name\tEmail\tTeam\tComment";
        String studentsInfo = "Jean Wong\tjean@email.tmt\tTeam 1\tExchange student"
                              + Const.EOL + "James Tan\tjames@email.tmt\tTeam 2\t";
        enrollString = headerRow + Const.EOL +  studentsInfo;
        
        submissionParams = new String[]{
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);
        
        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL_RESULT + "?error=false&user=idOfInstructor1OfCourse1",
                     pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        pageData = (InstructorCourseEnrollResultPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());

        StudentAttributes student1 = new StudentAttributes("jean", "jean@email.tmt", "Jean Wong", 
                                                           "Exchange student", courseId, "Team 1","None");
        student1.updateStatus = StudentAttributes.UpdateStatus.NEW;
        verifyStudentEnrollmentStatus(student1, pageData.getEnrollResultPanelList());
        
        StudentAttributes student2 = new StudentAttributes("james", "james@email.tmt", "James Tan", "", 
                                                           courseId, "Team 2","None");
        student2.updateStatus = StudentAttributes.UpdateStatus.NEW;
        verifyStudentEnrollmentStatus(student2, pageData.getEnrollResultPanelList());
        
        expectedLogSegment = "Students Enrolled in Course <span class=\"bold\">[" + courseId + "]:</span>"
                             + "<br>" + enrollString.replace("\n", "<br>"); 
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());
        
        ______TS("Failure case: enrollment failed due to invalid lines");
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        String studentWithoutEnoughParam = "Team 1\tStudentWithNoEmailInput";
        String studentWithInvalidEmail = "Team 2\tBenjamin Tan\tinvalid.email.tmt";
        enrollString = "Team | Name | Email" + Const.EOL;
        enrollString += studentWithoutEnoughParam + Const.EOL + studentWithInvalidEmail;
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);
        
        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageResult.destination);
        assertEquals(true, pageResult.isError);
        String expectedStatusMessage = "<p>"
                                            + "<span class=\"bold\">Problem in line : "
                                                + "<span class=\"invalidLine\">"
                                                    + studentWithoutEnoughParam 
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
                                                    + studentWithInvalidEmail 
                                                + "</span>"
                                            + "</span>"
                                            + "<br>"
                                            + "<span class=\"problemDetail\">&bull; "
                                                + "\"invalid.email.tmt\" is not acceptable to TEAMMATES as "
                                                + "an email because it is not in the correct format. An "
                                                + "email address contains some text followed by one '@' sign"
                                                + " followed by some more text. It cannot be longer than 45 "
                                                + "characters. It cannot be empty and it cannot have spaces."
                                            + "</span>"
                                        + "</p>";
        assertEquals(expectedStatusMessage, pageResult.getStatusMessage());
        
        InstructorCourseEnrollPageData enrollPageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, enrollPageData.getCourseId());
        assertEquals(enrollString, enrollPageData.getEnrollStudents());
        
        expectedLogSegment = expectedStatusMessage + "<br>Enrollment string entered by user:<br>" + (enrollString).replace("\n", "<br>");
        AssertHelper.assertContains(expectedLogSegment, enrollAction.getLogMessage());
        
        ______TS("Boundary test for size limit per enrollment");
        
        //TODO: sync this var with SIZE_LIMIT_PER_ENROLLMENT defined in StudentsLogic, by putting it in config or Const class
        int sizeLimitBoundary = 150;
        
        //can enroll, if within the size limit
        StringBuilder enrollStringBuilder = new StringBuilder("Section\tTeam\tName\tEmail");
        for(int i = 0; i < sizeLimitBoundary; i++) {
            enrollStringBuilder.append(Const.EOL).append("section" + i + "\tteam" + i + "\tname" + i 
                                                         + "\temail" + i + "@nonexistemail.nonexist");
        }
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollStringBuilder.toString()
        };
        enrollAction = getAction(submissionParams);
        pageResult = getShowPageResult(enrollAction);
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        //fail to enroll, if exceed the range
        enrollStringBuilder.append(Const.EOL).append("section" + sizeLimitBoundary + "\tteam" + sizeLimitBoundary 
                                                     + "\tname" + sizeLimitBoundary + "\temail" + sizeLimitBoundary 
                                                     + "@nonexistemail.nonexist");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollStringBuilder.toString()
        };
        enrollAction = getAction(submissionParams);
        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageResult.destination);
        assertEquals(true, pageResult.isError);
        assertEquals(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED, pageResult.getStatusMessage());
        
        ______TS("Failure case: empty input");

        enrollString = "";
    
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, enrollString
        };
        enrollAction = getAction(submissionParams);
        
        pageResult = getShowPageResult(enrollAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL +"?error=true&user=idOfInstructor1OfCourse1", 
                     pageResult.getDestinationWithParams());
        assertEquals(true, pageResult.isError);
        assertEquals(Const.StatusMessages.ENROLL_LINE_EMPTY, pageResult.getStatusMessage());
        
        enrollPageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, enrollPageData.getCourseId());
        assertEquals(enrollString, enrollPageData.getEnrollStudents());
        
        AssertHelper.assertContains(Const.StatusMessages.ENROLL_LINE_EMPTY, enrollAction.getLogMessage());
            
        CoursesLogic.inst().deleteCourseCascade("new-course");
        StudentsLogic.inst().deleteStudentsForCourseWithoutDocument(instructor1OfCourse1.courseId);
    }
    
    /**
     * Verify if <code>student exists in the <code>studentsAfterEnrollment
     */
    private void verifyStudentEnrollmentStatus(StudentAttributes student, List<EnrollResultPanel> panelList) {
        boolean result = false;
        
        StudentAttributes.UpdateStatus status = student.updateStatus;
        for (StudentAttributes s : panelList.get(status.numericRepresentation).getStudentList()) {
            if (s.isEnrollInfoSameAs(student)) {
                result = true;
                break;
            }
        }
        
        assertEquals(true, result);
    }
    
    private InstructorCourseEnrollSaveAction getAction(String... params) throws Exception {
        return (InstructorCourseEnrollSaveAction) (gaeSimulation.getActionObject(uri, params));
    }

}
