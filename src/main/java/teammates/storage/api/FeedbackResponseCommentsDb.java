package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.search.FeedbackResponseCommentSearchDocument;
import teammates.storage.search.FeedbackResponseCommentSearchQuery;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

/**
 * Handles CRUD operations for feedback response comments.
 * 
 * @see {@link FeedbackResponseComment}
 * @see {@link FeedbackResponseCommentAttributes}
 */
public class FeedbackResponseCommentsDb extends EntitiesDb {

    /**
     * This method is for testing only
     * @param commentsToAdd
     * @throws InvalidParametersException
     */
    public void createFeedbackResponseComments(Collection<FeedbackResponseCommentAttributes> commentsToAdd)
            throws InvalidParametersException {
        List<EntityAttributes> commentsToUpdate = createEntities(commentsToAdd);
        for (EntityAttributes entity : commentsToUpdate) {
            FeedbackResponseCommentAttributes comment = (FeedbackResponseCommentAttributes) entity;
            try {
                updateFeedbackResponseComment(comment);
            } catch (EntityDoesNotExistException e) {
             // This situation is not tested as replicating such a situation is
             // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }
    
    /**
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    @Override
    public FeedbackResponseCommentAttributes createEntity(EntityAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponseComment createdEntity = (FeedbackResponseComment) super.createEntity(entityToAdd);
        if (createdEntity == null) {
            log.info("Trying to get non-existent FeedbackResponseComment, possibly entity not persistent yet.");
            return null;
        }
        return new FeedbackResponseCommentAttributes(createdEntity);
    }
    
    /*
     * Remove search document for the given comment
     */
    public void deleteDocument(FeedbackResponseCommentAttributes commentToDelete) {
        if (commentToDelete.getId() == null) {
            FeedbackResponseComment commentEntity = (FeedbackResponseComment) getEntity(commentToDelete);
            FeedbackResponseCommentAttributes comment = new FeedbackResponseCommentAttributes(commentEntity);
            deleteDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, comment.getId().toString());
        } else {
            deleteDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, commentToDelete.getId().toString());
        }
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseCommentId);
        
        FeedbackResponseComment frc = getFeedbackResponseCommentEntity(feedbackResponseCommentId);
        
        if (frc == null) {
            log.info("Trying to get non-existent response comment: " + feedbackResponseCommentId + ".");
            return null;
        }
        
        return new FeedbackResponseCommentAttributes(frc);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
                                                     String feedbackResponseId, String giverEmail, Date createdAt) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, createdAt);
        
        FeedbackResponseComment frc = getFeedbackResponseCommentEntity(feedbackResponseId, giverEmail, createdAt);
        
        if (frc == null) {
            log.info("Trying to get non-existent response comment: "
                    + feedbackResponseId + "/from: " + giverEmail
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
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
                                                     String courseId, Date createdAt, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, createdAt);
        
        FeedbackResponseComment frc =
                (FeedbackResponseComment) getFeedbackResponseCommentEntity(courseId, createdAt, giverEmail);
        
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
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForGiver(
                                                           String courseId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntityForGiver(courseId, giverEmail);
        
        if (frcList == null) {
            log.info("Trying to get non-existent response comment: from: " + giverEmail
                    + " in the course: " + courseId);
            return null;
        }
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(new FeedbackResponseCommentAttributes(frc));
            }
        }
        
        return resultList;
    }
    
    /*
     * Get response comments for the response Id
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponse(String feedbackResponseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
        
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntitiesForResponse(feedbackResponseId);
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(new FeedbackResponseCommentAttributes(frc));
            }
        }
        
        return resultList;
    }
    
    /*
     * Remove response comments for the response Id
     */
    public void deleteFeedbackResponseCommentsForResponse(String responseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, responseId);
        
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntitiesForResponse(responseId);
        
        getPm().deletePersistentAll(frcList);
        getPm().flush();
    }
    
    /*
     * Remove response comments for the course Ids
     */
    public void deleteFeedbackResponseCommentsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        
        List<FeedbackResponseComment> feedbackResponseCommentList =
                getFeedbackResponseCommentEntitiesForCourses(courseIds);
        
        getPm().deletePersistentAll(feedbackResponseCommentList);
        getPm().flush();
    }
    
    public void deleteFeedbackResponseCommentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<String> courseIds = new ArrayList<String>();
        courseIds.add(courseId);
        deleteFeedbackResponseCommentsForCourses(courseIds);
    }
    
    /*
     * Get response comments for the course Ids
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForCourses(List<String> courseIds) {
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.setFilter(":p.contains(courseId)");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
                (List<FeedbackResponseComment>) q.execute(courseIds);
        return feedbackResponseCommentList;
    }
    
    /*
     * Get response comments for the course
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForCourse(String courseId) {
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntitiesForCourse(courseId);
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(new FeedbackResponseCommentAttributes(frc));
            }
        }
        
        return resultList;
    }
    
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSession(
                                                           String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        
        List<FeedbackResponseComment> frcList =
                getFeedbackResponseCommentEntitiesForSession(courseId, feedbackSessionName);
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(new FeedbackResponseCommentAttributes(frc));
            }
        }
        
        return resultList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSessionInSection(String courseId,
                                                           String feedbackSessionName, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);
        
        Collection<FeedbackResponseComment> frcList =
                getFeedbackResponseCommentEntitiesForSessionInSection(courseId, feedbackSessionName, section);
        
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(new FeedbackResponseCommentAttributes(frc));
            }
        }
        
        return resultList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(
                                                     FeedbackResponseCommentAttributes newAttributes)
                                                     throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);
        
        newAttributes.sanitizeForSaving();
        
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        FeedbackResponseComment frc = (FeedbackResponseComment) getEntity(newAttributes);
        
        if (frc == null || JDOHelper.isDeleted(frc)) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }
        
        frc.setCommentText(newAttributes.commentText);
        frc.setSendingState(newAttributes.sendingState);
        frc.setGiverSection(newAttributes.giverSection);
        frc.setReceiverSection(newAttributes.receiverSection);
        frc.setShowCommentTo(newAttributes.showCommentTo);
        frc.setShowGiverNameTo(newAttributes.showGiverNameTo);
        frc.setIsVisibilityFollowingFeedbackQuestion(false);
        frc.setLastEditorEmail(newAttributes.giverEmail);
        frc.setLastEditedAt(newAttributes.createdAt);
        
        if (newAttributes.feedbackResponseId != null) {
            frc.setFeedbackResponseId(newAttributes.feedbackResponseId);
        }
        
        log.info(newAttributes.getBackupIdentifier());
        getPm().close();
        
        return new FeedbackResponseCommentAttributes(frc);
    }
    
    /*
     * Update giver email (normally an instructor email) with the new one
     */
    public void updateGiverEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updatedEmail);
        
        if (oldEmail.equals(updatedEmail)) {
            return;
        }
        
        List<FeedbackResponseComment> responseComments =
                this.getFeedbackResponseCommentEntitiesForGiverInCourse(courseId, oldEmail);
        
        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setGiverEmail(updatedEmail);
        }
        
        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPm().close();
    }
    
    /*
     * Updates last editor for all comments last edited by the given instructor with the instructor's new email
     */
    public void updateLastEditorEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updatedEmail);
        
        if (oldEmail.equals(updatedEmail)) {
            return;
        }
        
        List<FeedbackResponseComment> responseComments =
                this.getFeedbackResponseCommentEntitiesForLastEditorInCourse(courseId, oldEmail);
        
        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setLastEditorEmail(updatedEmail);
        }
        
        log.info("updating last editor email from: " + oldEmail + " to: " + updatedEmail
                 + " for feedback response comments in the course: " + courseId);
        getPm().close();
    }
    
    /*
     * Get response comments for a sending state (SENT|SENDING|PENDING)
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSendingState(String courseId,
                                                           String sessionName, CommentSendingState state) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, state);
        
        List<FeedbackResponseComment> frcList =
                getFeedbackResponseCommentEntityForSendingState(courseId, sessionName, state);
        List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
        for (FeedbackResponseComment frc : frcList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(new FeedbackResponseCommentAttributes(frc));
            }
        }
        
        return resultList;
    }
    
    /*
     * Update response comments from old state to new state
     */
    public void updateFeedbackResponseComments(String courseId, String feedbackSessionName,
                                               CommentSendingState oldState, CommentSendingState newState) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<FeedbackResponseComment> frcList =
                getFeedbackResponseCommentEntityForSendingState(courseId, feedbackSessionName, oldState);
        
        for (FeedbackResponseComment frComment : frcList) {
            frComment.setSendingState(newState);
        }
        
        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPm().close();
    }
    
    /*
     * Create or update search document for the given comment
     */
    public void putDocument(FeedbackResponseCommentAttributes comment) {
        putDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, new FeedbackResponseCommentSearchDocument(comment));
    }
    
    /**
     * Search for response comments
     * @return {@link FeedbackResponseCommentSearchResultBundle}
     */
    public FeedbackResponseCommentSearchResultBundle search(String queryString,
                                                            List<InstructorAttributes> instructors,
                                                            String cursorString) {
        if (queryString.trim().isEmpty()) {
            return new FeedbackResponseCommentSearchResultBundle();
        }
        
        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT,
                new FeedbackResponseCommentSearchQuery(instructors, queryString, cursorString));
        
        return FeedbackResponseCommentSearchDocument.fromResults(results, instructors);
    }
    
    /**
     * @deprecated Not scalable. Don't use unless in admin features.
     */
    @Deprecated
    public List<FeedbackResponseCommentAttributes> getAllFeedbackResponseComments() {
        
        List<FeedbackResponseCommentAttributes> list = new ArrayList<FeedbackResponseCommentAttributes>();
        List<FeedbackResponseComment> entities = getAllFeedbackResponseCommentEntities();
        for (FeedbackResponseComment comment : entities) {
            if (!JDOHelper.isDeleted(comment)) {
                list.add(new FeedbackResponseCommentAttributes(comment));
            }
        }
        return list;
    }
    
    private List<FeedbackResponseComment> getAllFeedbackResponseCommentEntities() {
        
        String query = "select from " + FeedbackResponseComment.class.getName();
            
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
                (List<FeedbackResponseComment>) getPm().newQuery(query).execute();
    
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }

    private List<FeedbackResponseComment> getCommentsWithoutDeletedEntity(
                                                  List<FeedbackResponseComment> feedbackResponseCommentList) {
        List<FeedbackResponseComment> resultList = new ArrayList<FeedbackResponseComment>();
        for (FeedbackResponseComment frc : feedbackResponseCommentList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(frc);
            }
        }
        
        return resultList;
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        FeedbackResponseCommentAttributes feedbackResponseCommentToGet =
                (FeedbackResponseCommentAttributes) attributes;
        
        if (feedbackResponseCommentToGet.getId() != null) {
            return getFeedbackResponseCommentEntity(feedbackResponseCommentToGet.getId());
        }
        
        return getFeedbackResponseCommentEntity(
            feedbackResponseCommentToGet.courseId,
            feedbackResponseCommentToGet.createdAt,
            feedbackResponseCommentToGet.giverEmail);
    }
    
    private Object getFeedbackResponseCommentEntity(String courseId, Date createdAt, String giverEmail) {
        List<FeedbackResponseComment> frcList = getFeedbackResponseCommentEntityForGiver(courseId, giverEmail);
        if (frcList.isEmpty()) {
            return null;
        }
        
        for (FeedbackResponseComment frc : frcList) {
            if (!JDOHelper.isDeleted(frc)
                    && frc.getCourseId().equals(courseId)
                    && frc.getGiverEmail().equals(giverEmail)
                    && frc.getCreatedAt().equals(createdAt)) {
                return frc;
            }
        }
        return null;
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntityForGiver(String courseId, String giverEmail) {
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
                (List<FeedbackResponseComment>) q.execute(courseId, giverEmail);
    
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntityForSendingState(String courseId,
                                                  String feedbackSessionName, CommentSendingState state) {
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String fsNameParam, String sendingStateParam");
        q.setFilter("courseId == courseIdParam && feedbackSessionName == fsNameParam "
                + "&& sendingState == sendingStateParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
                (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName, state.toString());
    
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntity(Long feedbackResponseCommentId) {
        Query q = getPm().newQuery(FeedbackResponseComment.class);
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
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String feedbackResponseIdParam, "
                + "String giverEmailParam, java.util.Date createdAtParam");
        q.setFilter("feedbackResponseId == feedbackResponseIdParam && "
                + "giverEmail == giverEmailParam && "
                + "createdAt == createdAtParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
                (List<FeedbackResponseComment>) q.execute(feedbackResponseId, giverEmail, createdAt);
        
        if (feedbackResponseCommentList.isEmpty() || JDOHelper.isDeleted(feedbackResponseCommentList.get(0))) {
            return null;
        }
    
        return feedbackResponseCommentList.get(0);
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForGiverInCourse(String courseId,
                                                                                             String giverEmail) {
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseComments =
                (List<FeedbackResponseComment>) q.execute(courseId, giverEmail);
        
        return feedbackResponseComments;
    }
    
    /*
     * Gets a list of FeedbackResponseComments which have a last editor associated with the given email
     */
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForLastEditorInCourse(
                                                                    String courseId, String lastEditorEmail) {
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String lastEditorParam");
        q.setFilter("courseId == courseIdParam && lastEditorEmail == lastEditorParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseComments =
                (List<FeedbackResponseComment>) q.execute(courseId, lastEditorEmail);
        
        return feedbackResponseComments;
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForResponse(String feedbackResponseId) {
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String feedbackResponseIdParam");
        q.setFilter("feedbackResponseId == feedbackResponseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
                (List<FeedbackResponseComment>) q.execute(feedbackResponseId);
        
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSession(String courseId,
                                                                                       String feedbackSessionName) {
        
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String feedbackSessionNameParam");
        q.setFilter("courseId == courseIdParam && "
                + "feedbackSessionName == feedbackSessionNameParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
                (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName);
        
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }
    
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForCourse(String courseId) {
        
        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> feedbackResponseCommentList =
                (List<FeedbackResponseComment>) q.execute(courseId);
        
        return getCommentsWithoutDeletedEntity(feedbackResponseCommentList);
    }
    
    private Collection<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSessionInSection(
                                                        String courseId, String feedbackSessionName, String section) {

        Map<String, FeedbackResponseComment> feedbackResponseCommentList =
                new HashMap<String, FeedbackResponseComment>();

        Query q = getPm().newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String feedbackSessionNameParam, String sectionParam");
        q.setFilter("courseId == courseIdParam && "
                    + "feedbackSessionName == feedbackSessionNameParam && giverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> firstQueryResponseComments =
                (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName, section);
        for (FeedbackResponseComment responseComment : firstQueryResponseComments) {
            if (!JDOHelper.isDeleted(responseComment)) {
                feedbackResponseCommentList.put(
                        String.valueOf(responseComment.getFeedbackResponseCommentId()), responseComment);
            }
        }
        
        q.setFilter("courseId == courseIdParam && "
                + "feedbackSessionName == feedbackSessionNameParam && receiverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> secondQueryResponseComments =
                (List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName, section);
        for (FeedbackResponseComment responseComment : secondQueryResponseComments) {
            if (!JDOHelper.isDeleted(responseComment)) {
                feedbackResponseCommentList.put(
                        String.valueOf(responseComment.getFeedbackResponseCommentId()), responseComment);
            }
        }
        
        return feedbackResponseCommentList.values();
    }
}
