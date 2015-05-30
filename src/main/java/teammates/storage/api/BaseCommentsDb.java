package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.BaseCommentAttributes;
import teammates.common.datatransfer.BaseCommentSearchResultBundle;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.BaseComment;
import teammates.storage.entity.Comment;

public abstract class BaseCommentsDb extends EntitiesDb {

    public abstract String getUpdateError();

    public abstract String getReadError();

    private static final Logger log = Utils.getLogger();

    /**
     * This method is for testing only
     * @param commentsToAdd
     * @throws InvalidParametersException
     */
    public void createComments(Collection<? extends BaseCommentAttributes> commentsToAdd)
                throws InvalidParametersException {
        List<EntityAttributes> commentsToUpdate = createEntities(commentsToAdd);
        for (EntityAttributes entity : commentsToUpdate) {
            BaseCommentAttributes bca = (BaseCommentAttributes) entity;
            try {
                updateComment(bca);
            } catch (EntityDoesNotExistException e) {
                // This situation is not tested as replicating such a situation is difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }

    /**
     * Preconditions: <br>
     * All parameters are non-null.
     * @throws InvalidParametersException 
     * @throws EntityDoesNotExistException 
     */
    public BaseCommentAttributes updateComment(BaseCommentAttributes newAttributes)
                                 throws EntityDoesNotExistException, InvalidParametersException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);
        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        BaseComment bc = getEntity(newAttributes);
        if (bc == null) {
            throw new EntityDoesNotExistException(getUpdateError() + newAttributes.toString());
        }
        if (newAttributes.commentText != null) {
            bc.setCommentText(newAttributes.commentText);
        }
        if (newAttributes.sendingState != null) {
            bc.setSendingState(newAttributes.sendingState);
        }
        if (newAttributes.giverEmail != null) {
            bc.setLastEditorEmail(newAttributes.giverEmail);
        }
        if (newAttributes.createdAt != null) {
            bc.setLastEditedAt(newAttributes.createdAt);
        }
        bc = updateSpecificFields(bc, newAttributes);
        log.info(newAttributes.getBackupIdentifier());
        getPM().close();
        return getAttributeFromEntity(bc);
    }

    protected abstract BaseComment updateSpecificFields(BaseComment bc, BaseCommentAttributes bca);

    /**
     * Get comment for comment's id.<br>
     * Precondition: all parameters are non-null.
     * @return null if not found.
     */
    public BaseCommentAttributes getComment(Long commentId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentId);
        BaseComment bc = null;
        bc = getCommentEntity(commentId);
        if (bc == null) {
            log.info(getUpdateError() + commentId);
            return null;
        } else {
            return getAttributeFromEntity(bc);
        }
    }

    protected abstract BaseComment getCommentEntity(Long commentId);

    /**
     * Preconditions: <br>
     * {@code entityToAdd} is not null and has valid data.
     */
    @Override
    public BaseCommentAttributes createEntity(EntityAttributes entityToAdd) 
                                 throws InvalidParametersException, EntityAlreadyExistsException {
        BaseComment createdEntity = (BaseComment) super.createEntity(entityToAdd);
        if (createdEntity == null) {
            log.info(getReadError());
            return null;
        } else {
            return getAttributeFromEntity(createdEntity);
        }
    }

    protected List<? extends BaseCommentAttributes> getAttributesListFromEntitiesList(
                                    List<? extends BaseComment> bcList) {
        List<BaseCommentAttributes> resultList = new ArrayList<BaseCommentAttributes>();
        for (BaseComment bc : bcList) {
            resultList.add(getAttributeFromEntity(bc));
        }
        return resultList;
    }

    protected abstract BaseCommentAttributes getAttributeFromEntity(BaseComment createdEntity);

    /**
     * Remove search document for the given comment
     * @param commentToDelete
     */
    public void deleteDocument(BaseCommentAttributes commentToDelete) {
        if (commentToDelete.getId() == null) {
            BaseCommentAttributes comment = getCommentAttributes(commentToDelete);
            deleteDocument(comment.getId().toString());
        } else {
            deleteDocument(commentToDelete.getId().toString());
        }
    }

    protected abstract void deleteDocument(String commentId);

