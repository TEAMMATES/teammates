package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * Represents a data transfer object for {@link FeedbackResponseComment} entities.
 */
public class FeedbackResponseCommentAttributes extends EntityAttributes<FeedbackResponseComment> {

    // Required fields
    public String courseId;
    public String feedbackSessionName;
    /**
     * Contains the email of student/instructor if comment giver is student/instructor
     * and name of team if comment giver is a team.
     */
    public String commentGiver;
    public Text commentText;

    // Optional fields
    public String feedbackResponseId;
    public String feedbackQuestionId;
    public List<FeedbackParticipantType> showCommentTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public boolean isVisibilityFollowingFeedbackQuestion;
    public Instant createdAt;
    public String lastEditorEmail;
    public Instant lastEditedAt;
    public Long feedbackResponseCommentId;
    public String giverSection;
    public String receiverSection;
    // Determines the type of comment giver- instructor, student, or team
    public FeedbackParticipantType commentGiverType;
    // true if comment is given by response giver
    public boolean isCommentFromFeedbackParticipant;

    FeedbackResponseCommentAttributes() {
        giverSection = Const.DEFAULT_SECTION;
        receiverSection = Const.DEFAULT_SECTION;
        showCommentTo = new ArrayList<>();
        showGiverNameTo = new ArrayList<>();
        isVisibilityFollowingFeedbackQuestion = true;
        createdAt = Instant.now();
        commentGiverType = FeedbackParticipantType.INSTRUCTORS;
        isCommentFromFeedbackParticipant = false;
    }

    public static FeedbackResponseCommentAttributes valueOf(FeedbackResponseComment comment) {
        return builder(comment.getCourseId(), comment.getFeedbackSessionName(),
                    comment.getGiverEmail(), comment.getCommentText())
                .withFeedbackResponseId(comment.getFeedbackResponseId())
                .withFeedbackQuestionId(comment.getFeedbackQuestionId())
                .withFeedbackResponseCommentId(comment.getFeedbackResponseCommentId())
                .withCreatedAt(comment.getCreatedAt())
                .withGiverSection(comment.getGiverSection())
                .withReceiverSection(comment.getReceiverSection())
                .withCommentGiverType(comment.getCommentGiverType())
                .withLastEditorEmail(comment.getLastEditorEmail())
                .withLastEditedAt(comment.getLastEditedAt())
                .withVisibilityFollowingFeedbackQuestion(comment.getIsVisibilityFollowingFeedbackQuestion())
                .withShowCommentTo(comment.getShowCommentTo())
                .withShowGiverNameTo(comment.getShowGiverNameTo())
                .withCommentFromFeedbackParticipant(comment.getIsCommentFromFeedbackParticipant())
                .build();
    }

    /**
     * Returns new builder instance with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * <ul>
     * <li>{@code giverSection = "None"}</li>
     * <li>{@code receiverSection = "None"}</li>
     * <li>{@code showCommentTo = new ArrayList<>()}</li>
     * <li>{@code showGiverNameTo = new ArrayList<>()}</li>
     * <li>{@code isVisibilityFollowingFeedbackQuestion = true}</li>
     * </ul>
     */
    public static Builder builder(String courseId, String feedbackSessionName, String commentGiver, Text commentText) {
        return new Builder(courseId, feedbackSessionName, commentGiver, commentText);
    }

    public boolean isVisibleTo(FeedbackParticipantType viewerType) {
        return showCommentTo.contains(viewerType);
    }

    public Long getId() {
        return feedbackResponseCommentId;
    }

    /**
     * Converts comment text in form of string for csv i.e if it contains image, changes it into link.
     *
     * @return Comment in form of string
     */
    public String getCommentAsCsvString() {
        String htmlText = commentText.getValue();
        StringBuilder comment = new StringBuilder(200);
        comment.append(Jsoup.parse(htmlText).text());
        convertImageToLinkInComment(comment, htmlText);
        return SanitizationHelper.sanitizeForCsv(comment.toString());
    }

    /**
     * Converts comment text in form of string.
     *
     * @return Comment in form of string
     */
    public String getCommentAsHtmlString() {
        String htmlText = commentText.getValue();
        StringBuilder comment = new StringBuilder(200);
        comment.append(Jsoup.parse(htmlText).text());
        convertImageToLinkInComment(comment, htmlText);
        return SanitizationHelper.sanitizeForHtml(comment.toString());
    }

    // Converts image in comment text to link.
    private void convertImageToLinkInComment(StringBuilder comment, String htmlText) {
        if (!(Jsoup.parse(htmlText).getElementsByTag("img").isEmpty())) {
            comment.append(" Images Link: ");
            Elements ele = Jsoup.parse(htmlText).getElementsByTag("img");
            for (Element element : ele) {
                comment.append(element.absUrl("src") + ' ');
            }
        }
    }

