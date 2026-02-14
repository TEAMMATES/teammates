package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
    @GeneratedValue
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "responseId")
    private FeedbackResponse feedbackResponse;

    @Column(nullable = false)
    private String giver;

    @Column(nullable = false)
    @Convert(converter = FeedbackParticipantTypeConverter.class)
    private FeedbackParticipantType giverType;

    @ManyToOne
    @JoinColumn(name = "giverSectionId")
    private Section giverSection;

    @ManyToOne
    @JoinColumn(name = "recipientSectionId")
    private Section recipientSection;

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
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FeedbackResponse getFeedbackResponse() {
        return feedbackResponse;
    }

    public void setFeedbackResponse(FeedbackResponse feedbackResponse) {
        this.feedbackResponse = feedbackResponse;
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

    public void setGiverSection(Section giverSection) {
        this.giverSection = giverSection;
    }

    public Section getRecipientSection() {
        return recipientSection;
    }

    public void setRecipientSection(Section recipientSection) {
        this.recipientSection = recipientSection;
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
        return "FeedbackResponse [id=" + id + ", giver=" + giver + ", commentText=" + commentText
                + ", isVisibilityFollowingFeedbackQuestion=" + isVisibilityFollowingFeedbackQuestion
                + ", isCommentFromFeedbackParticipant=" + isCommentFromFeedbackParticipant
                + ", lastEditorEmail=" + lastEditorEmail + ", createdAt=" + getCreatedAt()
                + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackResponseComment otherResponse = (FeedbackResponseComment) other;
            return Objects.equals(this.getId(), otherResponse.getId());
        } else {
            return false;
        }
    }
}
