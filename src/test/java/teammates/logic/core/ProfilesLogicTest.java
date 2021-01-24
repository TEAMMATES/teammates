package teammates.logic.core;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;

/**
 * SUT: {@link ProfilesLogic}.
 */
public class ProfilesLogicTest extends BaseLogicTest {

    private static final ProfilesLogic profilesLogic = ProfilesLogic.inst();

    @Override
    protected void prepareTestData() {
        // no test data used for this test
    }

    @Test
    public void testStudentProfileFunctions() throws Exception {

        // 4 functions are tested together as:
        //      => The functions are very simple (one-liners)
        //      => They are fundamentally related and easily tested together
        //      => It saves time during tests

        ______TS("get SP");
        StudentProfileAttributes expectedSpa = StudentProfileAttributes.builder("id")
                .withShortName("shortName")
                .withEmail("personal@email.com")
                .withInstitute("institute")
                .withNationality("American")
                .withGender(StudentProfileAttributes.Gender.FEMALE)
                .withMoreInfo("moreInfo")
                .build();

        StudentProfileAttributes updateSpa = profilesLogic.updateOrCreateStudentProfile(
                StudentProfileAttributes.updateOptionsBuilder(expectedSpa.googleId)
                        .withShortName(expectedSpa.shortName)
                        .withEmail(expectedSpa.email)
                        .withInstitute(expectedSpa.institute)
                        .withNationality(expectedSpa.nationality)
                        .withGender(expectedSpa.gender)
                        .withMoreInfo(expectedSpa.moreInfo)
                        .build());

        StudentProfileAttributes actualSpa = profilesLogic.getStudentProfile(expectedSpa.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        assertEquals(expectedSpa.toString(), updateSpa.toString());
    }

    @Test
    public void testDeleteStudentProfile() throws Exception {
        // more tests in ProfilesDbTest

        profilesLogic.updateOrCreateStudentProfile(
                StudentProfileAttributes.updateOptionsBuilder("sp.logic.test")
                        .withShortName("Test Name")
                        .build());
        StudentProfileAttributes savedProfile = profilesLogic.getStudentProfile("sp.logic.test");
        assertNotNull(savedProfile);

        profilesLogic.deleteStudentProfile("sp.logic.test");
        // check that profile get deleted and picture get deleted
        verifyAbsentInDatastore(savedProfile);
    }

}
