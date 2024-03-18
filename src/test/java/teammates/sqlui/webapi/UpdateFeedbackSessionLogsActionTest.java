package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackSessionLogEntry;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.ui.webapi.UpdateFeedbackSessionLogsAction;

/**
 * SUT: {@link UpdateFeedbackSessionLogsAction}.
 */
public class UpdateFeedbackSessionLogsActionTest
        extends BaseActionTest<UpdateFeedbackSessionLogsAction> {

    static final int COLLECTION_TIME_PERIOD = 60; // represents one hour
    static final long SPAM_FILTER = 2000L; // in ms

    String student1 = "student1";
    String student2 = "student2";

    String feedbackSession1 = "fs1";
    String feedbackSession2 = "fs2";

    Instant endTime;
    Instant startTime;

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_SESSION_LOGS_PROCESSING;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        endTime = TimeHelper.getInstantNearestHourBefore(Instant.now());
        startTime = endTime.minus(COLLECTION_TIME_PERIOD, ChronoUnit.MINUTES);
        mockLogsProcessor.getOrderedFeedbackSessionLogs("", "", 0, 0, "").clear();
    }

    @Test
    public void testExecute_noRecentLogs_noLogsCreated()
            throws EntityAlreadyExistsException, InvalidParametersException {
        UpdateFeedbackSessionLogsAction action = getAction();
        action.execute();

        verify(mockLogic)
                .createFeedbackSessionLogs(argThat(filteredLogs -> filteredLogs.size() == 0));
    }

    @Test
    public void testExecute_recentLogsNoSpam_allLogsCreated()
            throws EntityAlreadyExistsException, InvalidParametersException {
        // Different Types
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(300).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.SUBMISSION.getLabel(),
                startTime.plusSeconds(300).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.VIEW_RESULT.getLabel(),
                startTime.plusSeconds(300).toEpochMilli());

        // Different feedback sessions
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(600).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession2,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(600).toEpochMilli());

        // Different Student
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(900).toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(student2, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusSeconds(900).toEpochMilli());

        // Gap is larger than spam filter
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli());

        UpdateFeedbackSessionLogsAction action = getAction();
        action.execute();

        // method returns all logs regardless of params
        List<FeedbackSessionLogEntry> expected =
                mockLogsProcessor.getOrderedFeedbackSessionLogs("", "", 0, 0, "");

        verify(mockLogic).createFeedbackSessionLogs(
                argThat(filteredLogs -> isEqualExceptId(expected, filteredLogs)));
    }

    @Test
    public void testExecute_recentLogsWithSpam_someLogsCreated()
            throws EntityAlreadyExistsException, InvalidParametersException {
        // Gap is smaller than spam filter
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.toEpochMilli());
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER - 2).toEpochMilli());

        // Filters multiple logs within one spam window
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER - 1).toEpochMilli());

        // Correctly adds new log after filtering
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli());

        // Filters out spam in the new window
        mockLogsProcessor.insertFeedbackSessionLog(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER + 2).toEpochMilli());

        UpdateFeedbackSessionLogsAction action = getAction();
        action.execute();

        List<FeedbackSessionLogEntry> expected = new ArrayList<>();
        expected.add(new FeedbackSessionLogEntry(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(), startTime.toEpochMilli()));
        expected.add(new FeedbackSessionLogEntry(student1, feedbackSession1,
                FeedbackSessionLogType.ACCESS.getLabel(),
                startTime.plusMillis(SPAM_FILTER + 1).toEpochMilli()));

        verify(mockLogic).createFeedbackSessionLogs(
                argThat(filteredLogs -> isEqualExceptId(expected, filteredLogs)));
    }

    @Test
    public void testSpecificAccessControl_isAdmin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    public void testSpecificAccessControl_isInstructor_cannotAccess() {
        loginAsInstructor("user-id");
        verifyCannotAccess();
    }

    @Test
    public void testSpecificAccessControl_isStudent_cannotAccess() {
        loginAsStudent("user-id");
        verifyCannotAccess();
    }

    @Test
    public void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    private Boolean isEqualExceptId(List<FeedbackSessionLogEntry> expected,
            List<FeedbackSessionLog> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            FeedbackSessionLogEntry expectedEntry = expected.get(i);
            FeedbackSessionLog actualLog = actual.get(i);

            if (!expectedEntry.getStudentEmail().equals(actualLog.getStudentEmail())) {
                return false;
            }
            if (!expectedEntry.getFeedbackSessionName()
                    .equals(actualLog.getFeedbackSessionName())) {
                return false;
            }
            if (!expectedEntry.getFeedbackSessionLogType()
                    .equals(actualLog.getFeedbackSessionLogType().getLabel())) {
                return false;
            }
            if (expectedEntry.getTimestamp() != actualLog.getTimestamp().toEpochMilli()) {
                return false;
            }
        }

        return true;
    }
}
