package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackQuestion;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

public class FeedbackQuestionAttributes extends EntityAttributes implements Comparable<FeedbackQuestionAttributes> {
    private String feedbackQuestionId = null;
    public String feedbackSessionName;
    public String courseId;
    public String creatorEmail;
    /** 
     * Contains the JSON formatted string that holds the information of the question details <br>
     * Don't use directly unless for storing/loading from data store <br>
     * To get the question text use {@code getQuestionDetails().questionText}
     */
    public Text questionMetaData;
    public int questionNumber;
    public FeedbackQuestionType questionType;
    public FeedbackParticipantType giverType;
    public FeedbackParticipantType recipientType;
    public int numberOfEntitiesToGiveFeedbackTo;
    public List<FeedbackParticipantType> showResponsesTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public List<FeedbackParticipantType> showRecipientNameTo;

    public FeedbackQuestionAttributes() {
        
    }

    public FeedbackQuestionAttributes(FeedbackQuestion fq) {
        this.feedbackQuestionId = fq.getId();
        this.feedbackSessionName = fq.getFeedbackSessionName();
        this.courseId = fq.getCourseId();
        this.creatorEmail = fq.getCreatorEmail();
        this.questionMetaData = fq.getQuestionMetaData();
        this.questionNumber = fq.getQuestionNumber();
        this.questionType = fq.getQuestionType();
        this.giverType = fq.getGiverType();
        this.recipientType = fq.getRecipientType();
        this.numberOfEntitiesToGiveFeedbackTo = fq.getNumberOfEntitiesToGiveFeedbackTo();
        this.showResponsesTo = fq.getShowResponsesTo();
        this.showGiverNameTo = fq.getShowGiverNameTo();
        this.showRecipientNameTo = fq.getShowRecipientNameTo();
    }

    public FeedbackQuestionAttributes(String feedbackSessionName, String courseId, String creatorEmail,
                   Text questionMetaData, int questionNumber, FeedbackQuestionType questionType,
                   FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
                   int numberOfEntitiesToGiveFeedbackTo, List<FeedbackParticipantType> showResponsesTo,
                   List<FeedbackParticipantType> showGiverNameTo,
                   List<FeedbackParticipantType> showRecipientNameTo) {
        this.feedbackSessionName = Sanitizer.sanitizeTitle(feedbackSessionName);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.creatorEmail = Sanitizer.sanitizeGoogleId(creatorEmail);
        this.questionMetaData = questionMetaData;
        this.questionNumber = questionNumber;
        this.questionType = questionType;
        this.giverType = giverType;
        this.recipientType = recipientType;
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
        this.showResponsesTo = showResponsesTo;
        this.showGiverNameTo = showGiverNameTo;
        this.showRecipientNameTo = showRecipientNameTo;
    }

    public String getId() {
        return feedbackQuestionId;
    }

    /** NOTE: Only use this to match and search for the ID of a known existing question entity. */
    public void setId(String id) {
        this.feedbackQuestionId = id;
    }

    public FeedbackQuestion toEntity() {
        return new FeedbackQuestion(feedbackSessionName, courseId, creatorEmail,
                                    questionMetaData, questionNumber, questionType, giverType,
                                    recipientType, numberOfEntitiesToGiveFeedbackTo,
                                    showResponsesTo, showGiverNameTo, showRecipientNameTo);
    }

    @Override
    public String toString() {
        return "FeedbackQuestionAttributes [feedbackSessionName="
               + feedbackSessionName + ", courseId=" + courseId
               + ", creatorEmail=" + creatorEmail + ", questionText="
               + questionMetaData + ", questionNumber=" + questionNumber
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
        return Utils.getTeammatesGson().toJson(this, FeedbackQuestionAttributes.class);
    }

    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;

        error = validator.getInvalidityInfo(FieldType.FEEDBACK_SESSION_NAME, feedbackSessionName);
        if (!error.isEmpty()) { errors.add(error); }

        error = validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
        if (!error.isEmpty()) { errors.add(error); }

        error = validator.getInvalidityInfo(FieldType.EMAIL, "creator's email", creatorEmail);
        if (!error.isEmpty()) { errors.add(error); }

        errors.addAll(validator.getValidityInfoForFeedbackParticipantType(giverType, recipientType));

        errors.addAll(validator.getValidityInfoForFeedbackResponseVisibility(showResponsesTo,
                                                                             showGiverNameTo,
                                                                             showRecipientNameTo));

