package teammates.client.scripts.sql;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import com.googlecode.objectify.Objectify;

import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.questions.FeedbackConstantSumQuestion.FeedbackConstantSumQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackContributionQuestion.FeedbackContributionQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackMcqQuestion.FeedbackMcqQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackMsqQuestion.FeedbackMsqQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackNumericalScaleQuestion.FeedbackNumericalScaleQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackRankOptionsQuestion.FeedbackRankOptionsQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackRankRecipientsQuestion.FeedbackRankRecipientsQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackRubricQuestion.FeedbackRubricQuestionDetailsConverter;
import teammates.storage.sqlentity.questions.FeedbackTextQuestion.FeedbackTextQuestionDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackConstantSumResponse.FeedbackConstantSumResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackContributionResponse.FeedbackContributionResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackMcqResponse.FeedbackMcqResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackMsqResponse.FeedbackMsqResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackNumericalScaleResponse.FeedbackNumericalScaleResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRankOptionsResponse.FeedbackRankOptionsResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse.FeedbackRankRecipientsResponseDetailsConverter;
import teammates.storage.sqlentity.responses.FeedbackRubricResponse.FeedbackRubricResponseDetailsConverter;

/**
 * Shared migration logic for the feedback chain (FeedbackSession, FeedbackQuestion,
 * FeedbackResponse, FeedbackResponseComment). Used by {@link DataMigrationForCourseEntitySql}.
 */
public final class FeedbackChainMigrator {

    private static final int MAX_LENGTH_2000 = 2000;

    private final Supplier<Objectify> ofySupplier;

    /**
     * Creates a FeedbackChainMigrator with the given Objectify supplier.
     *
     * @param ofySupplier supplies the Objectify instance for Datastore reads
     *                    (e.g. {@code this::ofy} from a {@link teammates.client.connector.DatastoreClient})
     */
    public FeedbackChainMigrator(Supplier<Objectify> ofySupplier) {
        this.ofySupplier = ofySupplier;
    }

    /**
     * Migrates all feedback sessions and their questions, responses, and comments for the course.
     * Runs in a single transaction. Caller must not be inside another transaction.
     */
    public void migrate(String courseId) {
        HibernateUtil.beginTransaction();
        try {
            List<teammates.storage.entity.FeedbackSession> oldFeedbackSessions = ofySupplier.get().load()
                    .type(teammates.storage.entity.FeedbackSession.class)
                    .filter("courseId", courseId)
                    .list();

            Course newCourse = getCourse(courseId);

            Map<String, List<FeedbackQuestion>> feedbackSessionNameToQuestionsMap = ofySupplier.get().load()
                    .type(FeedbackQuestion.class)
                    .filter("courseId", courseId)
                    .list().stream()
                    .collect(Collectors.groupingBy(FeedbackQuestion::getFeedbackSessionName));

            Map<String, Section> sectionNameToSectionMap = new HashMap<>();
            for (Section s : getSections(courseId)) {
                sectionNameToSectionMap.put(s.getName(), s);
            }

            for (teammates.storage.entity.FeedbackSession oldFeedbackSession : oldFeedbackSessions) {
                FeedbackSession newFeedbackSession = createFeedbackSession(newCourse, oldFeedbackSession);
                HibernateUtil.persist(newFeedbackSession);

                String oldFeedbackSessionName = oldFeedbackSession.getFeedbackSessionName();
                List<FeedbackQuestion> oldQuestions = feedbackSessionNameToQuestionsMap.get(oldFeedbackSessionName);
                if (oldQuestions == null) {
                    continue;
                }
                for (FeedbackQuestion oldQuestion : oldQuestions) {
                    migrateFeedbackQuestion(newFeedbackSession, oldQuestion, sectionNameToSectionMap);
                }
            }
            HibernateUtil.commitTransaction();
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction();
            throw e;
        }
    }

    private Course getCourse(String courseId) {
        return HibernateUtil.get(Course.class, courseId);
    }

    private List<Section> getSections(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Section> cr = cb.createQuery(Section.class);
        Root<Section> sectionRoot = cr.from(Section.class);
        cr.select(sectionRoot).where(cb.equal(sectionRoot.get("course").get("id"), courseId));
        return HibernateUtil.createQuery(cr).getResultList();
    }

