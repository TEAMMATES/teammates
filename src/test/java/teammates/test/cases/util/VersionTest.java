package teammates.test.cases.util;

import org.testng.annotations.Test;

import teammates.common.util.Version;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link Version}.
 */
public class VersionTest extends BaseTestCase {
    @Test
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP") // A version with 4 numbers is not an IP address
    public void testVersionConversion() {
        ______TS("Test versions with 2 numbers");
        Version version = new Version("15.09");
        assertEquals("15.09", version.toString());
        assertEquals("15-09", version.toStringWithDashes());

        ______TS("Test versions with 3 numbers");
        version = new Version("100-09-01");
        assertEquals("100.09.01", version.toString());
        assertEquals("100-09-01", version.toStringWithDashes());

        ______TS("Test versions with 4 numbers");
        version = new Version("15.09.01.1");
        assertEquals("15.09.01.1", version.toString());
        assertEquals("15-09-01-1", version.toStringWithDashes());

        ______TS("Test versions with rc");
        version = new Version("15.09rc");
        assertEquals("15.09rc", version.toString());
        assertEquals("15-09rc", version.toStringWithDashes());
    }

    @Test
    public void testVersionComparison() {
        Version version1 = new Version("15.09");
        Version version2 = new Version("15.09");
        assertEquals(version1.compareTo(version2), 0);

        version1 = new Version("15.09");
        version2 = new Version("1.09");
        assertTrue(version1.compareTo(version2) < 0);
        assertTrue(version2.compareTo(version1) > 0);

        version1 = new Version("15.09");
        version2 = new Version("15.09.01");
        assertTrue(version1.compareTo(version2) > 0);

        ______TS("Test rc versions will come first when the rest are similar");
        version1 = new Version("15.09");
        version2 = new Version("15.09rc");
        assertTrue(version2.compareTo(version1) < 0); // 15.09rc < 15.09

    }
}
