package teammates.test.cases.pagedata;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.StudentProfilePageData;
import teammates.ui.template.StudentProfileEditBox;
import teammates.ui.template.StudentProfileUploadPhotoModal;

/**
 * SUT: {@link StudentProfilePageData}.
 */
public class StudentProfilePageDataTest extends BaseTestCase {

    private StudentProfileAttributes spa;
    private AccountAttributes acct;
    private String isEditingPhoto;
    private String pictureUrl;

    private StudentProfilePageData sppd;

    @Test
    public void testAll() {
        testWithPictureKeyAndNoNullFields();
        testWithNoPictureKeyAndNullFields();
    }

    private void testWithPictureKeyAndNoNullFields() {
        sppd = initializeDataWithPictureKeyAndNoNullFields();
        testProfileEditBox(sppd.getProfileEditBox());
        testUploadPhotoModal(sppd.getUploadPhotoModal());
    }

    private void testWithNoPictureKeyAndNullFields() {
        sppd = initializeDataWithNoPictureKeyAndNullFields();
        testProfileEditBox(sppd.getProfileEditBox());
        testUploadPhotoModal(sppd.getUploadPhotoModal());
    }

    private StudentProfilePageData initializeDataWithPictureKeyAndNoNullFields() {
        spa = StudentProfileAttributes.builder("valid.id.2")
                .withShortName("short name")
                .withEmail("e@mail2.com")
                .withInstitute("inst")
                .withNationality("American")
                .withGender("male")
                .withMoreInfo("more info")
                .withPictureKey("pictureKey")
                .build();

        acct = AccountAttributes.builder()
                .withGoogleId("valid.id")
                .withName("full name")
                .withEmail("e@email.com")
                .withInstitute("inst")
                .withIsInstructor(false)
                .withStudentProfileAttributes(spa)
                .build();
        isEditingPhoto = "false";
        pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE
                   + "?" + Const.ParamsNames.BLOB_KEY + "=" + spa.pictureKey
                   + "&" + Const.ParamsNames.USER_ID + "=" + acct.googleId;
        return new StudentProfilePageData(acct, dummySessionToken, isEditingPhoto);
    }

    private StudentProfilePageData initializeDataWithNoPictureKeyAndNullFields() {
        spa = StudentProfileAttributes.builder("valid.id.2")
                .withGender("male")
                .build();
        acct = AccountAttributes.builder()
                .withGoogleId("valid.id")
                .withName("full name")
                .withEmail("e@email.com")
                .withInstitute("inst")
                .withIsInstructor(false)
                .withStudentProfileAttributes(spa)
                .build();
        pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
        return new StudentProfilePageData(acct, dummySessionToken, isEditingPhoto);
    }

    private void testProfileEditBox(StudentProfileEditBox profileEditBox) {
        assertEquals(acct.name, profileEditBox.getName());
        assertEquals(isEditingPhoto, profileEditBox.getEditingPhoto());
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.shortName), profileEditBox.getShortName());
        /*
         * The email field value is the one entered by student (long-term contact email), not the one
         * entered by instructor during enrollment. It comes from SPA, not AA.
         */
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.email), profileEditBox.getEmail());
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.institute), profileEditBox.getInstitute());
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.nationality), profileEditBox.getNationality());
        assertEquals(spa.gender, profileEditBox.getGender());
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.moreInfo), profileEditBox.getMoreInfo());
        /*
         * Currently across the application googleId is always taken from Account.
         * TODO check if googleId in SPA can ever be different from AA.
         */
        assertEquals(acct.googleId, profileEditBox.getGoogleId());
        assertEquals(pictureUrl, profileEditBox.getPictureUrl());
    }

    private void testUploadPhotoModal(StudentProfileUploadPhotoModal uploadPhotoModal) {
        assertEquals(acct.googleId, uploadPhotoModal.getGoogleId());
        assertEquals(pictureUrl, uploadPhotoModal.getPictureUrl());
        assertEquals(spa.pictureKey, uploadPhotoModal.getPictureKey());
    }

}
