package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;

import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

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

}
