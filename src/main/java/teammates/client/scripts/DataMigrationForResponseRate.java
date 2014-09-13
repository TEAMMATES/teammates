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

public class DataMigrationForResponseRate extends RemoteApiClient {
    
    private Logic logic = new Logic();
    private FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    
    public static void main(String[] args) throws IOException {
        DataMigrationForResponseRate migrator = new DataMigrationForResponseRate();
        migrator.doOperationRemotely();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void doOperation() {
        List<FeedbackSessionAttributes> feedbackSessions = fsDb.getAllFeedbackSessions();
        try {
            for(FeedbackSessionAttributes session : feedbackSessions){
                logic.updateRespondants(session.feedbackSessionName, session.courseId);
                System.out.println("Successfully update response rate for session " + session.feedbackSessionName + " in course " + session.courseId);
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            Assumption.fail("Fail to update respondants");
        }
    }
    
    

}
