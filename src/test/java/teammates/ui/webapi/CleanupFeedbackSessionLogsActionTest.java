package teammates.ui.webapi;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link CleanupFeedbackSessionLogsAction}.
 */
public class CleanupFeedbackSessionLogsActionTest extends BaseActionTest<CleanupFeedbackSessionLogsAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_SESSION_LOGS_CLEANUP;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
        verifyMaintainersCannotAccess();
    }

    @Test
    void testExecute_normalCase_shouldDeleteLogsOlderThanRetentionPeriod() {
        CleanupFeedbackSessionLogsAction action = getAction();
        action.execute();

        verify(mockLogic).deleteFeedbackSessionLogsOlderThan(argThat(cutoffTime ->
                Math.abs(cutoffTime.toEpochMilli()
                - Instant.now().minus(Const.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD).toEpochMilli())
                        < 5000));
    }
}
