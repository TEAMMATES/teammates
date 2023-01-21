package teammates.logic.external;

import org.testng.annotations.Test;

import com.google.cloud.logging.Severity;

import teammates.common.datatransfer.logs.LogSeverity;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link GoogleCloudLoggingService}.
 */
public class GoogleCloudLogginServiceTest extends BaseTestCase {

    GoogleCloudLoggingService google = new GoogleCloudLoggingService();

    /**
     * Tests severityTrue.
     */
    @Test
    public void testConvertSeverityTrue() {
        // Input: Severity.ERROR, expected output: LogSeverity.ERROR
        assertEquals(LogSeverity.ERROR, google.convertSeverity(Severity.ERROR));

        // Input: Severity.WARNING, expected output: LogSeverity.WARNING
        assertEquals(LogSeverity.WARNING, google.convertSeverity(Severity.WARNING));

        // Input: Severity.INFO, expected output: LogSeverity.INFO
        assertEquals(LogSeverity.INFO, google.convertSeverity(Severity.INFO));

        // Input: Severity.NOTICE, expected output: LogSeverity.INFO
        assertEquals(LogSeverity.INFO, google.convertSeverity(Severity.NOTICE));

        // Input: Severity.CRITICAL, expected output: LogSeverity.CRITICAL
        assertEquals(LogSeverity.CRITICAL, google.convertSeverity(Severity.CRITICAL));

        // Input: Severity.ALERT, expected output: LogSeverity.CRITICAL
        assertEquals(LogSeverity.CRITICAL, google.convertSeverity(Severity.ALERT));

        // Input: Severity.EMERGENCY, expected output: LogSeverity.CRITICAL
        assertEquals(LogSeverity.CRITICAL, google.convertSeverity(Severity.EMERGENCY));

        // Input: Severity.DEBUG, expected output: LogSeverity.DEBUG
        assertEquals(LogSeverity.DEBUG, google.convertSeverity(Severity.DEBUG));
    }

    /**
     * Tests severityFalse.
     */
    @Test
    public void testConvertSeverityFalse() {
        // Input: value not present in the Severity enumeration, expected output: LogSeverity.DEFAULT
        assertEquals(LogSeverity.DEFAULT, google.convertSeverity(null));
    }

}
