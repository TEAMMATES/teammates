package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.Gender;
import teammates.test.cases.BaseTestCaseWithMinimalGaeEnvironment;


/**
 * SUT: {@link Gender}.
 */
public class GenderTest extends BaseTestCaseWithMinimalGaeEnvironment {

    @Test
    public void testGetGenderEnumValue() {
        // invalid values
        assertEquals(Gender.OTHER, Gender.getGenderEnumValue("'\"'invalidGender"));
        assertEquals(Gender.OTHER, Gender.getGenderEnumValue("invalidGender"));

        // valid values
        assertEquals(Gender.MALE, Gender.getGenderEnumValue("MALE"));
        assertEquals(Gender.FEMALE, Gender.getGenderEnumValue("FEMALE"));
        assertEquals(Gender.OTHER, Gender.getGenderEnumValue("OTHER"));
        assertEquals(Gender.MALE, Gender.getGenderEnumValue("male"));
        assertEquals(Gender.FEMALE, Gender.getGenderEnumValue("female"));
        assertEquals(Gender.OTHER, Gender.getGenderEnumValue("other"));
    }
}
