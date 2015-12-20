package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.pageobjects.AdminSearchPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.util.Url;

public class AdminSearchPageUiTest extends BaseUiTestCase {
    public static final int ADMIN_SEARCH_INSTRUCTOR_TABLE_NUM_COLUMNS = 5;
    public static final int ADMIN_SEARCH_STUDENT_TABLE_NUM_COLUMNS = 6;
    
    private static Browser browser;
    private static AdminSearchPage searchPage;
    private static DataBundle testData;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorSearchPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        putDocuments(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test 
    public void allTests() throws Exception{    
        testContent();
        testSearch();        
    }
    
    private void testContent() {
        
        ______TS("content: default search page");
        
        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        searchPage = getAdminSearchPage(instructorId);
        
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
        assertTrue(isEmptyKeyErrorMessageShown());
        
        ______TS("search for student1");
        
        searchPage.clearSearchBox();
        searchContent = "student1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        
        assertTrue(isSearchPanelPresent());
        assertTrue(isSearchDataDisplayCorrect());
    }

    private AdminSearchPage getAdminSearchPage(String instructorId) {
        Url commentsPageUrl = createUrl(Const.ActionURIs.ADMIN_SEARCH_PAGE);

        return loginAdminToPage(browser, commentsPageUrl, AdminSearchPage.class);
    }
    
    private boolean isPageTitleCorrect() {
        return searchPage.getPageTitle().equals("Admin Search");
    }
    
    private boolean isSearchPanelPresent() {
        return searchPage.isElementPresent(By.id("filterQuery"))
            && searchPage.isElementPresent(By.id("searchButton"));
    }
    
    private boolean isEmptyKeyErrorMessageShown() {
        String statusMessage = searchPage.getStatus();
        
        return statusMessage.equals("Search key cannot be empty");
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
            for (int i = 0 ; i < numSearchDataTables ; i++) {
                if (!isSearchTableHeaderCorrect(i)) {
                    return false;
                }
            }
            return true;
        } else {     
            String statusMessage = searchPage.getStatus();
            return statusMessage.equals("No result found, please try again");
        }
        
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
            
            for (int i = 0 ; i < numColumns ; i++) {
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
            for (int i = 0 ; i < numColumns ; i++) {
                actualSessionTableHeaders.add(searchPage.getHeaderValueFromDataTable(tableNum, 0, i));
            }
            
            break;
            
        default:
            return false;
        }
        
        return actualSessionTableHeaders.equals(expectedSearchTableHeaders);
    }

    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
