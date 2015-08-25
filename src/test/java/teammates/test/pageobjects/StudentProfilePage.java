package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
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

    public StudentProfilePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Student Profile</h1>");
    }

    public StudentProfilePage submitEditedProfile() {
        submitButton.click();
        waitForPageToLoad();
        return changePageType(StudentProfilePage.class);
    }

    public void fillProfilePic(String fileName) throws Exception {
        showPictureEditor();
        RemoteWebElement ele = (RemoteWebElement) browser.driver.findElement(By.id("studentPhoto"));
        fillFileBox(ele, fileName);
    }

    public void showPictureEditor() throws Exception {
        uploadPopupButton.click();
        waitForElementVisibility(browser.driver.findElement(By.id("studentPhotoUploader")));
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

    public void selectGender(String gender) throws Exception {
        switch (gender) {
            case Const.GenderTypes.MALE:
                genderMaleRadio.click();
                break;
            case Const.GenderTypes.FEMALE:
                genderFemaleRadio.click();
                break;
            case Const.GenderTypes.OTHER:
                genderOtherRadio.click();
                break;
            default:
                throw new InvalidParametersException("Given gender " + gender + " is not valid!");
        }
    }

    public void editProfileThroughUi(String fileName, String shortName, String email, String institute,
                                     String nationality, String gender, String moreInfo) throws Exception {
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
                Assumption.fail("unexpected gender value given");
        }
    }

    public void uploadPicture() {
        uploadPictureSubmit.click();
        waitForPageToLoad();
    }

    public void editProfilePhoto() {
        editPictureSubmit.click();
    }

    public void verifyPhotoSize(int height, int width) throws Exception {
        assertEquals(String.valueOf(height), browser.driver.findElement(By.id("pictureHeight"))
                                                           .getAttribute("value"));
        assertEquals(String.valueOf(width), browser.driver.findElement(By.id("pictureWidth"))
                                                          .getAttribute("value"));
    }

}
