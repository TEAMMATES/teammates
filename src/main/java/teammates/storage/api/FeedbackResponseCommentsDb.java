package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.search.FeedbackResponseCommentSearchDocument;
import teammates.storage.search.FeedbackResponseCommentSearchQuery;

public class FeedbackResponseCommentsDb extends EntitiesDb {

    private static final Logger log = Utils.getLogger();
    
    @Override
    public FeedbackResponseCommentAttributes createEntity(EntityAttributes entityToAdd) 
            throws InvalidParametersException, EntityAlreadyExistsException{
        FeedbackResponseComment createdEntity = (FeedbackResponseComment) super.createEntity(entityToAdd);
        if(createdEntity == null){
            log.info("Trying to get non-existent FeedbackResponseComment, possibly entity not persistent yet.");
            return null;
        } else{
            FeedbackResponseCommentAttributes createdComment = new FeedbackResponseCommentAttributes(createdEntity);
            return createdComment;
        }
    }
    
    public void deleteDocument(FeedbackResponseCommentAttributes commentToDelete){
        FeedbackResponseComment commentEntity = (FeedbackResponseComment) getEntity(commentToDelete);
        FeedbackResponseCommentAttributes comment = new FeedbackResponseCommentAttributes(commentEntity);
        deleteDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, comment.getId().toString());
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseCommentId);
        
        FeedbackResponseComment frc = 
                getFeedbackResponseCommentEntity(feedbackResponseCommentId);
        
        if (frc == null) {
            log.info("Trying to get non-existent response comment: " +
                    feedbackResponseCommentId + ".");
            return null;
        }
        
        return new FeedbackResponseCommentAttributes(frc);    
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(String feedbackResponseId, String giverEmail, Date createdAt) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, createdAt);
        
        FeedbackResponseComment frc = 
                getFeedbackResponseCommentEntity(feedbackResponseId, giverEmail, createdAt);
        
        if (frc == null) {
            log.info("Trying to get non-existent response comment: " +
                    feedbackResponseId + "/from: " + giverEmail
                    + "created at: " + createdAt);
            return null;
        }
        
        return new FeedbackResponseCommentAttributes(frc);    
    }
    
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(String courseId, Date createdAt, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, createdAt);
        
        FeedbackResponseComment frc = (FeedbackResponseComment)
                getFeedbackResponseCommentEntity(courseId, createdAt, giverEmail);
        
        if (frc == null) {
            log.info("Trying to get non-existent response comment: from: " + giverEmail
                    + " in the course " + courseId + " created at: " + createdAt);
            return null;
        }
        
        return new FeedbackResponseCommentAttributes(frc);    
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForGiver(String courseId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        
        List<FeedbackResponseComment> frcList = 
                getFeedbackResponseCommentEntityForGiver(courseId, giverEmail);
        
        if (frcList == null) {
            log.info("Trying to get non-existent response comment: from: " + giverEmail
                    + " in the course: " + courseId);
            return null;
        }
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            resultList.add(new FeedbackResponseCommentAttributes(frc));
        }
        
        return resultList;    
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponse(String feedbackResponseId){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
        
        List<FeedbackResponseComment> frcList = 
                getFeedbackResponseCommentEntitiesForResponse(feedbackResponseId);
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            resultList.add(new FeedbackResponseCommentAttributes(frc));
        }
        
        return resultList; 
    }
    
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSession(String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        
        List<FeedbackResponseComment> frcList = 
                getFeedbackResponseCommentEntitiesForSession(courseId, feedbackSessionName);
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            resultList.add(new FeedbackResponseCommentAttributes(frc));
        }
        
        return resultList;    
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSessionInSection(String courseId, String feedbackSessionName, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);
        
        Collection<FeedbackResponseComment> frcList = 
                getFeedbackResponseCommentEntitiesForSessionInSection(courseId, feedbackSessionName, section);
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            resultList.add(new FeedbackResponseCommentAttributes(frc));
        }
        
        return resultList;    
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws InvalidParametersException 
     * @throws EntityDoesNotExistException 
     */
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(FeedbackResponseCommentAttributes newAttributes) 
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes.getId());
        
        newAttributes.sanitizeForSaving();
        
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        FeedbackResponseComment frc = (FeedbackResponseComment) getEntity(newAttributes);
        
        if (frc == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }
        
        frc.setCommentText(newAttributes.commentText);
        frc.setSendingState(newAttributes.sendingState);
        frc.setGiverSection(newAttributes.giverSection);
        frc.setReceiverSection(newAttributes.receiverSection);
        
        getPM().close();
        
        FeedbackResponseCommentAttributes updatedComment = new FeedbackResponseCommentAttributes(frc);
        return updatedComment;
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSendingState(String courseId, String sessionName,
            CommentSendingState state){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, state);
        
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntityForSendingState(courseId, sessionName, state);
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            resultList.add(new FeedbackResponseCommentAttributes(frc));
        }
        
        return resultList;  
    }
    
    public void updateFeedbackResponseComments(String courseId, String feedbackSessionName,
            CommentSendingState oldState, CommentSendingState newState) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntityForSendingState(courseId, feedbackSessionName, oldState);
        
        for(FeedbackResponseComment frComment : frcList){
            frComment.setSendingState(newState);
        }
        
        getPM().close();
    }
    
    public void putDocument(FeedbackResponseCommentAttributes comment){
        putDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, new FeedbackResponseCommentSearchDocument(comment));
    }
    
    public FeedbackResponseCommentSearchResultBundle search(String queryString, String googleId, String cursorString){
        if(queryString.trim().isEmpty())
            return new FeedbackResponseCommentSearchResultBundle();
        
        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, 
                new FeedbackResponseCommentSearchQuery(googleId, queryString, cursorString));
        
        return new FeedbackResponseCommentSearchResultBundle().fromResults(results, googleId);
    }
    
    @Deprecated
    public List<FeedbackResponseCommentAttributes> getAllFeedbackResponseComments() {
        
        List<FeedbackResponseCommentAttributes> list = new ArrayList<FeedbackResponseCommentAttributes>();
        List<FeedbackResponseComment> entities = getAllFeedbackResponseCommentEntities();
        for(FeedbackResponseComment comment: entities){
            list.add(new FeedbackResponseCommentAttributes(comment));
        }
        return list;
    }
    
    private List<FeedbackResponseComment> getAllFeedbackResponseCommentEntities() {
        
        String query = "select from " + FeedbackResponseComment.class.getName();
            
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> commentList = (List<FeedbackResponseComment>) getPM()
                .newQuery(query).execute();
    
        return commentList;
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        FeedbackResponseCommentAttributes feedbackResponseCommentToGet =
                (FeedbackResponseCommentAttributes) attributes;
        
        if (feedbackResponseCommentToGet.getId() != null) {
            return getFeedbackResponseCommentEntity(feedbackResponseCommentToGet.getId());
        } else { 
            return getFeedbackResponseCommentEntity(
                feedbackResponseCommentToGet.courseId,
                feedbackResponseCommentToGet.createdAt,
                feedbackResponseCommentToGet.giverEmail
                );
        }
    }
    
    private Object getFeedbackResponseCommentEntity(String courseId, Date createdAt,
            String giverEmail) {
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntityForGiver(courseId, giverEmail);
        if(frcList.isEmpty()){
            return null;
        }
        
        for(FeedbackResponseComment frc:frcList){
            if(frc.getCourseId().equals(courseId)
                    && frc.getGiverEmail().equals(giverEmail)
                    && frc.getCreatedAt().equals(createdAt)){
                return frc;
            }
        }
        return null;
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntityForGiver(String courseId, String giverEmail) {
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(courseId, giverEmail);
    
        return feedbackResponseCommentList;
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntityForSendingState(String courseId, String feedbackSessionName,
            CommentSendingState state) {
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String fsNameParam, String sendingStateParam");
        q.setFilter("courseId == courseIdParam && feedbackSessionName == fsNameParam && sendingState == sendingStateParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName, state.toString());
    
        return feedbackResponseCommentList;
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntity(Long feedbackResponseCommentId) {
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String feedbackResponseCommentIdParam");
        q.setFilter("feedbackResponseCommentId == feedbackResponseCommentIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(feedbackResponseCommentId);
        
        if (feedbackResponseCommentList.isEmpty() || JDOHelper.isDeleted(feedbackResponseCommentList.get(0))) {
            return null;
        }
    
        return feedbackResponseCommentList.get(0);
    }
    
    private FeedbackResponseComment getFeedbackResponseCommentEntity(
            String feedbackResponseId, String giverEmail, Date createdAt) {
        
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String feedbackResponseIdParam, " +
                "String giverEmailParam, java.util.Date createdAtParam");
        q.setFilter("feedbackResponseId == feedbackResponseIdParam && " +
                "giverEmail == giverEmailParam && " +
                "createdAt == createdAtParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(feedbackResponseId, giverEmail, createdAt);
        
        if (feedbackResponseCommentList.isEmpty() || JDOHelper.isDeleted(feedbackResponseCommentList.get(0))) {
            return null;
        }
    
        return feedbackResponseCommentList.get(0);
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForResponse(String feedbackResponseId){
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String feedbackResponseIdParam");
        q.setFilter("feedbackResponseId == feedbackResponseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(feedbackResponseId);
        
        List<FeedbackResponseComment> resultList = new ArrayList<FeedbackResponseComment>();
        for (FeedbackResponseComment frc : feedbackResponseCommentList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(frc);
            }
        }
        
        return resultList;
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSession(
            String courseId, String feedbackSessionName) {
        
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String feedbackSessionNameParam");
        q.setFilter("courseId == courseIdParam && " +
                "feedbackSessionName == feedbackSessionNameParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName);
        
        List<FeedbackResponseComment> resultList = new ArrayList<FeedbackResponseComment>();
        for (FeedbackResponseComment frc : feedbackResponseCommentList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(frc);
            }
        }
        
        return resultList;
    }

    private Collection<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSessionInSection(
            String courseId, String feedbackSessionName, String section) {

        Map<String, FeedbackResponseComment> FeedbackResponseCommentList = new HashMap<String, FeedbackResponseComment>();

        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String feedbackSessionNameParam, String sectionParam");
        q.setFilter("courseId == courseIdParam && " +
                "feedbackSessionName == feedbackSessionNameParam && giverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> firstQueryResponseComments =
            (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName, section);
        for(FeedbackResponseComment responseComment : firstQueryResponseComments){
            FeedbackResponseCommentList.put(String.valueOf(responseComment.getFeedbackResponseCommentId()), responseComment);
        }
        
        q.setFilter("courseId == courseIdParam && " +
                "feedbackSessionName == feedbackSessionNameParam && receiverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> secondQueryResponseComments =
            (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName, section);
        for(FeedbackResponseComment responseComment : secondQueryResponseComments){
            FeedbackResponseCommentList.put(String.valueOf(responseComment.getFeedbackResponseCommentId()), responseComment);
        }
        
        return FeedbackResponseCommentList.values();
    }
}
