package teammates.logic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.Test;

import teammates.common.util.StringHelperExtension;
import teammates.test.BaseTestCase;

/**
 * Tests for {@link DemoCourseGenerator}.
 */
public class DemoCourseGeneratorTest extends BaseTestCase {

    @Test
    public void generateDemoCourseIdWithRandomSuffix_email_returnsIdEndingWithSixBase62Chars() {
        String base = DemoCourseGenerator.getDemoCourseIdRoot("lebron@gmail.com");
        String result = DemoCourseGenerator.generateDemoCourseIdWithRandomSuffix("lebron@gmail.com", 40);

        assertTrue(result.startsWith(base));
        String suffix = result.substring(base.length());
        assertEquals(DemoCourseGenerator.RANDOM_SUFFIX_LENGTH, suffix.length());
        assertTrue(suffix.matches("[0-9a-z]+"));
    }

    @Test
    public void generateDemoCourseIdWithRandomSuffix_longEmail_truncatesHeadPreservingSuffix() {
        int maxLength = 20;
        String longEmail = StringHelperExtension.generateStringOfLength(20) + "@gmail.tmt";
        String result = DemoCourseGenerator.generateDemoCourseIdWithRandomSuffix(longEmail, maxLength);

        assertEquals(maxLength, result.length());
        String suffix = result.substring(result.length() - DemoCourseGenerator.RANDOM_SUFFIX_LENGTH);
        assertTrue(suffix.matches("[0-9a-z]+"));
    }

    @Test
    public void getInstructorAsStudentEmail_instructorEmail_replacesAtWithPlusStudentAt() {
        assertEquals("instructor+student@example.com",
                DemoCourseGenerator.getInstructorAsStudentEmail("instructor@example.com"));
    }
}
