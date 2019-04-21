package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link StudentProfileAttributes.Gender}.
 */
public class GenderTest extends BaseTestCase {

    @Test
    public void testGetGenderEnumValue() {
        // invalid values
        assertEquals(StudentProfileAttributes.Gender.OTHER,
                StudentProfileAttributes.Gender.getGenderEnumValue(null));
        assertEquals(StudentProfileAttributes.Gender.OTHER,
                StudentProfileAttributes.Gender.getGenderEnumValue("'\"'invalidGender"));
        assertEquals(StudentProfileAttributes.Gender.OTHER,
                StudentProfileAttributes.Gender.getGenderEnumValue("invalidGender"));

        // valid values
        assertEquals(StudentProfileAttributes.Gender.MALE,
                StudentProfileAttributes.Gender.getGenderEnumValue("MALE"));
        assertEquals(StudentProfileAttributes.Gender.FEMALE,
                StudentProfileAttributes.Gender.getGenderEnumValue("female"));
        assertEquals(StudentProfileAttributes.Gender.OTHER,
                StudentProfileAttributes.Gender.getGenderEnumValue("oTheR"));
    }

}
