package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import teammates.common.util.Const;
import teammates.common.util.NationalityHelper;

public class StudentProfilePage extends AppPage {

    @FindBy(id = "studentShortname")
    private WebElement shortNameBox;

    @FindBy(id = "studentEmail")
    private WebElement emailBox;

    @FindBy(id = "studentInstitution")
    private WebElement institutionBox;

    @FindBy(id = "studentNationality")
    private WebElement studentNationalityDropdown;

    @FindBy(id = "genderMale")
    private WebElement genderMaleRadio;

    @FindBy(id = "genderFemale")
    private WebElement genderFemaleRadio;

    @FindBy(id = "genderOther")
    private WebElement genderOtherRadio;

    @FindBy(id = "studentMoreInfo")
    private WebElement moreInfoBox;

    @FindBy(id = "studentPhotoUploader")
    private WebElement uploadEditModal;

    @FindBy(id = "uploadEditPhoto")
    private WebElement uploadPopupButton;

    @FindBy(id = "profileEditSubmit")
    private WebElement submitButton;

    @FindBy(id = "profileUploadPictureSubmit")
    private WebElement uploadPictureSubmit;

    @FindBy(id = "profileEditPictureSubmit")
    private WebElement editPictureSubmit;

    @FindBy(id = "profilePicEditRotateLeft")
    private WebElement editPictureRotateLeft;

    @FindBy(id = "profilePicEditZoomIn")
    private WebElement editPictureZoomIn;

    @FindBy(id = "profilePicEditZoomOut")
    private WebElement editPictureZoomOut;

    @FindBy(id = "profilePicEditRotateRight")
    private WebElement editPictureRotateRight;

    @FindBy(id = "profilePicEditPanUp")
    private WebElement editPicturePanUp;

    @FindBy(id = "profilePicEditPanLeft")
    private WebElement editPicturePanLeft;

    @FindBy(id = "profilePicEditPanRight")
    private WebElement editPicturePanRight;

    @FindBy(id = "profilePicEditPanDown")
    private WebElement editPicturePanDown;

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

    /**
     * Selects student nationality from the dropdown list if the nationality is
     * valid, otherwise it fails with a message.
     */
    public void selectNationality(String studentNationality) {
        if (NationalityHelper.getNationalities().contains(studentNationality) || "".equals(studentNationality)) {
            Select dropdown = new Select(studentNationalityDropdown);
            dropdown.selectByValue(studentNationality);
        } else {
            fail("Given nationality " + studentNationality + " is not valid!");
        }
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

    public void editProfileThroughUi(String shortName, String email, String institute,
                                     String nationality, String gender, String moreInfo) {
        fillShortName(shortName);
        fillEmail(email);
        fillInstitution(institute);
        selectNationality(nationality);
        fillMoreInfo(moreInfo);
        selectGender(gender);
        submitEditedProfile();
    }

    public void ensureProfileContains(String shortName, String email, String institute, String nationality,
                                      String gender, String moreInfo) {
        assertEquals(shortName, shortNameBox.getAttribute("value"));
        assertEquals(email, emailBox.getAttribute("value"));
        assertEquals(institute, institutionBox.getAttribute("value"));
        ensureNationalityIsSelectedAs(nationality);
        ensureGenderIsSelectedAs(gender);
        assertEquals(moreInfo, moreInfoBox.getText());
    }

    /**
     * Makes sure that the nationality is selected in the dropdown list.
     * If not, it fails with a message.
     */
    private void ensureNationalityIsSelectedAs(String nationality) {
        if (NationalityHelper.getNationalities().contains(nationality) || "".equals(nationality)) {
            assertEquals(nationality, studentNationalityDropdown.getAttribute("value"));
        } else {
            fail("unexpected nationality value given");
        }
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
