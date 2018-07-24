package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.test.pageobjects.InstructorHelpPage;

/**
 * SUT: {@link InstructorHelpPage}.
 */
public class InstructorHelpPageUiTest extends BaseUiTestCase {
    private InstructorHelpPage helpPage;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        helpPage = getInstructorHelpPage();
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
        helpPage.verifyHtmlMainContent("/instructorHelpPageSearchSingleKeyword.html");

        ______TS("search for multiple keywords");

        searchContent = "enroll students";
        helpPage.clearSearchBox();
        helpPage.inputSearchQuery(searchContent);
        helpPage.clickSearchButton();
        String searchResultsMultipleKeywords = helpPage.getSearchResults();
        helpPage.verifyHtmlMainContent("/instructorHelpPageSearchMultipleKeywords.html");

        ______TS("check case insensitivity of query");

        searchContent = "eNroLL STUDENTs";
        helpPage.clearSearchBox();
        helpPage.inputSearchQuery(searchContent);
        helpPage.clickSearchButton();
        assertEquals(searchResultsMultipleKeywords, helpPage.getSearchResults());

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
