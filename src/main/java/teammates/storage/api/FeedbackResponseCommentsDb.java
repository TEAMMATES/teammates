package teammates.storage.api;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.common.util.SanitizationHelper;
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackResponse;
import teammates.logic.entity.FeedbackResponseComment;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Section;

/**
 * Handles CRUD operations for feedbackResponseComments.
 *
 * @see FeedbackResponseComment
 */
public final class FeedbackResponseCommentsDb {

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
    public FeedbackResponseComment getFeedbackResponseComment(UUID frId) {
        assert frId != null;

        return HibernateUtil.get(FeedbackResponseComment.class, frId);
    }

    /**
     * Creates a feedbackResponseComment.
     */
    public FeedbackResponseComment createFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment) {
        assert feedbackResponseComment != null;

        HibernateUtil.persist(feedbackResponseComment);
        return feedbackResponseComment;
    }

    /**
     * Deletes a feedbackResponseComment.
     */
    public void deleteFeedbackResponseComment(FeedbackResponseComment frc) {
        HibernateUtil.remove(frc);
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
                        cb.equal(frJoin.get("id"), feedbackResponseId),
                        cb.equal(root.get("isCommentFromFeedbackParticipant"), true)));
        return HibernateUtil.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    /**
     * Updates the giver email for all of the giver's comments in a course.
     */
    public void updateGiverEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        assert courseId != null;
        assert oldEmail != null;
        assert updatedEmail != null;

        if (SanitizationHelper.areEmailsEqual(oldEmail, updatedEmail)) {
            return;
        }

        List<FeedbackResponseComment> responseComments =
                getFeedbackResponseCommentEntitiesForGiverInCourse(courseId, oldEmail);

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setGiver(updatedEmail);
        }
    }

    /**
     * Updates the last editor to a new one for all comments in a course.
     */
    public void updateLastEditorEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        assert courseId != null;
        assert oldEmail != null;
        assert updatedEmail != null;

        if (SanitizationHelper.areEmailsEqual(oldEmail, updatedEmail)) {
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
     * Gets all comments in the given session where the giver or recipient is in the given section.
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

        Join<FeedbackResponseComment, Section> giverJoin = root.join("giverSection");
        Join<FeedbackResponseComment, Section> recipientJoin = root.join("recipientSection");

        cq.select(root)
                .where(cb.and(
                    cb.equal(cJoin.get("id"), courseId),
                    cb.equal(fsJoin.get("name"), feedbackSessionName),
                    cb.or(
                        cb.equal(giverJoin.get("name"), sectionName),
                        cb.equal(recipientJoin.get("name"), sectionName))
                    ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

    /**
     * Gets all comments for a question where the giver or recipient is in the given section.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForQuestionInSection(
            UUID questionId, String sectionName) {
        assert questionId != null;
        assert sectionName != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackResponseComment> cq = cb.createQuery(FeedbackResponseComment.class);
        Root<FeedbackResponseComment> root = cq.from(FeedbackResponseComment.class);
        Join<FeedbackResponseComment, FeedbackResponse> frJoin = root.join("feedbackResponse");
        Join<FeedbackResponse, FeedbackQuestion> fqJoin = frJoin.join("feedbackQuestion");

        Join<FeedbackResponseComment, Section> giverJoin = root.join("giverSection");
        Join<FeedbackResponseComment, Section> recipientJoin = root.join("recipientSection");

        cq.select(root)
                .where(cb.and(
                    cb.equal(fqJoin.get("id"), questionId),
                    cb.or(
                        cb.equal(giverJoin.get("name"), sectionName),
                        cb.equal(recipientJoin.get("name"), sectionName))
                    ));

        return HibernateUtil.createQuery(cq).getResultList();
    }

}
