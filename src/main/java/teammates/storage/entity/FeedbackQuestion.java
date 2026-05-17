package teammates.storage.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.FieldValidator;
import teammates.storage.entity.questions.FeedbackConstantSumQuestion;
import teammates.storage.entity.questions.FeedbackContributionQuestion;
import teammates.storage.entity.questions.FeedbackMcqQuestion;
import teammates.storage.entity.questions.FeedbackMsqQuestion;
import teammates.storage.entity.questions.FeedbackNumericalScaleQuestion;
import teammates.storage.entity.questions.FeedbackRankOptionsQuestion;
import teammates.storage.entity.questions.FeedbackRankRecipientsQuestion;
import teammates.storage.entity.questions.FeedbackRubricQuestion;
import teammates.storage.entity.questions.FeedbackTextQuestion;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sessionId")
    private FeedbackSession feedbackSession;

    @Column(insertable = false, updatable = false)
    private UUID sessionId;

    @OneToMany(mappedBy = "feedbackQuestion")
    private Set<FeedbackResponse> feedbackResponses = new HashSet<>();

    @Column(nullable = false)
    private Integer questionNumber;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionGiverType giverType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionRecipientType recipientType;

    @Column(nullable = false)
    private Integer numOfEntitiesToGiveFeedbackTo;

    @Column(nullable = false)
    @Convert(converter = ViewerTypeListConverter.class)
    private List<ViewerType> showResponsesTo;

    @Column(nullable = false)
    @Convert(converter = ViewerTypeListConverter.class)
    private List<ViewerType> showGiverNameTo;

    @Column(nullable = false)
    @Convert(converter = ViewerTypeListConverter.class)
    private List<ViewerType> showRecipientNameTo;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    protected FeedbackQuestion() {
        // required by Hibernate
    }

    protected FeedbackQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<ViewerType> showResponsesTo,
            List<ViewerType> showGiverNameTo, List<ViewerType> showRecipientNameTo
    ) {
        this.setId(UUID.randomUUID());
        this.setFeedbackSession(feedbackSession);
        this.setQuestionNumber(questionNumber);
        this.setDescription(description);
        this.setGiverType(giverType);
        this.setRecipientType(recipientType);
        this.setNumOfEntitiesToGiveFeedbackTo(numOfEntitiesToGiveFeedbackTo);
        this.setShowResponsesTo(showResponsesTo);
        this.setShowGiverNameTo(showGiverNameTo);
        this.setShowRecipientNameTo(showRecipientNameTo);
    }

    /**
     * Gets the type of the feedback question.
     */
    public abstract FeedbackQuestionType getQuestionType();

    /**
     * Gets a copy of the question details of the feedback question.
     */
    public abstract FeedbackQuestionDetails getQuestionDetailsCopy();

    /**
     * Make a copy of the FeedbackQuestion.
     */
    public abstract FeedbackQuestion makeDeepCopy();

    /**
     * Creates a feedback question according to its {@code FeedbackQuestionType}.
     */
    public static FeedbackQuestion makeQuestion(
            Integer questionNumber,
            String description, QuestionGiverType giverType, QuestionRecipientType recipientType,
            Integer numOfEntitiesToGiveFeedbackTo, List<ViewerType> showResponsesTo,
            List<ViewerType> showGiverNameTo, List<ViewerType> showRecipientNameTo,
            FeedbackQuestionDetails feedbackQuestionDetails
    ) {
        FeedbackQuestion feedbackQuestion = null;
        switch (feedbackQuestionDetails.getQuestionType()) {
        case TEXT:
            feedbackQuestion = new FeedbackTextQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        case MCQ:
            feedbackQuestion = new FeedbackMcqQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        case MSQ:
            feedbackQuestion = new FeedbackMsqQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        case NUMSCALE:
            feedbackQuestion = new FeedbackNumericalScaleQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        case CONSTSUM, CONSTSUM_OPTIONS, CONSTSUM_RECIPIENTS:
            feedbackQuestion = new FeedbackConstantSumQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        case CONTRIB:
            feedbackQuestion = new FeedbackContributionQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        case RUBRIC:
            feedbackQuestion = new FeedbackRubricQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        case RANK_OPTIONS:
            feedbackQuestion = new FeedbackRankOptionsQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        case RANK_RECIPIENTS:
            feedbackQuestion = new FeedbackRankRecipientsQuestion(
                    questionNumber, description, giverType, recipientType,
                    numOfEntitiesToGiveFeedbackTo, showResponsesTo, showGiverNameTo, showRecipientNameTo,
                    feedbackQuestionDetails
            );
            break;
        }
        return feedbackQuestion;
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        errors.addAll(FieldValidator.getValidityInfoForFeedbackParticipantType(giverType, recipientType));

        errors.addAll(FieldValidator.getValidityInfoForFeedbackResponseVisibility(showResponsesTo,
                showGiverNameTo,
                showRecipientNameTo));

        return errors;
    }

    /**
     * Checks if updating this question to the question will
     * require the responses to be deleted for consistency.
     * Does not check if any responses exist.
     */
    public boolean areResponseDeletionsRequiredForChanges(QuestionGiverType giverType,
                                                          QuestionRecipientType recipientType,
                                                          FeedbackQuestionDetails questionDetails) {
        if (giverType != this.giverType
                || recipientType != this.recipientType) {
            return true;
        }

        return this.getQuestionDetailsCopy().shouldChangesRequireResponseDeletion(questionDetails);
    }

    /**
     * Adds a feedback response to the question.
     */
    public void addFeedbackResponse(FeedbackResponse feedbackResponse) {
        this.feedbackResponses.add(feedbackResponse);
        feedbackResponse.setFeedbackQuestion(this);
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

    public String getFeedbackSessionName() {
        return feedbackSession.getName();
    }

    public UUID getSessionId() {
        return sessionId;
    }

    /**
     * Sets the feedback session of the question.
     */
    public void setFeedbackSession(FeedbackSession feedbackSession) {
        this.feedbackSession = feedbackSession;
        this.sessionId = feedbackSession == null ? null : feedbackSession.getId();
    }

    public Set<FeedbackResponse> getFeedbackResponses() {
        return feedbackResponses;
    }

    public void setFeedbackResponses(Set<FeedbackResponse> feedbackResponses) {
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

    /**
     * Set the question details of the question.
     */
    public abstract void setQuestionDetails(FeedbackQuestionDetails questionDetails);

    public QuestionGiverType getGiverType() {
        return giverType;
    }

    public void setGiverType(QuestionGiverType giverType) {
        this.giverType = giverType;
    }

    public QuestionRecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(QuestionRecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public Integer getNumOfEntitiesToGiveFeedbackTo() {
        return numOfEntitiesToGiveFeedbackTo;
    }

    public void setNumOfEntitiesToGiveFeedbackTo(Integer numOfEntitiesToGiveFeedbackTo) {
        this.numOfEntitiesToGiveFeedbackTo = numOfEntitiesToGiveFeedbackTo;
    }

    public List<ViewerType> getShowResponsesTo() {
        return showResponsesTo;
    }

    public void setShowResponsesTo(List<ViewerType> showResponsesTo) {
        this.showResponsesTo = showResponsesTo;
    }

    public List<ViewerType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public void setShowGiverNameTo(List<ViewerType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public List<ViewerType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }

    public void setShowRecipientNameTo(List<ViewerType> showRecipientNameTo) {
        this.showRecipientNameTo = showRecipientNameTo;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Course getCourse() {
        return this.feedbackSession.getCourse();
    }

    public String getCourseId() {
        return this.feedbackSession.getCourseId();
    }

    @Override
    public String toString() {
        return "Question [id=" + id + ", questionNumber=" + questionNumber + ", description=" + description
                + ", giverType=" + giverType + ", recipientType=" + recipientType
                + ", numOfEntitiesToGiveFeedbackTo=" + numOfEntitiesToGiveFeedbackTo + ", showResponsesTo="
                + showResponsesTo + ", showGiverNameTo=" + showGiverNameTo + ", showRecipientNameTo="
                + showRecipientNameTo + ", createdAt=" + getCreatedAt() + ", updatedAt=" + updatedAt + "]";
    }

    @Override
    public int compareTo(FeedbackQuestion o) {
        if (o == null) {
            return 1;
        }

        if (!this.questionNumber.equals(o.questionNumber)) {
            return Integer.compare(this.questionNumber, o.questionNumber);
        }

        // In the event that two questions have the same question number, we order them by their Ids
        // to ensure that the question order is always consistent to the user.
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FeedbackQuestion other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns true if the response is visible to the given participant type.
     */
    public boolean isResponseVisibleTo(ViewerType userType) {
        return showResponsesTo.contains(userType);
    }
}

