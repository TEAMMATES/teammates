package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
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
     * Gets all responses with a specific giver and recipient in a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesForGiverAndRecipientForCourse(String courseId, String giver,
            String recipient) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponse> cr = cb.createQuery(FeedbackResponse.class);
        Root<FeedbackResponse> frRoot = cr.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = frRoot.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");

        cr.select(frRoot)
                .where(cb.and(
                    cb.equal(cJoin.get("id"), courseId),
                    cb.equal(frRoot.get("recipient"), recipient),
                    cb.equal(frRoot.get("giver"), giver)));

        return HibernateUtil.createQuery(cr).getResultList();
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
     * Deletes all feedback responses of a question cascade its associated comments.
     */
    public void deleteFeedbackResponsesForQuestionCascade(UUID feedbackQuestionId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaDelete<FeedbackResponse> cd = cb.createCriteriaDelete(FeedbackResponse.class);
        Root<FeedbackResponse> frRoot = cd.from(FeedbackResponse.class);
        Subquery<UUID> subquery = cd.subquery(UUID.class);
        Root<FeedbackResponse> subqueryRoot = subquery.from(FeedbackResponse.class);
        Join<FeedbackResponse, FeedbackQuestion> sqJoin = subqueryRoot.join("feedbackQuestion");
        subquery.select(subqueryRoot.get("id"));
        subquery.where(cb.equal(sqJoin.get("id"), feedbackQuestionId));
        cd.where(cb.in(frRoot.get("id")).value(subquery));
        HibernateUtil.createMutationQuery(cd).executeUpdate();
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
     * Updates a feedbackResponse.
     *
     * @throws EntityDoesNotExistException if the feedbackResponse does not exist
     * @throws InvalidParametersException if the feedbackResponse is not valid
     */
    public FeedbackResponse updateFeedbackResponse(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert feedbackResponse != null;

        if (!feedbackResponse.isValid()) {
            throw new InvalidParametersException(feedbackResponse.getInvalidityInfo());
        }

        if (getFeedbackResponse(feedbackResponse.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(feedbackResponse);
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

        // unless specified by fetchType, do not filter by giver/recipient section
        Predicate giverSectionFilter = cb.isTrue(cb.literal(true));
        Predicate recipientSectionFilter = cb.isTrue(cb.literal(true));
        Join<FeedbackResponse, Section> giverJoin = root.join("giverSection");
        Join<FeedbackResponse, Section> recipientJoin = root.join("recipientSection");

        if (fetchType.shouldFetchByGiver()) {
            giverSectionFilter = cb.equal(giverJoin.get("name"), sectionName);
        }
        if (fetchType.shouldFetchByReceiver()) {
            recipientSectionFilter = cb.equal(recipientJoin.get("name"), sectionName);
        }

        cq.select(root)
                .where(cb.and(
                    cb.equal(fsJoin.get("id"), feedbackSession.getId()),
                    cb.equal(cJoin.get("id"), courseId),
                    giverSectionFilter,
                    recipientSectionFilter
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

        // unless specified by fetchType, do not filter by giver/recipient section
        Predicate giverSectionFilter = cb.isTrue(cb.literal(true));
        Predicate recipientSectionFilter = cb.isTrue(cb.literal(true));
        Join<FeedbackResponse, Section> giverJoin = root.join("giverSection");
        Join<FeedbackResponse, Section> recipientJoin = root.join("recipientSection");

        if (fetchType.shouldFetchByGiver()) {
            giverSectionFilter = cb.equal(giverJoin.get("name"), sectionName);
        }
        if (fetchType.shouldFetchByReceiver()) {
            recipientSectionFilter = cb.equal(recipientJoin.get("name"), sectionName);
        }

        cq.select(root)
                .where(cb.and(
                    cb.equal(fqJoin.get("id"), questionId),
                    giverSectionFilter,
                    recipientSectionFilter
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
