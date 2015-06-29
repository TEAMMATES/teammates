package teammates.test.cases.ui.pagedata;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCourseEnrollPageData;

public class InstructorCourseEnrollPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testAll() {
        ______TS("test typical case");
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account);
        String courseId = "CourseId";
        String enroll = "Section | Team | Name | Email | Comments\n" + 
                        "Tut Group 1 | Team 1 | Tom Jacobs | tom@email.com" + 
                        "Tut Group 1 | Team 1 | Jean Wong | jean@email.com | Exchange Student" + 
                        "Tut Group 1 | Team 2 | Jack Wayne | jack@email.com" + 
                        "Tut Group 2 | Team 3 | Thora Parker | thora@email.com";  
        pageData.init(courseId, enroll);
        
        assertNotNull(pageData.getCourseId());
        assertNotNull(pageData.getEnrollStudents());
        assertNotNull(pageData.account);
    }
}
