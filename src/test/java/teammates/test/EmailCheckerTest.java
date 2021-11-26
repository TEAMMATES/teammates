package teammates.test;

import org.testng.annotations.Test;

import teammates.common.util.Config;

/**
 * SUT: {@link EmailChecker}.
 */
public class EmailCheckerTest {

    @Test
    public void testEmailContentChecking() throws Exception {
        String actual = FileHelper.readFile(TestProperties.TEST_EMAILS_FOLDER + "/sampleEmailActual.html");
        actual = injectContextDependentValuesForTest(actual);
        actual = EmailChecker.processEmailForComparison(actual);

        EmailChecker.verifyEmailContent(actual, "/sampleEmailExpected.html");
    }

    private String injectContextDependentValuesForTest(String emailContent) {
        return emailContent.replace("<!-- support.email -->", Config.SUPPORT_EMAIL)
                           .replace("<!-- app.url -->", getAppUrl());
    }

    private static String getAppUrl() {
        return Config.getFrontEndAppUrl("").toAbsoluteString();
    }

}
