package teammates.client.scripts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.storage.datastore.Datastore;
import teammates.test.driver.TestProperties;
import teammates.common.util.FileHelper;

public class GenerateLargeScaledData extends RemoteApiClient{
    private static Logger logger = Logger.getLogger(GenerateLargeScaledData.class.getName());
    
    public static void main(String[] args) throws IOException {
        GenerateLargeScaledData dataGenerator = new GenerateLargeScaledData();
        dataGenerator.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize(); //TODO: push to parent class
        Logic logic = new Logic();
        DataBundle largeScaleBundle = loadDataBundle("/largeScaleTest.json");
        
        try{
            int index = 0;
            /*
            for(StudentAttributes student : largeScaleBundle.students.values()){
                logic.createStudent(student);
                index++;
                if(index % 100 == 0){
                    logger.info("Create student " + index);
                }
            }
            */
          
            for(FeedbackResponseAttributes response : largeScaleBundle.feedbackResponses.values()){
                logic.createFeedbackResponse(injectRealIds(response));
                index++;
                if(index % 100 == 0){
                    logger.info("Create response " + index);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private FeedbackResponseAttributes injectRealIds(FeedbackResponseAttributes response) {
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);
        
            response.feedbackQuestionId = 
                FeedbackQuestionsLogic.inst().getFeedbackQuestion(
                        response.feedbackSessionName, response.courseId,
                        qnNumber).getId();
        } catch (NumberFormatException e) {
            // Correct question ID was already attached to response.
        }
        
        return response;
    }
    
    private static DataBundle loadDataBundle(String pathToJsonFile){
        if(pathToJsonFile.startsWith("/")){
            pathToJsonFile = TestProperties.TEST_DATA_FOLDER + pathToJsonFile;
        }
        String jsonString;
        try {
            jsonString = FileHelper.readFile(pathToJsonFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Utils.getTeammatesGson().fromJson(jsonString, DataBundle.class);
    }
}
