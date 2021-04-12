package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.NationalityHelper;

/**
 * Page Object Model for student profile page.
 */
public class StudentProfilePage extends AppPage {

    @FindBy(id = "studentshortname")
    private WebElement shortNameBox;

    @FindBy(id = "studentprofileemail")
    private WebElement emailBox;

    @FindBy(id = "studentprofileinstitute")
    private WebElement institutionBox;

    @FindBy(id = "studentnationality")
    private WebElement studentNationalityDropdown;

    @FindBy(className = "student-gender-radio")
    private List<WebElement> genderRadio;

    @FindBy(id = "studentprofilemoreinfo")
    private WebElement moreInfoBox;

    @FindBy(className = "student-save-profile")
    private WebElement saveProfileButton;

    @FindBy(className = "upload-edit-photo")
    private WebElement uploadPopupButton;

    @FindBy(className = "profile-upload-picture-submit")
    private WebElement uploadPictureSubmit;

    @FindBy(tagName = "tm-upload-edit-profile-picture-modal")
    private WebElement uploadEditModal;

    @FindBy(className = "btn-space")
    private List<WebElement> editPictureTools;

    public StudentProfilePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Student Profile");
    }

    public void editProfileThroughUi(String shortName, String email, String institute,
                                     String nationality, StudentProfileAttributes.Gender gender, String moreInfo) {
        fillShortName(shortName);
        fillEmail(email);
        fillInstitution(institute);
        selectNationality(nationality);
        fillMoreInfo(moreInfo);
        selectGender(gender);
        submitEditedProfile();
    }

    private StudentProfilePage submitEditedProfile() {
        click(saveProfileButton);
        waitForConfirmationModalAndClickOk();
        waitForPageToLoad(true);
        return changePageType(StudentProfilePage.class);
    }

    private void fillShortName(String shortName) {
        fillTextBox(shortNameBox, shortName);
    }

    private void fillEmail(String studentEmail) {
        fillTextBox(emailBox, studentEmail);
    }

    private void fillInstitution(String studentInstitution) {
        fillTextBox(institutionBox, studentInstitution);
    }

    /**
     * Selects student nationality from the dropdown list if the nationality is
     * valid, otherwise it fails with a message.
     */
    private void selectNationality(String studentNationality) {
        if (NationalityHelper.getNationalities().contains(studentNationality) || "".equals(studentNationality)) {
            Select dropdown = new Select(studentNationalityDropdown);
            dropdown.selectByValue(studentNationality);
        } else {
            fail("Given nationality " + studentNationality + " is not valid!");
        }
    }

    private void fillMoreInfo(String moreInfo) {
        fillTextBox(moreInfoBox, moreInfo);
    }

    private void selectGender(StudentProfileAttributes.Gender gender) {
        switch (gender) {
        case MALE:
            click(genderRadio.get(0));
            break;
        case FEMALE:
            click(genderRadio.get(1));
            break;
        case OTHER:
            click(genderRadio.get(2));
            break;
        default:
            fail("Given gender " + gender + " is not valid!");
            break;
        }
    }

    public void uploadPicture() {
        click(uploadPictureSubmit);
        waitForPageToLoad(true);
    }

    public void editProfilePhoto() {
        WebElement editPictureRotateRight = editPictureTools.get(0);
        WebElement editPictureFlipHorizontal = editPictureTools.get(1);
        WebElement editPictureFlipVertical = editPictureTools.get(2);
        WebElement editPictureRotateLeft = editPictureTools.get(3);

        click(editPictureRotateRight);
        click(editPictureRotateLeft);
        click(editPictureRotateRight);

        click(editPictureFlipVertical);
        click(editPictureFlipVertical);
        click(editPictureFlipVertical);

        click(editPictureFlipHorizontal);
        click(editPictureFlipHorizontal);
        click(editPictureFlipHorizontal);
    }

    public void fillProfilePic(String fileName) {
        showPictureEditor();
        RemoteWebElement ele = (RemoteWebElement) browser.driver.findElement(By.className("student-photo"));
        fillFileBox(ele, fileName);
    }

    public void showPictureEditor() {
        click(uploadPopupButton);
        waitForUploadEditModalVisible();
    }

    public void verifyPhotoSize(String height, String width) {
        assertEquals(height, browser.driver.findElement(By.className("profile-pic")).getCssValue("height"));
        assertEquals(width, browser.driver.findElement(By.className("profile-pic")).getCssValue("width"));
        click(uploadEditModal.findElement(By.className("close")));
    }

    public void ensureProfileContains(String shortName, String email, String institute, String nationality,
                                      StudentProfileAttributes.Gender gender, String moreInfo) {
        assertEquals(shortName, shortNameBox.getAttribute("value"));
        assertEquals(email, emailBox.getAttribute("value"));
        assertEquals(institute, institutionBox.getAttribute("value"));
        ensureNationalityIsSelectedAs(nationality);
        ensureGenderIsSelectedAs(gender);
        assertEquals(moreInfo, moreInfoBox.getAttribute("value"));
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

    private void ensureGenderIsSelectedAs(StudentProfileAttributes.Gender gender) {
        switch (gender) {
        case MALE:
            assertTrue(genderRadio.get(0).isSelected());
            break;
        case FEMALE:
            assertTrue(genderRadio.get(1).isSelected());
            break;
        case OTHER:
            assertTrue(genderRadio.get(2).isSelected());
            break;
        default:
            fail("unexpected gender value given");
            break;
        }
    }

    public void waitForUploadEditModalVisible() {
        waitForElementVisibility(uploadEditModal);
    }
}
