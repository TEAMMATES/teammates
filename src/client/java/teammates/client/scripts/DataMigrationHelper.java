package teammates.client.scripts;

public final class DataMigrationHelper {

    private static final int PRINT_CYCLE = 100;

    private DataMigrationHelper() {
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

    /**
     * Prints the count on system output when count is a multiple of PRINT_CYCLE
     */
    public static void printCountRegularly(int count, String itemName) {
        if (count % PRINT_CYCLE == 0) {
            System.out.println("On the " + count + "th " + itemName + ".");
        }
    }
}
