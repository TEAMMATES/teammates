package teammates.test.cases.ui.pagedata;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;
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
        InstructorCourseJoinConfirmationPageData pageData = new InstructorCourseJoinConfirmationPageData(account);
        String regkey = "someRandomKey";
        String institute = "Institute Name";
        pageData.init(regkey, institute);
        
        assertNotNull(pageData.getRegkey());
        assertEquals(regkey, pageData.getRegkey());
        
        assertNotNull(pageData.getInstitue());
        assertEquals(institute, pageData.getInstitue());
        
        assertNotNull(pageData.getConfirmationLink());
        String confirmationLink = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED + "?key=" + regkey 
                                  + "&" + Const.ParamsNames.INSTRUCTOR_INSTITUTION + "=" 
                                  + Sanitizer.sanitizeForUri(institute);
        assertEquals(confirmationLink, pageData.getConfirmationLink());
    }
}
