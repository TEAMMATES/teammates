package teammates.test.cases.ui.pagedata;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

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
        String courseId = "CourseId";
        String enroll = "Section | Team | Name | Email | Comments\n" + 
                        "Tut Group 1 | Team 1 | Tom Jacobs | tom@email.com\n" + 
                        "Tut Group 1 | Team 1 | Jean Wong | jean@email.com | Exchange Student\n" + 
                        "Tut Group 1 | Team 2 | Jack Wayne | jack@email.com\n" + 
                        "Tut Group 2 | Team 3 | Thora Parker | thora@email.com";  
        
        InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account, courseId, enroll);
        
        assertNotNull(pageData.getCourseId());
        assertEquals(courseId, pageData.getCourseId());
        
        assertNotNull(pageData.getEnrollStudents());
        assertEquals(enroll, pageData.getEnrollStudents());
        
        assertNotNull(pageData.account);
        assertEquals(account.googleId, pageData.account.googleId);
    }
}
