package teammates.logic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import teammates.common.util.StringHelperExtension;
import teammates.test.BaseTestCase;

/**
 * Tests for {@link DemoCourseGenerator}.
 */
public class DemoCourseGeneratorTest extends BaseTestCase {

    @Test
    public void getDemoCourseIdRoot_typicalEmail_returnsCourseIdRoot() {
        assertEquals("john.exa-demo-", DemoCourseGenerator.getDemoCourseIdRoot("john@example.com"));
    }

    @Test
    public void getDemoCourseIdRoot_hostShorterThanThreeChars_usesFullHost() {
        assertEquals("john.ab-demo-", DemoCourseGenerator.getDemoCourseIdRoot("john@ab.com"));
    }

    @Test
    public void getDemoCourseIdRoot_usernameWithIllegalChars_replacesWithUnderscore() {
        assertEquals("john_doe.exa-demo-", DemoCourseGenerator.getDemoCourseIdRoot("john+doe@example.com"));
    }

    @Test
    public void generateDemoCourseIdWithRandomSuffix_email_returnsIdEndingWithSixLowercaseAlphanumericChars() {
        String root = DemoCourseGenerator.getDemoCourseIdRoot("lebron@gmail.com");
        String result = DemoCourseGenerator.generateDemoCourseIdWithRandomSuffix("lebron@gmail.com", 40);

        assertTrue(result.startsWith(root));
        String suffix = result.substring(root.length());
        assertEquals(DemoCourseGenerator.RANDOM_SUFFIX_LENGTH, suffix.length());
        assertTrue(suffix.matches("[0-9a-z]+"));
    }

    @Test
    public void generateDemoCourseIdWithRandomSuffix_calledTwice_returnsDifferentIds() {
        String first = DemoCourseGenerator.generateDemoCourseIdWithRandomSuffix("lebron@gmail.com", 40);
        String second = DemoCourseGenerator.generateDemoCourseIdWithRandomSuffix("lebron@gmail.com", 40);
        assertNotEquals(first, second);
    }

    @Test(dataProvider = "truncateHeadCases")
    public void generateDemoCourseIdWithRandomSuffix_longEmail_truncatesHeadPreservingSuffix(
            String email, int maxLength) {
        String result = DemoCourseGenerator.generateDemoCourseIdWithRandomSuffix(email, maxLength);

        assertEquals(maxLength, result.length());
        String suffix = result.substring(result.length() - DemoCourseGenerator.RANDOM_SUFFIX_LENGTH);
        assertTrue(suffix.matches("[0-9a-z]+"));
    }

    @DataProvider
    public Object[][] truncateHeadCases() {
        return new Object[][] {
                {StringHelperExtension.generateStringOfLength(20) + "@gmail.tmt", 20},
                {StringHelperExtension.generateStringOfLength(50) + "@gmail.tmt", 15},
        };
    }

    @Test
    public void getInstructorAsStudentEmail_instructorEmail_replacesAtWithPlusStudentAt() {
        assertEquals("instructor+student@example.com",
                DemoCourseGenerator.getInstructorAsStudentEmail("instructor@example.com"));
    }
}
