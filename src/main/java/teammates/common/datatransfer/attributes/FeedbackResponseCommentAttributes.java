package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public String giverEmail;
    public Text commentText;

    // Optional fields
    public String feedbackResponseId;
    public String feedbackQuestionId;
    public List<FeedbackParticipantType> showCommentTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public boolean isVisibilityFollowingFeedbackQuestion;
    public Date createdAt;
    public String lastEditorEmail;
    public Date lastEditedAt;
    public Long feedbackResponseCommentId;
    public String giverSection;
    public String receiverSection;

    FeedbackResponseCommentAttributes() {
        giverSection = Const.DEFAULT_SECTION;
        receiverSection = Const.DEFAULT_SECTION;
        showCommentTo = new ArrayList<>();
        showGiverNameTo = new ArrayList<>();
        isVisibilityFollowingFeedbackQuestion = true;
        createdAt = new Date();
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
                .withLastEditorEmail(comment.getLastEditorEmail())
                .withLastEditedAt(comment.getLastEditedAt())
                .withVisibilityFollowingFeedbackQuestion(comment.getIsVisibilityFollowingFeedbackQuestion())
                .withShowCommentTo(comment.getShowCommentTo())
                .withShowGiverNameTo(comment.getShowGiverNameTo())
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
    public static Builder builder(String courseId, String feedbackSessionName, String giverEmail, Text commentText) {
        return new Builder(courseId, feedbackSessionName, giverEmail, commentText);
    }

    public boolean isVisibleTo(FeedbackParticipantType viewerType) {
        return showCommentTo.contains(viewerType);
    }

    public Long getId() {
        return feedbackResponseCommentId;
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

        addNonEmptyError(validator.getInvalidityInfoForEmail(giverEmail), errors);

        //TODO: handle the new attributes showCommentTo and showGiverNameTo

        return errors;
    }

    @Override
    public FeedbackResponseComment toEntity() {
        return new FeedbackResponseComment(courseId, feedbackSessionName, feedbackQuestionId, giverEmail,
                feedbackResponseId, createdAt, commentText, giverSection, receiverSection,
                showCommentTo, showGiverNameTo, lastEditorEmail, lastEditedAt);
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
        //TODO: print visibilityOptions also
        return "FeedbackResponseCommentAttributes ["
                + "feedbackResponseCommentId = " + feedbackResponseCommentId
                + ", courseId = " + courseId
                + ", feedbackSessionName = " + feedbackSessionName
                + ", feedbackQuestionId = " + feedbackQuestionId
                + ", giverEmail = " + giverEmail
                + ", feedbackResponseId = " + feedbackResponseId
                + ", commentText = " + commentText.getValue()
                + ", createdAt = " + createdAt
                + ", lastEditorEmail = " + lastEditorEmail
                + ", lastEditedAt = " + lastEditedAt + "]";
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
            frca.giverEmail = giverEmail;
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

        public Builder withCreatedAt(Date createdAt) {
            if (createdAt != null) {
                frca.createdAt = createdAt;
            }

            return this;
        }

        public Builder withLastEditorEmail(String lastEditorEmail) {
            frca.lastEditorEmail = lastEditorEmail == null
                    ? frca.giverEmail
                    : lastEditorEmail;
            return this;
        }

        public Builder withLastEditedAt(Date lastEditedAt) {
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
