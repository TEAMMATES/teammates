package teammates.test.cases.browsertests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.test.pageobjects.AdminSearchPage;

/**
 * SUT: {@link Const.ActionURIs#ADMIN_SEARCH_PAGE}.
 */
public class AdminSearchPageUiTest extends BaseUiTestCase {
    private static final int ADMIN_SEARCH_INSTRUCTOR_TABLE_NUM_COLUMNS = 5;
    private static final int ADMIN_SEARCH_STUDENT_TABLE_NUM_COLUMNS = 6;

    private AdminSearchPage searchPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorSearchPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    public void allTests() {
        testContent();
        testSearch();
        testSanitization();
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
        searchPage.waitForTextsForAllStatusMessagesToUserEquals("Search key cannot be empty");

        ______TS("search for student1");

        searchPage.clearSearchBox();
        searchContent = "student1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();

        assertTrue(isSearchPanelPresent());
        assertTrue(isSearchDataDisplayCorrect());

        StudentAttributes student = testData.students.get("student1InCourse1");
        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        CourseAttributes course = testData.courses.get("typicalCourse1");
        assertStudentRowDisplayed(student, instructor, course);

        ______TS("search for student1 email");

        searchPage.clearSearchBox();
        searchContent = "searchUI.student1InCourse1@gmail.tmt";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();

        assertTrue(isSearchPanelPresent());
        assertTrue(isSearchDataDisplayCorrect());
        searchPage.waitForTextsForAllStatusMessagesToUserEquals("Total results found: 1");

        ______TS("search for student name with special characters");

        searchPage.clearSearchBox();
        searchContent = "student(1)";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();

        assertTrue(isSearchPanelPresent());
        assertTrue(isSearchDataDisplayCorrect());
    }

    private void testSanitization() {
        ______TS("search for student with data requiring sanitization");

        searchPage.clearSearchBox();
        String searchContent = "searchUI.normal@sanitization.tmt";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();

        StudentAttributes student = testData.students.get("student1InTestingSanitizationCourse");
        InstructorAttributes instructor = testData.instructors.get("instructor1OfTestingSanitizationCourse");
        CourseAttributes course = testData.courses.get("testingSanitizationCourse");
        assertStudentRowDisplayed(student, instructor, course);

        ______TS("search for instructor with data requiring sanitization");

        searchPage.clearSearchBox();
        searchContent = "searchUI.instructor1@sanitization.tmt";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();

        assertInstructorRowDisplayed(instructor, course);
    }

    private AdminSearchPage getAdminSearchPage() {
        AppUrl searchPageUrl = createUrl(Const.ActionURIs.ADMIN_SEARCH_PAGE);
        return loginAdminToPage(searchPageUrl, AdminSearchPage.class);
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
        searchPage.waitForTextsForAllStatusMessagesToUserEquals("No result found, please try again");
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
            actualSessionTableHeaders = new ArrayList<>();

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
            actualSessionTableHeaders = new ArrayList<>();
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
     * Returns true if the student is displayed correctly in the student table.
     *
     * @param student
     *            the student to be displayed
     * @param instructorToMasquaradeAs
     *            a registered instructor with co-owner privileges or the
     *            privilege to modify instructors
     */
    private void assertStudentRowDisplayed(StudentAttributes student, InstructorAttributes instructorToMasquaradeAs,
                                              CourseAttributes course) {

        WebElement studentRow = searchPage.getStudentRow(student);

        assertStudentContentCorrect(studentRow, student, course);
        assertTrue(isStudentLinkCorrect(studentRow, student, instructorToMasquaradeAs));
    }

    /**
     * Returns true if the {@code student}'s course, section, team, name,
     * googleId and comment are displayed correctly.
     *
     * @param studentRow
     *            a row of the student table
     * @param student
     *            the student to be displayed
     */
    private void assertStudentContentCorrect(WebElement studentRow, StudentAttributes student, CourseAttributes course) {

        String actualDetails = studentRow.findElement(By.xpath("td[2]")).getText();
        String actualCourseName = studentRow.findElement(By.xpath("td[2]")).getAttribute("data-original-title");
        String actualName = studentRow.findElement(By.xpath("td[3]")).getText();
        String actualGoogleId = studentRow.findElement(By.xpath("td[4]")).getText();
        String actualComment = studentRow.findElement(By.xpath("td[5]")).getText();

        String expectedDetails = student.course + "\n"
                                 + (student.section == null ? Const.DEFAULT_SECTION : student.section) + "\n"
                                 + student.team;
        // courseName resides in tooltip and is expected to be sanitized in the attribute
        // adjustments made based on differences in sanitization used in fn:escapeXml
        String expectedCourseName = sanitizeWithFnEscapeXml(course.getName());
        String expectedName = student.name;
        String expectedGoogleId = StringHelper.convertToEmptyStringIfNull(student.googleId);
        String expectedComment = StringHelper.convertToEmptyStringIfNull(student.comments);

        assertEquals(expectedDetails, actualDetails);
        assertEquals(expectedCourseName, actualCourseName);
        assertEquals(expectedName, actualName);
        assertEquals(expectedGoogleId, actualGoogleId);
        assertEquals(expectedComment, actualComment);
    }

    /**
     * Returns true if the links associated with the {@code student}'s name and
     * googleId (if he/she is registered) are correct.
     *
     * @param studentRow
     *            a row of the student table
     * @param student
     *            the student to be displayed
     * @param instructorToMasquaradeAs
     *            a registered instructor with co-owner privileges or the
     *            privilege to modify instructors
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

    /**
     * Returns true if the instructor is displayed correctly in the instructor table.
     *
     * @param instructor                  the instructor to be displayed
     */
    private void assertInstructorRowDisplayed(InstructorAttributes instructor, CourseAttributes course) {

        WebElement instructorRow = searchPage.getInstructorRow(instructor);

        String actualCourseId = instructorRow.findElement(By.xpath("td[1]")).getText();
        String actualCourseName = instructorRow.findElement(By.xpath("td[1]")).getAttribute("data-original-title");
        String actualName = instructorRow.findElement(By.xpath("td[2]")).getText();
        String actualGoogleId = instructorRow.findElement(By.xpath("td[3]")).getText();

        String expectedCourseId = instructor.courseId;
        // courseName resides in tooltip and is expected to be sanitized in the attribute
        // adjustments made based on differences in sanitization used in fn:escapeXml
        String expectedCourseName = sanitizeWithFnEscapeXml(course.getName());
        String expectedName = instructor.name;
        String expectedGoogleId = StringHelper.convertToEmptyStringIfNull(instructor.googleId);

        assertEquals(expectedCourseId, actualCourseId);
        assertEquals(expectedCourseName, actualCourseName);
        assertEquals(expectedName, actualName);
        assertEquals(expectedGoogleId, actualGoogleId);
    }

    private String sanitizeWithFnEscapeXml(String string) {
        return SanitizationHelper.sanitizeForHtml(string)
                .replace("&#39;", "&#039;")
                .replace("&quot;", "&#034;")
                .replace("&#x2f;", "/");
    }
}
