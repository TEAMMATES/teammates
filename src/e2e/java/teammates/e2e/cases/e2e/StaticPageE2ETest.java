package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.e2e.util.TestProperties;

/**
 * Placeholder test to check if Selenium works.
 */
public class StaticPageE2ETest extends BaseE2ETestCase {

    @Test
    public void placeholderTest() {
        browser.driver.get(TestProperties.TEAMMATES_URL);
        browser.driver.get(TestProperties.TEAMMATES_URL + "/web/front/home");
    }

    @Override
    protected void prepareTestData() throws Exception {
        // nothing to do
    }

}