    /**
     * Get comment for a given comment attribute
     */
    public BaseCommentAttributes getCommentAttributes(BaseCommentAttributes bca) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, bca);
        BaseComment bc = null;
        bc = getEntity(bca);
        if (bc == null) {
            log.info(getReadError() + bca);
            return null;
        } else {
            return getAttributeFromEntity(bc);
        }
    }

    protected abstract BaseComment getEntityFromAttributes(BaseCommentAttributes bca);

    /**
     * Search for comments
     * @return {@link CommentSearchResultBundle}
     */
    public BaseCommentSearchResultBundle search(String queryString, String googleId, String cursorString) {
        if (queryString.trim().isEmpty()) {
            return getNewSearchResultBundle();
        }
        Results<ScoredDocument> results = getSearchResult(googleId, queryString, cursorString);
        return getNewSearchResultBundle().fromResults(results, googleId);
    }

    protected abstract Results<ScoredDocument> getSearchResult(String googleId, String queryString,
                                                               String cursorString);

    protected abstract BaseCommentSearchResultBundle getNewSearchResultBundle();

    public abstract void updateInstructorEmail(String courseId, String oldInstrEmail, String updatedInstrEmail);

    public void updateGiverEmailOfComment(String courseId, String oldEmail, String updatedEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updatedEmail);
        if (oldEmail.equals(updatedEmail)) {
            return;
        }
        List<? extends BaseComment> giverComments = getCommentEntitiesForGiver(courseId, oldEmail);
        for (BaseComment giverComment : giverComments) {
            giverComment.setGiverEmail(updatedEmail);
        }
        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPM().close();
    }

    /**
     * Get comments for a giver email.<br>
     * Precondition: all parameters are non-null.
     * @return Null if not found.
     */
    public List<? extends BaseCommentAttributes> getCommentsForGiver(String courseId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        List<? extends BaseComment> comments = getCommentEntitiesForGiver(courseId, giverEmail);
        if (comments == null) {
            log.info(getReadError() + " from: " + giverEmail + " in the course: " + courseId);
            return null;
        }
        return getAttributesListFromEntitiesList(comments);
    }

    protected abstract List<? extends BaseComment> getCommentEntitiesForGiver(String courseId, String giverEmail);

    /**
     * Get comment for the sending state (SENT|SENDING|PENDING)
     */
    public List<? extends BaseCommentAttributes> getCommentsForSendingState(String courseId, String sessionName,
                                                                            CommentSendingState state) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, state);
        List<? extends BaseComment> frcList = getCommentEntitiesForSendingState(courseId, sessionName, state);
        return getAttributesListFromEntitiesList(frcList);
    }

    protected abstract List<? extends BaseComment> getCommentEntitiesForSendingState(String courseId, String sessionName,
                                                                                     CommentSendingState state);

    /**
     * Delete comments in certain courses
     */
    public void deleteCommentsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        List<? extends BaseComment> commentsToDelete = getCommentEntitiesForCourses(courseIds);
        getPM().deletePersistentAll(commentsToDelete);
        getPM().flush();
    }

    protected abstract List<? extends BaseComment> getCommentEntitiesForCourses(List<String> courseIds);

    /**
     * Delete comments in a certain course
     */
    public void deleteCommentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        List<? extends BaseComment> courseComments = getCommentEntitiesForCourse(courseId);
        getPM().deletePersistentAll(courseComments);
        getPM().flush();
    }

    /*
     * Delete comments given by certain instructor
     */
    public void deleteCommentsForInstructorEmail(String courseId, String instructorEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, instructorEmail);
        List<? extends BaseComment> giverComments = getCommentEntitiesForGiver(courseId, instructorEmail);
        getPM().deletePersistentAll(giverComments);
        getPM().flush();
    }

    protected abstract List<? extends BaseComment> getCommentEntitiesForCourse(String courseIds);

    protected BaseComment getEntity(EntityAttributes attributes) {
        BaseCommentAttributes bca = (BaseCommentAttributes) attributes;
        BaseComment bc = null;
        if (bca.getId() != null) {
            bc = getCommentEntity(bca.getId());
        } else {
            bc = getEntityFromAttributes(bca);
        }
        return bc;
    }

    /**
     * Update comment from old state to new state
     */
    public void updateComments(String courseId, String sessionName, CommentSendingState oldState,
                               CommentSendingState newState) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldState);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newState);
        List<? extends BaseComment> comments = getCommentEntitiesForSendingState(courseId, sessionName, oldState);
        for (BaseComment comment : comments) {
            comment.setSendingState(newState);
        }
        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPM().close();
    }

    /**
     * Get comments for the course
     */
    public List<? extends BaseCommentAttributes> getCommentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        List<? extends BaseComment> comments = getCommentEntitiesForCourse(courseId);
        return getAttributesListFromEntitiesList(comments);
    }

    /**
     * @deprecated Not scalable. Don't use unless in admin features.
     */
    @Deprecated
    public List<? extends BaseCommentAttributes> getAllComments(String className) {
        String query = "select from " + className;
        @SuppressWarnings("unchecked")
        List<? extends BaseComment> commentList = (List<Comment>) getPM().newQuery(query).execute();
        List<? extends BaseComment> entities = getCommentsWithoutDeletedEntity(commentList);
        return getAttributesListFromEntitiesList(entities);
    }

    @Deprecated
    public abstract List<? extends BaseCommentAttributes> getAllComments();

    protected List<? extends BaseComment> getCommentsWithoutDeletedEntity(List<? extends BaseComment> bcList) {
        List<BaseComment> resultList = new ArrayList<BaseComment>();
        for (BaseComment frc : bcList) {
            if (!JDOHelper.isDeleted(frc)) {
                resultList.add(frc);
            }
        }
        return resultList;
    }
    
    public abstract void putDocument(BaseCommentAttributes bca);

}
