package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.api.FeedbackSessionsDb;

public class DataMigrationForResponseRate extends RemoteApiClient {

    private Logic logic = new Logic();
    private FeedbackSessionsDb fsDb = new FeedbackSessionsDb();

    // modify this value to choose to update respondents for all sessions or a specific session
    private boolean isForAllSession = true;
    // if modifying all sessions, modify this value to only update sessions with no respondents
    private boolean isOnlyModifyingZeroResponseRate = true;

    // modify for preview
    private boolean isPreview = true;

    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();

        DataMigrationForResponseRate migrator = new DataMigrationForResponseRate();
        migrator.doOperationRemotely();

        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
    }

    @Override
    protected void doOperation() {
        if (isForAllSession) {
            updateRespondentsForAllSessions();
        } else {
            updateRespondentsForSession("Feedback Session Name", "Course ID"); // feedback session info
        }
    }

    @SuppressWarnings("deprecation")
    private void updateRespondentsForAllSessions() {
        List<FeedbackSessionAttributes> feedbackSessions;

        feedbackSessions = isOnlyModifyingZeroResponseRate
                         ? getFeedbackSessionsWithZeroResponseRate()
                         : fsDb.getAllFeedbackSessions();

        for (FeedbackSessionAttributes session : feedbackSessions) {
            updateRespondentsForSession(session.getFeedbackSessionName(), session.getCourseId());
        }
    }

    private List<FeedbackSessionAttributes> getFeedbackSessionsWithZeroResponseRate() {
        @SuppressWarnings("deprecation")
        List<FeedbackSessionAttributes> feedbackSessions = fsDb.getAllFeedbackSessions();

        List<FeedbackSessionAttributes> feedbackSessionsWithNoRespondents = new ArrayList<>();

        for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            if (feedbackSession.getRespondingStudentList().size() != 0
                    || feedbackSession.getRespondingInstructorList().size() != 0) {
                continue;
            }

            feedbackSessionsWithNoRespondents.add(feedbackSession);
        }

        return feedbackSessionsWithNoRespondents;
    }

    /* Operation for a specific session */
    private void updateRespondentsForSession(String feedbackSessionName, String courseId) {
        if (isPreview) {
            System.out.println("Modifying : [" + courseId + ": " + feedbackSessionName + "]");
            return;
        }

        try {
            logic.updateRespondents(feedbackSessionName, courseId);
            System.out.println("Successfully updated response rate for session " + feedbackSessionName
                               + " in course " + courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            System.out.println("ERROR Failed to update respondents for session " + feedbackSessionName
                               + " in course " + courseId);
            e.printStackTrace();
        }
    }

}
