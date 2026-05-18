package teammates.storage.api;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;

/**
 * Handles CRUD operations for feedbackResponses.
 *
 * @see FeedbackResponse
 */
public final class FeedbackResponsesDb {

    private static final FeedbackResponsesDb instance = new FeedbackResponsesDb();

    private FeedbackResponsesDb() {
        // prevent initialization
    }

    public static FeedbackResponsesDb inst() {
        return instance;
    }

    /**
     * Gets a feedbackResponse or null if it does not exist.
     */
    public FeedbackResponse getFeedbackResponse(UUID frId) {
        assert frId != null;

        return HibernateUtil.get(FeedbackResponse.class, frId);
    }

    /**
     * Creates a feedbackResponse.
     */
    public FeedbackResponse createFeedbackResponse(FeedbackResponse feedbackResponse) {
        assert feedbackResponse != null;

        HibernateUtil.persist(feedbackResponse);
        return feedbackResponse;
    }

    /**
     * Deletes a feedbackResponse.
     */
    public void deleteFeedbackResponse(FeedbackResponse feedbackResponse) {
        HibernateUtil.remove(feedbackResponse);
    }

    /**
     * Gets the feedback responses for a feedback question.
     * @param feedbackQuestionId the Id of the feedback question.
     * @param giverUserId the userId of the response giver.
     * @param giverTeamId the teamId of the response giver.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForQuestion(
            UUID feedbackQuestionId, UUID giverUserId, UUID giverTeamId) {
        if (giverUserId == null && giverTeamId == null) {
            return List.of();
        }

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> frJoin = root.join("feedbackQuestion");
        if (giverTeamId != null) {
            cq.select(root).where(cb.and(
                    cb.equal(frJoin.get("id"), feedbackQuestionId),
                    cb.equal(root.get("giver").get("giverTeamId"), giverTeamId)));
        } else {
            cq.select(root).where(cb.and(
                    cb.equal(frJoin.get("id"), feedbackQuestionId),
                    cb.equal(root.get("giver").get("giverUserId"), giverUserId)));
        }

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Checks whether there are responses for a question.
     */
    public boolean areThereResponsesForQuestion(UUID questionId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");

        cq.select(cb.count(root))
                .where(cb.equal(fqJoin.get("id"), questionId));
        return HibernateUtil.createQuery(cq).getSingleResult() > 0;
    }

    /**
     * Get responses for a question.
     */
    public List<FeedbackResponse> getResponsesForQuestion(UUID questionId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");

        cq.select(root)
                .where(cb.equal(fqJoin.get("id"), questionId));
        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Checks whether there are responses for a course.
     */
    public boolean hasResponsesForCourse(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> courseJoin = fsJoin.join("course");

        cq.select(root)
                .where(cb.equal(courseJoin.get("id"), courseId));

        return !HibernateUtil.createQuery(cq).getResultList().isEmpty();
    }

    /**
     * Gets all responses received by a user for a question.
     */
    public List<FeedbackResponse> getFeedbackResponsesForRecipientForQuestion(
            UUID questionId, UUID recipientUserId, UUID recipientTeamId) {
        if (recipientUserId == null && recipientTeamId == null) {
            return List.of();
        }

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");

        if (recipientTeamId != null) {
            cq.select(root).where(cb.and(
                    cb.equal(fqJoin.get("id"), questionId),
                    cb.equal(root.get("recipient").get("recipientTeamId"), recipientTeamId)
            ));
        } else {
            cq.select(root).where(cb.and(
                    cb.equal(fqJoin.get("id"), questionId),
                    cb.equal(root.get("recipient").get("recipientUserId"), recipientUserId)
            ));
        }

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets all responses of a feedback session in a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesForSession(
            FeedbackSession feedbackSession, String courseId) {
        assert feedbackSession != null;
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");

        cq.select(root)
                .where(cb.and(
                    cb.equal(fsJoin.get("id"), feedbackSession.getId()),
                    cb.equal(cJoin.get("id"), courseId)
                    ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

}
