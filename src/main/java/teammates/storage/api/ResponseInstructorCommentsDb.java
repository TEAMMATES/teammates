package teammates.storage.api;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.ResponseInstructorComment;

/**
 * Handles CRUD operations for responseInstructorComments.
 *
 * @see ResponseInstructorComment
 */
public final class ResponseInstructorCommentsDb {

    private static final ResponseInstructorCommentsDb instance = new ResponseInstructorCommentsDb();

    private ResponseInstructorCommentsDb() {
        // prevent initialization
    }

    public static ResponseInstructorCommentsDb inst() {
        return instance;
    }

    /**
    * Gets a responseInstructorComment or null if it does not exist.
     */
    public ResponseInstructorComment getResponseInstructorComment(UUID frId) {
        assert frId != null;

        return HibernateUtil.get(ResponseInstructorComment.class, frId);
    }

    /**
    * Creates a responseInstructorComment.
     */
    public ResponseInstructorComment createResponseInstructorComment(ResponseInstructorComment responseInstructorComment) {
        assert responseInstructorComment != null;

        HibernateUtil.persist(responseInstructorComment);
        return responseInstructorComment;
    }

    /**
    * Deletes a responseInstructorComment.
     */
    public void deleteResponseInstructorComment(ResponseInstructorComment frc) {
        HibernateUtil.remove(frc);
    }

    /**
     * Gets all comments for the given feedback response IDs.
     */
    public List<ResponseInstructorComment> getResponseInstructorCommentsForResponses(List<UUID> feedbackResponseIds) {
        assert feedbackResponseIds != null;

        if (feedbackResponseIds.isEmpty()) {
            return List.of();
        }

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<ResponseInstructorComment> cq = cb.createQuery(ResponseInstructorComment.class);
        Root<ResponseInstructorComment> root = cq.from(ResponseInstructorComment.class);

        cq.select(root)
                .where(root.get("responseId").in(feedbackResponseIds));

        return HibernateUtil.createQuery(cq).getResultList();
    }

}
