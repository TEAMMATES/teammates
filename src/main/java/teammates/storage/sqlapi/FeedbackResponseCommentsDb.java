package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Section;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/**
 * Handles CRUD operations for feedbackResponseComments.
 *
 * @see FeedbackResponseComment
 */
public final class FeedbackResponseCommentsDb extends EntitiesDb {

    private static final FeedbackResponseCommentsDb instance = new FeedbackResponseCommentsDb();

    private FeedbackResponseCommentsDb() {
        // prevent initialization
    }

    public static FeedbackResponseCommentsDb inst() {
        return instance;
    }

    /**
     * Gets a feedbackResponseComment or null if it does not exist.
     */
    public FeedbackResponseComment getFeedbackResponseComment(Long frId) {
        assert frId != null;

        return HibernateUtil.get(FeedbackResponseComment.class, frId);
    }

    /**
     * Creates a feedbackResponseComment.
     */
    public FeedbackResponseComment createFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert feedbackResponseComment != null;

        if (!feedbackResponseComment.isValid()) {
            throw new InvalidParametersException(feedbackResponseComment.getInvalidityInfo());
        }

        if (feedbackResponseComment.getId() != null
                && getFeedbackResponseComment(feedbackResponseComment.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, feedbackResponseComment.toString()));
        }

        persist(feedbackResponseComment);
        return feedbackResponseComment;
    }

    /**
     * Deletes a feedbackResponseComment.
     */
    public void deleteFeedbackResponseComment(Long frcId) {
        assert frcId != null;

        FeedbackResponseComment frc = getFeedbackResponseComment(frcId);
        if (frc != null) {
            delete(frc);
        }
    }

    /**
     * Gets all feedback response comments for a response.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForResponse(UUID feedbackResponseId) {
        assert feedbackResponseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponseComment> cq = cb.createQuery(FeedbackResponseComment.class);
        Root<FeedbackResponseComment> root = cq.from(FeedbackResponseComment.class);
        Join<FeedbackResponseComment, FeedbackResponse> frJoin = root.join("feedbackResponse");
        cq.select(root)
                .where(cb.and(
                        cb.equal(frJoin.get("id"), feedbackResponseId)));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets the comment associated with the feedback response.
     */
    public FeedbackResponseComment getFeedbackResponseCommentForResponseFromParticipant(
            UUID feedbackResponseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponseComment> cq = cb.createQuery(FeedbackResponseComment.class);
        Root<FeedbackResponseComment> root = cq.from(FeedbackResponseComment.class);
        Join<FeedbackResponseComment, FeedbackResponse> frJoin = root.join("feedbackResponse");
        cq.select(root)
                .where(cb.and(
                        cb.equal(frJoin.get("id"), feedbackResponseId)));
        return HibernateUtil.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    /**
     * Updates a feedback response comment by {@link FeedbackResponseComment}.
     *
     * @return updated feedback response comment
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponseComment updateFeedbackResponseComment(FeedbackResponseComment newFeedbackResponseComment)
        throws InvalidParametersException, EntityDoesNotExistException {
        assert newFeedbackResponseComment != null;

        FeedbackResponseComment oldFeedbackResponseComment = getFeedbackResponseComment(newFeedbackResponseComment.getId());
        if (oldFeedbackResponseComment == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + newFeedbackResponseComment);
        }
        
        newFeedbackResponseComment.sanitizeForSaving();
        if (!newFeedbackResponseComment.isValid()) {
            throw new InvalidParametersException(newFeedbackResponseComment.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes = 
            this.<Long>hasSameValue(newFeedbackResponseComment.getId(), oldFeedbackResponseComment.getId())
            && this.<String>hasSameValue(
                newFeedbackResponseComment.getCommentText(), oldFeedbackResponseComment.getCommentText())
            && this.<List<FeedbackParticipantType>>hasSameValue(
                newFeedbackResponseComment.getShowCommentTo(), oldFeedbackResponseComment.getShowCommentTo())
            && this.<List<FeedbackParticipantType>>hasSameValue(
                newFeedbackResponseComment.getShowGiverNameTo(), oldFeedbackResponseComment.getShowGiverNameTo())
            && this.<String>hasSameValue(
                newFeedbackResponseComment.getLastEditorEmail(), oldFeedbackResponseComment.getLastEditorEmail())
            && this.<Instant>hasSameValue(
                newFeedbackResponseComment.getUpdatedAt(), oldFeedbackResponseComment.getUpdatedAt())
            && this.<Section>hasSameValue(
                newFeedbackResponseComment.getGiverSection(), oldFeedbackResponseComment.getGiverSection())
            && this.<Section>hasSameValue(
                newFeedbackResponseComment.getRecipientSection(), oldFeedbackResponseComment.getRecipientSection());
        if (hasSameAttributes) {
            log.info(String.format(
                    OPTIMIZED_SAVING_POLICY_APPLIED, 
                    FeedbackResponseComment.class.getSimpleName(), 
                    newFeedbackResponseComment));
            return newFeedbackResponseComment;
        }

        merge(newFeedbackResponseComment);
        return newFeedbackResponseComment;
    }

    /**
     * Updates the giver email for all of the giver's comments in a course.
     */
    public void updateGiverEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        assert courseId != null;
        assert oldEmail != null;
        assert updatedEmail != null;

        if (oldEmail.equals(updatedEmail)) {
            return;
        }

        List<FeedbackResponseComment> responseComments =
                getFeedbackResponseCommentEntitiesForGiverInCourse(courseId, oldEmail);

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setGiver(updatedEmail);
            merge(responseComment);
        }
    }

    /**
     * Updates the last editor to a new one for all comments in a course.
     */
    public void updateLastEditorEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        assert courseId != null;
        assert oldEmail != null;
        assert updatedEmail != null;

        if (oldEmail.equals(updatedEmail)) {
            return;
        }

        List<FeedbackResponseComment> responseComments =
                getFeedbackResponseCommentEntitiesForLastEditorInCourse(courseId, oldEmail);

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setLastEditorEmail(updatedEmail);
        }
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForGiverInCourse(
            String courseId, String giver) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponseComment> cq = cb.createQuery(FeedbackResponseComment.class);
        Root<FeedbackResponseComment> root = cq.from(FeedbackResponseComment.class);
        Join<FeedbackResponseComment, FeedbackResponse> frJoin = root.join("feedbackResponse");
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = frJoin.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");

        cq.select(root)
                .where(cb.and(
                    cb.equal(cJoin.get("id"), courseId),
                    cb.equal(root.get("giver"), giver)));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForLastEditorInCourse(
            String courseId, String lastEditorEmail) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponseComment> cq = cb.createQuery(FeedbackResponseComment.class);
        Root<FeedbackResponseComment> root = cq.from(FeedbackResponseComment.class);
        Join<FeedbackResponseComment, FeedbackResponse> frJoin = root.join("feedbackResponse");
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = frJoin.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");

        cq.select(root)
                .where(cb.and(
                    cb.equal(cJoin.get("id"), courseId),
                    cb.equal(root.get("lastEditorEmail"), lastEditorEmail)));

        return HibernateUtil.createQuery(cq).getResultList();
    }

}
