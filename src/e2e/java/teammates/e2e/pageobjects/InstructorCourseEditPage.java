package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.ThreadHelper;

/**
 * Represents the instructor course edit page of the website.
 */
public class InstructorCourseEditPage extends AppPage {
    private static final int INSTRUCTOR_TYPE_COOWNER = 0;
    private static final int INSTRUCTOR_TYPE_MANAGER = 1;
    private static final int INSTRUCTOR_TYPE_OBSERVER = 2;
    private static final int INSTRUCTOR_TYPE_TUTOR = 3;
    private static final int INSTRUCTOR_TYPE_CUSTOM = 4;

    private static final int COURSE_MODIFY_COURSE = 0;
    private static final int COURSE_MODIFY_INSTRUCTORS = 1;
    private static final int COURSE_MODIFY_SESSIONS = 2;
    private static final int COURSE_MODIFY_STUDENTS = 3;
    private static final int COURSE_VIEW_STUDENTS = 4;
    private static final int COURSE_GIVE_RESPONSES_IN_SESSION = 5;
    private static final int COURSE_VIEW_RESPONSES_IN_SESSION = 6;
    private static final int COURSE_MODIFY_RESPONSES_IN_SESSION = 7;

    private static final int SECTION_VIEW_STUDENTS = 0;
    private static final int SECTION_GIVE_RESPONSES_IN_SESSION = 1;
    private static final int SECTION_VIEW_RESPONSES_IN_SESSION = 2;
    private static final int SECTION_MODIFY_RESPONSES_IN_SESSION = 3;

    private static final int SESSION_GIVE_RESPONSES = 0;
    private static final int SESSION_VIEW_RESPONSES = 1;
    private static final int SESSION_MODIFY_RESPONSES = 2;

    @FindBy(id = "course-id")
    private WebElement courseIdTextBox;

    @FindBy(id = "course-name")
    private WebElement courseNameTextBox;

    @FindBy(id = "course-institute")
    private WebElement courseInstituteTextBox;

    @FindBy(id = "time-zone")
    private WebElement timeZoneDropDown;

    @FindBy(id = "btn-edit-course")
    private WebElement editCourseButton;

    @FindBy(id = "btn-delete-course")
    private WebElement deleteCourseButton;

    @FindBy(id = "btn-save-course")
    private WebElement saveCourseButton;

    @FindBy(id = "btn-add-instructor")
    private WebElement addInstructorButton;

    @FindBy(id = "btn-copy-instructor")
    private WebElement copyInstructorsButton;