    private void migrateFeedbackQuestion(FeedbackSession newSession, FeedbackQuestion oldQuestion,
            Map<String, Section> sectionNameToSectionMap) {
        teammates.storage.sqlentity.FeedbackQuestion newFeedbackQuestion =
                createFeedbackQuestion(newSession, oldQuestion);
        HibernateUtil.persist(newFeedbackQuestion);

        Map<String, List<FeedbackResponseComment>> responseIdToCommentsMap = ofySupplier.get().load()
                .type(FeedbackResponseComment.class)
                .filter("feedbackQuestionId", oldQuestion.getId()).list().stream()
                .collect(Collectors.groupingBy(FeedbackResponseComment::getFeedbackResponseId));

        List<FeedbackResponse> oldResponses = ofySupplier.get().load()
                .type(FeedbackResponse.class)
                .filter("feedbackQuestionId", oldQuestion.getId())
                .list();

        for (FeedbackResponse oldResponse : oldResponses) {
            String giverSectionName = oldResponse.getGiverSection();
            String recipientSectionName = oldResponse.getRecipientSection();
            String giverSectionKey = (giverSectionName == null || giverSectionName.isEmpty())
                    ? Const.DEFAULT_SECTION : giverSectionName;
            String recipientSectionKey = (recipientSectionName == null || recipientSectionName.isEmpty())
                    ? Const.DEFAULT_SECTION : recipientSectionName;
            Section newGiverSection = sectionNameToSectionMap.get(giverSectionKey);
            Section newRecipientSection = sectionNameToSectionMap.get(recipientSectionKey);
            migrateFeedbackResponse(newFeedbackQuestion, oldResponse, newGiverSection, newRecipientSection,
                    responseIdToCommentsMap);
        }
    }

    private void migrateFeedbackResponse(teammates.storage.sqlentity.FeedbackQuestion newQuestion,
            FeedbackResponse oldResponse, Section newGiverSection, Section newRecipientSection,
            Map<String, List<FeedbackResponseComment>> responseIdToCommentsMap) {
        teammates.storage.sqlentity.FeedbackResponse newResponse = createFeedbackResponse(newQuestion, oldResponse,
                newGiverSection, newRecipientSection);
        HibernateUtil.persist(newResponse);

        List<FeedbackResponseComment> oldComments = responseIdToCommentsMap.getOrDefault(
                oldResponse.getId(), Collections.emptyList());
        for (FeedbackResponseComment oldComment : oldComments) {
            migrateFeedbackResponseComment(newResponse, oldComment, newGiverSection, newRecipientSection);
        }
    }

    private void migrateFeedbackResponseComment(teammates.storage.sqlentity.FeedbackResponse newResponse,
            FeedbackResponseComment oldComment, Section newGiverSection, Section newRecipientSection) {
        teammates.storage.sqlentity.FeedbackResponseComment newComment = createFeedbackResponseComment(newResponse,
                oldComment, newGiverSection, newRecipientSection);
        HibernateUtil.persist(newComment);
    }

    private FeedbackSession createFeedbackSession(Course newCourse,
            teammates.storage.entity.FeedbackSession oldSession) {
        String truncatedSessionInstructions = truncate(oldSession.getInstructions(), MAX_LENGTH_2000);

        FeedbackSession newSession = new FeedbackSession(
                oldSession.getFeedbackSessionName(),
                newCourse,
                oldSession.getCreatorEmail(),
                truncatedSessionInstructions,
                oldSession.getStartTime(),
                oldSession.getEndTime(),
                oldSession.getSessionVisibleFromTime(),
                oldSession.getResultsVisibleFromTime(),
                Duration.ofMinutes(oldSession.getGracePeriod()),
                oldSession.isOpenedEmailEnabled(),
                oldSession.isClosingSoonEmailEnabled(),
                oldSession.isPublishedEmailEnabled());

        newSession.setClosedEmailSent(oldSession.isSentClosedEmail());
        newSession.setClosingSoonEmailSent(oldSession.isSentClosingSoonEmail());
        newSession.setOpenedEmailSent(oldSession.isSentOpenedEmail());
        newSession.setOpeningSoonEmailSent(oldSession.isSentOpeningSoonEmail());
        newSession.setPublishedEmailSent(oldSession.isSentPublishedEmail());
        newSession.setCreatedAt(oldSession.getCreatedTime());
        newSession.setDeletedAt(oldSession.getDeletedTime());

        return newSession;
    }

    private teammates.storage.sqlentity.FeedbackQuestion createFeedbackQuestion(FeedbackSession newSession,
            FeedbackQuestion oldQuestion) {
        teammates.storage.sqlentity.FeedbackQuestion newFeedbackQuestion =
                teammates.storage.sqlentity.FeedbackQuestion.makeQuestion(
                        newSession,
                        oldQuestion.getQuestionNumber(),
                        oldQuestion.getQuestionDescription(),
                        oldQuestion.getGiverType(),
                        oldQuestion.getRecipientType(),
                        oldQuestion.getNumberOfEntitiesToGiveFeedbackTo(),
                        oldQuestion.getShowResponsesTo(),
                        oldQuestion.getShowGiverNameTo(),
                        oldQuestion.getShowRecipientNameTo(),
                        getFeedbackQuestionDetails(oldQuestion));

        newFeedbackQuestion.setCreatedAt(oldQuestion.getCreatedAt());
        return newFeedbackQuestion;
    }

