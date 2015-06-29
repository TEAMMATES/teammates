package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.ui.controller.InstructorCourseStudentDetailsPageData;

public class InstructorCourseStudentDetailsEditPageDataTest extends InstructorCourseStudentDetailsPageDataTest {
    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
    }
    
    @Test
    public void allTests() {
        InstructorCourseStudentDetailsPageData data = createData();
        
        ______TS("With no student profile (Details edit shows only the info table)");
        assertNull(data.getStudentProfile());
        testStudentInfoTable(data.getStudentInfoTable());
    }
    
    protected InstructorCourseStudentDetailsPageData createData() {
        String name = "John Doe";
        String email = "john@doe.com";
        
        createStudent(name, email);
        return super.createData();
    }
}