package teammates.test.cases.ui.pagedata;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCourseJoinConfirmationPageData;

public class InstructorCourseJoinConfirmationPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testAll() {
        ______TS("test typical case");
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        String regkey = "someRandomKey";
        String institute = "Institute Name";
        
        InstructorCourseJoinConfirmationPageData pageData = new InstructorCourseJoinConfirmationPageData(account, regkey, institute);
        
        assertNotNull(pageData.getRegkey());
        assertEquals(regkey, pageData.getRegkey());
        
        assertNotNull(pageData.getInstitute());
        assertEquals(institute, pageData.getInstitute());
        
        assertNotNull(pageData.getConfirmationLink());
        String confirmationLink = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED + "?key=" + regkey 
                                  + "&" + Const.ParamsNames.INSTRUCTOR_INSTITUTION + "=" 
                                  + Sanitizer.sanitizeForUri(institute);
        assertEquals(confirmationLink, pageData.getConfirmationLink());
        
        ______TS("test case when institute is null");
        account = dataBundle.accounts.get("instructor1OfCourse1");
        regkey = "someRandomKey";
        institute = null;
        
        pageData = new InstructorCourseJoinConfirmationPageData(account, regkey, institute);
        
        assertNotNull(pageData.getRegkey());
        assertEquals(regkey, pageData.getRegkey());
        
        assertNull(pageData.getInstitute());
        
        assertNotNull(pageData.getConfirmationLink());
        confirmationLink = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED + "?key=" + regkey;
        assertEquals(confirmationLink, pageData.getConfirmationLink());
    }
}
