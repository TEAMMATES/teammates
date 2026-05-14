package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a feedback response comment.
 */
@Entity
@Table(name = "FeedbackResponseComments")
public class FeedbackResponseComment extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "responseId")
    private FeedbackResponse feedbackResponse;

    @Column(insertable = false, updatable = false)
    private UUID responseId;

    @Embedded
    private ResponseGiver giver;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "giverType", column = @Column(name = "lastEditedByType", nullable = false)),
            @AttributeOverride(name = "giverId", column = @Column(name = "lastEditedById", nullable = false))
    })
    private ResponseGiver lastEditedBy;

    @Column(nullable = false)
    private String commentText;

    @Column(nullable = false)
    private boolean isVisibilityFollowingFeedbackQuestion;

    @Column(nullable = false)
    private boolean isCommentFromFeedbackParticipant;

    @Column(nullable = false)
    @Convert(converter = ViewerTypeListConverter.class)
    private List<ViewerType> showCommentTo;

    @Column(nullable = false)
    @Convert(converter = ViewerTypeListConverter.class)
    private List<ViewerType> showGiverNameTo;

    @UpdateTimestamp
    private Instant updatedAt;

    protected FeedbackResponseComment() {
        // required by Hibernate
    }

    public FeedbackResponseComment(
            ResponseGiver giver, String commentText,
            boolean isVisibilityFollowingFeedbackQuestion, boolean isCommentFromFeedbackParticipant,
            List<ViewerType> showCommentTo, List<ViewerType> showGiverNameTo,
            ResponseGiver lastEditedBy
    ) {
        this.setGiver(giver);
        this.setCommentText(commentText);
        this.setIsVisibilityFollowingFeedbackQuestion(isVisibilityFollowingFeedbackQuestion);
        this.setIsCommentFromFeedbackParticipant(isCommentFromFeedbackParticipant);
        this.setShowCommentTo(showCommentTo);
        this.setShowGiverNameTo(showGiverNameTo);
        this.setLastEditedBy(lastEditedBy);
        this.setId(UUID.randomUUID());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public FeedbackResponse getFeedbackResponse() {
        return feedbackResponse;
    }

    public UUID getResponseId() {
        return responseId;
    }

    /**
     * Sets the feedback response of the response comment.
     */
    public void setFeedbackResponse(FeedbackResponse feedbackResponse) {
        this.feedbackResponse = feedbackResponse;
        this.responseId = feedbackResponse == null ? null : feedbackResponse.getId();
    }

    public ResponseGiver getGiver() {
        return giver;
    }

    public void setGiver(ResponseGiver giver) {
        this.giver = giver;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public boolean getIsVisibilityFollowingFeedbackQuestion() {
        return this.isVisibilityFollowingFeedbackQuestion;
    }

    public void setIsVisibilityFollowingFeedbackQuestion(boolean isVisibilityFollowingFeedbackQuestion) {
        this.isVisibilityFollowingFeedbackQuestion = isVisibilityFollowingFeedbackQuestion;
    }

    public boolean getIsCommentFromFeedbackParticipant() {
        return this.isCommentFromFeedbackParticipant;
    }

    public void setIsCommentFromFeedbackParticipant(boolean isCommentFromFeedbackParticipant) {
        this.isCommentFromFeedbackParticipant = isCommentFromFeedbackParticipant;
    }

    public List<ViewerType> getShowCommentTo() {
        return showCommentTo;
    }

    public void setShowCommentTo(List<ViewerType> showCommentTo) {
        this.showCommentTo = showCommentTo;
    }

    public List<ViewerType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public void setShowGiverNameTo(List<ViewerType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ResponseGiver getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(ResponseGiver lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    /**
     * Formats the entity before persisting in database.
     */
    public void sanitizeForSaving() {
        this.commentText = SanitizationHelper.sanitizeForRichText(this.commentText);
    }

    /**
     * Returns true if the response comment is visible to the given participant type.
     */
    public boolean checkIsVisibleTo(ViewerType viewerType) {
        return showCommentTo.contains(viewerType);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForVisibilityOfFeedbackParticipantComments(
                isCommentFromFeedbackParticipant, isVisibilityFollowingFeedbackQuestion), errors);

        return errors;
    }

    @Override
    public String toString() {
        return "FeedbackResponseComment [id=" + id + ", giver=" + giver + ", commentText=" + commentText
                + ", isVisibilityFollowingFeedbackQuestion=" + isVisibilityFollowingFeedbackQuestion
                + ", isCommentFromFeedbackParticipant=" + isCommentFromFeedbackParticipant
                + ", lastEditedBy=" + lastEditedBy + ", createdAt=" + getCreatedAt()
                + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FeedbackResponseComment other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
