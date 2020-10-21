package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;

/**
 * Page Object Model for instructor student records page.
 */
public class InstructorStudentRecordsPage extends AppPage {
    private static final String NOT_SPECIFIED_LABEL = "Not Specified";

    @FindBy(id = "records-header")
    private WebElement headerText;

    @FindBy (id = "name-with-gender")
    private WebElement studentNameWithGender;

    @FindBy (id = "personal-email")
    private WebElement studentPersonalEmail;

    @FindBy (id = "institution")
    private WebElement studentInstitution;

    @FindBy (id = "nationality")
    private WebElement studentNationality;

    @FindBy (id = "more-info")
    private WebElement moreInformation;

    public InstructorStudentRecordsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("'s Records");
    }

    public void verifyIsCorrectPage(String courseId, String studentName) {
        String expected = String.format("%s's Records - %s", studentName, courseId);
        assertEquals(expected, headerText.getText());
    }

    public void verifyStudentDetails(StudentProfileAttributes studentProfile, StudentAttributes student) {
        verifyIsCorrectPage(student.getCourse(), student.getName());

        StudentProfileAttributes profileToTest = studentProfile;
        if (studentProfile == null) {
            profileToTest = StudentProfileAttributes.builder(student.getGoogleId()).build();
        }
        verifyDetail(getExpectedNameWithGender(profileToTest), studentNameWithGender);
        verifyDetail(profileToTest.getEmail(), studentPersonalEmail);
        verifyDetail(profileToTest.getInstitute(), studentInstitution);
        verifyDetail(profileToTest.getNationality(), studentNationality);
        verifyDetail(profileToTest.getMoreInfo(), moreInformation);
    }

    private void verifyDetail(String expected, WebElement detailField) {
        if (expected.isEmpty()) {
            assertEquals(NOT_SPECIFIED_LABEL, detailField.getText());
        } else {
            assertEquals(expected, detailField.getText());
        }
    }

    private String getExpectedNameWithGender(StudentProfileAttributes profile) {
        String name = profile.getShortName();
        StudentProfileAttributes.Gender gender = profile.getGender();
        String expectedName = name.isEmpty()
                ? NOT_SPECIFIED_LABEL
                : name;
        String expectedGender = gender.equals(StudentProfileAttributes.Gender.OTHER)
                ? NOT_SPECIFIED_LABEL
                : gender.toString();

        return expectedName + " (" + expectedGender + ")";
    }

}
