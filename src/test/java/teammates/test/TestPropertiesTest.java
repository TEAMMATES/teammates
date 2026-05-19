package teammates.test;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

/**
 * SUT: {@link TestProperties}.
 */
public class TestPropertiesTest extends BaseTestCase {

    @Test
    public void testUpdateSnapshotFlag() {
        Assertions.assertFalse(TestProperties.IS_SNAPSHOT_UPDATE);
    }

}
