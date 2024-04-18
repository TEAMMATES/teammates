package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents the instructor course enrollment page.
 */
public class InstructorCourseEnrollPage extends AppPage {
    private static final int SPREADSHEET_NUM_STARTING_ROWS = 20;
    private static final int NUM_ENROLLMENT_ATTRIBUTES = 5;

    @FindBy(id = "enroll-header")
    private WebElement enrollHeader;

    @FindBy(id = "toggle-existing-students")
    private WebElement toggleExistingStudentsHeader;

    @FindBy(id = "existingStudentsHOT")
    private WebElement existingStudentsTable;

    @FindBy(id = "newStudentsHOT")
    private WebElement enrollSpreadsheet;

    @FindBy(id = "btn-enroll")
    private WebElement enrollButton;

    @FindBy(id = "results-panel")
    private WebElement resultsPanel;

    @FindBy(id = "btn-add-empty-rows")
    private WebElement addRowsButton;

    @FindBy(id = "number-of-rows")
    private WebElement addRowsInput;

    public InstructorCourseEnrollPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Enroll Students for");
    }

    public void verifyIsCorrectPage(String courseId) {
        assertEquals("Enroll Students for " + courseId, enrollHeader.getText());
    }

    public void clickToggleExistingStudentsHeader() {
        click(toggleExistingStudentsHeader);
        waitUntilAnimationFinish();
    }

    public void clickEnrollButton() {
        click(enrollButton);
    }

    public void clickAddButton() {
        click(addRowsButton);
        waitForPageToLoad();
    }

    public void addEnrollSpreadsheetRows(int numRows) {
        fillTextBox(addRowsInput, Integer.toString(numRows));
        clickAddButton();
    }

    public void verifyNumAddedEnrollSpreadsheetRows(int addedNumRows) {
        WebElement firstCell = getEnrollSpreadsheetFirstCell();
        scrollElementToCenterAndClick(firstCell);

        // make last row of spreadsheet visible
        Actions actions = new Actions(browser.driver);
        actions.sendKeys(Keys.PAGE_UP).perform();

        List<WebElement> indexCells = enrollSpreadsheet.findElements(By.cssSelector("span.rowHeader"));
        String lastIndexCellText = indexCells.get(indexCells.size() - 1).getAttribute("innerHTML");

        int expectedNumRows = addedNumRows + SPREADSHEET_NUM_STARTING_ROWS;
        assertEquals(lastIndexCellText, Integer.toString(expectedNumRows));

        // reset spreadsheet to original position
        actions.sendKeys(Keys.PAGE_DOWN).perform();
    }

    public void enroll(StudentAttributes[] studentsData) {
        fillEnrollSpreadsheet(getEnrollmentData(studentsData));
        waitForElementToBeClickable(enrollButton);
        clickEnrollButton();
    }

    public void verifyExistingStudentsTableContains(StudentAttributes[] expectedStudents) {
        clickToggleExistingStudentsHeader();
        verifyTableBodyValues(existingStudentsTable, getEnrollmentData(expectedStudents));
    }

    public void verifyResultsPanelContains(StudentAttributes[] expectedNewStudents,
                                           StudentAttributes[] expectedModifiedStudents,
                                           StudentAttributes[] expectedModifiedWithoutChangeStudents,
                                           StudentAttributes[] expectedErrorStudents,
                                           StudentAttributes[] expectedUnmodifiedStudents) {
        waitForElementVisibility(resultsPanel);
        // number of tables depends on what results are present
        int numTables = 0;
        List<WebElement> tables = resultsPanel.findElements(By.tagName("table"));
        if (expectedErrorStudents != null) {
            verifyTableBodyValues(tables.get(numTables++), getEnrollmentData(expectedErrorStudents));
        }
        if (expectedNewStudents != null) {
            verifyTableBodyValues(tables.get(numTables++), getEnrollmentData(expectedNewStudents));
        }
        if (expectedModifiedStudents != null) {
            verifyTableBodyValues(tables.get(numTables++), getEnrollmentData(expectedModifiedStudents));
        }
        if (expectedModifiedWithoutChangeStudents != null) {
            verifyTableBodyValues(tables.get(numTables++), getEnrollmentData(expectedModifiedWithoutChangeStudents));
        }
        if (expectedUnmodifiedStudents != null) {
            verifyTableBodyValues(tables.get(numTables), getEnrollmentData(expectedUnmodifiedStudents));
        }
    }

    private void fillEnrollSpreadsheet(String[][] expectedStudentData) {
        WebElement firstCell = getEnrollSpreadsheetFirstCell();
        scrollElementToCenterAndClick(firstCell);
        Actions actions = new Actions(browser.driver);
        for (String[] expectedRowData : expectedStudentData) {
            for (String expectedCellData : expectedRowData) {
                actions.sendKeys(expectedCellData + Keys.TAB).perform();
            }
        }
    }

    private WebElement getEnrollSpreadsheetFirstCell() {
        return enrollSpreadsheet.findElement(By.tagName("tbody")).findElement(By.tagName("td"));
    }

    private String[][] getEnrollmentData(StudentAttributes[] studentsData) {
        String[][] tableData = new String[studentsData.length][NUM_ENROLLMENT_ATTRIBUTES];
        for (int i = 0; i < studentsData.length; i++) {
            String[] student = {studentsData[i].getSection(), studentsData[i].getTeam(),
                    studentsData[i].getName(), studentsData[i].getEmail(), studentsData[i].getComments()};
            tableData[i] = student;
        }
        return tableData;
    }
}
