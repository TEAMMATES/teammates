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
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Section;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.ListJoin;
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

    /**
     * Updates the feedback response comment.
     */
    public FeedbackResponseComment updateFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment) {
        assert feedbackResponseComment != null;

        return merge(feedbackResponseComment);
    }

    /**
     * Gets all comments in a feedback session of a course.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForSession(
            String courseId, String feedbackSessionName) {
        assert courseId != null;
        assert feedbackSessionName != null;

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
                        cb.equal(fsJoin.get("name"), feedbackSessionName)
                        ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets all comments of a feedback question of a course.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForQuestion(UUID questionId) {
        assert questionId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponseComment> cq = cb.createQuery(FeedbackResponseComment.class);
        Root<FeedbackResponseComment> root = cq.from(FeedbackResponseComment.class);
        Join<FeedbackResponseComment, FeedbackResponse> frJoin = root.join("feedbackResponse");
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = frJoin.join("feedbackQuestion");

        cq.select(root)
                .where(cb.and(
                    cb.equal(fqJoin.get("id"), questionId)));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets all comments which have its corresponding response given to/from a section of a feedback session of a course.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForSessionInSection(
            String courseId, String feedbackSessionName, String sectionName) {
        assert courseId != null;
        assert feedbackSessionName != null;
        assert sectionName != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponseComment> cq = cb.createQuery(FeedbackResponseComment.class);
        Root<FeedbackResponseComment> root = cq.from(FeedbackResponseComment.class);
        Join<FeedbackResponseComment, FeedbackResponse> frJoin = root.join("feedbackResponse");
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = frJoin.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");
        ListJoin<Course, Section> sectionsJoin = cJoin.joinList("sections");

        cq.select(root)
                .where(cb.and(
                    cb.equal(cJoin.get("id"), courseId),
                    cb.equal(fsJoin.get("name"), feedbackSessionName),
                    cb.in(cb.literal(sectionName)).value(sectionsJoin.get("name"))
                    ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets all comments which have its corresponding response given to/from a section of a feedback question of a course.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForQuestionInSection(
            UUID questionId, String sectionName) {
        assert questionId != null;
        assert sectionName != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponseComment> cq = cb.createQuery(FeedbackResponseComment.class);
        Root<FeedbackResponseComment> root = cq.from(FeedbackResponseComment.class);
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = root.join("feedbackQuestion");
        Join<FeedbackQuestion, FeedbackSession> fsJoin = fqJoin.join("feedbackSession");
        Join<FeedbackSession, Course> cJoin = fsJoin.join("course");
        ListJoin<Course, Section> sectionsJoin = cJoin.joinList("sections");

        cq.select(root)
                .where(cb.and(
                    cb.equal(fqJoin.get("id"), questionId),
                    cb.in(cb.literal(sectionName)).value(sectionsJoin.get("name"))
                    ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

}
