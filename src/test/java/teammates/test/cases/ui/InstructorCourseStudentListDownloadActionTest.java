package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.StudentsLogic;
import teammates.ui.controller.FileDownloadResult;
import teammates.ui.controller.InstructorCourseStudentListDownloadAction;

public class InstructorCourseStudentListDownloadActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        String instructorId = dataBundle.instructors.get("instructor1OfCourse1").googleId;
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Invalid params");
        String[] submissionParams = {};
        verifyAssumptionFailure(submissionParams);
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, course.id
        };
        
        ______TS("Typical case: student list downloaded successfully");
        InstructorCourseStudentListDownloadAction a = getAction(submissionParams);
        FileDownloadResult r = (FileDownloadResult)a.executeAndPostProcess();
        
        String expectedFileName = "idOfTypicalCourse1_studentList";
        assertEquals(expectedFileName, r.getFileName());
        // look at LogicTest.testGetCourseStudentListAsCsv. the logic api to generate Csv file content is tested in LogicTest
        String[] fileContentLines = r.getFileContent().split(Const.EOL);
        assertEquals("Course ID," + "\"" + course.id + "\"", fileContentLines[0]);
        assertEquals("Course Name," + "\"" + course.name + "\"", fileContentLines[1]);
        assertEquals("", fileContentLines[2]);
        assertEquals("", fileContentLines[3]);
        assertEquals("Section,Team,Full Name,Last Name,Status,Email", fileContentLines[4]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student1 In Course1\",\"Course1\",\"Joined\",\"student1InCourse1@gmail.tmt\"", fileContentLines[5]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"Joined\",\"student2InCourse1@gmail.tmt\"", fileContentLines[6]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"Joined\",\"student3InCourse1@gmail.tmt\"", fileContentLines[7]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"Joined\",\"student4InCourse1@gmail.tmt\"", fileContentLines[8]);
        assertEquals("\"Section 2\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"Joined\",\"student5InCourse1@gmail.tmt\"", fileContentLines[9]);
        assertEquals("",r.getStatusMessage());
        
        
        ______TS("Typical case: student list downloaded successfully with student last name specified within braces");
        
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        student1InCourse1.name = "new name {new last name}";
        StudentsLogic.inst().updateStudentCascade(student1InCourse1.email, student1InCourse1);
        
        a = getAction(submissionParams);
        r = (FileDownloadResult)a.executeAndPostProcess();
        
        expectedFileName = "idOfTypicalCourse1_studentList";
        assertEquals(expectedFileName, r.getFileName());
        // look at LogicTest.testGetCourseStudentListAsCsv. the logic api to generate Csv file content is tested in LogicTest
        fileContentLines = r.getFileContent().split(Const.EOL);
        assertEquals("Course ID," + "\"" + course.id + "\"", fileContentLines[0]);
        assertEquals("Course Name," + "\"" + course.name + "\"", fileContentLines[1]);
        assertEquals("", fileContentLines[2]);
        assertEquals("", fileContentLines[3]);
        assertEquals("Section,Team,Full Name,Last Name,Status,Email", fileContentLines[4]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"new name new last name\",\"new last name\",\"Joined\",\"student1InCourse1@gmail.tmt\"", fileContentLines[5]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"Joined\",\"student2InCourse1@gmail.tmt\"", fileContentLines[6]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"Joined\",\"student3InCourse1@gmail.tmt\"", fileContentLines[7]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"Joined\",\"student4InCourse1@gmail.tmt\"", fileContentLines[8]);
        assertEquals("\"Section 2\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"Joined\",\"student5InCourse1@gmail.tmt\"", fileContentLines[9]);
        assertEquals("",r.getStatusMessage());
        
        removeAndRestoreTypicalDataInDatastore();
        
        
        ______TS("Typical case: student list downloaded successfully with special team name");
        
        student1InCourse1 = StudentsLogic.inst().getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");
        student1InCourse1.team = "N/A";
        StudentsLogic.inst().updateStudentCascade("student1InCourse1@gmail.tmt", student1InCourse1);
        
        a = getAction(submissionParams);
        r = (FileDownloadResult)a.executeAndPostProcess();
        
        expectedFileName = "idOfTypicalCourse1_studentList";
        assertEquals(expectedFileName, r.getFileName());
        // look at LogicTest.testGetCourseStudentListAsCsv. the logic api to generate Csv file content is tested in LogicTest
        fileContentLines = r.getFileContent().split(Const.EOL);
        assertEquals("Course ID," + "\"" + course.id + "\"", fileContentLines[0]);
        assertEquals("Course Name," + "\"" + course.name + "\"", fileContentLines[1]);
        assertEquals("", fileContentLines[2]);
        assertEquals("", fileContentLines[3]);
        assertEquals("Section,Team,Full Name,Last Name,Status,Email", fileContentLines[4]);
        assertEquals("\"Section 1\",\"N/A\",\"student1 In Course1\",\"Course1\",\"Joined\",\"student1InCourse1@gmail.tmt\"", fileContentLines[5]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student2 In Course1\",\"Course1\",\"Joined\",\"student2InCourse1@gmail.tmt\"", fileContentLines[6]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student3 In Course1\",\"Course1\",\"Joined\",\"student3InCourse1@gmail.tmt\"", fileContentLines[7]);
        assertEquals("\"Section 1\",\"Team 1.1\",\"student4 In Course1\",\"Course1\",\"Joined\",\"student4InCourse1@gmail.tmt\"", fileContentLines[8]);
        assertEquals("\"Section 2\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"Joined\",\"student5InCourse1@gmail.tmt\"", fileContentLines[9]);
        assertEquals("",r.getStatusMessage());
        
    }
    
    private InstructorCourseStudentListDownloadAction getAction(String... params) throws Exception{
            return (InstructorCourseStudentListDownloadAction) (gaeSimulation.getActionObject(uri, params));
    }
}
