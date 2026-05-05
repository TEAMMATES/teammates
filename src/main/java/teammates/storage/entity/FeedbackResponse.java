package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
import teammates.storage.entity.responses.FeedbackConstantSumResponse;
import teammates.storage.entity.responses.FeedbackContributionResponse;
import teammates.storage.entity.responses.FeedbackMcqResponse;
import teammates.storage.entity.responses.FeedbackMsqResponse;
import teammates.storage.entity.responses.FeedbackNumericalScaleResponse;
import teammates.storage.entity.responses.FeedbackRankOptionsResponse;
import teammates.storage.entity.responses.FeedbackRankRecipientsResponse;
import teammates.storage.entity.responses.FeedbackRubricResponse;
import teammates.storage.entity.responses.FeedbackTextResponse;

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

    @Column(insertable = false, updatable = false)
    private UUID questionId;

    @OneToMany(mappedBy = "feedbackResponse", cascade = CascadeType.REMOVE)
    private List<FeedbackResponseComment> feedbackResponseComments = new ArrayList<>();

    @Column(nullable = false)
    private String giver;

    @ManyToOne
    @JoinColumn(name = "giverSectionId")
    private Section giverSection;

    @Column(insertable = false, updatable = false)
    private UUID giverSectionId;

    @Column(nullable = false)
    private String recipient;

    @ManyToOne
    @JoinColumn(name = "recipientSectionId")
    private Section recipientSection;

    @Column(insertable = false, updatable = false)
    private UUID recipientSectionId;

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

    /**
     * Add a comment to the feedback response.
     */
    public void addFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment) {
        this.feedbackResponseComments.add(feedbackResponseComment);
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

    public UUID getQuestionId() {
        return questionId;
    }

    /**
     * Sets the feedback question of the feedback response.
     */
    public void setFeedbackQuestion(FeedbackQuestion feedbackQuestion) {
        this.feedbackQuestion = feedbackQuestion;
        this.questionId = feedbackQuestion == null ? null : feedbackQuestion.getId();
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

    public UUID getGiverSectionId() {
        return giverSectionId;
    }

    public String getGiverSectionName() {
        return giverSection.getName();
    }

    /**
     * Sets the giver section of the feedback response.
     */
    public void setGiverSection(Section giverSection) {
        this.giverSection = giverSection;
        this.giverSectionId = giverSection == null ? null : giverSection.getId();
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

    public UUID getRecipientSectionId() {
        return recipientSectionId;
    }

    public String getRecipientSectionName() {
        return recipientSection.getName();
    }

    /**
     * Sets the recipient section of the feedback response.
     */
    public void setRecipientSection(Section recipientSection) {
        this.recipientSection = recipientSection;
        this.recipientSectionId = recipientSection == null ? null : recipientSection.getId();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FeedbackResponse other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
