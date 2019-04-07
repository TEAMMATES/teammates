package teammates.test.driver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import teammates.common.util.Const;

/**
 * Checks CSV file content and accounts for unpredictable values such as hash values.
 */
public final class CsvChecker {

    private static final String REGEX_ANONYMOUS_PARTICIPANT_HASH = "[0-9]{1,10}";

    private CsvChecker() {
        // Utility class
    }

    /**
     * Verifies that the given {@code csvContent} is the same as
     * the content given in the file at {@code fileName}. <br>
     */
    public static void verifyCsvContent(String csvContent, String fileName) throws IOException {
        String filePath = TestProperties.TEST_CSV_FOLDER + fileName;
        String actual = processCsvForComparison(csvContent);
        try {
            String expected = FileHelper.readFile(filePath);
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

    private static boolean updateSnapshot(String filePath, String csvContent) throws IOException {
        return TestProperties.IS_SNAPSHOT_UPDATE && regenerateCsvFile(filePath, csvContent);
    }

    private static boolean regenerateCsvFile(String filePath, String csvContent) throws IOException {
        if (csvContent == null || csvContent.isEmpty()) {
            return false;
        }

        FileHelper.saveFile(filePath, csvContent + System.lineSeparator());
        return true;
    }

    /**
     * Processes the {@code csvContent} for comparison.
     */
    public static String processCsvForComparison(String csvContent) {
        return replaceUnpredictableValuesWithPlaceholders(csvContent);
    }

    /**
     * Substitutes values that are different across various test runs with placeholders.
     * These values are identified using their known, unique formats.
     */
    private static String replaceUnpredictableValuesWithPlaceholders(String csvContent) {
        return csvContent // anonymous participant names
                .replaceAll(
                        Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " (student|instructor|team) "
                                + REGEX_ANONYMOUS_PARTICIPANT_HASH,
                        Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " $1 \\${participant\\.hash}");

    }

}
