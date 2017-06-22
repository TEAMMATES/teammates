package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
public class FeedbackResponseCommentAttributes extends EntityAttributes {

    /* Required fields */
    public String courseId;
    public String feedbackSessionName;
    public String feedbackQuestionId;
    public String giverEmail;
    public String feedbackResponseId;

    /* Optional fields */
    public List<FeedbackParticipantType> showCommentTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public boolean isVisibilityFollowingFeedbackQuestion;
    public Date createdAt;
    public Text commentText;
    public String lastEditorEmail;
    public Date lastEditedAt;
    public Long feedbackResponseCommentId;
    /* Response giver section */
    public String giverSection;
    /* Response receiver section */
    public String receiverSection;

    public FeedbackResponseCommentAttributes() {
        giverSection = "None";
        receiverSection = "None";
        showCommentTo = new ArrayList<>();
        showGiverNameTo = new ArrayList<>();
        isVisibilityFollowingFeedbackQuestion = true;
    }

    public static FeedbackResponseCommentAttributes valueOf(FeedbackResponseComment comment) {
        return builder(comment.getCourseId(), comment.getFeedbackSessionName(),
                comment.getFeedbackQuestionId(), comment.getFeedbackResponseId(), comment.getGiverEmail())
                .withFeedbackResponseCommentId(comment.getFeedbackResponseCommentId())
                .withCreatedAt(comment.getCreatedAt())
                .withCommentText(comment.getCommentText())
                .withGiverSection(comment.getGiverSection())
                .withReceiverSection(comment.getReceiverSection())
                .withLastEditorEmail(comment.getLastEditorEmail())
                .withLastEditedAt(comment.getLastEditedAt())
                .withShowCommentTo(comment.getShowCommentTo())
                .withShowGiverNameTo(comment.getShowGiverNameTo())
                .build();
    }

    /**
     * Return new builder instance with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * {@code giverSection = "None"} <br>
     * {@code receiverSection = "None"} <br>
     * {@code showCommentTo = new ArrayList<>()} <br>
     * {@code showGiverNameTo = new ArrayList<>()} <br>
     * {@code isVisibilityFollowingFeedbackQuestion = true} <br>
     */
    public static Builder builder(String courseId, String feedbackSessionName, String feedbackQuestionId,
                                  String feedbackResponseId, String giverEmail) {
        return new Builder(courseId, feedbackSessionName, feedbackQuestionId, feedbackResponseId, giverEmail);
    }

    /**
     * A Builder for {@link FeedbackResponseCommentAttributes}.
     */
    public static class Builder {
        private final FeedbackResponseCommentAttributes feedbackAttributes;

        public Builder(String courseId, String feedbackSessionName, String feedbackQuestionId,
                       String feedbackResponseId, String giverEmail) {
            feedbackAttributes = new FeedbackResponseCommentAttributes();

            feedbackAttributes.courseId = courseId;
            feedbackAttributes.feedbackSessionName = feedbackSessionName;
            feedbackAttributes.feedbackQuestionId = feedbackQuestionId;
            feedbackAttributes.feedbackResponseId = feedbackResponseId;
            feedbackAttributes.giverEmail = giverEmail;
        }

        public Builder withShowCommentTo(List<FeedbackParticipantType> showCommentTo) {
            feedbackAttributes.showCommentTo = showCommentTo;
            return this;
        }

        public Builder withShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
            feedbackAttributes.showGiverNameTo = showGiverNameTo;
            return this;
        }

        public Builder withVisibilityFollowingFeedbackQuestion(boolean visibilityFollowingFeedbackQuestion) {
            feedbackAttributes.isVisibilityFollowingFeedbackQuestion = visibilityFollowingFeedbackQuestion;
            return this;
        }

        public Builder withCreatedAt(Date createdAt) {
            feedbackAttributes.createdAt = createdAt;
            return this;
        }

        public Builder withCommentText(Text commentText) {
            feedbackAttributes.commentText = commentText;
            return this;
        }

        public Builder withLastEditorEmail(String lastEditorEmail) {
            feedbackAttributes.lastEditorEmail = lastEditorEmail == null
                    ? feedbackAttributes.giverEmail
                    : lastEditorEmail;
            return this;
        }

        public Builder withLastEditedAt(Date lastEditedAt) {
            feedbackAttributes.lastEditedAt = lastEditedAt == null
                    ? feedbackAttributes.createdAt
                    : lastEditedAt;
            return this;
        }

        public Builder withFeedbackResponseCommentId(Long feedbackResponseCommentId) {
            feedbackAttributes.feedbackResponseCommentId = feedbackResponseCommentId;
            return this;
        }

        public Builder withGiverSection(String giverSection) {
            feedbackAttributes.giverSection = giverSection == null ? "None" : giverSection;
            return this;
        }

        public Builder withReceiverSection(String receiverSection) {
            feedbackAttributes.receiverSection = receiverSection == null
                    ? "None"
                    : receiverSection;
            return this;
        }

        public FeedbackResponseCommentAttributes build() {
            return feedbackAttributes;
        }
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
        List<String> errors = new ArrayList<String>();

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
        Collections.sort(frcs, new Comparator<FeedbackResponseCommentAttributes>() {
            @Override
            public int compare(FeedbackResponseCommentAttributes frc1, FeedbackResponseCommentAttributes frc2) {
                return frc1.createdAt.compareTo(frc2.createdAt);
            }
        });
    }
}
