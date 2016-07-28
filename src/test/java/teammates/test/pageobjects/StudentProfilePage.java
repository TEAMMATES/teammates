package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;

public class StudentProfilePage extends AppPage {

    @FindBy(id = "studentPhoto")
    protected WebElement profilePicBox;

    @FindBy(id = "studentShortname")
    protected WebElement shortNameBox;

    @FindBy(id = "studentEmail")
    protected WebElement emailBox;

    @FindBy(id = "studentInstitution")
    protected WebElement institutionBox;

    @FindBy(id = "studentNationality")
    protected WebElement countryBox;

    @FindBy(id = "genderMale")
    protected WebElement genderMaleRadio;

    @FindBy(id = "genderFemale")
    protected WebElement genderFemaleRadio;

    @FindBy(id = "genderOther")
    protected WebElement genderOtherRadio;

    @FindBy(id = "studentMoreInfo")
    protected WebElement moreInfoBox;

    @FindBy(id = "studentPhotoUploader")
    protected WebElement uploadEditModal;

    @FindBy(id = "uploadEditPhoto")
    protected WebElement uploadPopupButton;

    @FindBy(id = "profileEditSubmit")
    protected WebElement submitButton;

    @FindBy(id = "profileUploadPictureSubmit")
    protected WebElement uploadPictureSubmit;

    @FindBy(id = "profileEditPictureSubmit")
    protected WebElement editPictureSubmit;

    @FindBy(id = "profilePicEditRotateLeft")
    protected WebElement editPictureRotateLeft;

    @FindBy(id = "profilePicEditZoomIn")
    protected WebElement editPictureZoomIn;

    @FindBy(id = "profilePicEditZoomOut")
    protected WebElement editPictureZoomOut;

    @FindBy(id = "profilePicEditRotateRight")
    protected WebElement editPictureRotateRight;

    @FindBy(id = "profilePicEditPanUp")
    protected WebElement editPicturePanUp;

    @FindBy(id = "profilePicEditPanLeft")
    protected WebElement editPicturePanLeft;

    @FindBy(id = "profilePicEditPanRight")
    protected WebElement editPicturePanRight;

    @FindBy(id = "profilePicEditPanDown")
    protected WebElement editPicturePanDown;

    public StudentProfilePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Student Profile</h1>");
    }

    public StudentProfilePage submitEditedProfile() {
        click(submitButton);
        waitForPageToLoad();
        return changePageType(StudentProfilePage.class);
    }

    public void fillProfilePic(String fileName) {
        showPictureEditor();
        RemoteWebElement ele = (RemoteWebElement) browser.driver.findElement(By.id("studentPhoto"));
        fillFileBox(ele, fileName);
    }

    public void showPictureEditor() {
        click(uploadPopupButton);
        waitForUploadEditModalVisible();
    }

    public void fillShortName(String shortName) {
        fillTextBox(shortNameBox, shortName);
    }

    public void fillEmail(String studentEmail) {
        fillTextBox(emailBox, studentEmail);
    }

    public void fillInstitution(String studentInstitution) {
        fillTextBox(institutionBox, studentInstitution);
    }

    public void fillNationality(String studentNationality) {
        fillTextBox(countryBox, studentNationality);
    }

    public void fillMoreInfo(String moreInfo) {
        fillTextBox(moreInfoBox, moreInfo);
    }

    public void selectGender(String gender) {
        switch (gender) {
        case Const.GenderTypes.MALE:
            click(genderMaleRadio);
            break;
        case Const.GenderTypes.FEMALE:
            click(genderFemaleRadio);
            break;
        case Const.GenderTypes.OTHER:
            click(genderOtherRadio);
            break;
        default:
            fail("Given gender " + gender + " is not valid!");
            break;
        }
    }

    public void editProfileThroughUi(String fileName, String shortName, String email, String institute,
                                     String nationality, String gender, String moreInfo) {
        fillShortName(shortName);
        fillEmail(email);
        fillInstitution(institute);
        fillNationality(nationality);
        fillMoreInfo(moreInfo);
        selectGender(gender);
        submitEditedProfile();
    }

    public void ensureProfileContains(String shortName, String email, String institute, String nationality,
                                      String gender, String moreInfo) {
        assertEquals(shortName, shortNameBox.getAttribute("value"));
        assertEquals(email, emailBox.getAttribute("value"));
        assertEquals(institute, institutionBox.getAttribute("value"));
        assertEquals(nationality, countryBox.getAttribute("value"));
        ensureGenderIsSelectedAs(gender);
        assertEquals(moreInfo, moreInfoBox.getText());
    }

    private void ensureGenderIsSelectedAs(String gender) {
        switch (gender) {
        case Const.GenderTypes.MALE:
            assertTrue(genderMaleRadio.isSelected());
            break;
        case Const.GenderTypes.FEMALE:
            assertTrue(genderFemaleRadio.isSelected());
            break;
        case Const.GenderTypes.OTHER:
            assertTrue(genderOtherRadio.isSelected());
            break;
        default:
            fail("unexpected gender value given");
            break;
        }
    }

    public void uploadPicture() {
        click(uploadPictureSubmit);
        waitForPageToLoad();
    }

    public void editProfilePhoto() {
        click(editPictureZoomIn);
        click(editPictureZoomOut);
        click(editPictureZoomIn);

        click(editPictureRotateRight);
        click(editPictureRotateLeft);
        click(editPictureRotateRight);

        click(editPicturePanDown);
        click(editPicturePanUp);
        click(editPicturePanDown);

        click(editPicturePanLeft);
        click(editPicturePanRight);
        click(editPicturePanLeft);

        click(editPictureSubmit);
        waitForPageToLoad();
    }

    public void verifyPhotoSize(int height, int width) {
        assertEquals(String.valueOf(height), browser.driver.findElement(By.id("pictureHeight"))
                                                           .getAttribute("value"));
        assertEquals(String.valueOf(width), browser.driver.findElement(By.id("pictureWidth"))
                                                          .getAttribute("value"));
    }

    public void verifyUploadButtonState(boolean expectedState) {
        assertEquals(expectedState, uploadPictureSubmit.isEnabled());
        
    }
    
    public void waitForUploadEditModalVisible() {
        waitForElementVisibility(uploadEditModal);
    }

    public void closeEditPictureModal() {
        WebElement closeButton = browser.driver.findElement(By.className("close"));
        waitForElementVisibility(closeButton);
        click(closeButton);
    }

}
