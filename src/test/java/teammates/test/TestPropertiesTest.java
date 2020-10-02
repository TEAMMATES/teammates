package teammates.test;

import org.testng.annotations.Test;

/**
 * SUT: {@link TestProperties}.
 */
public class TestPropertiesTest extends BaseTestCase {

    @Test
    public void testUpdateSnapshotFlag() {
        assertFalse(TestProperties.IS_SNAPSHOT_UPDATE);
    }

}
