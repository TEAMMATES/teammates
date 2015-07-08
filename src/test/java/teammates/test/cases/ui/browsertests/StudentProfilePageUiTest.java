package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.logic.backdoor.BackDoorServlet;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentHomePage;
import teammates.test.pageobjects.StudentProfilePage;
import teammates.test.pageobjects.StudentProfilePicturePage;
import teammates.test.util.Priority;

@Priority(-3)
public class StudentProfilePageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    private StudentProfilePage profilePage;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentProfilePageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }

    @Test
    public void allTests() throws Exception {
        // Do not change the order
        testNavLinkToPage();
        testContent();
        testActions();
        testAjaxPictureUrl();
    }

    private void testNavLinkToPage() {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                                   .withUserId(testData.accounts.get("studentWithEmptyProfile").googleId);
        StudentHomePage shp = loginAdminToPage(browser, profileUrl, StudentHomePage.class);
        profilePage = shp.loadProfileTab().changePageType(StudentProfilePage.class);
    }

    private void testContent() {
        // assumes it is run after NavLinks Test
        // (ie already logged in as studentWithEmptyProfile
        ______TS("Typical case: empty profile values");

        // This is the full HTML verification for Registered Student Profile Submit Page, the rest can all be verifyMainHtml
        profilePage.verifyHtml("/studentProfilePageDefault.html");

        ______TS("Typical case: existing profile values");
        // this test uses actual user accounts
        profilePage = getProfilePageForStudent("studentWithExistingProfile");
        profilePage.verifyHtmlPart(By.id("editProfileDiv"), "/studentProfileEditDivExistingValues.html");
    }

    private void testActions() throws Exception {
        // assumes it is run after NavLinks Test 
        // (ie already logged in as studentWithExistingProfile
        String studentGoogleId = testData.accounts.get("studentWithExistingProfile").googleId;

        ______TS("Typical case: no picture");

        profilePage.editProfileThroughUi("", "short.name", "e@email.tmt", "inst", "Usual Nationality",
                                         "male", "this is enough!$%&*</>");
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "Usual Nationality",
                                          "male", "this is enough!$%&*</>");
        profilePage.verifyStatus(Const.StatusMessages.STUDENT_PROFILE_EDITED);
        
        ______TS("Typical case: changing genders for complete coverage");

        profilePage.editProfileThroughUi("", "short.name", "e@email.tmt", "inst", "Usual Nationality",
                                         "other", "this is enough!$%&*</>");
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "Usual Nationality",
                                          "other", "this is enough!$%&*</>");
        profilePage.editProfileThroughUi("", "short.name", "e@email.tmt", "inst", "Usual Nationality",
                                        "female", "this is enough!$%&*</>");
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "Usual Nationality",
                                         "female", "this is enough!$%&*</>");

        ______TS("Failure case: script injection");

        StudentProfileAttributes spa = new StudentProfileAttributes("valid.id",
                                                                    "<script>alert(\"Hello world!\");</script>",
                                                                    "e@email.tmt", " inst", "Usual Nationality",
                                                                    "male", "this is enough!$%&*</>", "");
        profilePage.editProfileThroughUi(spa.googleId, spa.shortName, spa.email, spa.institute, spa.nationality,
                                         spa.gender, spa.moreInfo);
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "Usual Nationality",
                                          "female", "this is enough!$%&*</>");
        profilePage.verifyStatus(StringHelper.toString(spa.getInvalidityInfo(), " ")
                                             // de-sanitize
                                             .replace("&lt;", "<").replace("&gt;", ">")
                                             .replace("&quot;", "\"").replace("&#x2f;", "/"));
        
        ______TS("Failure case: invalid data");

        spa = new StudentProfileAttributes("valid.id", "$$short.name", "e@email.tmt", " inst  ",
                                           StringHelper.generateStringOfLength(54),
                                           "male", "this is enough!$%&*</>", "");
        profilePage.editProfileThroughUi("", spa.shortName, spa.email, spa.institute, spa.nationality,
                                         spa.gender, spa.moreInfo);
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "Usual Nationality",
                                          "female", "this is enough!$%&*</>");
        profilePage.verifyStatus(StringHelper.toString(spa.getInvalidityInfo(), " "));

        ______TS("Typical case: picture upload and edit");

        profilePage.fillProfilePic("src/test/resources/images/profile_pic.png");
        profilePage.uploadPicture();

        // Verify with retry after upload picture due to inconsistency of .click in detecting page load
        profilePage.verifyStatusWithRetry(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED, 10);
        profilePage.isElementVisible("studentPhotoUploader");

        profilePage.editProfilePhoto();
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "Usual Nationality",
                                          "female", "this is enough!$%&*</>");
        profilePage.verifyPhotoSize(150, 150);

        String prevPictureKey = BackDoor.getStudentProfile(studentGoogleId).pictureKey;
        verifyPictureIsPresent(prevPictureKey);

        ______TS("Typical case: repeated edit");
        
        profilePage.showPictureEditor();
        profilePage.editProfilePhoto();
        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "Usual Nationality",
                                          "female", "this is enough!$%&*</>");
        profilePage.verifyPhotoSize(150, 150);
        
        prevPictureKey = BackDoor.getStudentProfile(studentGoogleId).pictureKey;
        verifyPictureIsPresent(prevPictureKey);
        
        ______TS("Failure case: not a picture");

        profilePage.fillProfilePic("src/test/resources/images/not_a_picture.txt");
        profilePage.uploadPicture();

        // Verify with retry after upload picture due to inconsistency of .click in detecting page load
        profilePage.verifyStatusWithRetry(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE, 10);
        verifyPictureIsPresent(prevPictureKey);

        ______TS("Failure case: picture too large");

        profilePage.fillProfilePic("src/test/resources/images/profile_pic_too_large.jpg");
        profilePage.uploadPicture();

        // Verify with retry after upload picture due to inconsistency of .click in detecting page load
        profilePage.verifyStatusWithRetry(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE, 10);
        verifyPictureIsPresent(prevPictureKey);

        ______TS("Typical case: update picture (too tall)");

        profilePage.fillProfilePic("src/test/resources/images/image_tall.jpg");
        profilePage.uploadPicture();
        profilePage.isElementVisible("studentPhotoUploader");
        profilePage.verifyPhotoSize(3074, 156);

        String currentPictureKey = BackDoor.getStudentProfile(studentGoogleId).pictureKey;
        verifyPictureIsPresent(currentPictureKey);
    }

    private void testAjaxPictureUrl() throws Exception {
        String studentId = "studentWithExistingProfile";
        String instructorId = "SHomeUiT.instr";
        String helperId = "SHomeUiT.helper";
        String studentGoogleId = testData.accounts.get("studentWithExistingProfile").googleId;
        String currentPictureKey = BackDoor.getStudentProfile(studentGoogleId).pictureKey;
        String email = testData.students.get("studentWithExistingProfile").email;
        String courseId = testData.students.get("studentWithExistingProfile").course;

        email = StringHelper.encrypt(email);
        courseId = StringHelper.encrypt(courseId);
        String invalidEmail = StringHelper.encrypt("random-EmAIl");

        ______TS("Typical case: with blob-key");

        getProfilePicturePage(studentId, currentPictureKey).verifyHasPicture();

        ______TS("Failure case: invalid blob-key");

        String expectedFilename = "/studentProfilePictureNotFound.html";
        getProfilePicturePage(studentId, "random-StRing123").verifyIsErrorPage(expectedFilename);

        ______TS("Typical case: with email and course");

        getProfilePicturePage(instructorId, email, courseId).verifyHasPicture();

        ______TS("Failure case: instructor does not have privilege");

        expectedFilename = "/studentProfilePictureUnauthorized.html";
        getProfilePicturePage(helperId, email, courseId)
                .verifyIsUnauthorisedErrorPage(expectedFilename);

        ______TS("Failure case: non-existent student");

        expectedFilename = "/studentProfilePictureStudentDoesNotExist.html";
        getProfilePicturePage(instructorId, invalidEmail, courseId).verifyIsEntityNotFoundErrorPage(expectedFilename);
    }

    private StudentProfilePicturePage getProfilePicturePage(String instructorId, String email,
                                                            String courseId) {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                                   .withUserId(testData.accounts.get(instructorId).googleId)
                                   .withParam(Const.ParamsNames.STUDENT_EMAIL, email)
                                   .withParam(Const.ParamsNames.COURSE_ID, courseId);
        return loginAdminToPage(browser, profileUrl, StudentProfilePicturePage.class);
    }

    private StudentProfilePicturePage getProfilePicturePage(String studentId, String pictureKey) {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                                   .withUserId(testData.accounts.get(studentId).googleId)
                                   .withParam(Const.ParamsNames.BLOB_KEY, pictureKey);
        return loginAdminToPage(browser, profileUrl, StudentProfilePicturePage.class);
    }

    private void verifyPictureIsPresent(String pictureKey) {
        assertEquals(BackDoorServlet.RETURN_VALUE_TRUE, BackDoor.getWhetherPictureIsPresentInGcs(pictureKey));
    }

    private StudentProfilePage getProfilePageForStudent(String studentId) {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PAGE)
                                   .withUserId(testData.accounts.get(studentId).googleId);
        return loginAdminToPage(browser, profileUrl, StudentProfilePage.class);
    }

    @AfterClass
    public void testAfter() {
        BrowserPool.release(browser);
    }

}
