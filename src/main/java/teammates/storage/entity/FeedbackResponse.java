package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "questionId")
    private FeedbackQuestion feedbackQuestion;

    @Column(insertable = false, updatable = false)
    private UUID questionId;

    @OneToMany(mappedBy = "feedbackResponse")
    private Set<FeedbackResponseComment> feedbackResponseComments = new HashSet<>();

    @Embedded
    private ResponseGiver giver;

    @Embedded
    private ResponseRecipient recipient;

    @UpdateTimestamp
    private Instant updatedAt;

    protected FeedbackResponse() {
        // required by Hibernate
    }

    protected FeedbackResponse(
            ResponseGiver giver, ResponseRecipient recipient
    ) {
        this.setId(UUID.randomUUID());
        this.giver = giver;
        this.recipient = recipient;
    }

    /**
     * Creates a feedback response according to its {@code FeedbackQuestionType}.
     */
    public static FeedbackResponse makeResponse(
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails
    ) {
        FeedbackResponse feedbackResponse = null;
        switch (responseDetails.getQuestionType()) {
        case TEXT:
            feedbackResponse = new FeedbackTextResponse(
                    giver, recipient, responseDetails
            );
            break;
        case MCQ:
            feedbackResponse = new FeedbackMcqResponse(
                    giver, recipient, responseDetails
            );
            break;
        case MSQ:
            feedbackResponse = new FeedbackMsqResponse(
                    giver, recipient, responseDetails
            );
            break;
        case NUMSCALE:
            feedbackResponse = new FeedbackNumericalScaleResponse(
                    giver, recipient, responseDetails
            );
            break;
        case CONSTSUM, CONSTSUM_OPTIONS, CONSTSUM_RECIPIENTS:
            feedbackResponse = new FeedbackConstantSumResponse(
                    giver, recipient, responseDetails
            );
            break;
        case CONTRIB:
            feedbackResponse = new FeedbackContributionResponse(
                    giver, recipient, responseDetails
            );
            break;
        case RUBRIC:
            feedbackResponse = new FeedbackRubricResponse(
                    giver, recipient, responseDetails
            );
            break;
        case RANK_OPTIONS:
            feedbackResponse = new FeedbackRankOptionsResponse(
                    giver, recipient, responseDetails
            );
            break;
        case RANK_RECIPIENTS:
            feedbackResponse = new FeedbackRankRecipientsResponse(
                    giver, recipient, responseDetails
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
            FeedbackQuestion feedbackQuestion,
            ResponseGiver giver, ResponseRecipient recipient,
            FeedbackResponseDetails responseDetails
    ) {
        // TODO: update should be in logic layer
        FeedbackResponse updatedFeedbackResponse = makeResponse(
                giver,
                recipient,
                responseDetails
        );
        updatedFeedbackResponse.setCreatedAt(originalFeedbackResponse.getCreatedAt());
        updatedFeedbackResponse.setId(originalFeedbackResponse.getId());
        feedbackQuestion.addFeedbackResponse(updatedFeedbackResponse);
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
        feedbackResponseComment.setFeedbackResponse(this);
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

    public ResponseGiver getGiver() {
        return giver;
    }

    public void setGiver(ResponseGiver giver) {
        this.giver = giver;
    }

    public ResponseRecipient getRecipient() {
        return recipient;
    }

    public void setRecipient(ResponseRecipient recipient) {
        this.recipient = recipient;
    }

    /**
     * Sets the feedback question of the feedback response.
     */
    public void setFeedbackQuestion(FeedbackQuestion feedbackQuestion) {
        this.feedbackQuestion = feedbackQuestion;
        this.questionId = feedbackQuestion == null ? null : feedbackQuestion.getId();
    }

    public Set<FeedbackResponseComment> getFeedbackResponseComments() {
        return feedbackResponseComments;
    }

    public void setFeedbackResponseComments(Set<FeedbackResponseComment> feedbackResponseComments) {
        this.feedbackResponseComments = feedbackResponseComments;
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
