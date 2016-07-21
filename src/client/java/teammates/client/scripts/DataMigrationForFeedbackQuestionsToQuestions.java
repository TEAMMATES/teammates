package teammates.client.scripts;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.logic.api.Logic;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.QuestionsDb;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to create a Question copy of old FeedbackQuestions.
 * 
 */
public class DataMigrationForFeedbackQuestionsToQuestions extends RemoteApiClient {
    
    private final boolean isPreview = true;
    
    private enum ScriptTarget {
        BY_TIME, BY_COURSE, ALL;
    }
    
    ScriptTarget target = ScriptTarget.BY_TIME;
    
    private final int numDays = 100;
    private final String courseId = "";
    
    
    public static void main(String[] args) throws IOException {
        new DataMigrationForFeedbackQuestionsToQuestions().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        
        Datastore.initialize();
        List<FeedbackQuestion> feedbackQuestions;
        if (target == ScriptTarget.BY_TIME) {
            Calendar startCal = Calendar.getInstance();
            startCal.add(Calendar.DAY_OF_YEAR, -1 * numDays);
            
            feedbackQuestions = getOldQuestionsSince(startCal.getTime());
            
        } else if (target == ScriptTarget.BY_COURSE) {
            feedbackQuestions = getFeedbackQuestionEntitiesForCourse(courseId);
            
        } else if (target == ScriptTarget.ALL) {
            feedbackQuestions = getAllOldQuestions();
            
        } else {
            feedbackQuestions = null;
            Assumption.fail("no target selected");
        }
        
        List<FeedbackQuestionAttributes> feedbackQuestionAttributes =
                FeedbackQuestionsDb.getListOfQuestionAttributes(feedbackQuestions);
        for (FeedbackQuestionAttributes old : feedbackQuestionAttributes) {
            FeedbackSessionAttributes session = new Logic().getFeedbackSession(old.getFeedbackSessionName(), old.getCourseId());
            if (session == null) {
                System.out.println("question: " + old.getIdentificationString());
                System.out.println(String.format("error finding session %s",
                                                 old.getFeedbackSessionName() + ":"
                                                 + old.getCourseId()));
                System.out.println("possibly due to orphaned responses");
                continue;
            }
            
            if (isPreview) {
                FeedbackQuestionAttributes existingQn =
                        new QuestionsDb().getFeedbackQuestion(old.feedbackSessionName, old.courseId, old.getId());
                if (existingQn == null) {
                    System.out.println("Will create question: " + old.getIdentificationString());
                } else {
                    System.out.println("new qn type already exists for question:" + existingQn.getIdentificationString());
                }
            } else {
                try {
                    new QuestionsDb().createFeedbackQuestion(session, old);
                } catch (EntityDoesNotExistException | InvalidParametersException e) {
                    e.printStackTrace();
                    throw new RuntimeException(
                            String.format("Unable to update existing session %s with question %s",
                                    session.getIdentificationString(),
                                    old.getIdentificationString()),
                                    e);
                } catch (EntityAlreadyExistsException e) {
                    // ignore if a copy of the old question already exists
                    System.out.println("new qn type already exists for question:" + old.getIdentificationString());
                }
            }
        }
        Datastore.getPersistenceManager().close();
    }

    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForCourse(String courseId) {
    
        Query q = Datastore.getPersistenceManager().newQuery(FeedbackQuestion.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestionList = (List<FeedbackQuestion>) q.execute(courseId);
        
        return feedbackQuestionList;
    }

    private List<FeedbackQuestion> getAllOldQuestions() {
        String query = "SELECT FROM " + FeedbackQuestion.class.getName();
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestions = 
            (List<FeedbackQuestion>) Datastore.getPersistenceManager().newQuery(query).execute();
        return feedbackQuestions;
    }
    
    private List<FeedbackQuestion> getOldQuestionsSince(Date date) {
        String query = "SELECT FROM " + FeedbackQuestion.class.getName()
                        + " WHERE updatedAt >= startDate"
                        + " PARAMETERS java.util.Date startDate";
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestions = 
            (List<FeedbackQuestion>) Datastore.getPersistenceManager().newQuery(query).execute(date);
        return feedbackQuestions;
    }
    
}