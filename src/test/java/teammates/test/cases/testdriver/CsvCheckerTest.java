package teammates.test.cases.testdriver;

import java.io.IOException;

import org.testng.annotations.Test;

import teammates.test.driver.CsvChecker;
import teammates.test.driver.FileHelper;
import teammates.test.driver.TestProperties;

/**
 * SUT: {@link CsvChecker}.
 */
public class CsvCheckerTest {

    @Test
    public void testCsvContentChecking() throws IOException {
        String actual = FileHelper.readFile(TestProperties.TEST_CSV_FOLDER + "/sampleCsvActual.csv");
        actual = CsvChecker.processCsvForComparison(actual);

        CsvChecker.verifyCsvContent(actual, "/sampleCsvExpected.csv");
    }

}