    public InstructorCourseEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Edit Course Details");
    }

    public void verifyCourseDetails(CourseAttributes course) {
        assertEquals(course.getId(), getCourseId());
        assertEquals(course.getName(), getCourseName());
        assertEquals(course.getInstitute(), getCourseInstitute());
        assertEquals(course.getTimeZone(), getTimeZone());
    }

    public void verifyInstructorDetails(InstructorAttributes instructor) {
        int instrNum = getIntrNum(instructor.getEmail());
        if (instructor.getGoogleId() != null) {
            assertEquals(instructor.getGoogleId(), getInstructorGoogleId(instrNum));
        }
        assertEquals(instructor.getName(), getInstructorName(instrNum));
        assertEquals(instructor.getEmail(), getInstructorEmail(instrNum));
        assertEquals(instructor.isDisplayedToStudents(), getInstructorDisplayedToStudents(instrNum));
        if (instructor.isDisplayedToStudents()) {
            assertEquals(instructor.getDisplayedName(), getInstructorDisplayName(instrNum));
        } else {
            assertEquals("(This instructor will NOT be displayed to students)", getInstructorDisplayName(instrNum));
        }
        assertEquals(instructor.getRole(), getInstructorRole(instrNum));
        if (instructor.getRole().equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)
                && getEditInstructorButton(instrNum).isEnabled()) {
            verifyCustomPrivileges(instrNum, instructor.getPrivileges());
        }
    }

    public void verifyCustomPrivileges(int instrNum, InstructorPrivileges privileges) {
        clickEditInstructorButton(instrNum);

        InstructorPermissionSet courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        Map<String, InstructorPermissionSet> sectionLevelPrivileges = privileges.getSectionLevelPrivileges();
        Map<String, Map<String, InstructorPermissionSet>> sessionLevelPrivileges = privileges.getSessionLevelPrivileges();

        verifyCourseLevelPrivileges(instrNum, courseLevelPrivileges);
        verifySectionLevelPrivileges(instrNum, sectionLevelPrivileges);
        verifySessionLevelPrivileges(instrNum, sessionLevelPrivileges);

        clickCancelInstructorButton(instrNum);
    }

    private void verifyCourseLevelPrivileges(int instrNum, InstructorPermissionSet courseLevelPrivileges) {
        List<WebElement> checkboxes = getCourseLevelPanelCheckBoxes(instrNum);
        for (Map.Entry<String, Boolean> privilege : courseLevelPrivileges.toLegacyMapFormat().entrySet()) {
            if (!InstructorPrivileges.isPrivilegeNameValid(privilege.getKey())) {
                continue;
            }
            if (privilege.getValue()) {
                assertTrue(checkboxes.get(getCourseLevelPrivilegeIndex(privilege.getKey())).isSelected());
            } else {
                assertFalse(checkboxes.get(getCourseLevelPrivilegeIndex(privilege.getKey())).isSelected());
            }
        }
    }

    private void verifySectionLevelPrivileges(int instrNum, Map<String, InstructorPermissionSet> sectionLevelPrivileges) {
        for (Map.Entry<String, InstructorPermissionSet> section : sectionLevelPrivileges.entrySet()) {
            int panelNum = getSectionLevelPanelNumWithSectionSelected(instrNum, section.getKey());
            for (Map.Entry<String, Boolean> privilege : section.getValue().toLegacyMapFormat().entrySet()) {
                if (!InstructorPrivileges.isPrivilegeNameValidForSectionLevel(section.getKey())) {
                    continue;
                }
                if (privilege.getValue()) {
                    assertTrue(getSectionLevelCheckBox(instrNum, panelNum,
                            getSectionLevelPrivilegeIndex(privilege.getKey())).isSelected());
                } else {
                    assertFalse(getSectionLevelCheckBox(instrNum, panelNum,
                            getSectionLevelPrivilegeIndex(privilege.getKey())).isSelected());
                }
            }
        }
    }

    private void verifySessionLevelPrivileges(
            int instrNum, Map<String, Map<String, InstructorPermissionSet>> sessionLevelPrivileges) {
        for (Map.Entry<String, Map<String, InstructorPermissionSet>> section : sessionLevelPrivileges.entrySet()) {
            int panelNum = getSectionLevelPanelNumWithSectionSelected(instrNum, section.getKey());
            for (Map.Entry<String, InstructorPermissionSet> session : section.getValue().entrySet()) {
                int sessionIndex = getSessionIndex(instrNum, session.getKey());
                for (Map.Entry<String, Boolean> privilege : session.getValue().toLegacyMapFormat().entrySet()) {
                    if (!InstructorPrivileges.isPrivilegeNameValidForSessionLevel(privilege.getKey())) {
                        continue;
                    }
                    if (privilege.getValue()) {
                        assertTrue(getSessionLevelCheckbox(instrNum, panelNum, sessionIndex,
                                getSessionLevelPrivilegeIndex(privilege.getKey())).isSelected());
                    } else {
                        assertFalse(getSessionLevelCheckbox(instrNum, panelNum, sessionIndex,
                                getSessionLevelPrivilegeIndex(privilege.getKey())).isSelected());
                    }
                }
            }
        }
    }

    public void verifyCourseNotEditable() {
        assertFalse(editCourseButton.isEnabled());
        assertFalse(deleteCourseButton.isEnabled());
    }

    public void verifyInstructorsNotEditable() {
        for (int i = 1; i <= getNumInstructors(); i++) {
            assertFalse(getEditInstructorButton(i).isEnabled());
            assertFalse(getDeleteInstructorButton(i).isEnabled());
        }
    }

    public void verifyAddInstructorNotAllowed() {
        clickAddNewInstructorButton();
        clickSaveInstructorButton(getNumInstructors());
        verifyStatusMessage("You are not authorized to access this resource.");
        clickCancelInstructorButton(getNumInstructors());
    }

    public void verifyCopyInstructorsNotAllowed() {
        verifyUnclickable(copyInstructorsButton);
    }

    public void verifyNumInstructorsEquals(int expectedNum) {
        assertEquals(getNumInstructors(), expectedNum);
    }

    public void editCourse(CourseAttributes newCourse) {
        clickEditCourseButton();
        fillTextBox(courseNameTextBox, newCourse.getName());
        selectNewTimeZone(newCourse.getTimeZone());
        clickSaveCourseButton();
    }

    public void deleteCourse() {
        click(deleteCourseButton);
    }

    public void addInstructor(InstructorAttributes newInstructor) {
        clickAddNewInstructorButton();
        int instructorIndex = getNumInstructors();

        fillTextBox(getNameField(instructorIndex), newInstructor.getName());
        fillTextBox(getEmailField(instructorIndex), newInstructor.getEmail());
        if (newInstructor.isDisplayedToStudents()) {
            markOptionAsSelected(getDisplayedToStudentCheckBox(instructorIndex));
            fillTextBox(getDisplayNameField(instructorIndex), newInstructor.getDisplayedName());
        } else {
            markOptionAsUnselected(getDisplayedToStudentCheckBox(instructorIndex));
        }
        selectRoleForInstructor(instructorIndex, getRoleIndex(newInstructor.getRole()));
        clickSaveInstructorButton(instructorIndex);
    }

    public void copyInstructors(List<InstructorAttributes> newInstructors) {
        Map<String, List<String>> courseInstructorEmailsMap = new HashMap<>();
        for (InstructorAttributes instructor : newInstructors) {
            courseInstructorEmailsMap.putIfAbsent(instructor.getCourseId(), new ArrayList<>());
            courseInstructorEmailsMap.get(instructor.getCourseId()).add(instructor.getEmail());
        }

        clickCopyInstructorsButton();
        WebElement copyInstructorModal = waitForElementPresence(By.id("copy-instructor-modal"));

        List<WebElement> cards = copyInstructorModal.findElements(By.className("card"));
        for (WebElement card : cards) {
            WebElement cardHeader = card.findElement(By.className("card-header"));
            String cardHeaderText = cardHeader.getText();
            String courseId = cardHeaderText.substring(1, cardHeaderText.indexOf(']'));
            if (courseInstructorEmailsMap.containsKey(courseId)) {
                click(cardHeader);
                WebElement cardBody = waitForElementPresence(By.className("card-body"));
                // reload instructors
                WebElement reloadBtn = cardBody.findElement(By.tagName("button"));
                click(reloadBtn);
                WebElement table = waitForElementPresence(By.id("copy-instructor-table"));
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
                for (WebElement row : rows) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (courseInstructorEmailsMap.get(courseId).contains(cells.get(2).getText())) {
                        markOptionAsSelected(cells.get(0).findElement(By.id("enabled-checkbox")));
                    }
                }
                // collapse tab
                click(cardHeader);
            }
        }
        click(browser.driver.findElement(By.id("btn-confirm-copy-instructor")));
        waitUntilAnimationFinish();
    }

    public void verifyCopyInstructorWithExistingEmailNotAllowed(InstructorAttributes newInstructor) {
        copyInstructors(List.of(newInstructor));
        verifyStatusMessage("An instructor with email address " + newInstructor.getEmail()
                + " already exists in the course and/or you have selected more than one instructor "
                + "with this email address.");
        click(browser.driver.findElement(By.id("btn-cancel-copy-instructor")));
    }

    public void resendInstructorInvite(InstructorAttributes instructor) {
        int instrNum = getIntrNum(instructor.getEmail());
        clickAndConfirm(getInviteInstructorButton(instrNum));
    }

    public void deleteInstructor(InstructorAttributes instructor) {
        int instrNum = getIntrNum(instructor.getEmail());
        clickAndConfirm(getDeleteInstructorButton(instrNum));
    }

    public void editInstructor(int instrNum, InstructorAttributes instructor) {
        clickEditInstructorButton(instrNum);

        fillTextBox(getNameField(instrNum), instructor.getName());
        fillTextBox(getEmailField(instrNum), instructor.getEmail());
        if (instructor.isDisplayedToStudents()) {
            markOptionAsSelected(getDisplayedToStudentCheckBox(instrNum));
            fillTextBox(getDisplayNameField(instrNum), instructor.getDisplayedName());
        } else {
            markOptionAsUnselected(getDisplayedToStudentCheckBox(instrNum));
        }
        selectRoleForInstructor(instrNum, getRoleIndex(instructor.getRole()));
        clickSaveInstructorButton(instrNum);
    }

    public void toggleCustomCourseLevelPrivilege(int instrNum, String privilege) {
        if (!getInstructorRole(instrNum).equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            return;
        }

        clickEditInstructorButton(instrNum);
        click(getCourseLevelPanelCheckBox(instrNum, getCourseLevelPrivilegeIndex(privilege)));
        clickSaveInstructorButton(instrNum);
    }

    public void toggleCustomSectionLevelPrivilege(int instrNum, int panelNum, String section,
                                                String privilege) {
        if (!getInstructorRole(instrNum).equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            return;
        }

        clickEditInstructorButton(instrNum);
        clickAddSectionPrivilegeLink(instrNum);

        click(getSectionSelectionCheckBox(instrNum, panelNum, getSectionIndex(instrNum, section)));
        click(getSectionLevelCheckBox(instrNum, panelNum, getSectionLevelPrivilegeIndex(privilege)));
        clickSaveInstructorButton(instrNum);
    }

    public void toggleCustomSessionLevelPrivilege(int instrNum, int panelNum, String section, String session,
                                               String privilege) {
        if (!getInstructorRole(instrNum).equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            return;
        }

        clickEditInstructorButton(instrNum);
        clickAddSectionPrivilegeLink(instrNum);
        clickAddSessionPrivilegeLink(instrNum, panelNum);

        click(getSectionSelectionCheckBox(instrNum, panelNum, getSectionIndex(instrNum, section)));
        click(getSessionLevelCheckbox(instrNum, panelNum, getSessionIndex(instrNum, session),
                getSessionLevelPrivilegeIndex(privilege)));
        clickSaveInstructorButton(instrNum);
    }

    private int getNumInstructors() {
        return browser.driver.findElements(By.cssSelector(".card-header")).size() - 1;
    }

    // Methods for clicking buttons and links

    private void clickEditCourseButton() {
        click(editCourseButton);
    }

    private void clickSaveCourseButton() {
        click(saveCourseButton);
    }

    private void selectNewTimeZone(String timeZone) {
        scrollElementToCenter(timeZoneDropDown);
        Select dropdown = new Select(timeZoneDropDown);
        dropdown.selectByValue(timeZone);
    }

    private void clickAddNewInstructorButton() {
        click(addInstructorButton);
    }

    private void clickCopyInstructorsButton() {
        click(copyInstructorsButton);
    }

    private void clickEditInstructorButton(int instrNum) {
        click(getEditInstructorButton(instrNum));
        waitUntilAnimationFinish();
    }

    private void clickCancelInstructorButton(int instrNum) {
        click(getCancelInstructorButton(instrNum));
    }

    private void clickSaveInstructorButton(int instrNum) {
        click(getSaveInstructorButton(instrNum));
        ThreadHelper.waitFor(1000);
    }

    private void clickAddSectionPrivilegeLink(int instrNum) {
        click(getAddSectionLevelPrivilegesLink(instrNum));
    }

    private void clickAddSessionPrivilegeLink(int instrNum, int panelNum) {
        click(getAddSessionLevelPrivilegesLink(instrNum, panelNum));
    }

    // Methods that return WebElements of the page

    public String getCourseId() {
        return courseIdTextBox.getAttribute("value");
    }

    public String getCourseName() {
        return courseNameTextBox.getAttribute("value");
    }

    public String getCourseInstitute() {
        return courseInstituteTextBox.getAttribute("value");
    }

    public String getTimeZone() {
        return timeZoneDropDown.getAttribute("value");
    }

    private WebElement getEditInstructorButton(int instrNum) {
        return browser.driver.findElement(By.id("btn-edit-instructor-" + instrNum));
    }

    private WebElement getInviteInstructorButton(int instrNum) {
        return browser.driver.findElement(By.id("btn-resend-invite-" + instrNum));
    }

    private WebElement getDeleteInstructorButton(int instrNum) {
        return browser.driver.findElement(By.id("btn-delete-instructor-" + instrNum));
    }

    private WebElement getCancelInstructorButton(int instrNum) {
        return browser.driver.findElement(By.id("btn-cancel-instructor-" + instrNum));
    }

    private WebElement getSaveInstructorButton(int instrNum) {
        return browser.driver.findElement(By.id("btn-save-instructor-" + instrNum));
    }

    private WebElement getNameField(int instrNum) {
        return browser.driver.findElement(By.id("name-instructor-" + instrNum));
    }

    private WebElement getEmailField(int instrNum) {
        return browser.driver.findElement(By.id("email-instructor-" + instrNum));
    }

    private WebElement getDisplayedToStudentCheckBox(int instrNum) {
        return browser.driver.findElement(By.id("checkbox-display-instructor-" + instrNum));
    }

    private WebElement getDisplayNameField(int instrNum) {
        return browser.driver.findElement(By.id("displayed-name-instructor-" + instrNum));
    }

    public String getInstructorGoogleId(int instrNum) {
        return browser.driver.findElement(By.id("google-id-instructor-" + instrNum)).getAttribute("value");
    }

    public String getInstructorName(int instrNum) {
        return browser.driver.findElement(By.id("name-instructor-" + instrNum)).getAttribute("value");
    }

    public String getInstructorEmail(int instrNum) {
        return browser.driver.findElement(By.id("email-instructor-" + instrNum)).getAttribute("value");
    }

    public boolean getInstructorDisplayedToStudents(int instrNum) {
        return browser.driver.findElement(By.id("checkbox-display-instructor-" + instrNum)).isSelected();
    }

    public String getInstructorDisplayName(int instrNum) {
        return browser.driver.findElement(By.id("displayed-name-instructor-" + instrNum)).getAttribute("value");
    }

    public String getInstructorRole(int instrNum) {
        String roleAndDescription = browser.driver.findElement(By.id("role-instructor-" + instrNum)).getText();
        return roleAndDescription.split(":")[0];
    }

    private WebElement getAccessLevels(int instrNum) {
        return browser.driver.findElement(By.id("access-levels-instructor-" + instrNum));
    }

    private WebElement getAccessLevelsRadioButton(int instrNum, int radioNum) {
        WebElement accessLevels = getAccessLevels(instrNum);
        return accessLevels.findElements(By.cssSelector("input[type='radio']")).get(radioNum);
    }

    public void selectRoleForInstructor(int instrNum, int roleIndex) {
        click(getAccessLevelsRadioButton(instrNum, roleIndex));
    }

    private WebElement getCourseLevelPanel(int instrNum) {
        return browser.driver.findElement(By.cssSelector("#custom-access-instructor-" + instrNum
                + " #custom-course"));
    }

    private List<WebElement> getCourseLevelPanelCheckBoxes(int instrNum) {
        WebElement courseLevelPanel = getCourseLevelPanel(instrNum);
        return courseLevelPanel.findElements(By.cssSelector("input[type='checkbox']"));
    }

    private WebElement getCourseLevelPanelCheckBox(int instrNum, int checkboxNum) {
        WebElement courseLevelPanel = getCourseLevelPanel(instrNum);
        return courseLevelPanel.findElements(By.cssSelector("input[type='checkbox']")).get(checkboxNum);
    }

    private WebElement getAddSectionLevelPrivilegesLink(int instrNum) {
        return browser.driver.findElement(By.cssSelector("#custom-access-instructor-" + instrNum
                + " #btn-add-section-level"));
    }

    private WebElement getAddSessionLevelPrivilegesLink(int instrNum, int panelNum) {
        return browser.driver.findElements(By.cssSelector("#custom-access-instructor-" + instrNum
                + " #btn-add-session-level")).get(panelNum - 1);
    }

    private WebElement getSectionSelections(int instrNum, int panelNum) {
        return browser.driver.findElements(By.cssSelector("#custom-access-instructor-" + instrNum
                + " #custom-sections")).get(panelNum - 1);
    }

    private WebElement getSectionLevelPanelBody(int instrNum, int panelNum) {
        return browser.driver.findElements(By.cssSelector("#custom-access-instructor-" + instrNum
                + " #custom-sections-access-levels")).get(panelNum - 1);
    }

    private int getNumSectionLevelPanels(int instrNum) {
        return browser.driver.findElements(By.cssSelector("#custom-access-instructor-" + instrNum
                + " #custom-sections-access-levels")).size();
    }

    private int getSectionLevelPanelNumWithSectionSelected(int instrNum, String section) {
        int sectionIndex = getSectionIndex(instrNum, section);
        int numPanels = getNumSectionLevelPanels(instrNum);
        for (int i = 0; i < numPanels; i++) {
            if (getSectionSelectionCheckBox(instrNum, i + 1, sectionIndex).isSelected()) {
                return i + 1;
            }
        }
        return -1;
    }

    private WebElement getSectionSelectionCheckBox(int instrNum, int panelNum, int sectionNum) {
        WebElement sectionPanel = getSectionSelections(instrNum, panelNum);
        return sectionPanel.findElements(By.cssSelector("input[type='checkbox']")).get(sectionNum);
    }

    private WebElement getSectionLevelCheckBox(int instrNum, int panelNum, int checkBoxIndex) {
        WebElement sectionPanelBody = getSectionLevelPanelBody(instrNum, panelNum);
        return sectionPanelBody.findElements(By.cssSelector("input[type='checkbox']")).get(checkBoxIndex);
    }

    private WebElement getSessionLevelTable(int instrNum, int panelNum) {
        WebElement sectionPanelBody = getSectionLevelPanelBody(instrNum, panelNum);
        return sectionPanelBody.findElement(By.id("custom-sessions"));
    }

    private WebElement getSessionLevelTableRow(int instrNum, int panelNum, int sessionIndex) {
        WebElement sessionLevelTableBody = getSessionLevelTable(instrNum, panelNum);
        return sessionLevelTableBody.findElements(By.cssSelector("tbody tr")).get(sessionIndex);
    }

    private WebElement getSessionLevelCheckbox(int instrNum, int panelNum, int sessionIndex,
                                               int checkBoxIndex) {
        WebElement sessionLevelTableRow = getSessionLevelTableRow(instrNum, panelNum, sessionIndex);
        return sessionLevelTableRow.findElements(By.cssSelector("input[type='checkbox']")).get(checkBoxIndex);
    }

    // Methods for indexing

    private int getRoleIndex(String role) {
        switch (role) {
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
            return INSTRUCTOR_TYPE_COOWNER;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
            return INSTRUCTOR_TYPE_MANAGER;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
            return INSTRUCTOR_TYPE_OBSERVER;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
            return INSTRUCTOR_TYPE_TUTOR;
        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
            return INSTRUCTOR_TYPE_CUSTOM;
        default:
            return -1;
        }
    }

    private int getCourseLevelPrivilegeIndex(String privilege) {
        switch (privilege) {
        case Const.InstructorPermissions.CAN_MODIFY_COURSE:
            return COURSE_MODIFY_COURSE;
        case Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR:
            return COURSE_MODIFY_INSTRUCTORS;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION:
            return COURSE_MODIFY_SESSIONS;
        case Const.InstructorPermissions.CAN_MODIFY_STUDENT:
            return COURSE_MODIFY_STUDENTS;
        case Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS:
            return COURSE_VIEW_STUDENTS;
        case Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS:
            return COURSE_GIVE_RESPONSES_IN_SESSION;
        case Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS:
            return COURSE_VIEW_RESPONSES_IN_SESSION;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS:
            return COURSE_MODIFY_RESPONSES_IN_SESSION;
        default:
            return -1;
        }
    }

    private int getSectionLevelPrivilegeIndex(String privilege) {
        switch (privilege) {
        case Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS:
            return SECTION_VIEW_STUDENTS;
        case Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS:
            return SECTION_GIVE_RESPONSES_IN_SESSION;
        case Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS:
            return SECTION_VIEW_RESPONSES_IN_SESSION;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS:
            return SECTION_MODIFY_RESPONSES_IN_SESSION;
        default:
            return -1;
        }
    }

    private int getSessionLevelPrivilegeIndex(String privilege) {
        switch (privilege) {
        case Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS:
            return SESSION_GIVE_RESPONSES;
        case Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS:
            return SESSION_VIEW_RESPONSES;
        case Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS:
            return SESSION_MODIFY_RESPONSES;
        default:
            return -1;
        }
    }

    private int getIntrNum(String email) {
        for (int i = 1; i <= getNumInstructors(); i++) {
            if (getInstructorEmail(i).equals(email)) {
                return i;
            }
        }
        return -1;
    }

    private int getSectionIndex(int instrNum, String section) {
        List<WebElement> accessLevelCheckboxes = browser.driver.findElements(
                By.cssSelector("#custom-access-instructor-" + instrNum + " #custom-sections div"));
        for (int i = 0; i < accessLevelCheckboxes.size(); i++) {
            if (accessLevelCheckboxes.get(i).getText().equals(section)) {
                return i;
            }
        }
        return -1;
    }

    private int getSessionIndex(int instrNum, String session) {
        List<WebElement> tableHeaders = browser.driver.findElements(
                By.cssSelector("#custom-access-instructor-" + instrNum + " tbody th"));
        for (int i = 0; i < tableHeaders.size(); i++) {
            if (tableHeaders.get(i).getText().equals(session)) {
                return i;
            }
        }
        return -1;
    }
}



