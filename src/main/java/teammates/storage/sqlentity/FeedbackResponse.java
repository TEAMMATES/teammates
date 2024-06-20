package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.storage.sqlentity.responses.FeedbackConstantSumResponse;
import teammates.storage.sqlentity.responses.FeedbackContributionResponse;
import teammates.storage.sqlentity.responses.FeedbackMcqResponse;
import teammates.storage.sqlentity.responses.FeedbackMsqResponse;
import teammates.storage.sqlentity.responses.FeedbackNumericalScaleResponse;
import teammates.storage.sqlentity.responses.FeedbackRankOptionsResponse;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse;
import teammates.storage.sqlentity.responses.FeedbackRubricResponse;
import teammates.storage.sqlentity.responses.FeedbackTextResponse;

/**
 * Represents a Feedback Response.
 */
@Entity
@Table(name = "FeedbackResponses")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class FeedbackResponse extends BaseEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "questionId")
    private FeedbackQuestion feedbackQuestion;

    @OneToMany(mappedBy = "feedbackResponse", cascade = CascadeType.REMOVE)
    private List<FeedbackResponseComment> feedbackResponseComments = new ArrayList<>();

    @Column(nullable = false)
    private String giver;

    @ManyToOne
    @JoinColumn(name = "giverSectionId")
    private Section giverSection;

    @Column(nullable = false)
    private String recipient;

    @ManyToOne
    @JoinColumn(name = "recipientSectionId")
    private Section recipientSection;

    @UpdateTimestamp
    private Instant updatedAt;

    protected FeedbackResponse() {
        // required by Hibernate
    }

    public FeedbackResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String recipient, Section recipientSection
    ) {
        this.setId(UUID.randomUUID());
        this.setFeedbackQuestion(feedbackQuestion);
        this.setGiver(giver);
        this.setGiverSection(giverSection);
        this.setRecipient(recipient);
        this.setRecipientSection(recipientSection);
    }

    /**
     * Creates a feedback response according to its {@code FeedbackQuestionType}.
     */
    public static FeedbackResponse makeResponse(
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String receiver, Section receiverSection,
            FeedbackResponseDetails responseDetails
    ) {
        FeedbackResponse feedbackResponse = null;
        switch (responseDetails.getQuestionType()) {
        case TEXT:
            feedbackResponse = new FeedbackTextResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        case MCQ:
            feedbackResponse = new FeedbackMcqResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        case MSQ:
            feedbackResponse = new FeedbackMsqResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        case NUMSCALE:
            feedbackResponse = new FeedbackNumericalScaleResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        case CONSTSUM:
        case CONSTSUM_OPTIONS:
        case CONSTSUM_RECIPIENTS:
            feedbackResponse = new FeedbackConstantSumResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        case CONTRIB:
            feedbackResponse = new FeedbackContributionResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        case RUBRIC:
            feedbackResponse = new FeedbackRubricResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        case RANK_OPTIONS:
            feedbackResponse = new FeedbackRankOptionsResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        case RANK_RECIPIENTS:
            feedbackResponse = new FeedbackRankRecipientsResponse(
                feedbackQuestion, giver, giverSection, receiver, receiverSection, responseDetails
            );
            break;
        }
        return feedbackResponse;
    }

    /**
     * Update a feedback response according to its {@code FeedbackQuestionType}.
     */
    public static FeedbackResponse updateResponse(
            FeedbackResponse originalFeedbackResponse,
            FeedbackQuestion feedbackQuestion, String giver,
            Section giverSection, String receiver, Section receiverSection,
            FeedbackResponseDetails responseDetails
    ) {
        FeedbackResponse updatedFeedbackResponse = makeResponse(
                feedbackQuestion,
                giver,
                giverSection,
                receiver,
                receiverSection,
                responseDetails
        );
        updatedFeedbackResponse.setCreatedAt(originalFeedbackResponse.getCreatedAt());
        updatedFeedbackResponse.setId(originalFeedbackResponse.getId());
        return updatedFeedbackResponse;
    }

    /**
     * Gets a copy of the question details of the feedback question.
     */
    public abstract FeedbackResponseDetails getFeedbackResponseDetailsCopy();

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

    public String getGiverSectionName() {
        return giverSection.getName();
    }

    public void setGiverSection(Section giverSection) {
        this.giverSection = giverSection;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Section getRecipientSection() {
        return recipientSection;
    }

    public String getRecipientSectionName() {
        return recipientSection.getName();
    }

    public void setRecipientSection(Section recipientSection) {
        this.recipientSection = recipientSection;
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
        return "FeedbackResponse [id=" + id + ", giver=" + giver + ", recipient=" + recipient
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
            return Objects.equals(this.getId(), otherResponse.getId());
        } else {
            return false;
        }
    }
}
