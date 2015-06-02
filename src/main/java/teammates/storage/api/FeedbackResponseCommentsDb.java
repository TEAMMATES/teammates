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

import teammates.common.datatransfer.BaseCommentAttributes;
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
import teammates.storage.entity.BaseComment;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.search.FeedbackResponseCommentSearchDocument;
import teammates.storage.search.FeedbackResponseCommentSearchQuery;

/**
 * Handles CRUD Operations for {@link FeedbackResponseComment}.
 * The API uses data transfer classes (i.e. *Attributes) instead of persistable classes.
 */
public class FeedbackResponseCommentsDb extends BaseCommentsDb {

    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent response comment: ";
    public static final String ERROR_GET_NON_EXISTENT = "Trying to get non-existent response comment: ";
    public String getUpdateError() {
        return ERROR_UPDATE_NON_EXISTENT;
    }
    public String getReadError() {
        return ERROR_GET_NON_EXISTENT;
    }

    private static final Logger log = Utils.getLogger();
    
    public FeedbackResponseCommentAttributes createEntity(EntityAttributes entityToAdd) throws InvalidParametersException, EntityAlreadyExistsException {
        return (FeedbackResponseCommentAttributes) super.createEntity(entityToAdd);
    }
    
    public void createFeedbackResponseComments(Collection<FeedbackResponseCommentAttributes> commentsToAdd) throws InvalidParametersException{
        super.createComments(commentsToAdd);
    }
    
    protected FeedbackResponseCommentAttributes getAttributeFromEntity(BaseComment frc) {
        return new FeedbackResponseCommentAttributes((FeedbackResponseComment) frc);
    }
    
    @SuppressWarnings("unchecked")
    protected List<FeedbackResponseCommentAttributes> getAttributesListFromEntitiesList(List<? extends BaseComment> bcList) {
        return (List<FeedbackResponseCommentAttributes>) super.getAttributesListFromEntitiesList(bcList);
    }
    
