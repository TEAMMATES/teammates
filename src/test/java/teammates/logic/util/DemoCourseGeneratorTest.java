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
    public void generateDemoCourseIdCandidate_attempt0_returnsBaseId() {
        assertEquals("lebron.gma-demo",
                DemoCourseGenerator.generateDemoCourseIdCandidate("lebron@gmail.com", 0, 40));
    }

    @Test
    public void generateDemoCourseIdCandidate_attempt1_appendsZero() {
        assertEquals("lebron.gma-demo0",
                DemoCourseGenerator.generateDemoCourseIdCandidate("lebron@gmail.com", 1, 40));
    }

    @Test(dataProvider = "subsequentAttemptCases")
    public void generateDemoCourseIdCandidate_subsequentAttempt_appendsAttemptMinusOne(
            int attempt, String expected) {
        assertEquals(expected,
                DemoCourseGenerator.generateDemoCourseIdCandidate("lebron@gmail.com", attempt, 40));
    }

    @DataProvider
    public Object[][] subsequentAttemptCases() {
        return new Object[][] {
                {2, "lebron.gma-demo1"},
                {10, "lebron.gma-demo9"},
                {11, "lebron.gma-demo10"},
        };
    }

    @Test(dataProvider = "truncateHeadCases")
    public void generateDemoCourseIdCandidate_resultExceedsMaxLength_truncatesHead(
            String email, int attempt, int maxLength, String expected) {
        assertEquals(expected,
                DemoCourseGenerator.generateDemoCourseIdCandidate(email, attempt, maxLength));
    }

    @DataProvider
    public Object[][] truncateHeadCases() {
        int maxLength = 20;
        String suffix = ".gma-demo"; // 9 chars
        String atEmail = "@gmail.tmt";
        // With maxLength=20 and suffix=9, the username can be at most 11 chars before truncation.
        String exactFit = StringHelperExtension.generateStringOfLength(maxLength - suffix.length());
        String oneLonger = StringHelperExtension.generateStringOfLength(maxLength - suffix.length() + 1);
        return new Object[][] {
                // base ID exactly fits — no truncation
                {exactFit + atEmail, 0, maxLength, exactFit + suffix},
                // base ID one char too long — head is truncated by one
                {oneLonger + atEmail, 0, maxLength, oneLonger.substring(1) + suffix},
                // attempt 11 (suffix "10") pushes a near-max ID over the limit
                {exactFit.substring(1) + atEmail, 11, maxLength,
                        exactFit.substring(2) + suffix + "10"},
        };
    }

    @Test
    public void getInstructorAsStudentEmail_instructorEmail_replacesAtWithPlusStudentAt() {
        assertEquals("instructor+student@example.com",
                DemoCourseGenerator.getInstructorAsStudentEmail("instructor@example.com"));
    }
}
