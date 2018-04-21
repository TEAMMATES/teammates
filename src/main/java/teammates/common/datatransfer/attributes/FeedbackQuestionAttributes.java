package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackQuestion;

public class FeedbackQuestionAttributes extends EntityAttributes<FeedbackQuestion>
        implements Comparable<FeedbackQuestionAttributes> {
    public String feedbackSessionName;
    public String courseId;
    public String creatorEmail;
    /**
     * Contains the JSON formatted string that holds the information of the question details.
     *
     * <p>Don't use directly unless for storing/loading from data store.<br>
     * To get the question text use {@code getQuestionDetails().questionText}
     */
    public Text questionMetaData;
    public Text questionDescription;
    public int questionNumber;
    public FeedbackQuestionType questionType;
    public FeedbackParticipantType giverType;
    public FeedbackParticipantType recipientType;
    public int numberOfEntitiesToGiveFeedbackTo;
    public List<FeedbackParticipantType> showResponsesTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public List<FeedbackParticipantType> showRecipientNameTo;
    protected transient Instant createdAt;
    protected transient Instant updatedAt;
    private String feedbackQuestionId;

    protected FeedbackQuestionAttributes() {
        //attributes to be built by Builder
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FeedbackQuestionAttributes valueOf(FeedbackQuestion fq) {
        return builder()
                .withFeedbackSessionName(fq.getFeedbackSessionName())
                .withCourseId(fq.getCourseId())
                .withCreatorEmail(fq.getCreatorEmail())
                .withQuestionMetaData(fq.getQuestionMetaData())
                .withQuestionDescription(fq.getQuestionDescription())
                .withQuestionNumber(fq.getQuestionNumber())
                .withQuestionType(fq.getQuestionType())
                .withGiverType(fq.getGiverType())
                .withRecipientType(fq.getRecipientType())
                .withNumOfEntitiesToGiveFeedbackTo(fq.getNumberOfEntitiesToGiveFeedbackTo())
                .withShowResponseTo(fq.getShowResponsesTo())
                .withShowGiverNameTo(fq.getShowGiverNameTo())
                .withShowRecipientNameTo(fq.getShowRecipientNameTo())
                .withCreatedAt(fq.getCreatedAt())
                .withUpdatedAt(fq.getUpdatedAt())
                .withFeedbackQuestionId(fq.getId())
                .build();

    }

    /**
     * A Builder class for {@link FeedbackQuestionAttributes}.
     */
    public static class Builder {
        private final FeedbackQuestionAttributes feedbackQuestionAttributes;

        public Builder() {
            feedbackQuestionAttributes = new FeedbackQuestionAttributes();
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            if (feedbackSessionName != null) {
                feedbackQuestionAttributes.feedbackSessionName = feedbackSessionName;
            }
            return this;
        }

        public Builder withCourseId(String courseId) {
            if (courseId != null) {
                feedbackQuestionAttributes.courseId = courseId;
            }
            return this;
        }

        public Builder withCreatorEmail(String creatorEmail) {
            if (creatorEmail != null) {
                feedbackQuestionAttributes.creatorEmail = creatorEmail;
            }
            return this;
        }

        public Builder withQuestionMetaData(Text questionMetaData) {
            if (questionMetaData != null) {
                feedbackQuestionAttributes.questionMetaData = questionMetaData;
            }
            return this;
        }

        public Builder withQuestionMetaData(FeedbackQuestionDetails questionDetails) {
            if (questionDetails != null) {
                feedbackQuestionAttributes.setQuestionDetails(questionDetails);
            }
            return this;
        }

        public Builder withQuestionDescription(Text questionDescription) {
            if (questionDescription != null) {
                feedbackQuestionAttributes.setQuestionDescription(questionDescription);
            }
            return this;
        }

        public Builder withQuestionNumber(int questionNumber) {
            feedbackQuestionAttributes.questionNumber = questionNumber;
            return this;
        }

        public Builder withQuestionType(FeedbackQuestionType questionType) {
            if (questionType != null) {
                feedbackQuestionAttributes.questionType = questionType;
            }
            return this;
        }

        public Builder withGiverType(FeedbackParticipantType giverType) {
            if (giverType != null) {
                feedbackQuestionAttributes.giverType = giverType;
            }
            return this;
        }

        public Builder withRecipientType(FeedbackParticipantType recipientType) {
            if (recipientType != null) {
                feedbackQuestionAttributes.recipientType = recipientType;
            }
            return this;
        }

        public Builder withNumOfEntitiesToGiveFeedbackTo(int numOfEntitiesToGiveFeedbackTo) {
            feedbackQuestionAttributes.numberOfEntitiesToGiveFeedbackTo = numOfEntitiesToGiveFeedbackTo;
            return this;
        }

        public Builder withShowResponseTo(List<FeedbackParticipantType> showResponseTo) {
            feedbackQuestionAttributes.showResponsesTo =
                    showResponseTo == null ? new ArrayList<>()
                            : new ArrayList<>(showResponseTo);
            return this;
        }

        public Builder withShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
            feedbackQuestionAttributes.showGiverNameTo =
                    showGiverNameTo == null ? new ArrayList<>()
                            : new ArrayList<>(showGiverNameTo);
            return this;
        }

        public Builder withShowRecipientNameTo(List<FeedbackParticipantType> showRecipientNameTo) {
            feedbackQuestionAttributes.showRecipientNameTo =
                    showRecipientNameTo == null ? new ArrayList<>()
                            : new ArrayList<>(showRecipientNameTo);
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            if (createdAt != null) {
                feedbackQuestionAttributes.createdAt = createdAt;
            }
            return this;
        }

        public Builder withUpdatedAt(Instant updatedAt) {
            if (updatedAt != null) {
                feedbackQuestionAttributes.updatedAt = updatedAt;
            }
            return this;
        }

        public Builder withFeedbackQuestionId(String feedbackQuestionId) {
            if (feedbackQuestionId != null) {
                feedbackQuestionAttributes.feedbackQuestionId = feedbackQuestionId;
            }
            return this;
        }

        public FeedbackQuestionAttributes build() {
            feedbackQuestionAttributes.questionDescription =
                    SanitizationHelper.sanitizeForRichText(feedbackQuestionAttributes.questionDescription);
            feedbackQuestionAttributes.removeIrrelevantVisibilityOptions();

            return feedbackQuestionAttributes;
        }
    }

    public Instant getCreatedAt() {
        return createdAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt == null ? Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP : updatedAt;
    }

    public String getId() {
        return feedbackQuestionId;
    }

    /** NOTE: Only use this to match and search for the ID of a known existing question entity. */
    public void setId(String id) {
        this.feedbackQuestionId = id;
    }

    @Override
    public FeedbackQuestion toEntity() {
        return new FeedbackQuestion(feedbackSessionName, courseId, creatorEmail,
                                    questionMetaData, questionDescription, questionNumber, questionType, giverType,
                                    recipientType, numberOfEntitiesToGiveFeedbackTo,
                                    showResponsesTo, showGiverNameTo, showRecipientNameTo);
    }

    @Override
    public String toString() {
        return "FeedbackQuestionAttributes [feedbackSessionName="
               + feedbackSessionName + ", courseId=" + courseId
               + ", creatorEmail=" + creatorEmail + ", questionText="
               + questionMetaData + ", questionDescription=" + questionDescription
               + ", questionNumber=" + questionNumber
               + ", questionType=" + questionType + ", giverType=" + giverType
               + ", recipientType=" + recipientType
               + ", numberOfEntitiesToGiveFeedbackTo="
               + numberOfEntitiesToGiveFeedbackTo + ", showResponsesTo="
               + showResponsesTo + ", showGiverNameTo=" + showGiverNameTo
               + ", showRecipientNameTo=" + showRecipientNameTo + "]";
    }

    @Override
    public String getIdentificationString() {
        return this.questionNumber + ". " + this.questionMetaData.toString() + "/"
               + this.feedbackSessionName + "/" + this.courseId;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Feedback Question";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, FeedbackQuestionAttributes.class);
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();

        addNonEmptyError(validator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(validator.getInvalidityInfoForCourseId(courseId), errors);

        // special case when additional text should be added to error text
        String error = validator.getInvalidityInfoForEmail(creatorEmail);
        if (!error.isEmpty()) {
            error = new StringBuffer()
                    .append("Invalid creator's email: ")
                    .append(error)
                    .toString();
        }
        addNonEmptyError(error, errors);

        errors.addAll(validator.getValidityInfoForFeedbackParticipantType(giverType, recipientType));

        errors.addAll(validator.getValidityInfoForFeedbackResponseVisibility(showResponsesTo,
                                                                             showGiverNameTo,
                                                                             showRecipientNameTo));

        return errors;
    }

    // TODO: move following methods to PageData?
    // Answer: OK to move to the respective PageData class. Unit test this thoroughly.
    public List<String> getVisibilityMessage() {
        List<String> message = new ArrayList<>();

        for (FeedbackParticipantType participant : showResponsesTo) {
            StringBuilder line = new StringBuilder(100);

            // Exceptional case: self feedback
            if (participant == FeedbackParticipantType.RECEIVER
                    && recipientType == FeedbackParticipantType.SELF) {
                message.add("You can see your own feedback in the results page later on.");
                continue;
            }

            // Front fragment: e.g. Other students in the course..., The receiving.., etc.
            line.append(participant.toVisibilityString()).append(' ');

            // Recipient fragment: e.g. student, instructor, etc.
            if (participant == FeedbackParticipantType.RECEIVER) {
                line.append(recipientType.toSingularFormString());

                if (numberOfEntitiesToGiveFeedbackTo > 1) {
                    line.append('s');
                }

                line.append(' ');
            }

            line.append("can see your response");

            // Visibility fragment: e.g. can see your name, but not...
            if (showRecipientNameTo.contains(participant)) {
                if (participant != FeedbackParticipantType.RECEIVER
                        && recipientType != FeedbackParticipantType.NONE) {
                    line.append(", the name of the recipient");
                }

                if (showGiverNameTo.contains(participant)) {
                    line.append(", and your name");
                } else {
                    line.append(", but not your name");
                }
            } else {
                if (showGiverNameTo.contains(participant)) {
                    line.append(", and your name");
                }

                if (recipientType == FeedbackParticipantType.NONE) {
                    if (!showGiverNameTo.contains(participant)) {
                        line.append(", but not your name");
                    }
                } else {
                    line.append(", but not the name of the recipient");

                    if (!showGiverNameTo.contains(participant)) {
                        line.append(", or your name");
                    }
                }

            }

            line.append('.');
            message.add(line.toString());
        }

        if (message.isEmpty()) {
            message.add("No-one can see your responses.");
        }

        return message;
    }

    @Override
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    public boolean isGiverAStudent() {
        return giverType == FeedbackParticipantType.SELF
               || giverType == FeedbackParticipantType.STUDENTS;
    }

    public boolean isRecipientNameHidden() {
        return recipientType == FeedbackParticipantType.NONE
               || recipientType == FeedbackParticipantType.SELF;
    }

    public boolean isRecipientAStudent() {
        return recipientType == FeedbackParticipantType.SELF
               || recipientType == FeedbackParticipantType.STUDENTS
               || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS
               || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
    }

    public boolean isRecipientInstructor() {
        return recipientType == FeedbackParticipantType.INSTRUCTORS;
    }

    public boolean isResponseVisibleTo(FeedbackParticipantType userType) {
        return showResponsesTo.contains(userType);
    }

    /**
     * Checks if updating this question to the {@code newAttributes} will
     * require the responses to be deleted for consistency.
     * Does not check if any responses exist.
     */
    public boolean areResponseDeletionsRequiredForChanges(FeedbackQuestionAttributes newAttributes) {
        if (!newAttributes.giverType.equals(this.giverType)
                || !newAttributes.recipientType.equals(this.recipientType)) {
            return true;
        }

        return this.getQuestionDetails().shouldChangesRequireResponseDeletion(newAttributes.getQuestionDetails());
    }

    @Override
    public int compareTo(FeedbackQuestionAttributes o) {
        if (o == null) {
            return 1;
        }

        if (this.questionNumber != o.questionNumber) {
            return Integer.compare(this.questionNumber, o.questionNumber);
        }
        /**
         * Although question numbers ought to be unique in a feedback session,
         * eventual consistency can result in duplicate questions numbers.
         * Therefore, to ensure that the question order is always consistent to the user,
         * compare feedbackQuestionId, which is guaranteed to be unique,
         * when the questionNumbers are the same.
         */
        return this.feedbackQuestionId.compareTo(o.feedbackQuestionId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + (courseId == null ? 0 : courseId.hashCode());

        result = prime * result + (creatorEmail == null ? 0 : creatorEmail.hashCode());

        result = prime * result + (feedbackSessionName == null ? 0 : feedbackSessionName.hashCode());

        result = prime * result + (giverType == null ? 0 : giverType.hashCode());

        result = prime * result + numberOfEntitiesToGiveFeedbackTo;

        result = prime * result + questionNumber;

        result = prime * result + (questionMetaData == null ? 0 : questionMetaData.hashCode());

        result = prime * result + (questionDescription == null ? 0 : questionDescription.hashCode());

        result = prime * result + (questionType == null ? 0 : questionType.hashCode());

        result = prime * result + (recipientType == null ? 0 : recipientType.hashCode());

        result = prime * result + (showGiverNameTo == null ? 0 : showGiverNameTo.hashCode());

        result = prime * result + (showRecipientNameTo == null ? 0 : showRecipientNameTo.hashCode());

        result = prime * result + (showResponsesTo == null ? 0 : showResponsesTo.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        FeedbackQuestionAttributes other = (FeedbackQuestionAttributes) obj;

        if (courseId == null) {
            if (other.courseId != null) {
                return false;
            }
        } else if (!courseId.equals(other.courseId)) {
            return false;
        }

        if (creatorEmail == null) {
            if (other.creatorEmail != null) {
                return false;
            }
        } else if (!creatorEmail.equals(other.creatorEmail)) {
            return false;
        }

        if (feedbackSessionName == null) {
            if (other.feedbackSessionName != null) {
                return false;
            }
        } else if (!feedbackSessionName.equals(other.feedbackSessionName)) {
            return false;
        }

        if (giverType != other.giverType) {
            return false;
        }

        if (numberOfEntitiesToGiveFeedbackTo != other.numberOfEntitiesToGiveFeedbackTo) {
            return false;
        }

        if (questionNumber != other.questionNumber) {
            return false;
        }

        if (questionMetaData == null) {
            if (other.questionMetaData != null) {
                return false;
            }
        } else if (!questionMetaData.equals(other.questionMetaData)) {
            return false;
        }

        if (questionDescription == null) {
            if (other.questionDescription != null) {
                return false;
            }
        } else if (!questionDescription.equals(other.questionDescription)) {
            return false;
        }

        if (questionType != other.questionType) {
            return false;
        }

        if (recipientType != other.recipientType) {
            return false;
        }

        if (showGiverNameTo == null) {
            if (other.showGiverNameTo != null) {
                return false;
            }
        } else if (!showGiverNameTo.equals(other.showGiverNameTo)) {
            return false;
        }

        if (showRecipientNameTo == null) {
            if (other.showRecipientNameTo != null) {
                return false;
            }
        } else if (!showRecipientNameTo.equals(other.showRecipientNameTo)) {
            return false;
        }

        if (showResponsesTo == null) {
            if (other.showResponsesTo != null) {
                return false;
            }
        } else if (!showResponsesTo.equals(other.showResponsesTo)) {
            return false;
        }

        return true;
    }

    public void updateValues(FeedbackQuestionAttributes newAttributes) {
        // These can't be changed anyway. Copy values to defensively avoid invalid parameters.
        newAttributes.feedbackSessionName = this.feedbackSessionName;
        newAttributes.courseId = this.courseId;
        newAttributes.creatorEmail = this.creatorEmail;

        if (newAttributes.questionMetaData == null) {
            newAttributes.questionMetaData = this.questionMetaData;
        }

        if (newAttributes.questionDescription == null) {
            newAttributes.questionDescription = this.questionDescription;
        }

        if (newAttributes.questionType == null) {
            newAttributes.questionType = this.questionType;
        }

        if (newAttributes.giverType == null) {
            newAttributes.giverType = this.giverType;
        }

        if (newAttributes.recipientType == null) {
            newAttributes.recipientType = this.recipientType;
        }

        if (newAttributes.showResponsesTo == null) {
            newAttributes.showResponsesTo = this.showResponsesTo;
        }

        if (newAttributes.showGiverNameTo == null) {
            newAttributes.showGiverNameTo = this.showGiverNameTo;
        }

        if (newAttributes.showRecipientNameTo == null) {
            newAttributes.showRecipientNameTo = this.showRecipientNameTo;
        }
    }

    public void removeIrrelevantVisibilityOptions() {
        List<FeedbackParticipantType> optionsToRemove = new ArrayList<>();

        if (recipientType != null) {
            switch (recipientType) {
            case NONE:
                optionsToRemove.add(FeedbackParticipantType.RECEIVER);
                optionsToRemove.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
                break;
            case TEAMS:
            case INSTRUCTORS:
            case OWN_TEAM:
            case OWN_TEAM_MEMBERS:
                optionsToRemove.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
                break;
            default:
                break;
            }
        }

        if (giverType != null) {
            switch (giverType) {
            case TEAMS:
            case INSTRUCTORS:
                optionsToRemove.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
                break;
            default:
                break;
            }
        }

        removeVisibilities(optionsToRemove);
    }

    private void removeVisibilities(List<FeedbackParticipantType> optionsToRemove) {
        if (showRecipientNameTo != null) {
            showResponsesTo.removeAll(optionsToRemove);
        }

        if (showGiverNameTo != null) {
            showGiverNameTo.removeAll(optionsToRemove);
        }

        if (showRecipientNameTo != null) {
            showRecipientNameTo.removeAll(optionsToRemove);
        }
    }

    @Override
    public void sanitizeForSaving() {
        this.questionDescription = SanitizationHelper.sanitizeForRichText(this.questionDescription);
    }

    private boolean isValidJsonString(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * Converts the given Feedback*QuestionDetails object to JSON for storing.
     */
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        questionMetaData = new Text(JsonUtils.toJson(questionDetails, getFeedbackQuestionDetailsClass()));
    }

    /**
     * Retrieves the Feedback*QuestionDetails object for this question.
     *
     * @return The Feedback*QuestionDetails object representing the question's details
     */
    public FeedbackQuestionDetails getQuestionDetails() {
        final String questionMetaDataValue = questionMetaData.getValue();
        // For old Text questions, the questionText simply contains the question, not a JSON
        if (questionType == FeedbackQuestionType.TEXT && !isValidJsonString(questionMetaDataValue)) {
            return new FeedbackTextQuestionDetails(questionMetaDataValue);
        }
        return JsonUtils.fromJson(questionMetaDataValue, getFeedbackQuestionDetailsClass());
    }

    /**
     * This method gets the appropriate class type for the Feedback*QuestionDetails object for this question.
     *
     * @return The Feedback*QuestionDetails class type appropriate for this question.
     */
    private Class<? extends FeedbackQuestionDetails> getFeedbackQuestionDetailsClass() {
        return questionType.getQuestionDetailsClass();
    }

    public String getFeedbackQuestionId() {
        return feedbackQuestionId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public Text getQuestionMetaData() {
        return questionMetaData;
    }

    public Text getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(Text questionDescription) {
        this.questionDescription = questionDescription;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public FeedbackParticipantType getGiverType() {
        return giverType;
    }

    public FeedbackParticipantType getRecipientType() {
        return recipientType;
    }

    public int getNumberOfEntitiesToGiveFeedbackTo() {
        return numberOfEntitiesToGiveFeedbackTo;
    }

    public List<FeedbackParticipantType> getShowResponsesTo() {
        return showResponsesTo;
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public List<FeedbackParticipantType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }

    public String getQuestionAdditionalInfoHtml() {
        return getQuestionDetails().getQuestionAdditionalInfoHtml(questionNumber, "");
    }
}
