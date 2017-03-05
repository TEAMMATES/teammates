package teammates.client.scripts;

public final class DataMigrationSanitizationHelper {

    private DataMigrationSanitizationHelper() {
        // utility class
    }

    /**
     * Returns true if string contains no characters to be sanitized and contains substrings of sanitized characters
     */
    public static boolean isSanitizedHtml(String string) {
        if (string == null) {
            return false;
        }
        if (string.indexOf('<') >= 0 || string.indexOf('>') >= 0 || string.indexOf('\"') >= 0
                || string.indexOf('/') >= 0 || string.indexOf('\'') >= 0) {
            return false;
        }
        return string.indexOf("&lt;") >= 0 || string.indexOf("&gt;") >= 0 || string.indexOf("&quot;") >= 0
               || string.indexOf("&#x2f;") >= 0 || string.indexOf("&#39;") >= 0 || string.indexOf("&amp;") >= 0;
    }
}
