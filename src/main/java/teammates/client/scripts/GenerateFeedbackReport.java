package teammates.client.scripts;


import java.io.IOException;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.logic.api.Logic;
import teammates.storage.datastore.Datastore;
import teammates.test.util.FileHelper;

/**
 * Generates the feedback report as a csv.
 */
public class GenerateFeedbackReport extends RemoteApiClient {
    
    
    public static void main(String[] args) throws IOException {
        GenerateFeedbackReport reportGenerator = new GenerateFeedbackReport();
        reportGenerator.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize(); //TODO: push to parent class
        Logic logic = new Logic();
        
        try {
            String fileContent = logic.getFeedbackSessionResultSummaryAsCsv("CourseID", "Session Name", "instructor@email.com");
            FileHelper.writeToFile("result.csv",fileContent);
        } catch (EntityDoesNotExistException | ExceedingRangeException e) {
            e.printStackTrace();
        }
        
    }
    
}