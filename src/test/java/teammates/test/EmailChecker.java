package teammates.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Checks email content and accounts for unpredictable values such as encrypted values,
 * support email, app URL.
 */
public final class EmailChecker {

    private static final String REGEX_KEY = "[A-F0-9]{32,}";
    private static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    private EmailChecker() {
        // Utility class
    }

    /**
     * Verifies that the given {@code emailContent} is the same as
     * the content given in the file at {@code fileName}. <br>
     */
    public static void verifyEmailContent(String emailContent, String fileName) throws IOException {
        String filePath = TestProperties.TEST_EMAILS_FOLDER + fileName;
        String actual = processEmailForComparison(emailContent);
        try {
            String expected = FileHelper.readFile(filePath);
            expected = injectTestProperties(expected);
            if (!expected.equals(actual)) {
                assertEquals("<expected>" + System.lineSeparator() + expected + "</expected>",
                        "<actual>" + System.lineSeparator() + actual + "</actual>");
            }
        } catch (IOException | AssertionError e) {
            if (!updateSnapshot(filePath, actual)) {
                throw e;
            }
        }
    }

    private static boolean updateSnapshot(String filePath, String emailContent) throws IOException {
        return TestProperties.IS_SNAPSHOT_UPDATE && regenerateEmailFile(filePath, emailContent);
    }

    private static boolean regenerateEmailFile(String filePath, String emailContent) throws IOException {
        if (emailContent == null || emailContent.isEmpty()) {
            return false;
        }

        String processedEmailContent = processEmailForExpectedEmailRegeneration(emailContent) + System.lineSeparator();
        FileHelper.saveFile(filePath, processedEmailContent);
        return true;
    }

    /**
     * Injects values specified in configuration files to the appropriate placeholders.
     */
    private static String injectTestProperties(String emailContent) {
        return emailContent.replace("${app.url}", getAppUrl())
                           .replace("${support.email}", Config.SUPPORT_EMAIL);
    }

    private static String getAppUrl() {
        return Config.getFrontEndAppUrl("").toAbsoluteString();
    }

    /**
     * Processes the {@code emailContent} for comparison.
     */
    public static String processEmailForComparison(String emailContent) {
        return replaceUnpredictableValuesWithPlaceholders(emailContent);
    }

    /**
     * Substitutes values that are different across various test runs with placeholders.
     * These values are identified using their known, unique formats.
     */
    private static String replaceUnpredictableValuesWithPlaceholders(String emailContent) {
        return emailContent // key in URLs
                           .replaceAll(Const.ParamsNames.KEY + "=" + REGEX_KEY,
                                       Const.ParamsNames.KEY + "=\\${key\\.enc}")
                           // feedbackSessionId (UUID) in path-based URLs e.g. /sessions/{uuid}/submission
                           .replaceAll("(/sessions/)" + REGEX_UUID + "(/)",
                                       "$1\\${fsid}$2");

    }

    private static String replaceInjectedValuesWithPlaceholders(String emailContent) {
        return emailContent.replace(getAppUrl(), "${app.url}")
                           .replace(Config.SUPPORT_EMAIL, "${support.email}");
    }

    /**
     * Processes the {@code emailContent} string for regeneration of expected email content.<br>
     * Pre-condition: {@code emailContent} has previously been processed with the
     * {@link #processEmailForComparison} function.
     */
    private static String processEmailForExpectedEmailRegeneration(String emailContent) {
        return replaceInjectedValuesWithPlaceholders(emailContent);
    }

}
