package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.EmailTemplates;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.AssertHelper;

public class BuildPropertiesTest extends BaseTestCase {

    @Test
    public void checkPresence() {
        assertTrue(null != Config.APP_URL);
        assertTrue(Config.PERSISTENCE_CHECK_DURATION >= 0);
    }

    @Test
    public void checkReadingEmailTemplates() {
    
        AssertHelper.assertContainsRegex("${userName}{*}${joinFragment}",
                EmailTemplates.USER_COURSE_JOIN);
        
        AssertHelper.assertContainsRegex("${joinUrl}",
                EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN);
    }

}
