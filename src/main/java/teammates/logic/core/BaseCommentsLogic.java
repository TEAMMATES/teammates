package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.BaseCommentAttributes;
import teammates.common.datatransfer.BaseCommentSearchResultBundle;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Utils;
import teammates.storage.api.BaseCommentsDb;

public abstract class BaseCommentsLogic {

    @SuppressWarnings("unused") // used by test
    private static final Logger log = Utils.getLogger();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    protected abstract BaseCommentsDb getDb();

    public BaseCommentAttributes createEntity(BaseCommentAttributes comment)
           throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        verifyIsCoursePresent(comment.courseId, "create");
        verifyIsInstructorOfCourse(comment.courseId, comment.giverEmail);
        return getDb().createEntity(comment);
    }

    protected BaseCommentAttributes getComment(Long commentId) {
        return getDb().getComment(commentId);
    }

    protected List<? extends BaseCommentAttributes> getCommentsForGiver(String courseId, String giverEmail)
                                                    throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        return getDb().getCommentsForGiver(courseId, giverEmail);
    }

    public List<? extends BaseCommentAttributes> getCommentsForSendingState(String courseId, String fsName,
                                                                            CommentSendingState sendingState)
                                                 throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        return getDb().getCommentsForSendingState(courseId, fsName, sendingState);
    }

    public void updateCommentsSendingState(String courseId, String fsName, CommentSendingState oldState,
                                           CommentSendingState newState)
                throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "clear pending");
        getDb().updateComments(courseId, fsName, oldState, newState);
    }

    public BaseCommentAttributes updateComment(BaseCommentAttributes bca)
                                 throws InvalidParametersException, EntityDoesNotExistException {
        verifyIsCoursePresent(bca.courseId, "update");
        return getDb().updateComment(bca);
    }

    /**
     * update comment's giver email (assume to be an instructor)
     * @param courseId
     * @param oldInstrEmail
     * @param updatedInstrEmail
     */
    public void updateInstructorEmail(String courseId, String oldInstrEmail, String updatedInstrEmail) {
        getDb().updateGiverEmailOfComment(courseId, oldInstrEmail, updatedInstrEmail);
    }

    /**
     * Create or update document for comment
     * @param comment
     */
    public void putDocument(BaseCommentAttributes comment) {
        getDb().putDocument(comment);
    }

    /**
     * Remove document for the given comment
     * @param commentToDelete
     */
    public void deleteDocument(BaseCommentAttributes commentToDelete) {
        getDb().deleteDocument(commentToDelete);
    }

    public void deleteComment(BaseCommentAttributes comment) {
        getDb().deleteEntity(comment);
    }

    public void deleteCommentsForCourse(String courseId) {
        getDb().deleteCommentsForCourse(courseId);
    }

    public void deleteCommentsForInstructor(String courseId, String instructorEmail) {
        getDb().deleteCommentsForInstructorEmail(courseId, instructorEmail);
    }

    public BaseCommentSearchResultBundle search(String queryString, String googleId, String cursorString) {
        return getDb().search(queryString, googleId, cursorString);
    }

    protected void verifyIsCoursePresent(String courseId, String action) throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException("Trying to " + action
                                                               + " comments for a course that does not exist.");
        }
    }

    protected void verifyIsInstructorOfCourse(String courseId, String email) throws EntityDoesNotExistException {
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        if (instructor == null) {
            throw new EntityDoesNotExistException("User " + email + " is not a registered instructor for course "
                                                          + courseId + ".");
        }
    }

    @Deprecated
    public List<? extends BaseCommentAttributes> getAllComments() {
        return getDb().getAllComments();
    }

}
