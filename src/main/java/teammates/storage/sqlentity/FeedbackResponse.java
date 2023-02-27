package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a Feedback Response.
 */
@Entity
@Table(name = "FeedbackReponses")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class FeedbackResponse extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "questionId")
    private FeedbackQuestion feedbackQuestion;

    @Column(nullable = false)
    @Convert(converter = FeedbackParticipantTypeConverter.class)
    private FeedbackQuestionType type;

    @OneToMany(mappedBy = "feedbackResponse")
    private List<FeedbackResponseComment> feedbackResponseComments = new ArrayList<>();

    @Column(nullable = false)
    private String giver;

    @ManyToOne
    @JoinColumn(name = "giverSectionId")
    private Section giverSection;

    @Column(nullable = false)
    private String receiver;

    @ManyToOne
    @JoinColumn(name = "receiverSectionId")
    private Section receiverSection;

    @UpdateTimestamp
    private Instant updatedAt;

    protected FeedbackResponse() {
        // required by Hibernate
    }

    public FeedbackResponse(
            FeedbackQuestion feedbackQuestion, FeedbackQuestionType type, String giver,
            Section giverSection, String receiver, Section receiverSection
    ) {
        this.setFeedbackQuestion(feedbackQuestion);
        this.setFeedbackQuestionType(type);
        this.setGiver(giver);
        this.setGiverSection(giverSection);
        this.setReceiver(receiver);
        this.setReceiverSection(receiverSection);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public FeedbackQuestion getFeedbackQuestion() {
        return feedbackQuestion;
    }

    public void setFeedbackQuestion(FeedbackQuestion feedbackQuestion) {
        this.feedbackQuestion = feedbackQuestion;
    }

    public FeedbackQuestionType getFeedbackQuestionType() {
        return type;
    }

    public void setFeedbackQuestionType(FeedbackQuestionType type) {
        this.type = type;
    }

    public List<FeedbackResponseComment> getFeedbackResponseComments() {
        return feedbackResponseComments;
    }

    public void setFeedbackResponseComments(List<FeedbackResponseComment> feedbackResponseComments) {
        this.feedbackResponseComments = feedbackResponseComments;
    }

    public String getGiver() {
        return giver;
    }

    public void setGiver(String giver) {
        this.giver = giver;
    }

    public Section getGiverSection() {
        return giverSection;
    }

    public void setGiverSection(Section giverSection) {
        this.giverSection = giverSection;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Section getReceiverSection() {
        return receiverSection;
    }

    public void setReceiverSection(Section receiverSection) {
        this.receiverSection = receiverSection;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public List<String> getInvalidityInfo() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "FeedbackResponse [id=" + id + ", giver=" + giver + ", receiver=" + receiver
                + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
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
            FeedbackResponse otherResponse = (FeedbackResponse) other;
            return Objects.equals(this.id, otherResponse.id);
        } else {
            return false;
        }
    }
}
