package teammates.client.scripts;

import java.io.IOException;
import java.util.List;
import javax.jdo.PersistenceManager;
import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.StringHelper;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.FeedbackSession;

/**
 * Previews and removes extra spaces in FeedbackSessionAttributes, FeedbackResponseAttributes,
 * FeedbackQuestionAttributes and FeedbackResponseCommentAttribute.
 */
public class RepairFeedbackSessionNameWithExtraWhiteSpace extends RemoteApiClient {
    private final boolean isPreview = true;
    
    private FeedbackSessionsDb feedbackSessionsDb = new FeedbackSessionsDb();
    private FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
    private FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
    private FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
    
    public static void main(String[] args) throws IOException {
        RepairFeedbackSessionNameWithExtraWhiteSpace migrator = new RepairFeedbackSessionNameWithExtraWhiteSpace();
        migrator.doOperationRemotely();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void doOperation() {
        Datastore.initialize();
        
        List<FeedbackSession> feedbackSessionList = feedbackSessionsDb.getAllFeedbackSessionEntities();
        System.out.println("There is/are " + feedbackSessionList.size() + " session(s).");
        
        if (isPreview) {
            System.out.println("Checking extra spaces in feedback session name...");
        } else {
            System.out.println("Removing extra spaces in feedback session name...");
        }
        
        try {
            int numberOfFeedbackSessionWithExtraWhiteSpacesInName = 0;
            for (FeedbackSession session : feedbackSessionList) {
                if (hasExtraSpaces(session.getFeedbackSessionName())) {
                    numberOfFeedbackSessionWithExtraWhiteSpacesInName++;
                    if (isPreview) {
                        showFeedbackSession(session);
                    } else {
                        fixFeedbackSession(session);
                    }
                }
            }
            
            if (isPreview) {
                System.out.println("There are/is " + numberOfFeedbackSessionWithExtraWhiteSpacesInName
                                   + "/" + feedbackSessionList.size() + " feedback session(s) with extra spaces in name!");
            } else {
                System.out.println("" + numberOfFeedbackSessionWithExtraWhiteSpacesInName
                                   + "/" + feedbackSessionList.size() + " feedback session(s) have been fixed!");
                System.out.println("Extra space removing done!");
            }
        } catch (InvalidParametersException | EntityDoesNotExistException | EntityAlreadyExistsException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Displays the feedback session.
     */
    private void showFeedbackSession(FeedbackSession session) {
        System.out.println("Feedback Session Name: \"" + session.getFeedbackSessionName() + "\" in " + session.getCourseId());
    }

    /**
     * Remove extra spaces in feedback session's name
     */
    private void fixFeedbackSession(FeedbackSession session) throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        fixFeedbackQuestionsOfFeedbackSession(session);
        fixFeedbackResponsesOfFeedbackSession(session);
        fixFeedbackResponseCommentsOfFeedbackSession(session);
        
        FeedbackSessionAttributes sessionAttribute = new FeedbackSessionAttributes(session);
        feedbackSessionsDb.deleteEntity(sessionAttribute);
        sessionAttribute.feedbackSessionName = StringHelper.removeExtraSpace(sessionAttribute.getFeedbackSessionName());
        feedbackSessionsDb.createEntity(sessionAttribute);
    }

    /**
     * Fixes feedbackSessionName in FeedbackResponseComments
     */
    private void fixFeedbackResponseCommentsOfFeedbackSession(FeedbackSession session) throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseCommentAttributes> feedbackResponseCommentList = feedbackResponseCommentsDb.getFeedbackResponseCommentsForSession(session.getCourseId(),
                                                                                                                                               session.getFeedbackSessionName());
        for (FeedbackResponseCommentAttributes comment : feedbackResponseCommentList) {
            comment.feedbackSessionName = StringHelper.removeExtraSpace(comment.feedbackSessionName);
            feedbackResponseCommentsDb.updateFeedbackResponseComment(comment);
        }
    }

    /**
     * Fixes feedbackSessionName in FeedbackResponses
     */
    private void fixFeedbackResponsesOfFeedbackSession(FeedbackSession session) throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseAttributes> feedbackResponseList = feedbackResponsesDb.getFeedbackResponsesForSession(session.getFeedbackSessionName(), 
                                                                                                                   session.getCourseId());
        for (FeedbackResponseAttributes response : feedbackResponseList) {
            response.feedbackSessionName = StringHelper.removeExtraSpace(response.feedbackSessionName);
            feedbackResponsesDb.updateFeedbackResponse(response);
        }
    }

    /**
     * Fixes feedbackSessionName in FeedbackQuestions
     */
    private void fixFeedbackQuestionsOfFeedbackSession(FeedbackSession session) 
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackQuestionAttributes> feedbackQuestionList = feedbackQuestionsDb.getFeedbackQuestionsForSession(session.getFeedbackSessionName(), 
                                                                                                                   session.getCourseId());
        for (FeedbackQuestionAttributes question : feedbackQuestionList) {
            question.feedbackSessionName = StringHelper.removeExtraSpace(question.feedbackSessionName);
            feedbackQuestionsDb.updateFeedbackQuestion(question);
        }
    }
    
    /**
     * Check if there is extra space in the string.
     */
    private boolean hasExtraSpaces(String s) {
        return !s.equals(StringHelper.removeExtraSpace(s));
    }
    
    protected PersistenceManager getPM() {
        return Datastore.getPersistenceManager();
    }
}