package teammates.test.driver;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.test.util.FileHelper;

public final class EmailChecker {
    
    private static final String REGEX_ENCRYPTED_REGKEY = "[A-F0-9]{32,}";
    
    private EmailChecker() {
        // Utility class
    }
    
    /**
     * Verifies that the given {@code email} has the same content as the content
     * the content given in the file at {@code filePathParam}. <br>
     * @param email
     * @param filePathParam If this starts with "/" (e.g., "/expected.html"), the
     * folder is assumed to be {@link TestProperties.TEST_EMAILS_FOLDER}.
     */
    public static void verifyEmailContent(EmailWrapper email, String filePathParam) throws IOException {
        String filePath = (filePathParam.startsWith("/") ? TestProperties.TEST_EMAILS_FOLDER : "") + filePathParam;
        String actual = email.getContent();
        actual = replaceUnpredictableValuesWithPlaceholders(actual);
        try {
            String expected = FileHelper.readFile(filePath);
            expected = injectTestProperties(expected);
            assertEquals(expected, actual);
        } catch (IOException | AssertionError e) {
            if (!testAndRunGodMode(filePath, actual)) {
                throw e;
            }
        }
    }
    
    private static boolean testAndRunGodMode(String filePath, String emailContent) throws IOException {
        return Boolean.parseBoolean(System.getProperty("godmode")) && regenerateEmailFile(filePath, emailContent);
    }
    
    private static boolean regenerateEmailFile(String filePath, String emailContent) throws IOException {
        if (emailContent == null || emailContent.isEmpty()) {
            return false;
        }
        
        String processedEmailContent = processEmailForExpectedEmailRegeneration(emailContent);
        FileHelper.saveFile(filePath, processedEmailContent);
        return true;
    }
    
    private static String injectTestProperties(String content) {
        return content.replace("${app.url}", Config.APP_URL)
                      .replace("${support.email}", Config.SUPPORT_EMAIL);
    }
    
    private static String replaceUnpredictableValuesWithPlaceholders(String content) {
        return content // regkey in URLs
                      .replaceAll(Const.ParamsNames.REGKEY + "=" + REGEX_ENCRYPTED_REGKEY,
                                  Const.ParamsNames.REGKEY + "=\\${regkey\\.enc}");

    }
    
    private static String replaceInjectedValuesWithPlaceholders(String content) {
        return content // URL used as the base for links sent via email
                      .replace(Config.APP_URL, "${app.url}")
                      .replace(Config.SUPPORT_EMAIL, "${support.email}");
    }
    
    private static String processEmailForExpectedEmailRegeneration(String email) {
        return replaceInjectedValuesWithPlaceholders(
                      replaceUnpredictableValuesWithPlaceholders(email));
    }
    
}
