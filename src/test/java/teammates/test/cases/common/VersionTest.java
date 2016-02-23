package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import teammates.common.util.Version;
import teammates.test.cases.BaseTestCase;

public class VersionTest extends BaseTestCase {
    @Test
    public void testVersionConversion() {
        Version version = new Version("15.09");
        assertEquals("15.09", version.toString());
        assertEquals("15-09", version.toStringForQuery());
        
        version = new Version("100-09-01");
        assertEquals("100.09.01", version.toString());
        assertEquals("100-09-01", version.toStringForQuery());
        
        version = new Version("15.09.01.1");
        assertEquals("15.09.01.1", version.toString());
        assertEquals("15-09-01-1", version.toStringForQuery());
    }
    
    @Test
    public void testVersionComparison() {
        Version version1 = new Version("15.09");
        Version version2 = new Version("15.09");
        assertTrue(version1.compareTo(version2) == 0);
        
        version1 = new Version("15.09");
        version2 = new Version("1.09");
        System.out.println(version1.compareTo(version2));
        assertTrue(version1.compareTo(version2) < 0);
        assertTrue(version2.compareTo(version1) > 0);
        
        version1 = new Version("15.09");
        version2 = new Version("15.09.01");
        assertTrue(version1.compareTo(version2) > 0);
    }
}
