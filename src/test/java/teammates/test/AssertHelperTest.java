package teammates.test;

import org.testng.annotations.Test;

/**
 * SUT: {@link AssertHelper}.
 */
public class AssertHelperTest extends BaseTestCase {

    @Test
    public void testAssertContains() {

        AssertHelper.assertContains("404 Page Not Found",
                "Error: 404 Page Not Found. Check the URL.");
        AssertHelper.assertContains("Fails on checking assert contains",
                "404 Page Not Found",
                "Error: 404 Page Not Found. Check the URL.");
    }

    @Test
    public void testAssertContainsRegex() {

        AssertHelper.assertContainsRegex("404 Page Not Found",
                "Error: 404 Page Not Found. Check the URL.");
        AssertHelper.assertContainsRegex("Fails on checking assert contains regex",
                "404 Page Not Found",
                "Error: 404 Page Not Found. Check the URL.");

        AssertHelper.assertContainsRegex(
                "<div>{*}</div><p>!@#$%^&*(){}_+[]</p>",
                "<html><body><div>Testing</div><p>!@#$%^&*(){}_+[]</p><a href='index.html'>HOME</a></body></html>");
        AssertHelper.assertContainsRegex("Fails on checking assert contains regex",
                "<div>{*}</div>",
                "<html><body><div>Testing</div><a href='index.html'>HOME</a></body></html>");

        AssertHelper.assertContainsRegex(
                "<html>\n\t<body>\n\t\t<div{*}>Hello world!</div>\n\t</body>\n\t</html>",
                "<html><body><div style=\"display:none\">Hello world!</div></body></html>");
        AssertHelper.assertContainsRegex("Fails on checking assert contains regex",
                "<html>\n\t<body>\n\t\t<div{*}>Hello world!</div>\n\t</body>\n\t</html>",
                "<html><body><div style=\"display:none\">Hello world!</div></body></html>");
    }

}
