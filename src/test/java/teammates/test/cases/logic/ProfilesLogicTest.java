package teammates.test.cases.logic;

import org.testng.annotations.Test;

import com.google.appengine.api.blobstore.BlobKey;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.ProfilesLogic;

/**
 * SUT: {@link ProfilesLogic}.
 */
public class ProfilesLogicTest extends BaseLogicTest {

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
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
                .withGender("female")
                .withMoreInfo("moreInfo")
                .build();

        AccountAttributes accountWithStudentProfile = AccountAttributes.builder()
                .withGoogleId("id")
                .withName("name")
                .withEmail("test@email.come")
                .withInstitute("dev")
                .withIsInstructor(true)
                .withStudentProfileAttributes(expectedSpa)
                .build();

        accountsLogic.createAccount(accountWithStudentProfile);

        StudentProfileAttributes actualSpa = profilesLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        assertEquals(expectedSpa.toString(), actualSpa.toString());

        ______TS("update SP");

        expectedSpa.pictureKey = "non-empty";
        accountWithStudentProfile.studentProfile.pictureKey = expectedSpa.pictureKey;
        profilesLogic.updateStudentProfile(accountWithStudentProfile.studentProfile);

        actualSpa = profilesLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        assertEquals(expectedSpa.toString(), actualSpa.toString());

        ______TS("update picture");

        expectedSpa.pictureKey = writeFileToGcs(expectedSpa.googleId, "src/test/resources/images/profile_pic.png");
        profilesLogic.updateStudentProfilePicture(expectedSpa.googleId, expectedSpa.pictureKey);
        actualSpa = profilesLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        assertEquals(expectedSpa.toString(), actualSpa.toString());

        ______TS("delete profile picture");

        profilesLogic.deleteStudentProfilePicture(expectedSpa.googleId);
        assertFalse(doesFileExistInGcs(new BlobKey(expectedSpa.pictureKey)));

        actualSpa = profilesLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        expectedSpa.pictureKey = "";
        assertEquals(expectedSpa.toString(), actualSpa.toString());

        // remove the account that was created
        accountsLogic.deleteAccountCascade("id");
    }

    @Test
    public void testDeletePicture() throws Exception {
        String keyString = writeFileToGcs("accountsLogicTestid", "src/test/resources/images/profile_pic.png");
        BlobKey key = new BlobKey(keyString);
        profilesLogic.deletePicture(key);
        assertFalse(doesFileExistInGcs(key));
    }

}