    private FeedbackQuestionDetails getFeedbackQuestionDetails(FeedbackQuestion oldQuestion) {
        switch (oldQuestion.getQuestionType()) {
        case MCQ:
            return new FeedbackMcqQuestionDetailsConverter().convertToEntityAttribute(oldQuestion.getQuestionText());
        case MSQ:
            return new FeedbackMsqQuestionDetailsConverter().convertToEntityAttribute(oldQuestion.getQuestionText());
        case TEXT:
            return new FeedbackTextQuestionDetailsConverter().convertToEntityAttribute(oldQuestion.getQuestionText());
        case RUBRIC:
            return new FeedbackRubricQuestionDetailsConverter().convertToEntityAttribute(oldQuestion.getQuestionText());
        case CONTRIB:
            return new FeedbackContributionQuestionDetailsConverter()
                    .convertToEntityAttribute(oldQuestion.getQuestionText());
        case CONSTSUM:
        case CONSTSUM_RECIPIENTS:
        case CONSTSUM_OPTIONS:
            return new FeedbackConstantSumQuestionDetailsConverter()
                    .convertToEntityAttribute(oldQuestion.getQuestionText());
        case NUMSCALE:
            return new FeedbackNumericalScaleQuestionDetailsConverter()
                    .convertToEntityAttribute(oldQuestion.getQuestionText());
        case RANK_OPTIONS:
            return new FeedbackRankOptionsQuestionDetailsConverter()
                    .convertToEntityAttribute(oldQuestion.getQuestionText());
        case RANK_RECIPIENTS:
            return new FeedbackRankRecipientsQuestionDetailsConverter()
                    .convertToEntityAttribute(oldQuestion.getQuestionText());
        default:
            throw new IllegalArgumentException("Invalid question type");
        }
    }

    private teammates.storage.sqlentity.FeedbackResponse createFeedbackResponse(
            teammates.storage.sqlentity.FeedbackQuestion newQuestion, FeedbackResponse oldResponse,
            Section giverSection, Section recipientSection) {
        teammates.storage.sqlentity.FeedbackResponse newResponse =
                teammates.storage.sqlentity.FeedbackResponse.makeResponse(
                        newQuestion,
                        oldResponse.getGiverEmail(),
                        giverSection,
                        oldResponse.getRecipientEmail(),
                        recipientSection,
                        getFeedbackResponseDetails(oldResponse));

        newResponse.setCreatedAt(oldResponse.getCreatedAt());
        newResponse.setUpdatedAt(oldResponse.getUpdatedAt());
        return newResponse;
    }

    private FeedbackResponseDetails getFeedbackResponseDetails(FeedbackResponse oldResponse) {
        switch (oldResponse.getFeedbackQuestionType()) {
        case MCQ -> {
            return new FeedbackMcqResponseDetailsConverter().convertToEntityAttribute(oldResponse.getAnswer());
        }
        case MSQ -> {
            return new FeedbackMsqResponseDetailsConverter().convertToEntityAttribute(oldResponse.getAnswer());
        }
        case TEXT -> {
            return new FeedbackTextResponseDetails(oldResponse.getAnswer());
        }
        case RUBRIC -> {
            return new FeedbackRubricResponseDetailsConverter().convertToEntityAttribute(oldResponse.getAnswer());
        }
        case CONTRIB -> {
            return new FeedbackContributionResponseDetailsConverter()
                    .convertToEntityAttribute(oldResponse.getAnswer());
        }
        case CONSTSUM, CONSTSUM_RECIPIENTS, CONSTSUM_OPTIONS -> {
            return new FeedbackConstantSumResponseDetailsConverter()
                    .convertToEntityAttribute(oldResponse.getAnswer());
        }
        case NUMSCALE -> {
            return new FeedbackNumericalScaleResponseDetailsConverter()
                    .convertToEntityAttribute(oldResponse.getAnswer());
        }
        case RANK_OPTIONS -> {
            return new FeedbackRankOptionsResponseDetailsConverter()
                    .convertToEntityAttribute(oldResponse.getAnswer());
        }
        case RANK_RECIPIENTS -> {
            return new FeedbackRankRecipientsResponseDetailsConverter()
                    .convertToEntityAttribute(oldResponse.getAnswer());
        }
        default -> throw new IllegalArgumentException("Invalid response type");
        }
    }

    private teammates.storage.sqlentity.FeedbackResponseComment createFeedbackResponseComment(
            teammates.storage.sqlentity.FeedbackResponse newResponse, FeedbackResponseComment oldComment,
            Section giverSection, Section recipientSection) {
        String truncatedCommentText = truncate(oldComment.getCommentText(), MAX_LENGTH_2000);

        teammates.storage.sqlentity.FeedbackResponseComment newComment =
                new teammates.storage.sqlentity.FeedbackResponseComment(
                        newResponse,
                        oldComment.getGiverEmail(),
                        oldComment.getCommentGiverType(),
                        giverSection,
                        recipientSection,
                        truncatedCommentText,
                        oldComment.getIsVisibilityFollowingFeedbackQuestion(),
                        oldComment.getIsCommentFromFeedbackParticipant(),
                        oldComment.getShowCommentTo(),
                        oldComment.getShowGiverNameTo(),
                        oldComment.getLastEditorEmail());

        newComment.setCreatedAt(oldComment.getCreatedAt());
        newComment.setUpdatedAt(oldComment.getLastEditedAt());
        return newComment;
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
