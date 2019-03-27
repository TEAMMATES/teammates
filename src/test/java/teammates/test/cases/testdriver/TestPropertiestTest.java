package teammates.test.cases.testdriver;

import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.TestProperties;

/**
 * SUT: {@link TestProperties}.
 */
public class TestPropertiestTest extends BaseTestCase {

    @Test
    public void testUpdateSnapshotFlag() {
        assertFalse(TestProperties.IS_SNAPSHOT_UPDATE);
    }

}
