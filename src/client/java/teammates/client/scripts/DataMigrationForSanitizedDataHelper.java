package teammates.client.scripts;

public final class DataMigrationForSanitizedDataHelper {

    private DataMigrationForSanitizedDataHelper() {
        // utility class
    }

    /**
     * Returns true if string contains no characters to be sanitized and contains substrings of sanitized characters
     */
    public static boolean isSanitizedHtml(String string) {
        if (string == null) {
            return false;
        }
        if (isTextContainingAny(string, "<", ">", "\"", "/", "\'")) {
            return false;
        }
        return isTextContainingAny(string, "&lt;", "&gt;", "&quot;", "&#x2f;", "&#39;", "&amp;");
    }

    /**
     * Checks if text contains any of the strings given.
     * @return true if text contains at least one of the strings,
     * false if text does not contain any of the strings or if no strings are given.
     */
    public static boolean isTextContainingAny(String text, String... strings) {
        for (String string : strings) {
            if (text.contains(string)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Utility class to be used with loops to print messages regularly.
     */
    public static class LoopHelper {

        public final int printCycle;
        public final String itemName;
        private int count;

        public LoopHelper(int cycle, String name) {
            printCycle = cycle;
            itemName = name;
            count = 0;
        }

        /**
         * Increments count and prints the count on system output when count is a multiple of printCycle.
         */
        public void recordLoop() {
            count++;
            if (count % printCycle == 0) {
                System.out.println("On the " + count + "th " + itemName + ".");
            }
        }

        public int getCount() {
            return count;
        }

    }
}
