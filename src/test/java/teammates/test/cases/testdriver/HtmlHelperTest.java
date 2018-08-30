package teammates.test.cases.testdriver;

import java.io.IOException;

import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.FileHelper;
import teammates.test.driver.HtmlHelper;
import teammates.test.driver.TestProperties;

/**
 * SUT: {@link HtmlHelper}.
 */
public class HtmlHelperTest extends BaseTestCase {

    @Test
    public void testComparison() throws IOException {
        String expected = "<html></html>";
        String actual = expected;
        HtmlHelper.assertSameHtml(expected, actual, false);

        actual = "<html> </html>";
        HtmlHelper.assertSameHtml(expected, actual, false);

        expected = "<HTML><HEAD><SCRIPT language=\"JavaScript\" src=\"a.js\" ></SCRIPT></HEAD>"
                   + "<BODY id=\"5\"><P>abc</P><DIV id=\"frameBottom\"><DIV></DIV></DIV></BODY></HTML>";
        actual = expected.replace("<HEAD>", "    <HEAD>    \t" + System.lineSeparator());
        HtmlHelper.assertSameHtml(expected, actual, false);

        //change attribute order
        actual = expected.replace("language=\"JavaScript\" src=\"a.js\"", "  src=\"a.js\"   language=\"JavaScript\"  ");
        HtmlHelper.assertSameHtml(expected, actual, false);

        actual = expected.replace("<P>", "<P>\n\n" + System.lineSeparator() + System.lineSeparator());
        HtmlHelper.assertSameHtml(expected, actual, false);

        actual = expected.replace("<DIV></DIV></DIV>",
            System.lineSeparator() + System.lineSeparator() + "\n<DIV>\n\n</DIV></DIV>\n\n" + System.lineSeparator());
        HtmlHelper.assertSameHtml(expected, actual, false);

        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/sampleExpected.html");
        actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/sampleActual.html");
        HtmlHelper.assertSameHtml(expected, actual, false);

    }

    @Test
    public void testConvertToStandardHtml() throws Exception {

        //Tool tip in actual. Should not be ignored.
        String actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        String expected = "<html><head></head><body></body></html>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, false));

        //'<div>' without attributes (not a tool tip). Should not be ignored.
        actual = "<html><head></head><body><div></div></body></html>";
        expected = "<html><head></head><body></body></html>";
        assertFalse(HtmlHelper.areSameHtml(expected, actual, false));

        //Using a '<div>' that is not a tool tip. Should not be ignored.
        actual = "<html><head></head><body><div id=\"otherId\"></div></body></html>";
        expected = "<html><head></head><body></body></html>";
        assertFalse(HtmlHelper.areSameHtml(expected, actual, false));

        //Different tool tips. Will be ignored (the logic does not detect this).
        actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        expected = "<html><head></head><body><div class=\"tooltip\"></div></body></html>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, false));

        //Test against areSameHtmlPart
        actual = "<html><head><script></script></head><body><div></div></body></html>";
        expected = "<html><head></head><body><script></script><div></div></body></html>";
        assertFalse(HtmlHelper.areSameHtml(expected, actual, false));

        //Test against areSameHtmlPart
        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/sampleExpected.html");
        actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/sampleActualPart.html");
        assertFalse(HtmlHelper.areSameHtml(expected, actual, false));
    }

    @Test
    public void testConvertToStandardHtmlPart() throws Exception {

        //Tool tip in actual. Should not be ignored.
        String actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        String expected = "<html><head></head><body></body></html>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, true));

        //one part does not contain html tag. Should be the same
        actual = "<html><head></head><body><div></div></body></html>";
        expected = "<html><div></div></html>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, true));

        //one part does not contain head tag. Should be the same
        actual = "<html><head></head><body><div></div></body></html>";
        expected = "<html><body><div></div></body></html>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, true));

        //one part does not contain body tag. Should be the same
        actual = "<html><head></head><body><div></div></body></html>";
        expected = "<html><head></head><div></div></html>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, true));

        //Different tool tips. Will be ignored (the logic does not detect this)
        actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        expected = "<html><head></head><body><div class=\"tooltip\"></div></body></html>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, true));

        //Different tool tips and three tags(html, head, body) ignored. Should be the same
        actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        expected = "<div class=\"tooltip\"></div>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, true));

        //Contents inside div are different . Should be different
        actual = "<html><head></head><body><div>content<br> 2nd line </div></body></html>";
        expected = "<html><head></head><body><div></div></body></html>";
        assertFalse(HtmlHelper.areSameHtml(expected, actual, true));

        //Test against areSameHtml
        actual = "<html><head><script></script></head><body><div></div></body></html>";
        expected = "<html><head></head><body><script></script><div></div></body></html>";
        assertTrue(HtmlHelper.areSameHtml(expected, actual, true));

        //Same html structure
        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/sampleExpected.html");
        actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/sampleActual.html");
        HtmlHelper.assertSameHtml(expected, actual, true);

        //Same after ignoring html & head & body tag
        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/sampleExpected.html");
        actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/sampleActualPart.html");
        HtmlHelper.assertSameHtml(expected, actual, true);

        //other cases are tested in testComparison
    }

    @Test
    public void testReplacement() throws IOException {
        String actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/godmode.html");
        actual = HtmlHelper.injectContextDependentValuesForTest(actual);
        actual = HtmlHelper.processPageSourceForHtmlComparison(actual);

        String expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/godmodeExpectedOutput.html");
        expected = HtmlHelper.injectTestProperties(expected);
        HtmlHelper.assertSameHtml(expected, actual, false);

        // TODO consider adding a comparison after each HtmlHelper process separately

        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER + "/godmodeExpectedPartOutput.html");
        expected = HtmlHelper.injectTestProperties(expected);
        HtmlHelper.assertSameHtml(expected, actual, true);

        // HtmlHelper.replaceInjectedValuesWithPlaceholders is not tested here
        // but in GodModeTest as it is not used outside of expected HTML regeneration
    }

}
