package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.datastore.Datastore;

public class DataMigrationForResponseRate extends RemoteApiClient {
    
    private Logic logic = new Logic();
    private FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    
    
    // modify this value to choose to update respondants for all sessions or a specific session
    boolean isForAllSession = true; 
    // if modifying all sessions, modify this value to only update sessions with no respondants
    boolean isOnlyModifyingZeroResponseRate = true; 

    // modify for preview
    boolean isPreview = true;
    
    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();
        
        DataMigrationForResponseRate migrator = new DataMigrationForResponseRate();
        migrator.doOperationRemotely();
        
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
    }
    
    @Override
    protected void doOperation() {
        Datastore.initialize();
        
        if (isForAllSession) {
            updateRespondantsForAllSessions();
        } else {
            updateRespondantsForSession("Feedback Session Name", "Course ID"); // feedback session info
        }
    }

    
    @SuppressWarnings("deprecation")
    private void updateRespondantsForAllSessions(){
        List<FeedbackSessionAttributes> feedbackSessions;
        
        feedbackSessions = isOnlyModifyingZeroResponseRate ? 
                           getFeedbackSessionsWithZeroResponseRate() :
                           fsDb.getAllFeedbackSessions();
        
       for(FeedbackSessionAttributes session : feedbackSessions){
           updateRespondantsForSession(session.feedbackSessionName, session.courseId);
       }   
    }
    
    public List<FeedbackSessionAttributes> getFeedbackSessionsWithZeroResponseRate() {
        @SuppressWarnings("deprecation")
        List<FeedbackSessionAttributes> feedbackSessions = fsDb.getAllFeedbackSessions();
        
        List<FeedbackSessionAttributes> feedbackSessionsWithNoRespondants = new ArrayList<FeedbackSessionAttributes>();
        
        for(FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            if (feedbackSession.respondingStudentList.size() != 0 || feedbackSession.respondingInstructorList.size() != 0) {
                continue;
            }
            
            feedbackSessionsWithNoRespondants.add(feedbackSession);
        }
        
        return feedbackSessionsWithNoRespondants;
    }
    
    /* Operation for a specific session */
    private void updateRespondantsForSession(String feedbackSessionName, String courseId) {
        if (isPreview) {
            System.out.println("Modifying : [" + courseId + ": " + feedbackSessionName + "]"); 
            return;
        }
        
        try {
            logic.updateRespondants(feedbackSessionName, courseId);
            System.out.println("Successfully updated response rate for session " + feedbackSessionName + " in course " + courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            System.out.println("ERROR Failed to update respondants for session " + feedbackSessionName + " in course " + courseId);
            e.printStackTrace();
        }
    }
    
}
