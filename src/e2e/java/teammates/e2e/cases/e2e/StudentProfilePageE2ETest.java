package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentProfilePage;

/**
 * SUT: {@link teammates.common.util.Const.WebPageURIs#STUDENT_PROFILE_PAGE}.
 */
public class StudentProfilePageE2ETest extends BaseE2ETestCase {


	@Override
    protected void prepareTestData() {
        testData = loadDataBundle(Const.TestCase.STUDENT_PROFILE_PAGE_E2E_TEST_JSON);
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {

        ______TS(Const.TestCase.TYPICAL_CASE_LOG_IN_WITH_FILLED_PROFILE_VALUES);

        AppUrl url = createUrl(Const.WebPageURIs.STUDENT_PROFILE_PAGE).withUserId(Const.TestCase.S_PROF_UI_T_STUDENT);
        StudentProfilePage profilePage = loginAdminToPage(url, StudentProfilePage.class);

        profilePage.ensureProfileContains(Const.TestCase.BEN, Const.TestCase.I_M_BENNY_GMAIL_TMT, Const.TestCase.TEAMMATES_TEST_INSTITUTE_4,
        		Const.TestCase.SINGAPOREAN, StudentProfileAttributes.Gender.MALE, Const.TestCase.I_AM_JUST_ANOTHER_STUDENT_P);

        ______TS(Const.TestCase.TYPICAL_CASE_PICTURE_UPLOAD_AND_EDIT);

        profilePage.fillProfilePic(Const.TestCase.SRC_TEST_RESOURCES_IMAGES_PROFILE_PIC_PNG);
        profilePage.uploadPicture();
        profilePage.verifyStatusMessage(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED);

        profilePage.showPictureEditor();
        profilePage.waitForUploadEditModalVisible();

        profilePage.editProfilePhoto();
        profilePage.verifyPhotoSize(Const.TestCase._220PX, Const.TestCase._220PX);

        ______TS(Const.TestCase.TYPICAL_CASE_EDIT_PROFILE_PAGE);
        profilePage.editProfileThroughUi(Const.TestCase.SHORT_NAME, Const.TestCase.E_EMAIL_TMT, Const.TestCase.INST, Const.TestCase.AMERICAN,
                StudentProfileAttributes.Gender.FEMALE, Const.TestCase.THIS_IS_ENOUGH_$);
        profilePage.verifyStatusMessage(Const.StatusMessages.STUDENT_PROFILE_EDITED);

        profilePage.ensureProfileContains(Const.TestCase.SHORT_NAME, Const.TestCase.E_EMAIL_TMT, Const.TestCase.INST, Const.TestCase.AMERICAN,
                StudentProfileAttributes.Gender.FEMALE, Const.TestCase.THIS_IS_ENOUGH_$);
    }
}
