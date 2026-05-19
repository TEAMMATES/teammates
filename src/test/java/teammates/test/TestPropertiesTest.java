package teammates.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

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
