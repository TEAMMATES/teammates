package teammates.test.cases.ui.browsertests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.pageobjects.AdminSearchPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminSearchPageUiTest extends BaseUiTestCase {
    public static final int ADMIN_SEARCH_INSTRUCTOR_TABLE_NUM_COLUMNS = 5;
    public static final int ADMIN_SEARCH_STUDENT_TABLE_NUM_COLUMNS = 6;
    
    private static Browser browser;
    private static AdminSearchPage searchPage;
    private static DataBundle testData;

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorSearchPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void allTests() {
        testContent();
        testSearch();
    }
    
    private void testContent() {
        
        ______TS("content: default search page");
        
        searchPage = getAdminSearchPage();
        
        assertTrue(isPageTitleCorrect());
        assertTrue(isSearchPanelPresent());
    }

    private void testSearch() {
        
        ______TS("search for nothing");
        
        String searchContent = "";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        
        assertTrue(isPageTitleCorrect());
        assertTrue(isSearchPanelPresent());
        searchPage.verifyStatus("Search key cannot be empty");
        
        ______TS("search for student1");
        
        searchPage.clearSearchBox();
        searchContent = "student1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        
        assertTrue(isSearchPanelPresent());
        assertTrue(isSearchDataDisplayCorrect());
        
        StudentAttributes student = testData.students.get("student1InCourse1");
        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        assertTrue(isStudentRowDisplayed(student, instructor));
        
        ______TS("search for student1 email");
        
        searchPage.clearSearchBox();
        searchContent = "searchUI.student1InCourse1@gmail.tmt";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        
        assertTrue(isSearchPanelPresent());
        assertTrue(isSearchDataDisplayCorrect());
        searchPage.verifyStatus("Total results found: 1");
        
        ______TS("search for student name with special characters");
        
        searchPage.clearSearchBox();
        searchContent = "student(1)";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        
        assertTrue(isSearchPanelPresent());
        assertTrue(isSearchDataDisplayCorrect());
    }

    private AdminSearchPage getAdminSearchPage() {
        AppUrl commentsPageUrl = createUrl(Const.ActionURIs.ADMIN_SEARCH_PAGE);

        return loginAdminToPage(browser, commentsPageUrl, AdminSearchPage.class);
    }
    
    private boolean isPageTitleCorrect() {
        return "Admin Search".equals(searchPage.getPageTitle());
    }
    
    private boolean isSearchPanelPresent() {
        return searchPage.isElementPresent(By.id("filterQuery"))
            && searchPage.isElementPresent(By.id("searchButton"));
    }
    
    /**
     * This method only checks if the search data tables are displayed correctly
     * i.e, table headers are correct, and appropriate message is displayed if no
     * search data is present.
     * It does not test for the table content
     */
    private boolean isSearchDataDisplayCorrect() {
        if (searchPage.isElementPresent(By.className("table"))) {
            int numSearchDataTables = browser.driver.findElements(By.className("table")).size();
            for (int i = 0; i < numSearchDataTables; i++) {
                if (!isSearchTableHeaderCorrect(i)) {
                    return false;
                }
            }
            return true;
        }
        searchPage.verifyStatus("No result found, please try again");
        return true;
        
    }
    
    private boolean isSearchTableHeaderCorrect(int tableNum) {
        List<String> expectedSearchTableHeaders;
        List<String> actualSessionTableHeaders;

        int numColumns = searchPage.getNumberOfColumnsFromDataTable(tableNum);
        
        switch (searchPage.getDataTableId(tableNum)) {
        // Instructor table
        case "search_table_instructor":
            if (numColumns != ADMIN_SEARCH_INSTRUCTOR_TABLE_NUM_COLUMNS) {
                return false;
            }
            expectedSearchTableHeaders = Arrays.asList("Course",
                                                       "Name",
                                                       "Google ID",
                                                       "Institute",
                                                       "Options");
            actualSessionTableHeaders = new ArrayList<String>();
            
            for (int i = 0; i < numColumns; i++) {
                actualSessionTableHeaders.add(searchPage.getHeaderValueFromDataTable(tableNum, 0, i));
            }
            
            break;
            
        // Student table
        case "search_table":
            if (numColumns != ADMIN_SEARCH_STUDENT_TABLE_NUM_COLUMNS) {
                return false;
            }
            expectedSearchTableHeaders = Arrays.asList("Institute",
                                                       "Course[Section](Team)",
                                                       "Name",
                                                       "Google ID[Details]",
                                                       "Comments",
                                                       "Options");
            actualSessionTableHeaders = new ArrayList<String>();
            for (int i = 0; i < numColumns; i++) {
                actualSessionTableHeaders.add(searchPage.getHeaderValueFromDataTable(tableNum, 0, i));
            }
            
            break;
            
        default:
            return false;
        }
        
        return actualSessionTableHeaders.equals(expectedSearchTableHeaders);
    }

    /**
     * @param student
     *            the student to be displayed
     * @param instructorToMasquaradeAs
     *            a registered instructor with co-owner privileges or the
     *            privilege to modify instructors
     * @return true if the student is displayed correctly in the student table,
     *         otherwise false
     */
    private boolean isStudentRowDisplayed(StudentAttributes student, InstructorAttributes instructorToMasquaradeAs) {

        By by = By.xpath("//table[@id = 'search_table']/tbody/tr[@class='studentRow']");
        List<WebElement> studentRows = browser.driver.findElements(by);
        
        for (WebElement studentRow : studentRows) {
            
            boolean isStudentCorrect = isStudentContentCorrect(studentRow, student)
                                       && isStudentLinkCorrect(studentRow, student, instructorToMasquaradeAs);
           
            if (isStudentCorrect) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * @param studentRow
     *            a row of the student table
     * @param student
     *            the student to be displayed
     * @return true if the {@code student}'s course, section, team, name,
     *         googleId and comment are displayed correctly, otherwise false
     */
    private boolean isStudentContentCorrect(WebElement studentRow, StudentAttributes student) {

        String actualDetails = studentRow.findElement(By.xpath("td[2]")).getText();
        String actualName = studentRow.findElement(By.xpath("td[3]")).getText();
        String actualGoogleId = studentRow.findElement(By.xpath("td[4]")).getText();
        String actualComment = studentRow.findElement(By.xpath("td[5]")).getText();

        String expectedDetails = student.course + "\n"
                                 + (student.section == null ? Const.DEFAULT_SECTION : student.section) + "\n"
                                 + student.team;
        String expectedName = student.name;
        String expectedGoogleId = StringHelper.convertToEmptyStringIfNull(student.googleId);
        String expectedComment = StringHelper.convertToEmptyStringIfNull(student.comments);

        return actualDetails.equals(expectedDetails)
               && actualName.equals(expectedName)
               && actualGoogleId.equals(expectedGoogleId)
               && actualComment.equals(expectedComment);
    }

    /**
     * @param studentRow
     *            a row of the student table
     * @param student
     *            the student to be displayed
     * @param instructorToMasquaradeAs
     *            a registered instructor with co-owner privileges or the
     *            privilege to modify instructors
     * @return true if the links associated with the {@code student}'s name and
     *         googleId (if he/she is registered) are correct, otherwise false
     */
    private boolean isStudentLinkCorrect(WebElement studentRow,
                                         StudentAttributes student,
                                         InstructorAttributes instructorToMasquaradeAs) {

        String actualNameLink = studentRow.findElement(By.xpath("td[3]/a")).getAttribute("href");
        String expectedNameLink = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                                  .withCourseId(student.course)
                                  .withStudentEmail(student.email)
                                  .withUserId(instructorToMasquaradeAs.googleId)
                                  .toAbsoluteString();

        if (student.isRegistered()) {

            String actualGoogleIdLink = studentRow.findElement(By.xpath("td[4]/a")).getAttribute("href");
            String expectedGoogleIdLink = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                                          .withUserId(student.googleId)
                                          .toAbsoluteString();

            return actualNameLink.equals(expectedNameLink)
                   && actualGoogleIdLink.equals(expectedGoogleIdLink);

        }
        return actualNameLink.equals(expectedNameLink);
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
}
