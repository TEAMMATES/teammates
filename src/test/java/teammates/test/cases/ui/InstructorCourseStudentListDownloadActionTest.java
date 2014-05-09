package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;

public class InstructorCourseStudentListDownloadActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, course.id
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
    
    // Testing of the content of file is done in logicTest.testGetCourseStudentListAsCsv
    // FileName is not tested -- trivial test
}