    protected void deleteDocument(String commentId) {
        deleteDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, commentId);        
    }

    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        return (FeedbackResponseCommentAttributes) super.getComment(feedbackResponseCommentId);
    }

    @SuppressWarnings("unchecked")
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForGiver(String courseId, String giverEmail) {
        return (List<FeedbackResponseCommentAttributes>) super.getCommentsForGiver(courseId, giverEmail);
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
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
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
    
    /*
     * Get response comments for the response Id
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponse(String feedbackResponseId){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
        
        List<FeedbackResponseComment> frcList = 
                getFeedbackResponseCommentEntitiesForResponse(feedbackResponseId);
        return getAttributesListFromEntitiesList(frcList);
    }
    
    /*
     * Remove response comments for the response Id
     */
    public void deleteFeedbackResponseCommentsForResponse(String responseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, responseId);
        
        List<FeedbackResponseComment> frcList = 
                getFeedbackResponseCommentEntitiesForResponse(responseId);
        
        getPM().deletePersistentAll(frcList);
        getPM().flush();
    }
    
    public void deleteFeedbackResponseCommentsForCourses(List<String> courseIds){
        super.deleteCommentsForCourses(courseIds);
    }

    protected List<FeedbackResponseComment> getCommentEntitiesForCourses(List<String> courseIds) {
        return getFeedbackResponseCommentEntitiesForCourses(courseIds);
    }
    
    /*
     * Get response comments for the course Ids
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForCourses(List<String> courseIds) {
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.setFilter(":p.contains(courseId)");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList = (List<FeedbackResponseComment>) q.execute(courseIds);
        return feedbackResponseCommentList;
    }
    
    @SuppressWarnings("unchecked")
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForCourse(String courseId) {
        return (List<FeedbackResponseCommentAttributes>) super.getCommentsForCourse(courseId);
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
        return getAttributesListFromEntitiesList(frcList);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSessionInSection(String courseId, String feedbackSessionName, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);
        
        List<FeedbackResponseComment> frcList = 
                getFeedbackResponseCommentEntitiesForSessionInSection(courseId, feedbackSessionName, section);
        return getAttributesListFromEntitiesList(frcList);
    }
    
    public FeedbackResponseCommentAttributes updateComment(BaseCommentAttributes bca)
            throws InvalidParametersException, EntityDoesNotExistException {
        return updateFeedbackResponseComment((FeedbackResponseCommentAttributes) bca);
    }
    
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(FeedbackResponseCommentAttributes newAttributes) 
            throws InvalidParametersException, EntityDoesNotExistException {
        return (FeedbackResponseCommentAttributes) super.updateComment(newAttributes);
    }
    
    protected FeedbackResponseComment updateSpecificFields(BaseComment bc, BaseCommentAttributes bca) {
        FeedbackResponseCommentAttributes newAttributes = (FeedbackResponseCommentAttributes) bca;
        FeedbackResponseComment frc = (FeedbackResponseComment) bc;
        if (newAttributes.giverSection != null) {
            frc.setGiverSection(newAttributes.giverSection);
        }
        if (newAttributes.receiverSection != null) {
            frc.setReceiverSection(newAttributes.receiverSection);
        }
        if (newAttributes.showCommentTo != null) {
            frc.setShowCommentTo(newAttributes.showCommentTo);
        }
        if (newAttributes.showGiverNameTo != null) {
            frc.setShowGiverNameTo(newAttributes.showGiverNameTo);
        }
        frc.setIsVisibilityFollowingFeedbackQuestion(Boolean.valueOf(false));
        return frc;
    }
    
    public void updateInstructorEmail(String courseId, String oldInstrEmail, String updatedInstrEmail) {
        updateGiverEmailOfFeedbackResponseComments(courseId, oldInstrEmail, updatedInstrEmail);
    }
    
    /*
     * Update giver email (normally an instructor email) with the new one
     */
    public void updateGiverEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        super.updateGiverEmailOfComment(courseId, oldEmail, updatedEmail);
    }
    
    @SuppressWarnings("unchecked")
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSendingState(String courseId, String sessionName,
            CommentSendingState state){
        return (List<FeedbackResponseCommentAttributes>) super.getCommentsForSendingState(courseId, sessionName, state);
    }
    
    public void updateFeedbackResponseComments(String courseId, String feedbackSessionName,
            CommentSendingState oldState, CommentSendingState newState) {
        super.updateComments(courseId, feedbackSessionName, oldState, newState);
    }
    
    public void putDocument(BaseCommentAttributes comment){
        putDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT,
                    new FeedbackResponseCommentSearchDocument((FeedbackResponseCommentAttributes) comment));
    }
    
    protected Results<ScoredDocument> getSearchResult(String googleId, String queryString, String cursorString) {
        return searchDocuments(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, 
                               new FeedbackResponseCommentSearchQuery(googleId, queryString, cursorString));
    }
    
    protected FeedbackResponseCommentSearchResultBundle getNewSearchResultBundle() {
        return new FeedbackResponseCommentSearchResultBundle();
    }
    
    @Deprecated
    public List<FeedbackResponseCommentAttributes> getAllFeedbackResponseComments() {
        return getAllComments();
    }
    
    @Deprecated
    @SuppressWarnings("unchecked")
    public List<FeedbackResponseCommentAttributes> getAllComments() {
        return (List<FeedbackResponseCommentAttributes>) super.getAllComments(FeedbackResponseComment.class.getName());
    }
    
    @SuppressWarnings("unchecked")
    protected List<FeedbackResponseComment> getCommentsWithoutDeletedEntity(List<? extends BaseComment> bcList) {
        return (List<FeedbackResponseComment>) super.getCommentsWithoutDeletedEntity(bcList);
    }
    
    protected FeedbackResponseComment getEntityFromAttributes(BaseCommentAttributes bca) {
        FeedbackResponseCommentAttributes feedbackResponseCommentToGet = (FeedbackResponseCommentAttributes) bca;
        return getFeedbackResponseCommentEntity(feedbackResponseCommentToGet.courseId,
                                                feedbackResponseCommentToGet.createdAt,
                                                feedbackResponseCommentToGet.giverEmail);
    }
    
    private FeedbackResponseComment getFeedbackResponseCommentEntity(String courseId, Date createdAt,
            String giverEmail) {
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntityForGiver(courseId, giverEmail);
        if(frcList.isEmpty()){
            return null;
        }
        
        for(FeedbackResponseComment frc:frcList){
            if(!JDOHelper.isDeleted(frc)
                    && frc.getCourseId().equals(courseId)
                    && frc.getGiverEmail().equals(giverEmail)
                    && frc.getCreatedAt().equals(createdAt)){
                return frc;
            }
        }
        return null;
    }
    
    protected List<FeedbackResponseComment> getCommentEntitiesForGiver(String courseId, String giverEmail) {
        return getFeedbackResponseCommentEntityForGiver(courseId, giverEmail);
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntityForGiver(String courseId, String giverEmail) {
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(courseId, giverEmail);
    
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }
    
    protected List<FeedbackResponseComment> getCommentEntitiesForSendingState(String courseId, String sessionName,
                                                                              CommentSendingState state) {
        return getFeedbackResponseCommentEntityForSendingState(courseId, sessionName, state);
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntityForSendingState(String courseId, String feedbackSessionName,
            CommentSendingState state) {
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String fsNameParam, String sendingStateParam");
        q.setFilter("courseId == courseIdParam && feedbackSessionName == fsNameParam && sendingState == sendingStateParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName, state.toString());
    
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }
    
    protected FeedbackResponseComment getCommentEntity(Long feedbackResponseCommentId) {
        return getFeedbackResponseCommentEntity(feedbackResponseCommentId);
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
        
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
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
        
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }
    
    protected List<FeedbackResponseComment> getCommentEntitiesForCourse(String courseId) {
        return getFeedbackResponseCommentEntitiesForCourse(courseId);
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForCourse(String courseId) {
        
        Query q = getPM().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
            (List<FeedbackResponseComment>) q.execute(courseId);
        
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSessionInSection(
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
            if(!JDOHelper.isDeleted(responseComment)){
                FeedbackResponseCommentList.put(String.valueOf(responseComment.getFeedbackResponseCommentId()), responseComment);
            }
        }
        
        q.setFilter("courseId == courseIdParam && " +
                "feedbackSessionName == feedbackSessionNameParam && receiverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> secondQueryResponseComments =
            (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName, section);
        for(FeedbackResponseComment responseComment : secondQueryResponseComments){
            if(!JDOHelper.isDeleted(responseComment)){
                FeedbackResponseCommentList.put(String.valueOf(responseComment.getFeedbackResponseCommentId()), responseComment);
            }
        }
        List<FeedbackResponseComment> resultAsList = new ArrayList<FeedbackResponseComment>();
        for (FeedbackResponseComment frc: FeedbackResponseCommentList.values()) {
            resultAsList.add(frc);
        }

        return resultAsList;
    }
}
