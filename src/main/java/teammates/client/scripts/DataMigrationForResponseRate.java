package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.logic.api.Logic;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.datastore.Datastore;

public class DataMigrationForResponseRate extends RemoteApiClient {
    
    private Logic logic = new Logic();
    private FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    
    public static void main(String[] args) throws IOException {
        DataMigrationForResponseRate migrator = new DataMigrationForResponseRate();
        migrator.doOperationRemotely();
    }
    
    @Override
    protected void doOperation() {
        Datastore.initialize();
        
        boolean isForAllSession = true; // modify this value to choose to update respondants for all sessions or a specific session

        if(isForAllSession){
            updateRespondantsForAllSessions();
        } else {
            updateRespondantsForSession("Feedback Session Name", "Course ID"); // feedback session info
        }
    }

    /* Operation for a specific session */
    @SuppressWarnings("deprecation")
    private void updateRespondantsForAllSessions(){
        List<FeedbackSessionAttributes> feedbackSessions = fsDb.getAllFeedbackSessions();
        for(FeedbackSessionAttributes session : feedbackSessions){
            updateRespondantsForSession(session.feedbackSessionName, session.courseId);
        }   
    }

    private void updateRespondantsForSession(String feedbackSessionName, String courseId) {
        try {
            logic.updateRespondants(feedbackSessionName, courseId);
            System.out.println("Successfully update response rate for session " + feedbackSessionName + " in course " + courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            System.out.println("Fail to update respondants for session " + feedbackSessionName + " in course " + courseId);
        }
    }
    
    

}
