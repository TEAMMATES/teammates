package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * The data transfer object for {@link FeedbackResponseComment} entities.
 */
public final class FeedbackResponseCommentAttributes extends EntityAttributes<FeedbackResponseComment> {

    private Long feedbackResponseCommentId;
    private String courseId;
    private String feedbackSessionName;
    /**
     * Contains the email of student/instructor if comment giver is student/instructor
     * and name of team if comment giver is a team.
     */
    private String commentGiver;
    private String commentText;
    private String feedbackResponseId;
    private String feedbackQuestionId;
    private List<FeedbackParticipantType> showCommentTo;
    private List<FeedbackParticipantType> showGiverNameTo;
    private boolean isVisibilityFollowingFeedbackQuestion;
    private Instant createdAt;
    private String lastEditorEmail;
    private Instant lastEditedAt;
    private String giverSection;
    private String receiverSection;
    // Determines the type of comment giver- instructor, student, or team
    private FeedbackParticipantType commentGiverType;
    // true if comment is given by response giver
    private boolean isCommentFromFeedbackParticipant;

    private FeedbackResponseCommentAttributes() {
        giverSection = Const.DEFAULT_SECTION;
        receiverSection = Const.DEFAULT_SECTION;
        showCommentTo = new ArrayList<>();
        showGiverNameTo = new ArrayList<>();
        isVisibilityFollowingFeedbackQuestion = true;
        createdAt = Instant.now();
        commentGiverType = FeedbackParticipantType.INSTRUCTORS;
        isCommentFromFeedbackParticipant = false;
    }

    /**
     * Gets the {@link FeedbackResponseCommentAttributes} instance of the given {@link FeedbackResponseComment}.
     */
    public static FeedbackResponseCommentAttributes valueOf(FeedbackResponseComment comment) {
        FeedbackResponseCommentAttributes frca = new FeedbackResponseCommentAttributes();
        frca.courseId = comment.getCourseId();
        frca.feedbackSessionName = comment.getFeedbackSessionName();
        frca.commentGiver = comment.getGiverEmail();
        frca.commentText = comment.getCommentText();
        frca.feedbackResponseId = comment.getFeedbackResponseId();
        frca.feedbackQuestionId = comment.getFeedbackQuestionId();
        if (comment.getShowCommentTo() != null) {
            frca.showCommentTo = new ArrayList<>(comment.getShowCommentTo());
        }
        if (comment.getShowGiverNameTo() != null) {
            frca.showGiverNameTo = new ArrayList<>(comment.getShowGiverNameTo());
        }
        frca.isVisibilityFollowingFeedbackQuestion = comment.getIsVisibilityFollowingFeedbackQuestion();
        if (comment.getCreatedAt() != null) {
            frca.createdAt = comment.getCreatedAt();
        }
        if (comment.getLastEditorEmail() == null) {
            frca.lastEditorEmail = frca.getCommentGiver();
        } else {
            frca.lastEditorEmail = comment.getLastEditorEmail();
        }
        if (comment.getLastEditedAt() == null) {
            frca.lastEditedAt = frca.getCreatedAt();
        } else {
            frca.lastEditedAt = comment.getLastEditedAt();
        }
        frca.feedbackResponseCommentId = comment.getFeedbackResponseCommentId();
        if (comment.getGiverSection() != null) {
            frca.giverSection = comment.getGiverSection();
        }
        if (comment.getReceiverSection() != null) {
            frca.receiverSection = comment.getReceiverSection();
        }
        frca.commentGiverType = comment.getCommentGiverType();
        frca.isCommentFromFeedbackParticipant = comment.getIsCommentFromFeedbackParticipant();

        return frca;
    }

    /**
     * Returns a builder for {@link FeedbackResponseCommentAttributes}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns true if the response comment is visible to the given participant type.
     */
    public boolean isVisibleTo(FeedbackParticipantType viewerType) {
        return showCommentTo.contains(viewerType);
    }

