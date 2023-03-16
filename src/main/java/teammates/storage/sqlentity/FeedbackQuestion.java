package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.FieldValidator;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a feedback question.
 */
@Entity
@Table(name = "FeedbackQuestions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class FeedbackQuestion extends BaseEntity implements Comparable<FeedbackQuestion> {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sessionId")
    private FeedbackSession feedbackSession;

    @OneToMany(mappedBy = "feedbackQuestion")
    private List<FeedbackResponse> feedbackResponses = new ArrayList<>();

    @Column(nullable = false)
    private Integer questionNumber;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackQuestionType questionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackParticipantType giverType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackParticipantType recipientType;

    @Column(nullable = false)
    private Integer numOfEntitiesToGiveFeedbackTo;

    @Column(nullable = false)
    @Convert(converter = FeedbackParticipantTypeListConverter.class)
    private List<FeedbackParticipantType> showResponsesTo;

    @Column(nullable = false)
    @Convert(converter = FeedbackParticipantTypeListConverter.class)
    private List<FeedbackParticipantType> showGiverNameTo;

    @Column(nullable = false)
    @Convert(converter = FeedbackParticipantTypeListConverter.class)
    private List<FeedbackParticipantType> showRecipientNameTo;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    protected FeedbackQuestion() {
        // required by Hibernate
    }

    public FeedbackQuestion(
            FeedbackSession feedbackSession, Integer questionNumber,
            String description, FeedbackQuestionType questionType,
            FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo, List<FeedbackParticipantType> showRecipientNameTo
    ) {
        this.setId(UUID.randomUUID());
        this.setFeedbackSession(feedbackSession);
        this.setQuestionNumber(questionNumber);
        this.setDescription(description);
        this.setQuestionType(questionType);
        this.setGiverType(giverType);
        this.setRecipientType(recipientType);
        this.setNumOfEntitiesToGiveFeedbackTo(numOfEntitiesToGiveFeedbackTo);
        this.setShowResponsesTo(showResponsesTo);
        this.setShowGiverNameTo(showGiverNameTo);
        this.setShowRecipientNameTo(showRecipientNameTo);
    }

    /**
     * Gets a copy of the question details of the feedback question.
     */
    public abstract FeedbackQuestionDetails getQuestionDetailsCopy();

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        errors.addAll(FieldValidator.getValidityInfoForFeedbackParticipantType(giverType, recipientType));

        errors.addAll(FieldValidator.getValidityInfoForFeedbackResponseVisibility(showResponsesTo,
                showGiverNameTo,
                showRecipientNameTo));

        return errors;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public FeedbackSession getFeedbackSession() {
        return feedbackSession;
    }

    public void setFeedbackSession(FeedbackSession feedbackSession) {
        this.feedbackSession = feedbackSession;
    }

    public List<FeedbackResponse> getFeedbackResponses() {
        return feedbackResponses;
    }

    public void setFeedbackResponses(List<FeedbackResponse> feedbackResponses) {
        this.feedbackResponses = feedbackResponses;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    public FeedbackParticipantType getGiverType() {
        return giverType;
    }

    public void setGiverType(FeedbackParticipantType giverType) {
        this.giverType = giverType;
    }

    public FeedbackParticipantType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(FeedbackParticipantType recipientType) {
        this.recipientType = recipientType;
    }

    public Integer getNumOfEntitiesToGiveFeedbackTo() {
        return numOfEntitiesToGiveFeedbackTo;
    }

    public void setNumOfEntitiesToGiveFeedbackTo(Integer numOfEntitiesToGiveFeedbackTo) {
        this.numOfEntitiesToGiveFeedbackTo = numOfEntitiesToGiveFeedbackTo;
    }

    public List<FeedbackParticipantType> getShowResponsesTo() {
        return showResponsesTo;
    }

    public void setShowResponsesTo(List<FeedbackParticipantType> showResponsesTo) {
        this.showResponsesTo = showResponsesTo;
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public void setShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public List<FeedbackParticipantType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }

    public void setShowRecipientNameTo(List<FeedbackParticipantType> showRecipientNameTo) {
        this.showRecipientNameTo = showRecipientNameTo;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Question [id=" + id + ", questionNumber=" + questionNumber + ", description=" + description
                + ", questionType=" + questionType
                + ", giverType=" + giverType + ", recipientType=" + recipientType
                + ", numOfEntitiesToGiveFeedbackTo=" + numOfEntitiesToGiveFeedbackTo + ", showResponsesTo="
                + showResponsesTo + ", showGiverNameTo=" + showGiverNameTo + ", showRecipientNameTo="
                + showRecipientNameTo + ", isClosingEmailEnabled=" + ", createdAt=" + getCreatedAt() + ", updatedAt="
                + updatedAt + "]";
    }

    @Override
    public int compareTo(FeedbackQuestion o) {
        if (o == null) {
            return 1;
        }

        if (!this.questionNumber.equals(o.questionNumber)) {
            return Integer.compare(this.questionNumber, o.questionNumber);
        }
        // Although question numbers ought to be unique in a feedback session,
        // eventual consistency can result in duplicate questions numbers.
        // Therefore, to ensure that the question order is always consistent to the user,
        // compare feedbackQuestionId, which is guaranteed to be unique,
        // when the questionNumbers are the same.
        return this.id.compareTo(o.id);
    }

    @Override
    public int hashCode() {
        // FeedbackQuestion ID uniquely identifies a FeedbackQuestion.
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getClass() == other.getClass()) {
            FeedbackQuestion otherQuestion = (FeedbackQuestion) other;
            return Objects.equals(this.getId(), otherQuestion.getId());
        } else {
            return false;
        }
    }

    /**
     * Returns true if the response is visible to the given participant type.
     */
    public boolean isResponseVisibleTo(FeedbackParticipantType userType) {
        return showResponsesTo.contains(userType);
    }
}

