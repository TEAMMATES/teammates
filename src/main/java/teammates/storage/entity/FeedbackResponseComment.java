package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.FeedbackParticipantType;
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

    @Column(nullable = false)
    private String giver;

    @Column(nullable = false)
    @Convert(converter = FeedbackParticipantTypeConverter.class)
    private FeedbackParticipantType giverType;

    @ManyToOne
    @JoinColumn(name = "giverSectionId")
    private Section giverSection;

    @Column(insertable = false, updatable = false)
    private UUID giverSectionId;

    @ManyToOne
    @JoinColumn(name = "recipientSectionId")
    private Section recipientSection;

    @Column(insertable = false, updatable = false)
    private UUID recipientSectionId;

    @Column(nullable = false)
    private String commentText;

    @Column(nullable = false)
    private boolean isVisibilityFollowingFeedbackQuestion;

    @Column(nullable = false)
    private boolean isCommentFromFeedbackParticipant;

    @Column(nullable = false)
    @Convert(converter = FeedbackParticipantTypeListConverter.class)
    private List<FeedbackParticipantType> showCommentTo;

    @Column(nullable = false)
    @Convert(converter = FeedbackParticipantTypeListConverter.class)
    private List<FeedbackParticipantType> showGiverNameTo;

    @UpdateTimestamp
    private Instant updatedAt;

    private String lastEditorEmail;

    protected FeedbackResponseComment() {
        // required by Hibernate
    }

    public FeedbackResponseComment(
            FeedbackResponse feedbackResponse, String giver, FeedbackParticipantType giverType,
            Section giverSection, Section recipientSection, String commentText,
            boolean isVisibilityFollowingFeedbackQuestion, boolean isCommentFromFeedbackParticipant,
            List<FeedbackParticipantType> showCommentTo, List<FeedbackParticipantType> showGiverNameTo,
            String lastEditorEmail
    ) {
        this.setFeedbackResponse(feedbackResponse);
        this.setGiver(giver);
        this.setGiverType(giverType);
        this.setGiverSection(giverSection);
        this.setRecipientSection(recipientSection);
        this.setCommentText(commentText);
        this.setIsVisibilityFollowingFeedbackQuestion(isVisibilityFollowingFeedbackQuestion);
        this.setIsCommentFromFeedbackParticipant(isCommentFromFeedbackParticipant);
        this.setShowCommentTo(showCommentTo);
        this.setShowGiverNameTo(showGiverNameTo);
        this.setLastEditorEmail(lastEditorEmail);
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

    public String getGiver() {
        return giver;
    }

    public void setGiver(String giver) {
        this.giver = giver;
    }

    public FeedbackParticipantType getGiverType() {
        return giverType;
    }

    public void setGiverType(FeedbackParticipantType giverType) {
        this.giverType = giverType;
    }

    public Section getGiverSection() {
        return giverSection;
    }

    public UUID getGiverSectionId() {
        return giverSectionId;
    }

    /**
     * Sets the giver section of the response comment.
     */
    public void setGiverSection(Section giverSection) {
        this.giverSection = giverSection;
        this.giverSectionId = giverSection == null ? null : giverSection.getId();
    }

    public Section getRecipientSection() {
        return recipientSection;
    }

    public UUID getRecipientSectionId() {
        return recipientSectionId;
    }

    /**
     * Sets the recipient section of the response comment.
     */
    public void setRecipientSection(Section recipientSection) {
        this.recipientSection = recipientSection;
        this.recipientSectionId = recipientSection == null ? null : recipientSection.getId();
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

    public List<FeedbackParticipantType> getShowCommentTo() {
        return showCommentTo;
    }

    public void setShowCommentTo(List<FeedbackParticipantType> showCommentTo) {
        this.showCommentTo = showCommentTo;
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public void setShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLastEditorEmail() {
        return lastEditorEmail;
    }

    public void setLastEditorEmail(String lastEditorEmail) {
        this.lastEditorEmail = lastEditorEmail;
    }

    /**
     * Formats the entity before persisting in database.
     * TODO: Override when BaseEntity adds abstract sanitizeForSaving
     */
    public void sanitizeForSaving() {
        this.commentText = SanitizationHelper.sanitizeForRichText(this.commentText);
    }

    /**
     * Returns true if the response comment is visible to the given participant type.
     */
    public boolean checkIsVisibleTo(FeedbackParticipantType viewerType) {
        return showCommentTo.contains(viewerType);
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForCommentGiverType(giverType), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForVisibilityOfFeedbackParticipantComments(
                isCommentFromFeedbackParticipant, isVisibilityFollowingFeedbackQuestion), errors);

        return errors;
    }

    @Override
    public String toString() {
        return "FeedbackResponseComment [id=" + id + ", giver=" + giver + ", commentText=" + commentText
                + ", isVisibilityFollowingFeedbackQuestion=" + isVisibilityFollowingFeedbackQuestion
                + ", isCommentFromFeedbackParticipant=" + isCommentFromFeedbackParticipant
                + ", lastEditorEmail=" + lastEditorEmail + ", createdAt=" + getCreatedAt()
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
