package teammates.test.cases.pagedata;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.InstructorCourseJoinConfirmationPageData;

/**
 * SUT: {@link InstructorCourseJoinConfirmationPageData}.
 */
public class InstructorCourseJoinConfirmationPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @Test
    public void testAll() {
        ______TS("test typical case");
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        String regkey = "someRandomKey";
        String institute = "Institute Name";

        InstructorCourseJoinConfirmationPageData pageData =
                new InstructorCourseJoinConfirmationPageData(account, dummySessionToken, regkey, institute);

        assertNotNull(pageData.getRegkey());
        assertEquals(regkey, pageData.getRegkey());

        assertNotNull(pageData.getInstitute());
        assertEquals(institute, pageData.getInstitute());

        assertNotNull(pageData.getConfirmationLink());
        String confirmationLink = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED + "?key=" + regkey
                                  + "&" + Const.ParamsNames.INSTRUCTOR_INSTITUTION + "="
                                  + SanitizationHelper.sanitizeForUri(institute);
        assertEquals(confirmationLink, pageData.getConfirmationLink());

        ______TS("test case when institute is null");
        account = dataBundle.accounts.get("instructor1OfCourse1");
        regkey = "someRandomKey";

        pageData = new InstructorCourseJoinConfirmationPageData(account, dummySessionToken, regkey, null);

        assertNotNull(pageData.getRegkey());
        assertEquals(regkey, pageData.getRegkey());

        assertNull(pageData.getInstitute());

        assertNotNull(pageData.getConfirmationLink());
        confirmationLink = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED + "?key=" + regkey;
        assertEquals(confirmationLink, pageData.getConfirmationLink());
    }
}
