package teammates.test.cases.testdriver;

import java.io.IOException;

import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.test.driver.EmailChecker;
import teammates.test.driver.FileHelper;
import teammates.test.driver.TestProperties;

/**
 * SUT: {@link EmailChecker}.
 */
public class EmailCheckerTest {

    @Test
    public void testEmailContentChecking() throws IOException {
        String actual = FileHelper.readFile(TestProperties.TEST_EMAILS_FOLDER + "/sampleEmailActual.html");
        actual = injectContextDependentValuesForTest(actual);
        actual = EmailChecker.processEmailForComparison(actual);

        EmailChecker.verifyEmailContent(actual, "/sampleEmailExpected.html");
    }

    private String injectContextDependentValuesForTest(String emailContent) {
        return emailContent.replace("<!-- support.email -->", Config.SUPPORT_EMAIL)
                           .replace("<!-- app.url -->", TestProperties.TEAMMATES_URL);
    }

}
