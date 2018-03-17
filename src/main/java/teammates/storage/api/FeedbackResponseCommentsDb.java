package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.search.FeedbackResponseCommentSearchDocument;
import teammates.storage.search.FeedbackResponseCommentSearchQuery;
import teammates.storage.search.SearchDocument;

/**
 * Handles CRUD operations for feedback response comments.
 *
 * @see FeedbackResponseComment
 * @see FeedbackResponseCommentAttributes
 */
public class FeedbackResponseCommentsDb extends EntitiesDb<FeedbackResponseComment, FeedbackResponseCommentAttributes> {

    private static final Logger log = Logger.getLogger();

    /**
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    public FeedbackResponseCommentAttributes createFeedbackResponseComment(FeedbackResponseCommentAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return makeAttributesOrNull(createEntity(entityToAdd),
                "Trying to get non-existent FeedbackResponseComment, possibly entity not persistent yet.");
    }

    /*
     * Removes search document for the given comment
     */
    public void deleteDocument(FeedbackResponseCommentAttributes commentToDelete) {
        Long id = commentToDelete.getId();

        if (id == null) {
            Key<FeedbackResponseComment> key = getEntityQueryKeys(commentToDelete).first().now();

            if (key == null) {
                return;
            }

            id = key.getId();
        }

        deleteDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, id.toString());
    }

    /**
     * Removes search document for the comment with given id.
     *
     * @param commentId ID of comment
     */
    public void deleteDocumentByCommentId(long commentId) {
        deleteDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, String.valueOf(commentId));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseCommentId);

        return makeAttributesOrNull(getFeedbackResponseCommentEntity(feedbackResponseCommentId),
                "Trying to get non-existent response comment: " + feedbackResponseCommentId + ".");
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String feedbackResponseId, String giverEmail, Instant createdAt) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, createdAt);

        return makeAttributesOrNull(getFeedbackResponseCommentEntity(feedbackResponseId, giverEmail, createdAt),
                "Trying to get non-existent response comment: " + feedbackResponseId + "/from: " + giverEmail
                + "created at: " + createdAt);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String courseId, Instant createdAt, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, createdAt);

        return makeAttributesOrNull(getFeedbackResponseCommentEntity(courseId, createdAt, giverEmail),
                "Trying to get non-existent response comment: from: " + giverEmail + " in the course " + courseId
                + " created at: " + createdAt);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForGiver(String courseId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        return makeAttributes(getFeedbackResponseCommentEntitiesForGiverInCourse(courseId, giverEmail));
    }

    /*
     * Get response comments for the response Id
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponse(String feedbackResponseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);

        return makeAttributes(getFeedbackResponseCommentEntitiesForResponse(feedbackResponseId));
    }

    /*
     * Remove response comments for the response Id
     */
    public void deleteFeedbackResponseCommentsForResponse(String responseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, responseId);

        ofy().delete().keys(getFeedbackResponseCommentsForResponseQuery(responseId).keys()).now();
    }

    /*
     * Remove response comments for the course Ids
     */
    public void deleteFeedbackResponseCommentsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        ofy().delete().keys(getFeedbackResponseCommentsForCoursesQuery(courseIds).keys()).now();
    }

    public void deleteFeedbackResponseCommentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        deleteFeedbackResponseCommentsForCourses(Arrays.asList(courseId));
    }

    private Query<FeedbackResponseComment> getFeedbackResponseCommentsForCoursesQuery(List<String> courseIds) {
        return load().filter("courseId in", courseIds);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSession(
            String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);

        return makeAttributes(getFeedbackResponseCommentEntitiesForSession(courseId, feedbackSessionName));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSessionInSection(
            String courseId, String feedbackSessionName, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseCommentEntitiesForSessionInSection(courseId, feedbackSessionName, section));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(FeedbackResponseCommentAttributes newAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);

        newAttributes.sanitizeForSaving();

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        FeedbackResponseComment frc = getEntity(newAttributes);

        if (frc == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }

        frc.setCommentText(newAttributes.commentText);
        frc.setGiverSection(newAttributes.giverSection);
        frc.setReceiverSection(newAttributes.receiverSection);
        frc.setShowCommentTo(newAttributes.showCommentTo);
        frc.setShowGiverNameTo(newAttributes.showGiverNameTo);
        frc.setIsVisibilityFollowingFeedbackQuestion(false);
        frc.setLastEditorEmail(newAttributes.giverEmail);
        frc.setLastEditedAt(newAttributes.createdAt);

        if (newAttributes.feedbackResponseId != null) {
            frc.setFeedbackResponseId(newAttributes.feedbackResponseId);
        }

        saveEntity(frc, newAttributes);

        return makeAttributes(frc);
    }

    /*
     * Update giver email (normally an instructor email) with the new one
     */
    public void updateGiverEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updatedEmail);

        if (oldEmail.equals(updatedEmail)) {
            return;
        }

        List<FeedbackResponseComment> responseComments =
                getFeedbackResponseCommentEntitiesForGiverInCourse(courseId, oldEmail);

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setGiverEmail(updatedEmail);
        }

        saveEntities(responseComments);
    }

    /*
     * Updates last editor for all comments last edited by the given instructor with the instructor's new email
     */
    public void updateLastEditorEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updatedEmail);

        if (oldEmail.equals(updatedEmail)) {
            return;
        }

        List<FeedbackResponseComment> responseComments =
                getFeedbackResponseCommentEntitiesForLastEditorInCourse(courseId, oldEmail);

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setLastEditorEmail(updatedEmail);
        }

        saveEntities(responseComments);
        log.info("updating last editor email from: " + oldEmail + " to: " + updatedEmail
                 + " for feedback response comments in the course: " + courseId);
    }

    /*
     * Create or update search document for the given comment
     */
    public void putDocument(FeedbackResponseCommentAttributes comment) {
        putDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, new FeedbackResponseCommentSearchDocument(comment));
    }

    /*
     * Batch creates or updates search documents for the given comments
     */
    public void putDocuments(List<FeedbackResponseCommentAttributes> comments) {
        List<SearchDocument> frcSearchDocuments = new ArrayList<>();
        for (FeedbackResponseCommentAttributes comment : comments) {
            frcSearchDocuments.add(new FeedbackResponseCommentSearchDocument(comment));
        }
        putDocuments(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, frcSearchDocuments);
    }

    /**
     * Searches for response comments.
     * @return {@link FeedbackResponseCommentSearchResultBundle}
     */
    public FeedbackResponseCommentSearchResultBundle search(String queryString, List<InstructorAttributes> instructors) {
        if (queryString.trim().isEmpty()) {
            return new FeedbackResponseCommentSearchResultBundle();
        }

        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT,
                new FeedbackResponseCommentSearchQuery(instructors, queryString));

        return FeedbackResponseCommentSearchDocument.fromResults(results, instructors);
    }

    /**
     * Returns all feedback response comments in the Datastore.
     *
     * @deprecated Not scalable. Don't use unless in admin features.
     */
    @Deprecated
    public List<FeedbackResponseCommentAttributes> getAllFeedbackResponseComments() {
        return makeAttributes(load().list());
    }

    /**
     * Removes comment with given id.
     *
     * @param id ID of comment
     */
    public void deleteCommentById(Long id) {
        ofy().delete().keys(getEntityQueryKeys(id)).now();
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntity(String courseId, Instant createdAt, String giverEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("createdAt =", TimeHelper.convertInstantToDate(createdAt))
                .filter("giverEmail =", giverEmail)
                .first().now();
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntity(Long feedbackResponseCommentId) {
        return load().id(feedbackResponseCommentId).now();
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntity(
            String feedbackResponseId, String giverEmail, Instant createdAt) {
        return load()
                .filter("feedbackResponseId =", feedbackResponseId)
                .filter("giverEmail =", giverEmail)
                .filter("createdAt =", TimeHelper.convertInstantToDate(createdAt))
                .first().now();
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForGiverInCourse(
            String courseId, String giverEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("giverEmail =", giverEmail)
                .list();
    }

    /*
     * Gets a list of FeedbackResponseComments which have a last editor associated with the given email
     */
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForLastEditorInCourse(
            String courseId, String lastEditorEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("lastEditorEmail =", lastEditorEmail)
                .list();
    }

    private Query<FeedbackResponseComment> getFeedbackResponseCommentsForResponseQuery(String feedbackResponseId) {
        return load().filter("feedbackResponseId =", feedbackResponseId);
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForResponse(String feedbackResponseId) {
        return getFeedbackResponseCommentsForResponseQuery(feedbackResponseId).list();
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSession(
            String courseId, String feedbackSessionName) {
        return load()
                .filter("courseId =", courseId)
                .filter("feedbackSessionName =", feedbackSessionName)
                .list();
    }

    private Collection<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSessionInSection(
            String courseId, String feedbackSessionName, String section) {
        Map<Long, FeedbackResponseComment> comments = new HashMap<>();

        List<FeedbackResponseComment> firstQueryResponseComments = load()
                .filter("courseId =", courseId)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("giverSection =", section)
                .list();

        for (FeedbackResponseComment comment : firstQueryResponseComments) {
            comments.put(comment.getFeedbackResponseCommentId(), comment);
        }

        List<FeedbackResponseComment> secondQueryResponseComments = load()
                .filter("courseId =", courseId)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("receiverSection =", section)
                .list();

        for (FeedbackResponseComment comment : secondQueryResponseComments) {
            comments.put(comment.getFeedbackResponseCommentId(), comment);
        }

        return comments.values();
    }

    @Override
    protected LoadType<FeedbackResponseComment> load() {
        return ofy().load().type(FeedbackResponseComment.class);
    }

    @Override
    protected FeedbackResponseComment getEntity(FeedbackResponseCommentAttributes attributes) {
        if (attributes.getId() != null) {
            return getFeedbackResponseCommentEntity(attributes.getId());
        }

        return getFeedbackResponseCommentEntity(attributes.courseId, attributes.createdAt, attributes.giverEmail);
    }

    @Override
    protected QueryKeys<FeedbackResponseComment> getEntityQueryKeys(FeedbackResponseCommentAttributes attributes) {
        Long id = attributes.getId();

        if (id != null) {
            return getEntityQueryKeys(id);
        }

        return load()
                .filter("courseId =", attributes.courseId)
                .filter("createdAt =", TimeHelper.convertInstantToDate(attributes.createdAt))
                .filter("giverEmail =", attributes.giverEmail)
                .keys();
    }

    private QueryKeys<FeedbackResponseComment> getEntityQueryKeys(long commentId) {
        return load().filterKey(Key.create(FeedbackResponseComment.class, commentId)).keys();
    }

    @Override
    protected FeedbackResponseCommentAttributes makeAttributes(FeedbackResponseComment entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return FeedbackResponseCommentAttributes.valueOf(entity);
    }
}
