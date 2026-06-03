package teammates.storage.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.util.SanitizationHelper;

/**
 * Represents a feedback response comment.
 */
@Entity
@Table(name = "ResponseInstructorComments")
public class ResponseInstructorComment extends BaseEntity {
    @Id
    private UUID id;

    @JsonIgnore
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
            @AttributeOverride(
                    name = "giverUserId",
                    column = @Column(name = "lastEditedByUserId", insertable = false, updatable = false)),
            @AttributeOverride(
                    name = "giverTeamId",
                    column = @Column(name = "lastEditedByTeamId", insertable = false, updatable = false))
    })
    @AssociationOverrides({
            @AssociationOverride(
                    name = "giverUser",
                    joinColumns = @JoinColumn(name = "lastEditedByUserId")),
            @AssociationOverride(
                    name = "giverTeam",
                    joinColumns = @JoinColumn(name = "lastEditedByTeamId"))
    })
    private ResponseGiver lastEditedBy;

    @Column(nullable = false)
    private String commentText;

    @Column(nullable = false)
    @Convert(converter = ViewerTypeListConverter.class)
    private List<ViewerType> showCommentTo;

    @Column(nullable = false)
    @Convert(converter = ViewerTypeListConverter.class)
    private List<ViewerType> showGiverNameTo;

    @UpdateTimestamp
    private Instant updatedAt;

    protected ResponseInstructorComment() {
        // required by Hibernate
    }

    public ResponseInstructorComment(
            ResponseGiver giver, String commentText,
            List<ViewerType> showCommentTo, List<ViewerType> showGiverNameTo,
            ResponseGiver lastEditedBy
    ) {
        this.setGiver(giver);
        this.setCommentText(commentText);
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

    /**
     * Gets the last editor of the response comment. If the last editor is not set, returns an empty ResponseGiver.
     */
    public ResponseGiver getLastEditedBy() {
        if (lastEditedBy == null) {
            return new ResponseGiver();
        }
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
        return List.of();
    }

    @Override
    public String toString() {
        return "ResponseInstructorComment [id=" + id + ", giver=" + giver + ", commentText=" + commentText
                + ", lastEditedBy=" + lastEditedBy + ", createdAt=" + getCreatedAt()
                + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ResponseInstructorComment other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
