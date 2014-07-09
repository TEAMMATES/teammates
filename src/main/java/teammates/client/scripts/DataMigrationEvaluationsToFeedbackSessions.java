package teammates.client.scripts;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;


import com.google.appengine.api.datastore.Text;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.datastore.Datastore;

public class DataMigrationEvaluationsToFeedbackSessions extends RemoteApiClient {

    /**
     * Issues:
     *  Who to put as fs creator email? Any random instructor in course?
     */
    
    protected static Logic logic = new Logic();
    protected static EvaluationsDb evalsDb = new EvaluationsDb();
    protected static FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    
    public static void main(String[] args) throws IOException {
        DataMigrationEvaluationsToFeedbackSessions migrator = new DataMigrationEvaluationsToFeedbackSessions();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();
        convertEvaluationsToFeedbackSessions();
    }
    
    @SuppressWarnings("deprecation")
    protected void convertEvaluationsToFeedbackSessions(){
        List<EvaluationAttributes> allEvaluations = evalsDb.getAllEvaluations();
        
        System.out.println(allEvaluations.size() + " evaluations found.");
        
        System.out.println("Original number of FS: " + fsDb.getAllFeedbackSessions().size());
        
        for(EvaluationAttributes evalAttribute : allEvaluations){
            convertOneEvaluationToFeedbackSession(evalAttribute);
        }
        
        System.out.println("After migration number of FS: " + fsDb.getAllFeedbackSessions().size());
    }
    
    protected void convertOneEvaluationToFeedbackSession(EvaluationAttributes eval){

        int num = 0;
        
        String courseId = eval.courseId;
        String creatorEmail = "damith@gmail.com"; //LOL
        Text instructions = eval.instructions;
        Date createdTime = (new Date()).compareTo(eval.startTime) > 0 ? new Date() : eval.startTime; //Now, or opening time if start time is earlier.
        Date startTime = eval.startTime;
        Date endTime = eval.endTime;
        Date sessionVisibleFromTime = eval.startTime;
        Date resultsVisibleFromTime = eval.endTime;
        double timeZone = eval.timeZone;
        int gracePeriod = eval.gracePeriod;
        FeedbackSessionType feedbackSessionType = FeedbackSessionType.STANDARD;
        boolean sentOpenEmail = (new Date()).compareTo(eval.startTime) > 0; //Assume sent openEmail if now > startTime
        boolean sentPublishedEmail = eval.published; //If eval is already published, assume email already sent.
        boolean isOpeningEmailEnabled = true; //Default value
        boolean isClosingEmailEnabled = true; //Default value
        boolean isPublishedEmailEnabled = true; //Default value
        
        while(true){ //Loop to retry if entity already exists.
            
            String feedbackSessionName = "Migrated - " + eval.name + (num==0 ? "" : ("("+num+")"));//Use same name, or if exists, use "<name>(<num>)"
            
            FeedbackSessionAttributes fsa = new FeedbackSessionAttributes(feedbackSessionName,
                    courseId, creatorEmail, instructions,
                    createdTime, startTime, endTime,
                    sessionVisibleFromTime, resultsVisibleFromTime,
                    timeZone, gracePeriod,
                    feedbackSessionType, sentOpenEmail, sentPublishedEmail,
                    isOpeningEmailEnabled, isClosingEmailEnabled, isPublishedEmailEnabled);
            
            try {
                fsDb.createEntity(fsa);
                break;
            } catch (InvalidParametersException e) {
                System.out.println("Something went wrong.");
                e.printStackTrace();
                break;
            } catch (EntityAlreadyExistsException e) {
                System.out.println("Entity already exists, retrying with a different name.");
                e.printStackTrace();
            }
            
            num++;
        }
        
        
        //Create feedback questions
        
        
        
    }

    protected PersistenceManager getPM() {
        return Datastore.getPersistenceManager();
    }
}
