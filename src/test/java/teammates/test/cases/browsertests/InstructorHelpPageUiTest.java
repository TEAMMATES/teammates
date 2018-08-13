package teammates.test.cases.browsertests;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.InstructorHelpPage;

/**
 * SUT: {@link InstructorHelpPage}.
 */
public class InstructorHelpPageUiTest extends BaseUiTestCase {
    private static final By SEARCH_RESULTS = By.id("searchResults");
    private InstructorHelpPage helpPage;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        helpPage = AppPage.getNewPageInstance(browser, createUrl(Const.ActionURIs.INSTRUCTOR_HELP_PAGE + ".jsp"),
                                                InstructorHelpPage.class);
    }

    @Test
    public void testAll() throws Exception {
        testSearch();
    }

    private void testSearch() throws Exception {

        ______TS("content: default help page");

        helpPage.verifyHtmlMainContent("/instructorHelpPageDefault.html");

        ______TS("search for empty keyword");

        helpPage.clearSearchBox();
        helpPage.clickSearchButton();
        helpPage.verifyHtmlMainContent("/instructorHelpPageDefault.html");

        ______TS("search for single keyword");

        String searchContent = "course";
        helpPage.inputSearchQuery(searchContent);
        helpPage.clickSearchButton();
        helpPage.verifyHtmlPart(SEARCH_RESULTS, "/instructorHelpPageSearchSingleKeyword.html");

        ______TS("search for multiple keywords");

        searchContent = "enroll students";
        helpPage.clearSearchBox();
        helpPage.inputSearchQuery(searchContent);
        helpPage.clickSearchButton();
        helpPage.verifyHtmlPart(SEARCH_RESULTS, "/instructorHelpPageSearchMultipleKeywords.html");

        ______TS("check case insensitivity of query");

        searchContent = "eNroLL STUDENTs";
        helpPage.clearSearchBox();
        helpPage.inputSearchQuery(searchContent);
        helpPage.clickSearchButton();
        helpPage.verifyHtmlPart(SEARCH_RESULTS, "/instructorHelpPageSearchMultipleKeywords.html");

        ______TS("reset search");

        helpPage.clickResetButton();
        helpPage.verifyHtmlMainContent("/instructorHelpPageDefault.html");

        ______TS("search for non existing keyword");

        searchContent = "non existing keyword";
        helpPage.clearSearchBox();
        helpPage.inputSearchQuery(searchContent);
        helpPage.clickSearchButton();
        assertEquals("", helpPage.getSearchResults());
    }

}
