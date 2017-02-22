package teammates.test.driver;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;

/**
 * Checks email content and accounts for unpredictable values such as encrypted values,
 * support email, app URL.
 */
public final class EmailChecker {
    
    private static final String REGEX_ENCRYPTED_REGKEY = "[A-F0-9]{32,}";
    
    /** Regex, used for fetching information about inviter from email content. */
    private static final String INVITER_INFO_REGEX = 
            "for the course by (.+), who can be reached at (.+)\\.";
    private static final Pattern INVITER_INFO_PATTERN = Pattern.compile(INVITER_INFO_REGEX);
    
    private EmailChecker() {
        // Utility class
    }
    
    /**
     * Verifies that the given {@code emailContent} is the same as
     * the content given in the file at {@code filePathParam}. <br>
     * @param emailContent
     * @param filePathParam If this starts with "/" (e.g., "/expected.html"), the
     * folder is assumed to be {@link TestProperties.TEST_EMAILS_FOLDER}.
     */
    public static void verifyEmailContent(String emailContent, String filePathParam) throws IOException {
        String filePath = (filePathParam.startsWith("/") ? TestProperties.TEST_EMAILS_FOLDER : "") + filePathParam;
        String actual = processEmailForComparison(emailContent);
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
    
    /**
     * Verifies that given {@code emailContent} contains information about {@code inviter}.
     * 
     * @param inviter
     * @param emailContent
     * 
     * @see EmailGenerator#generateInstructorCourseJoinEmail()
     */
    public static void verifyEmailContainsInviterInfo(AccountAttributes inviter, String emailContent) {
        Matcher matcher = INVITER_INFO_PATTERN.matcher(emailContent);
        matcher.find();
        
        String actualName = matcher.group(1);
        String actualEmail = matcher.group(2);
        
        assertEquals(inviter.getName(), actualName);
        assertEquals(inviter.getEmail(), actualEmail);
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
    
    /**
     * Injects values specified in configuration files to the appropriate placeholders.
     */
    private static String injectTestProperties(String emailContent) {
        return emailContent.replace("${app.url}", Config.APP_URL)
                           .replace("${support.email}", Config.SUPPORT_EMAIL);
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
        return emailContent // regkey in URLs
                           .replaceAll(Const.ParamsNames.REGKEY + "=" + REGEX_ENCRYPTED_REGKEY,
                                       Const.ParamsNames.REGKEY + "=\\${regkey\\.enc}");

    }
    
    private static String replaceInjectedValuesWithPlaceholders(String emailContent) {
        return emailContent.replace(Config.APP_URL, "${app.url}")
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
