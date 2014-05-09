package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.Assert.fail;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.ui.controller.InstructorStudentRecordsPageAction;
import teammates.ui.controller.InstructorStudentRecordsPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorStudentRecordsPageActionTest extends BaseActionTest {

    DataBundle dataBundle;

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }

    @Test
    public void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email 
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");
        String instructorId = instructor.googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Invalid parameters");
        
        //no params
        verifyAssumptionFailure();
        
        //null courseId
        String[] invalidParams = new String[]{
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };
        
        verifyAssumptionFailure(invalidParams);
        
        //null student email
        invalidParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor.courseId
        };
        
        verifyAssumptionFailure(invalidParams);
        
        // student not in course
        String studentEmailOfStudent1InCourse2 = dataBundle.students.get("student1InCourse2").email;
        invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, studentEmailOfStudent1InCourse2
        };
        
        verifyAssumptionFailure(invalidParams);
        

        ______TS("Typical case, student records view");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email
        };

        InstructorStudentRecordsPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS
                + "?error=false&user=idOfInstructor3",
                r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorStudentRecordsPageData pageData = (InstructorStudentRecordsPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(instructor.courseId, pageData.courseId);
        assertEquals(1, pageData.comments.size());
        assertEquals(7, pageData.sessions.size());

        String expectedLogMessage = "TEAMMATESLOG|||instructorStudentRecordsPage|||instructorStudentRecordsPage"+
                "|||true|||Instructor|||Instructor 3 of Course 1 and 2|||idOfInstructor3"+
                "|||instr3@course1n2.com|||instructorStudentRecords Page Load<br>" +
                "Viewing <span class=\"bold\">" + student.email + "'s</span> records " +
                "for Course <span class=\"bold\">[" + instructor.courseId + "]</span><br>" +
                "Number of sessions: 7" +
                "|||/page/instructorStudentRecordsPage";
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        
        ______TS("Typical case, student records view: no records");
        
        String instructor4Id = dataBundle.instructors.get("instructor4").googleId;
        gaeSimulation.loginAsInstructor(instructor4Id);  // re-login as another instructor for new test
        String courseIdWithNoSession = "idOfCourseNoEvals";
        try {
            createStudentInTypicalDataBundleForCourseWithNoSession();
        } catch(Exception e) {
            fail("Unexpected exception during test");
        }
        
        String[] submissionParamsWithNoSession = new String[] {
                Const.ParamsNames.COURSE_ID, courseIdWithNoSession,
                Const.ParamsNames.STUDENT_EMAIL, "emailTemp@gmail.com"
        };

        InstructorStudentRecordsPageAction aWithNoSession = getAction(submissionParamsWithNoSession);
        ShowPageResult rWithNoSession = getShowPageResult(aWithNoSession);
        assertEquals("No records were found for this student", rWithNoSession.getStatusMessage());
    }
    
    private void createStudentInTypicalDataBundleForCourseWithNoSession() throws EntityAlreadyExistsException, 
    InvalidParametersException {
        Logic logic = new Logic();
        StudentAttributes student = new StudentAttributes("team", "nameOfStudent", "emailTemp@gmail.com", "No comment", "idOfCourseNoEvals");
        logic.createStudent(student);
    }
    
    private InstructorStudentRecordsPageAction getAction(String... params) throws Exception {
        return (InstructorStudentRecordsPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
