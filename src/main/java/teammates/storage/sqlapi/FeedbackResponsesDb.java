package teammates.storage.sqlapi;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Section;

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
     * Gets all responses given by a user in a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForCourse(
            String courseId, String giver) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cr = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> frRoot = cr.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = frRoot.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");

        cr.select(frRoot)
                .where(cb.and(
                    cb.equal(cJoin.get("id"), courseId),
                    cb.equal(frRoot.get("giver"), giver)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all responses given to a user in a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesForRecipientForCourse(String courseId, String recipient) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cr = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> frRoot = cr.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = frRoot.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");

        cr.select(frRoot)
                .where(cb.and(
                    cb.equal(cJoin.get("id"), courseId),
                    cb.equal(frRoot.get("recipient"), recipient)));

        return HibernateUtil.createQuery(cr).getResultList();
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
        if (feedbackResponse != null) {
            HibernateUtil.remove(feedbackResponse);
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
            UUID questionId, String recipient) {
        assert questionId != null;
        assert recipient != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");

        cq.select(root)
                .where(cb.and(
                    cb.equal(fqJoin.get("id"), questionId),
                    cb.equal(root.get("recipient"), recipient)
                    ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets all responses given to/from a section in a feedback session in a course.
     * Optionally, retrieves by either giver, receiver sections, or both.
     */
    public List<FeedbackResponse> getFeedbackResponsesForSessionInSection(
            FeedbackSession feedbackSession, String courseId, String sectionName, FeedbackResultFetchType fetchType) {
        assert feedbackSession != null;
        assert courseId != null;
        assert sectionName != null;
        assert fetchType != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");

        Join<FeedbackResponse, Section> giverJoin = root.join("giverSection");
        Join<FeedbackResponse, Section> recipientJoin = root.join("recipientSection");

        Predicate sectionFilter;
        if (fetchType.shouldFetchByGiver() && fetchType.shouldFetchByReceiver()) {
            sectionFilter = cb.or(
                cb.equal(giverJoin.get("name"), sectionName),
                cb.equal(recipientJoin.get("name"), sectionName)
            );
        } else if (fetchType.shouldFetchByGiver()) {
            sectionFilter = cb.equal(giverJoin.get("name"), sectionName);
        } else if (fetchType.shouldFetchByReceiver()) {
            sectionFilter = cb.equal(recipientJoin.get("name"), sectionName);
        } else {
            sectionFilter = cb.conjunction();
        }

        cq.select(root)
                .where(cb.and(
                    cb.equal(fsJoin.get("id"), feedbackSession.getId()),
                    cb.equal(cJoin.get("id"), courseId),
                    sectionFilter
                    ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets all feedback responses of a question in a specific section.
     */
    public List<FeedbackResponse> getFeedbackResponsesForQuestionInSection(
            UUID questionId, String sectionName, FeedbackResultFetchType fetchType) {
        assert questionId != null;
        assert sectionName != null;
        assert fetchType != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cq = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> root = cq.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");

        Join<FeedbackResponse, Section> giverJoin = root.join("giverSection");
        Join<FeedbackResponse, Section> recipientJoin = root.join("recipientSection");

        Predicate sectionFilter;
        if (fetchType.shouldFetchByGiver() && fetchType.shouldFetchByReceiver()) {
            sectionFilter = cb.or(
                cb.equal(giverJoin.get("name"), sectionName),
                cb.equal(recipientJoin.get("name"), sectionName)
            );
        } else if (fetchType.shouldFetchByGiver()) {
            sectionFilter = cb.equal(giverJoin.get("name"), sectionName);
        } else if (fetchType.shouldFetchByReceiver()) {
            sectionFilter = cb.equal(recipientJoin.get("name"), sectionName);
        } else {
            sectionFilter = cb.conjunction();
        }

        cq.select(root)
                .where(cb.and(
                    cb.equal(fqJoin.get("id"), questionId),
                    sectionFilter
                ));

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
