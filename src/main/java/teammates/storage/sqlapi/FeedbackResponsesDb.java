package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;

import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

/**
 * Handles CRUD operations for feedbackResponses.
 *
 * @see FeedbackResponse
 */
public final class FeedbackResponsesDb extends EntitiesDb {

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
    public FeedbackResponse createFeedbackResponse(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert feedbackResponse != null;

        if (!feedbackResponse.isValid()) {
            throw new InvalidParametersException(feedbackResponse.getInvalidityInfo());
        }

        if (getFeedbackResponse(feedbackResponse.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, feedbackResponse.toString()));
        }

        persist(feedbackResponse);
        return feedbackResponse;
    }

    /**
     * Deletes a feedbackResponse.
     */
    public void deleteFeedbackResponse(FeedbackResponse feedbackResponse) {
        if (feedbackResponse != null) {
            delete(feedbackResponse);
        }
    }

    /**
     * Gets the feedback responses for a feedback question.
     * @param feedbackQuestionId the Id of the feedback question.
     * @param giverEmail the email of the response giver.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForQuestion(
            UUID feedbackQuestionId, String giverEmail) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> frJoin = root.join("feedbackQuestion");
        cq.select(root)
                .where(cb.and(
                        cb.equal(frJoin.get("id"), feedbackQuestionId),
                        cb.equal(root.get("giver"), giverEmail)));
        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Checks whether there are responses for a question.
     */
    public boolean areThereResponsesForQuestion(UUID questionId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");

        cq.select(root)
                .where(cb.equal(fqJoin.get("id"), questionId));
        return !HibernateUtil.createQuery(cq).getResultList().isEmpty();
    }

    /**
     * Checks whether a user has responses in a session.
     */
    public boolean hasResponsesFromGiverInSession(
        String giver, String feedbackSessionName, String courseId) {

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> courseJoin = fsJoin.join("course");

        cq.select(root)
                .where(cb.and(
                        cb.equal(root.get("giver"), giver),
                        cb.equal(fsJoin.get("name"), feedbackSessionName),
                        cb.equal(courseJoin.get("id"), courseId)));

        return !HibernateUtil.createQuery(cq).getResultList().isEmpty();
    }

}