    public Long getId() {
        return feedbackResponseCommentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getCommentGiver() {
        return commentGiver;
    }

    public void setCommentGiver(String commentGiver) {
        this.commentGiver = commentGiver;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getFeedbackResponseId() {
        return feedbackResponseId;
    }

    public void setFeedbackResponseId(String feedbackResponseId) {
        this.feedbackResponseId = feedbackResponseId;
    }

    public String getFeedbackQuestionId() {
        return feedbackQuestionId;
    }

    public void setFeedbackQuestionId(String feedbackQuestionId) {
        this.feedbackQuestionId = feedbackQuestionId;
    }

    public void setShowCommentTo(List<FeedbackParticipantType> showCommentTo) {
        this.showCommentTo = showCommentTo;
    }

    public List<FeedbackParticipantType> getShowCommentTo() {
        return showCommentTo;
    }

    public void setShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public boolean isVisibilityFollowingFeedbackQuestion() {
        return isVisibilityFollowingFeedbackQuestion;
    }

    public void setVisibilityFollowingFeedbackQuestion(boolean visibilityFollowingFeedbackQuestion) {
        isVisibilityFollowingFeedbackQuestion = visibilityFollowingFeedbackQuestion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastEditorEmail() {
        return lastEditorEmail;
    }

    public void setLastEditorEmail(String lastEditorEmail) {
        this.lastEditorEmail = lastEditorEmail;
    }

    public Instant getLastEditedAt() {
        return lastEditedAt;
    }

    public void setLastEditedAt(Instant lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
    }

    public String getGiverSection() {
        return giverSection;
    }

    public String getReceiverSection() {
        return receiverSection;
    }

    public FeedbackParticipantType getCommentGiverType() {
        return commentGiverType;
    }

    public void setCommentGiverType(FeedbackParticipantType commentGiverType) {
        this.commentGiverType = commentGiverType;
    }

    public boolean isCommentFromFeedbackParticipant() {
        return isCommentFromFeedbackParticipant;
    }

    public void setCommentFromFeedbackParticipant(boolean commentFromFeedbackParticipant) {
        isCommentFromFeedbackParticipant = commentFromFeedbackParticipant;
    }

    /**
     * Use only to match existing and known Comment.
     */
    public void setId(Long id) {
        this.feedbackResponseCommentId = id;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(courseId), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForCommentGiverType(commentGiverType), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForVisibilityOfFeedbackParticipantComments(
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
                + ", commentText = " + commentText
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

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.feedbackSessionName).append(this.feedbackQuestionId)
                .append(this.feedbackResponseId).append(this.courseId)
                .append(this.commentGiver).append(this.commentText)
                .append(this.giverSection).append(this.receiverSection);
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackResponseCommentAttributes otherCommentAttributes =
                    (FeedbackResponseCommentAttributes) other;
            return Objects.equals(this.feedbackSessionName, otherCommentAttributes.feedbackSessionName)
                    && Objects.equals(this.feedbackResponseId, otherCommentAttributes.feedbackResponseId)
                    && Objects.equals(this.feedbackQuestionId, otherCommentAttributes.feedbackQuestionId)
                    && Objects.equals(this.courseId, otherCommentAttributes.courseId)
                    && Objects.equals(this.commentGiver, otherCommentAttributes.commentGiver)
                    && Objects.equals(this.commentText, otherCommentAttributes.commentText)
                    && Objects.equals(this.giverSection, otherCommentAttributes.giverSection)
                    && Objects.equals(this.receiverSection, otherCommentAttributes.receiverSection);
        } else {
            return false;
        }
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.feedbackResponseIdOption.ifPresent(s -> feedbackResponseId = s);
        updateOptions.commentTextOption.ifPresent(s -> commentText = s);
        updateOptions.showCommentToOption.ifPresent(s -> showCommentTo = s);
        updateOptions.showGiverNameToOption.ifPresent(s -> showGiverNameTo = s);
        updateOptions.lastEditorEmailOption.ifPresent(s -> lastEditorEmail = s);
        updateOptions.lastEditedAtOption.ifPresent(s -> lastEditedAt = s);
        updateOptions.giverSectionOption.ifPresent(s -> giverSection = s);
        updateOptions.receiverSectionOption.ifPresent(s -> receiverSection = s);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a comment.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(long feedbackResponseCommentId) {
        return new UpdateOptions.Builder(feedbackResponseCommentId);
    }

    /**
     * A builder for {@link FeedbackResponseCommentAttributes}.
     */
    public static final class Builder extends BasicBuilder<FeedbackResponseCommentAttributes, Builder> {
        private final FeedbackResponseCommentAttributes frca;

        private Builder() {
            super(new UpdateOptions(0L));
            thisBuilder = this;

            frca = new FeedbackResponseCommentAttributes();
        }

        public Builder withCourseId(String courseId) {
            assert courseId != null;
            frca.courseId = courseId;

            return this;
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            assert feedbackSessionName != null;
            frca.feedbackSessionName = feedbackSessionName;

            return this;
        }

        public Builder withCommentGiver(String commentGiver) {
            assert commentGiver != null;
            frca.commentGiver = commentGiver;

            return this;
        }

        public Builder withFeedbackQuestionId(String feedbackQuestionId) {
            assert feedbackQuestionId != null;
            frca.feedbackQuestionId = feedbackQuestionId;

            return this;
        }

        public Builder withVisibilityFollowingFeedbackQuestion(boolean visibilityFollowingFeedbackQuestion) {
            frca.isVisibilityFollowingFeedbackQuestion = visibilityFollowingFeedbackQuestion;
            return this;
        }

        public Builder withCommentGiverType(FeedbackParticipantType commentGiverType) {
            assert commentGiverType != null;

            frca.commentGiverType = commentGiverType;
            return this;
        }

        public Builder withCommentFromFeedbackParticipant(boolean isCommentFromFeedbackParticipant) {
            frca.isCommentFromFeedbackParticipant = isCommentFromFeedbackParticipant;
            return this;
        }

        @Override
        public FeedbackResponseCommentAttributes build() {
            frca.update(updateOptions);

            return frca;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link FeedbackResponseCommentAttributes}.
     */
    public static final class UpdateOptions {
        private long feedbackResponseCommentId;

        private UpdateOption<String> feedbackResponseIdOption = UpdateOption.empty();
        private UpdateOption<String> commentTextOption = UpdateOption.empty();
        private UpdateOption<List<FeedbackParticipantType>> showCommentToOption = UpdateOption.empty();
        private UpdateOption<List<FeedbackParticipantType>> showGiverNameToOption = UpdateOption.empty();
        private UpdateOption<String> lastEditorEmailOption = UpdateOption.empty();
        private UpdateOption<Instant> lastEditedAtOption = UpdateOption.empty();
        private UpdateOption<String> giverSectionOption = UpdateOption.empty();
        private UpdateOption<String> receiverSectionOption = UpdateOption.empty();

        private UpdateOptions(long feedbackResponseCommentId) {
            this.feedbackResponseCommentId = feedbackResponseCommentId;
        }

        public long getFeedbackResponseCommentId() {
            return feedbackResponseCommentId;
        }

        @Override
        public String toString() {
            return "FeedbackResponseCommentAttributes.UpdateOptions ["
                    + "feedbackResponseCommentId = " + feedbackResponseCommentId
                    + ", commentText = " + commentTextOption
                    + ", showCommentTo = " + showCommentToOption
                    + ", showGiverNameTo = " + showGiverNameToOption
                    + ", lastEditorEmail = " + lastEditorEmailOption
                    + ", giverSection = " + giverSectionOption
                    + ", receiverSection = " + receiverSectionOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static final class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(Long feedbackResponseCommentId) {
                super(new UpdateOptions(feedbackResponseCommentId));
                thisBuilder = this;
            }

            public Builder withLastEditorEmail(String lastEditorEmail) {
                assert lastEditorEmail != null;

                updateOptions.lastEditorEmailOption = UpdateOption.of(lastEditorEmail);
                return this;
            }

            public Builder withLastEditorAt(Instant lastEditedAt) {
                assert lastEditedAt != null;

                updateOptions.lastEditedAtOption = UpdateOption.of(lastEditedAt);
                return this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link FeedbackResponseCommentAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withFeedbackResponseId(String feedbackResponseId) {
            assert feedbackResponseId != null;

            updateOptions.feedbackResponseIdOption = UpdateOption.of(feedbackResponseId);
            return thisBuilder;
        }

        public B withCommentText(String commentText) {
            assert commentText != null;

            updateOptions.commentTextOption = UpdateOption.of(commentText);
            return thisBuilder;
        }

        public B withShowCommentTo(List<FeedbackParticipantType> showCommentTo) {
            assert showCommentTo != null;

            updateOptions.showCommentToOption = UpdateOption.of(showCommentTo);
            return thisBuilder;
        }

        public B withShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
            assert showGiverNameTo != null;

            updateOptions.showGiverNameToOption = UpdateOption.of(showGiverNameTo);
            return thisBuilder;
        }

        public B withGiverSection(String giverSection) {
            assert giverSection != null;

            updateOptions.giverSectionOption = UpdateOption.of(giverSection);
            return thisBuilder;
        }

        public B withReceiverSection(String receiverSection) {
            assert receiverSection != null;

            updateOptions.receiverSectionOption = UpdateOption.of(receiverSection);
            return thisBuilder;
        }

        public abstract T build();

    }
}
