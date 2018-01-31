package teammates.test.cases.pagedata;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.AdminHomePageData;

/**
 * SUT: {@link AdminHomePageData}.
 */
public class AdminHomePageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    private AdminHomePageData pageData;

    @Test
    public void allTests() {
        createData();
        setHomePageAttributes();
        testHomePageAttributes();
    }

    private void createData() {
        AccountAttributes admin = dataBundle.accounts.get("instructor1OfCourse1");
        pageData = new AdminHomePageData(admin, dummySessionToken);
    }

    private void setHomePageAttributes() {
        pageData.instructorName = "Instructor1";
        pageData.instructorEmail = "instructor1@email.tmt";
        pageData.instructorInstitution = "Teammates";
    }

    private void testHomePageAttributes() {
        assertEquals("Instructor1", pageData.getInstructorName());
        assertEquals("instructor1@email.tmt", pageData.getInstructorEmail());
        assertEquals("Teammates", pageData.getInstructorInstitution());
    }
}
