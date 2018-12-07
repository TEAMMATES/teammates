package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.test.pageobjects.AppPage;

/**
 * SUT: {@link AppPage}.
 */
public class AppPageUiTest extends BaseUiTestCase {
    private AppPage page;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() throws Exception {
        page = AppPage.getNewPageInstance(browser).navigateTo(createLocalUrl("/appVerifyTablePatternTestPage.html"));
    }

    @Test
    public void testVerifyTablePattern() {
        String patternString = "0{*}1{*}2{*}3{*}15{*}24{*}33";
        page.verifyTablePattern(0, patternString);
        page.verifyTablePattern(0, 0, patternString);

        patternString = "01 January 2012{*}01 January 2013{*}02 January 2012{*}01 February 2012{*}"
                      + "03 February 2012{*}12 December 2011{*}25 July 2012";
        page.verifyTablePattern(2, patternString);
        page.verifyTablePattern(0, 2, patternString);

        patternString = "+2%{*}+1%{*}+3%{*}0%{*}+5%{*}-1%{*}+25%";
        page.verifyTablePattern(4, patternString);
        page.verifyTablePattern(0, 4, patternString);

        patternString = "N/S{*}E -2%{*}E +99%{*}E 0%{*}E +20%{*}E 0%{*}E +20%{*}E +5%";
        page.verifyTablePattern(1, 3, patternString);

        patternString = "Test 1{*}Test n{*}Test 2{*}Test m{*}Test 10{*}Test q{*}Test 5{*}Test 8";
        page.verifyTablePattern(2, 1, patternString);

        // users of this API are not supposed to let the following case happen
        // empty table--trivial case, will not be tested
        // empty cell--will not be tested
        // cannot find table--will not be tested
        // cannot find columns--will not be tested
    }

    //TODO: add test cases for other methods in AppPage

}
