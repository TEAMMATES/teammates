package teammates.logic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import teammates.common.util.StringHelperExtension;
import teammates.test.BaseTestCase;

/**
 * Tests for {@link DemoCourseGenerator}.
 */
public class DemoCourseGeneratorTest extends BaseTestCase {

    @Test
    public void generateNextDemoCourseId_emailInput_returnsBaseId() {
        assertEquals("lebron.gma-demo", DemoCourseGenerator.generateNextDemoCourseId("lebron@gmail.com", 40));
    }

    @Test
    public void generateNextDemoCourseId_baseIdEndingInDemo_appendsZero() {
        assertEquals("lebron.gma-demo0", DemoCourseGenerator.generateNextDemoCourseId("lebron.gma-demo", 40));
    }

    @Test(dataProvider = "incrementSuffixCases")
    public void generateNextDemoCourseId_idWithNumericSuffix_incrementsSuffix(
            String input, String expected) {
        assertEquals(expected, DemoCourseGenerator.generateNextDemoCourseId(input, 40));
    }

    @DataProvider
    public Object[][] incrementSuffixCases() {
        return new Object[][] {
                {"lebron.gma-demo0", "lebron.gma-demo1"},
                {"lebron.gma-demo9", "lebron.gma-demo10"},
                {"lebron.gma-demo99", "lebron.gma-demo100"},
        };
    }

    @Test(dataProvider = "truncateHeadCases")
    public void generateNextDemoCourseId_resultExceedsMaxLength_truncatesHead(
            String input, int maxLength, String expected) {
        assertEquals(expected, DemoCourseGenerator.generateNextDemoCourseId(input, maxLength));
    }

    @DataProvider
    public Object[][] truncateHeadCases() {
        int maxLength = 20;
        String suffix = ".gma-demo"; // length 9
        String atEmail = "@gmail.tmt";
        // maxLength - suffix.length() = 11 chars available for username prefix
        String exactFit = StringHelperExtension.generateStringOfLength(maxLength - suffix.length());
        String oneLonger = StringHelperExtension.generateStringOfLength(maxLength - suffix.length() + 1);
        return new Object[][] {
                // email whose base ID exactly fits
                {exactFit + atEmail, maxLength, exactFit + suffix},
                // email whose base ID is one char too long — head is truncated
                {oneLonger + atEmail, maxLength, oneLonger.substring(1) + suffix},
                // existing ID with two-digit suffix that would exceed max
                {exactFit.substring(1) + suffix + "9", maxLength,
                        exactFit.substring(2) + suffix + "10"},
        };
    }

    @Test
    public void getInstructorAsStudentEmail_instructorEmail_replacesAtWithPlusStudentAt() {
        assertEquals("instructor+student@example.com",
                DemoCourseGenerator.getInstructorAsStudentEmail("instructor@example.com"));
    }
}
