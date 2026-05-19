package teammates.common.util;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link HtmlHelper}.
 */
public class HtmlHelperTest extends BaseTestCase {

    @Test
    public void testHtmlToPlainText_nullAndEmpty() {
        Assertions.assertEquals("", HtmlHelper.htmlToPlainText(null));
        Assertions.assertEquals("", HtmlHelper.htmlToPlainText(""));
    }

    @Test
    public void testHtmlToPlainText_paragraphWithoutLinks() {
        Assertions.assertEquals("This is a test content",
                HtmlHelper.htmlToPlainText("<p>This is a test content</p>"));
    }

    @Test
    public void testHtmlToPlainText_linkWithDistinctText() {
        Assertions.assertEquals("submission link (https://example.com/submit)",
                HtmlHelper.htmlToPlainText("<a href=\"https://example.com/submit\">submission link</a>"));
    }

    @Test
    public void testHtmlToPlainText_linkTextEqualsHref() {
        Assertions.assertEquals("https://example.com/",
                HtmlHelper.htmlToPlainText("<a href=\"https://example.com/\">https://example.com/</a>"));
    }

    @Test
    public void testHtmlToPlainText_emptyLinkText() {
        Assertions.assertEquals("https://example.com/path",
                HtmlHelper.htmlToPlainText("<a href=\"https://example.com/path\"></a>"));
    }

    @Test
    public void testHtmlToPlainText_javascriptHrefUsesVisibleTextOnly() {
        Assertions.assertEquals("click me",
                HtmlHelper.htmlToPlainText("<a href=\"javascript:void(0)\">click me</a>"));
    }

    @Test
    public void testHtmlToPlainText_adjacentLinks() {
        String html = "<p><a href=\"https://a.test\">[submission link]</a><a href=\"https://b.test\">[result link]</a></p>";
        Assertions.assertEquals("[submission link] (https://a.test)[result link] (https://b.test)",
                HtmlHelper.htmlToPlainText(html));
    }
}
