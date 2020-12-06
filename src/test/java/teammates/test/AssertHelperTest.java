package teammates.test;

import org.testng.annotations.Test;

/**
 * SUT: {@link AssertHelper}.
 */
public class AssertHelperTest extends BaseTestCase {

    @Test
    public void testAssertContains() {

        AssertHelper.assertContains("404 Page Not Found",
                "Error: 404 Page Not Found. Check the URL.");
    }

}