    /**
     * Use only to match existing and known Comment.
     */
    public void setId(Long id) {
        this.feedbackResponseCommentId = id;
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();

        addNonEmptyError(validator.getInvalidityInfoForCourseId(courseId), errors);

        addNonEmptyError(validator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(validator.getInvalidityInfoForCommentGiverType(commentGiverType), errors);

        addNonEmptyError(validator.getInvalidityInfoForVisibilityOfFeedbackParticipantComments(
                isCommentFromFeedbackParticipant, isVisibilityFollowingFeedbackQuestion), errors);

        //TODO: handle the new attributes showCommentTo and showGiverNameTo

        return errors;
    }

    @Override
    public FeedbackResponseComment toEntity() {
        return new FeedbackResponseComment(courseId, feedbackSessionName, feedbackQuestionId, commentGiver,
                commentGiverType, feedbackResponseId, createdAt, commentText, giverSection, receiverSection,
                showCommentTo, showGiverNameTo, lastEditorEmail, lastEditedAt, isCommentFromFeedbackParticipant,
                isVisibilityFollowingFeedbackQuestion);
    }

    @Override
    public String getIdentificationString() {
        return toString();
    }

    @Override
    public String getEntityTypeAsString() {
        return "FeedbackResponseComment";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, FeedbackResponseCommentAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        this.commentText = SanitizationHelper.sanitizeForRichText(this.commentText);
    }

    @Override
    public String toString() {
        return "FeedbackResponseCommentAttributes ["
                + "feedbackResponseCommentId = " + feedbackResponseCommentId
                + ", courseId = " + courseId
                + ", feedbackSessionName = " + feedbackSessionName
                + ", feedbackQuestionId = " + feedbackQuestionId
                + ", commentGiver = " + commentGiver
                + ", feedbackResponseId = " + feedbackResponseId
                + ", commentText = " + commentText.getValue()
                + ", createdAt = " + createdAt
                + ", lastEditorEmail = " + lastEditorEmail
                + ", lastEditedAt = " + lastEditedAt
                + ", giverSection = " + giverSection
                + ", receiverSection = " + receiverSection
                + ", showCommentTo = " + showCommentTo
                + ", showGiverNameTo = " + showGiverNameTo
                + ", commentGiverType = " + commentGiverType
                + ", isVisibilityFollowingFeedbackQuestion = " + isVisibilityFollowingFeedbackQuestion
                + ", isCommentFromFeedbackParticipant = " + isCommentFromFeedbackParticipant
                + "]";
    }

    public static void sortFeedbackResponseCommentsByCreationTime(List<FeedbackResponseCommentAttributes> frcs) {
        frcs.sort(Comparator.comparing(frc -> frc.createdAt));
    }

    /**
     * A Builder for {@link FeedbackResponseCommentAttributes}.
     */
    public static class Builder {
        private static final String REQUIRED_FIELD_CANNOT_BE_NULL = "Required field cannot be null";

        private final FeedbackResponseCommentAttributes frca;

        public Builder(String courseId, String feedbackSessionName, String giverEmail, Text commentText) {
            frca = new FeedbackResponseCommentAttributes();

            validateRequiredFields(courseId, feedbackSessionName, giverEmail, commentText);

            frca.courseId = courseId;
            frca.feedbackSessionName = feedbackSessionName;
            frca.commentGiver = giverEmail;
            frca.commentText = commentText;
        }

        public Builder withFeedbackResponseId(String feedbackResponseId) {
            if (feedbackResponseId != null) {
                frca.feedbackResponseId = feedbackResponseId;
            }

            return this;
        }

        public Builder withFeedbackQuestionId(String feedbackQuestionId) {
            if (feedbackQuestionId != null) {
                frca.feedbackQuestionId = feedbackQuestionId;
            }

            return this;
        }

        public Builder withShowCommentTo(List<FeedbackParticipantType> showCommentTo) {
            frca.showCommentTo = showCommentTo == null ? new ArrayList<FeedbackParticipantType>() : showCommentTo;
            return this;
        }

        public Builder withShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
            frca.showGiverNameTo = showGiverNameTo == null ? new ArrayList<FeedbackParticipantType>() : showGiverNameTo;
            return this;
        }

        public Builder withVisibilityFollowingFeedbackQuestion(Boolean visibilityFollowingFeedbackQuestion) {
            frca.isVisibilityFollowingFeedbackQuestion = visibilityFollowingFeedbackQuestion == null
                    || visibilityFollowingFeedbackQuestion; // true as default value if param is null
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            if (createdAt != null) {
                frca.createdAt = createdAt;
            }

            return this;
        }

        public Builder withLastEditorEmail(String lastEditorEmail) {
            frca.lastEditorEmail = lastEditorEmail == null
                    ? frca.commentGiver
                    : lastEditorEmail;
            return this;
        }

        public Builder withLastEditedAt(Instant lastEditedAt) {
            frca.lastEditedAt = lastEditedAt == null
                    ? frca.createdAt
                    : lastEditedAt;
            return this;
        }

        public Builder withFeedbackResponseCommentId(Long feedbackResponseCommentId) {
            if (feedbackResponseCommentId != null) {
                frca.feedbackResponseCommentId = feedbackResponseCommentId;
            }
            return this;
        }

        public Builder withGiverSection(String giverSection) {
            frca.giverSection = giverSection == null ? Const.DEFAULT_SECTION : giverSection;
            return this;
        }

        public Builder withReceiverSection(String receiverSection) {
            frca.receiverSection = receiverSection == null
                    ? Const.DEFAULT_SECTION
                    : receiverSection;
            return this;
        }

        public Builder withCommentGiverType(FeedbackParticipantType commentGiverType) {
            frca.commentGiverType = commentGiverType;
            return this;
        }

        public Builder withCommentFromFeedbackParticipant(boolean isCommentFromFeedbackParticipant) {
            frca.isCommentFromFeedbackParticipant = isCommentFromFeedbackParticipant;
            return this;
        }

        public FeedbackResponseCommentAttributes build() {
            return frca;
        }

        private void validateRequiredFields(Object... objects) {
            for (Object object : objects) {
                Objects.requireNonNull(object, REQUIRED_FIELD_CANNOT_BE_NULL);
            }
        }
    }
}