        return errors;
    }

    // TODO: move following methods to PageData?
    // Answer: OK to move to the respective PageData class. Unit test this thoroughly.
    public List<String> getVisibilityMessage() {
        List<String> message = new ArrayList<String>();

        for (FeedbackParticipantType participant : showResponsesTo) {
            String line = "";

            // Exceptional case: self feedback
            if (participant == FeedbackParticipantType.RECEIVER
                && recipientType == FeedbackParticipantType.SELF) {
                message.add("You can see your own feedback in the results page later on.");
                continue;
            }

            // Front fragment: e.g. Other students in the course..., The receiving.., etc.
            line = participant.toVisibilityString() + " ";

            // Recipient fragment: e.g. student, instructor, etc.
            if (participant == FeedbackParticipantType.RECEIVER) {
                line += (recipientType.toSingularFormString());

                if (numberOfEntitiesToGiveFeedbackTo > 1) {
                    line += "s";
                }

                line += " ";
            }

            line += "can see your response";

            // Visibility fragment: e.g. can see your name, but not...
            if (showRecipientNameTo.contains(participant) == false) {
                if (showGiverNameTo.contains(participant) == true) {
                    line += ", and your name";
                }

                if (recipientType != FeedbackParticipantType.NONE) {
                    line += ", but not the name of the recipient";

                    if (showGiverNameTo.contains(participant) == false) {
                        line += ", or your name";
                    }
                } else {
                    if (showGiverNameTo.contains(participant) == false) {
                        line += ", but not your name";
                    }
                }

            } else if (showRecipientNameTo.contains(participant) == true) {
                if (participant != FeedbackParticipantType.RECEIVER
                    && recipientType != FeedbackParticipantType.NONE) {
                    line += ", the name of the recipient";
                }

                if (showGiverNameTo.contains(participant)) {
                    line += ", and your name";
                } else {
                    line += ", but not your name";
                }
            }

            line += ".";
            message.add(line);
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
        return (giverType == FeedbackParticipantType.SELF
                || giverType == FeedbackParticipantType.STUDENTS);
    }

    public boolean isRecipientNameHidden() {
        return (recipientType == FeedbackParticipantType.NONE
                || recipientType == FeedbackParticipantType.SELF);
    }

    public boolean isRecipientAStudent() {
        return (recipientType == FeedbackParticipantType.SELF
                || recipientType == FeedbackParticipantType.STUDENTS
                || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS
                || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
    }

    public boolean isResponseVisibleTo(FeedbackParticipantType userType) {
        return showResponsesTo.contains(userType);
    }

    /**
     * Checks if updating this question to the {@code newAttributes} will
     * require the responses to be deleted for consistency.
     * Does not check if any responses exist.
     * 
     * @param newAttributes
     * @return
     */
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionAttributes newAttributes) {
        if (newAttributes.giverType.equals(this.giverType) == false
            || newAttributes.recipientType.equals(this.recipientType) == false) {
            return true;
        }

        if (this.showResponsesTo.containsAll(newAttributes.showResponsesTo) == false
            || this.showGiverNameTo.containsAll(newAttributes.showGiverNameTo) == false
            || this.showRecipientNameTo.containsAll(newAttributes.showRecipientNameTo) == false) {
            return true;
        }

        if (this.getQuestionDetails().isChangesRequiresResponseDeletion(newAttributes.getQuestionDetails())) {
            return true;
        }

        return false;
    }

    @Override
    public int compareTo(FeedbackQuestionAttributes o) {
        if (o == null) {
            return 1;
        } else {
            return Integer.compare(this.questionNumber, o.questionNumber);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((courseId == null) ? 0 : courseId.hashCode());

        result = prime * result + ((creatorEmail == null) ? 0 : creatorEmail.hashCode());

        result = prime * result + ((feedbackSessionName == null) ? 0 : feedbackSessionName.hashCode());

        result = prime * result + ((giverType == null) ? 0 : giverType.hashCode());

        result = prime * result + numberOfEntitiesToGiveFeedbackTo;

        result = prime * result + questionNumber;

        result = prime * result + ((questionMetaData == null) ? 0 : questionMetaData.hashCode());

        result = prime * result + ((questionType == null) ? 0 : questionType.hashCode());

        result = prime * result + ((recipientType == null) ? 0 : recipientType.hashCode());

        result = prime * result + ((showGiverNameTo == null) ? 0 : showGiverNameTo.hashCode());

        result = prime * result + ((showRecipientNameTo == null) ? 0 : showRecipientNameTo.hashCode());

        result = prime * result + ((showResponsesTo == null) ? 0 : showResponsesTo.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }

        if (obj == null) { return false; }

        if (getClass() != obj.getClass()) { return false; }

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
        List<FeedbackParticipantType> optionsToRemove = new ArrayList<FeedbackParticipantType>();

        switch (recipientType) {
            case NONE:
                optionsToRemove.add(FeedbackParticipantType.RECEIVER);
                optionsToRemove.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
                break;
            case TEAMS:
                optionsToRemove.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
                break;
            default:
                break;
        }

        switch (giverType) {
            case TEAMS:
                optionsToRemove.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
                break;
            default:
                break;
        }

        removeVisibilities(optionsToRemove);
    }

    private void removeVisibilities(List<FeedbackParticipantType> optionsToRemove) {
        showResponsesTo.removeAll(optionsToRemove);
        showGiverNameTo.removeAll(optionsToRemove);
        showRecipientNameTo.removeAll(optionsToRemove);
    }

    @Override
    public void sanitizeForSaving() {
        this.feedbackQuestionId = Sanitizer.sanitizeTitle(feedbackQuestionId);
        this.feedbackSessionName = Sanitizer.sanitizeForHtml(feedbackSessionName);
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.creatorEmail = Sanitizer.sanitizeEmail(creatorEmail);
    }

    /** 
     * This method converts the given Feedback*QuestionDetails object to JSON for storing
     * 
     * @param questionDetails
     */
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        // For Text questions, the questionText simply contains the question, not a JSON
        // This is due to legacy data in the data store before there are multiple question types
        if (questionDetails.questionType == FeedbackQuestionType.TEXT) {
            questionMetaData = new Text(questionDetails.questionText);
        } else {
            Gson gson = teammates.common.util.Utils.getTeammatesGson();
            questionMetaData = new Text(gson.toJson(questionDetails, getFeedbackQuestionDetailsClass()));
        }
    }

    /** 
     * This method retrieves the Feedback*QuestionDetails object for this question
     * 
     * @return The Feedback*QuestionDetails object representing the question's details
     */
    public FeedbackQuestionDetails getQuestionDetails() {
        // For Text questions, the questionText simply contains the question, not a JSON
        // This is due to legacy data in the data store before there are multiple question types
        if (questionType == FeedbackQuestionType.TEXT) {
            return new FeedbackTextQuestionDetails(questionMetaData.getValue());
        } else {
            Gson gson = teammates.common.util.Utils.getTeammatesGson();
            return gson.fromJson(questionMetaData.getValue(), getFeedbackQuestionDetailsClass());
        }
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
