package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.AdminHomePageData;

public class AdminHomePageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    private AdminHomePageData pageData;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void allTests() {
        createData();
        setHomePageAttributes();
        testHomePageAttributes();
    }
    
    private void createData() {
        AccountAttributes admin = dataBundle.accounts.get("instructor1OfCourse1");
        pageData = new AdminHomePageData(admin);
    }
    
    private void setHomePageAttributes() {
        pageData.instructorShortName = "Inst1";
        pageData.instructorName = "Instructor1";
        pageData.instructorEmail = "instructor1@email.tmt";
        pageData.instructorInstitution = "Teammates";
    }
    
    private void testHomePageAttributes() {
        assertEquals("Inst1", pageData.getInstructorShortName());
        assertEquals("Instructor1", pageData.getInstructorName());
        assertEquals("instructor1@email.tmt", pageData.getInstructorEmail());
        assertEquals("Teammates", pageData.getInstructorInstitution());
    }
}
