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
    
    /**
     * 
     * BY_TIME: migration will affect questions created in the past {@code numDays} days
     * BY_COURSE: migration will affects questions in the specified {@code courseId}
     * ALL: all questions will be migrated
     */
    private enum ScriptTarget {
        BY_TIME, BY_COURSE, ALL;
    }
    
    private static final boolean isPreview = true;
    
    private static final ScriptTarget target = ScriptTarget.ALL;
    
    // When using ScriptTarget.BY_TIME, numDays can be changed to target
    // questions created in the past number of days
    private static final int numDays = 100;
    
    // When using ScriptTarget.BY_COURSE, specify the course to target with courseId
    private static final String courseId = "";
    
    public static void main(String[] args) throws IOException {
        new DataMigrationForFeedbackQuestionsToQuestions().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        
        Datastore.initialize();
        List<FeedbackQuestionAttributes> feedbackQuestions = getFeedbackQuestionsToMigrate(target);
        int studentsSize = feedbackQuestions.size();
        System.out.println("Size of feedbackQuestions = " + studentsSize);
        
        int i = 0;
        for (FeedbackQuestionAttributes old : feedbackQuestions) {
            i += 1;
            if (i % 100 == 0) {
                System.out.println("Question " + i + " out of " + studentsSize);
            }
            FeedbackSessionAttributes session =
                    new Logic().getFeedbackSession(old.getFeedbackSessionName(), old.getCourseId());
            if (session == null) {
                printErrorMessageForOrphanedQuestion(old);
                continue;
            }
            
            if (isPreview) {
                previewMigratedQuestion(old);
            } else {
                migrateQuestionInSession(old, session);
            }
        }
        System.out.println("Completed :)");
    }

    private void printErrorMessageForOrphanedQuestion(FeedbackQuestionAttributes old) {
        System.out.println("Question: " + old.getIdentificationString());
        System.out.println(String.format("Error finding session %s",
                                         old.getFeedbackSessionName() + ":"
                                         + old.getCourseId()));
        System.out.println("possibly due to orphaned responses :(");
    }

    /**
     * Creates question in the given session
     */
    private void migrateQuestionInSession(FeedbackQuestionAttributes old,
                                          FeedbackSessionAttributes session) {
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
            System.out.println("New question type entity already exists for question: "
                               + old.getIdentificationString());
        }
    }

    private void previewMigratedQuestion(FeedbackQuestionAttributes old) {
        FeedbackQuestionAttributes existingQn =
                new QuestionsDb().getFeedbackQuestion(old.feedbackSessionName, old.courseId, old.getId());
        if (existingQn == null) {
            System.out.println("Will create question: " + old.getIdentificationString() + "!");
        } else {
            System.out.println("New question type entity already exists for question: "
                               + existingQn.getIdentificationString());
        }
    }

    private List<FeedbackQuestionAttributes> getFeedbackQuestionsToMigrate(ScriptTarget target) {
        List<FeedbackQuestion> feedbackQuestionEntities;
        if (target == ScriptTarget.BY_TIME) {
            Calendar startCal = Calendar.getInstance();
            startCal.add(Calendar.DAY_OF_YEAR, -1 * numDays);
            
            feedbackQuestionEntities = getOldQuestionsSince(startCal.getTime());
            
        } else if (target == ScriptTarget.BY_COURSE) {
            feedbackQuestionEntities = getFeedbackQuestionEntitiesForCourse(courseId);
            
        } else if (target == ScriptTarget.ALL) {
            feedbackQuestionEntities = getAllOldQuestions();
            
        } else {
            feedbackQuestionEntities = null;
            Assumption.fail("no target selected");
        }
        
        return FeedbackQuestionsDb.getListOfQuestionAttributes(feedbackQuestionEntities);
    }

    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForCourse(String courseId) {
    
        Query q = Datastore.getPersistenceManager().newQuery(FeedbackQuestion.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestions = (List<FeedbackQuestion>) q.execute(courseId);
        
        return feedbackQuestions;
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
