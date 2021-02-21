package teammates.common.datatransfer.attributes;

import com.google.gson.JsonParseException;
import teammates.Globals;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackQuestion;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FeedbackQuestionAttributes extends EntityAttributes<FeedbackQuestion>
        implements Comparable<FeedbackQuestionAttributes> {

    public String feedbackSessionName;
    public String courseId;
    public FeedbackQuestionDetails questionDetails;
    public String questionDescription;
    public int questionNumber;
    public FeedbackParticipantType giverType;
    public FeedbackParticipantType recipientType;
    public int numberOfEntitiesToGiveFeedbackTo;
    public List<FeedbackParticipantType> showResponsesTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public List<FeedbackParticipantType> showRecipientNameTo;
    private transient Instant createdAt;
    private transient Instant updatedAt;

    private String feedbackQuestionId;
    private FeedbackQuestionAttributes() {
        this.showResponsesTo = new ArrayList<>();
        this.showGiverNameTo = new ArrayList<>();
        this.showRecipientNameTo = new ArrayList<>();
    }


    /**
     * Returns a builder for {@link FeedbackQuestionAttributes}.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static FeedbackQuestionAttributes valueOf(FeedbackQuestion fq) {
        FeedbackQuestionAttributes faq = new FeedbackQuestionAttributes();

        faq.feedbackSessionName = fq.getFeedbackSessionName();
        faq.courseId = fq.getCourseId();
        faq.questionDetails = deserializeFeedbackQuestionDetails(fq.getQuestionMetaData(), fq.getQuestionType());
        faq.questionDescription = fq.getQuestionDescription();
        faq.questionNumber = fq.getQuestionNumber();
        faq.giverType = fq.getGiverType();
        faq.recipientType = fq.getRecipientType();
        faq.numberOfEntitiesToGiveFeedbackTo = fq.getNumberOfEntitiesToGiveFeedbackTo();
        if (fq.getShowResponsesTo() != null) {
            faq.showResponsesTo = new ArrayList<>(fq.getShowResponsesTo());
        }
        if (fq.getShowGiverNameTo() != null) {
            faq.showGiverNameTo = new ArrayList<>(fq.getShowGiverNameTo());
        }
        if (fq.getShowRecipientNameTo() != null) {
            faq.showRecipientNameTo = new ArrayList<>(fq.getShowRecipientNameTo());
        }
        faq.createdAt = fq.getCreatedAt();
        faq.updatedAt = fq.getUpdatedAt();
        faq.feedbackQuestionId = fq.getId();

        return faq;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getId() {
        return feedbackQuestionId;
    }

    /** NOTE: Only use this to match and search for the ID of a known existing question entity. */
    @Deprecated
    public void setId(String id) {
        this.feedbackQuestionId = id;
    }

    @Override
    public FeedbackQuestion toEntity() {
        return new FeedbackQuestion(feedbackSessionName, courseId,
                                    getSerializedQuestionDetails(), questionDescription,
                                    questionNumber, getQuestionType(), giverType,
                                    recipientType, numberOfEntitiesToGiveFeedbackTo,
                                    showResponsesTo, showGiverNameTo, showRecipientNameTo);
    }

    public FeedbackQuestionAttributes getCopy() {
        FeedbackQuestionAttributes faq = new FeedbackQuestionAttributes();

        faq.feedbackSessionName = this.feedbackSessionName;
        faq.courseId = this.courseId;
        faq.questionDetails = this.getQuestionDetails();
        faq.questionDescription = this.questionDescription;
        faq.questionNumber = this.questionNumber;
        faq.giverType = this.giverType;
        faq.recipientType = this.recipientType;
        faq.numberOfEntitiesToGiveFeedbackTo = this.numberOfEntitiesToGiveFeedbackTo;
        faq.showResponsesTo = new ArrayList<>(this.showResponsesTo);
        faq.showGiverNameTo = new ArrayList<>(this.showGiverNameTo);
        faq.showRecipientNameTo = new ArrayList<>(this.showRecipientNameTo);
        faq.createdAt = this.createdAt;
        faq.updatedAt = this.updatedAt;
        faq.feedbackQuestionId = this.feedbackQuestionId;

        return faq;
    }

    @Override
    public String toString() {
        return "FeedbackQuestionAttributes [feedbackSessionName="
               + feedbackSessionName + ", courseId=" + courseId
               + ", questionText="
               + getSerializedQuestionDetails() + ", questionDescription=" + questionDescription
               + ", questionNumber=" + questionNumber
               + ", questionType=" + getQuestionType() + ", giverType=" + giverType
               + ", recipientType=" + recipientType
               + ", numberOfEntitiesToGiveFeedbackTo="
               + numberOfEntitiesToGiveFeedbackTo + ", showResponsesTo="
               + showResponsesTo + ", showGiverNameTo=" + showGiverNameTo
               + ", showRecipientNameTo=" + showRecipientNameTo + "]";
    }

    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = new ArrayList<>();

        addNonEmptyError(FieldValidator.getInvalidityInfoForFeedbackSessionName(feedbackSessionName), errors);

        addNonEmptyError(FieldValidator.getInvalidityInfoForCourseId(courseId), errors);

        errors.addAll(FieldValidator.getValidityInfoForFeedbackParticipantType(giverType, recipientType));

        errors.addAll(FieldValidator.getValidityInfoForFeedbackResponseVisibility(showResponsesTo,
                                                                             showGiverNameTo,
                                                                             showRecipientNameTo));

        return errors;
    }

    @Override
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
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
        // Although question numbers ought to be unique in a feedback session,
        // eventual consistency can result in duplicate questions numbers.
        // Therefore, to ensure that the question order is always consistent to the user,
        // compare feedbackQuestionId, which is guaranteed to be unique,
        // when the questionNumbers are the same.
        return this.feedbackQuestionId.compareTo(o.feedbackQuestionId);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;

        result = prime * result + (courseId == null ? 0 : courseId.hashCode());

        result = prime * result + (feedbackSessionName == null ? 0 : feedbackSessionName.hashCode());

        result = prime * result + (giverType == null ? 0 : giverType.hashCode());

        result = prime * result + numberOfEntitiesToGiveFeedbackTo;

        result = prime * result + questionNumber;

        result = prime * result + (questionDetails == null ? 0 : questionDetails.hashCode());

        result = prime * result + (questionDescription == null ? 0 : questionDescription.hashCode());

        result = prime * result + (recipientType == null ? 0 : recipientType.hashCode());

        result = prime * result + (showGiverNameTo == null ? 0 : showGiverNameTo.hashCode());

        result = prime * result + (showRecipientNameTo == null ? 0 : showRecipientNameTo.hashCode());

        result = prime * result + (showResponsesTo == null ? 0 : showResponsesTo.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Globals globals = Globals.getInstance();
        List<Boolean> list = globals.getEqualList();
        list.set(49, true);

        if (this == obj) {
            list.set(0, true); 
            return true;
        }
        list.set(1, true);

        if (obj == null) {
            list.set(2, true); 
            return false;
        }
        list.set(3, true);

        if (getClass() != obj.getClass()) {
            list.set(4, true);
            return false;
        }
        list.set(5, true);

        FeedbackQuestionAttributes other = (FeedbackQuestionAttributes) obj;

        if (courseId == null) {
            list.set(6, true);
            if (other.courseId != null) { //TODO 1. CourseID: Null, other.courseID: Null set  2. CourseId: null, other.CourseID: != null
                return false;
            }
            list.set(7, true);
        } else if (!courseId.equals(other.courseId)) { //TODO dead code, 1. null
            list.set(8, true);
            return false;
        }
        list.set(9, true);

        if (feedbackSessionName == null) {
            list.set(10, true);
            if (other.feedbackSessionName != null) { //TODO feedbackSessionName: null, other: null
                list.set(11, true);
                return false;
            }
            list.set(12, true); //TODO feedback: null, other: != null
        } else if (!feedbackSessionName.equals(other.feedbackSessionName)) { //TODO feedbacksessionname=other.feedbacksessionname
            list.set(13, true);
            return false;
        } else {
            list.set(14, true);
        }

        if (giverType != other.giverType) { //TODO givertype != other.givertype
            list.set(15, true);
            return false;
        }
        list.set(16, true);
        if (numberOfEntitiesToGiveFeedbackTo != other.numberOfEntitiesToGiveFeedbackTo) { //TODO  numberOfEntitiesToGiveFeedbackTo != other.numberOfEntitiesToGiveFeedbackTo
            list.set(17, true);
            return false;
        }
        list.set(18, true);
        if (questionNumber != other.questionNumber) { //TODO questionNumber != other.questionNumber
            list.set(19, true);
            return false;
        }
        list.set(20, true);
        if (questionDetails == null) {
            list.set(21, true);
            if (other.questionDetails != null) {
                list.set(22, true);
                return false;
            }
            list.set(23, true);
        } else if (!questionDetails.equals(other.questionDetails)) {
            list.set(24, true);
            return false;
        } else {
            list.set(25, true);
        }

        if (questionDescription == null) {
            list.set(26, true);
            if (other.questionDescription != null) {
                list.set(27, true);
                return false;
            }
            list.set(28, true);
        } else if (!questionDescription.equals(other.questionDescription)) {
            list.set(29, true);
            return false;
        } else {
            list.set(30, true);
        }

        if (recipientType != other.recipientType) {
            list.set(31, true);
            return false;
        }
        list.set(32, true);
        if (showGiverNameTo == null) {
            list.set(33, true);
            if (other.showGiverNameTo != null) {
                list.set(34, true);
                return false;
            }
            list.set(35, true);
        } else if (!showGiverNameTo.equals(other.showGiverNameTo)) {
            list.set(36, true);
            return false;
        } else {
            list.set(37, true);
        }

        if (showRecipientNameTo == null) {
            list.set(38, true);
            if (other.showRecipientNameTo != null) {
                list.set(39, true);
                return false;
            }
            list.set(40, true);
        } else if (!showRecipientNameTo.equals(other.showRecipientNameTo)) {
            list.set(41, true);
            return false;
        } else {
            list.set(42, true);
        }

        if (showResponsesTo == null) {
            list.set(43, true);
            if (other.showResponsesTo != null) {
                list.set(44, true);
                return false;
            }
            list.set(45, true);
        } else if (!showResponsesTo.equals(other.showResponsesTo)) {
            list.set(46, true);
            return false;
        } else {
            list.set(47, true);
        }

        globals.setEqualList(list);
        return true;
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

    public void setQuestionDetails(FeedbackQuestionDetails newQuestionDetails) {
        this.questionDetails = newQuestionDetails.getDeepCopy();
    }

    public FeedbackQuestionDetails getQuestionDetails() {
        return questionDetails.getDeepCopy();
    }

    public String getSerializedQuestionDetails() {
        return questionDetails.getJsonString();
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

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionDetails.getQuestionType();
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

    public int getNumberOfEntitiesToGiveFeedbackTo() {
        return numberOfEntitiesToGiveFeedbackTo;
    }

    public void setNumberOfEntitiesToGiveFeedbackTo(int numberOfEntitiesToGiveFeedbackTo) {
        this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
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

    private static FeedbackQuestionDetails deserializeFeedbackQuestionDetails(String questionDetailsInJson,
                                                                              FeedbackQuestionType questionType) {
        if (questionType == FeedbackQuestionType.TEXT) {
            return deserializeFeedbackTextQuestionDetails(questionDetailsInJson);
        }
        return JsonUtils.fromJson(questionDetailsInJson, questionType.getQuestionDetailsClass());
    }

    private static FeedbackQuestionDetails deserializeFeedbackTextQuestionDetails(String questionDetailsInJson) {
        try {
            // There are `FeedbackTextQuestion` with plain text, Json without `recommendedLength`, and complete Json
            // in data store. Gson cannot parse the plain text case, so we need to handle it separately.
            return JsonUtils.fromJson(questionDetailsInJson, FeedbackQuestionType.TEXT.getQuestionDetailsClass());
        } catch (JsonParseException e) {
            return new FeedbackTextQuestionDetails(questionDetailsInJson);
        }
    }

    /**
     * Updates with {@link UpdateOptions}.
     */
    public void update(FeedbackQuestionAttributes.UpdateOptions updateOptions) {
        updateOptions.questionNumberOption.ifPresent(s -> questionNumber = s);
        updateOptions.questionDetailsOption.ifPresent(s -> questionDetails = s.getDeepCopy());
        updateOptions.questionDescriptionOption.ifPresent(s -> questionDescription = s);
        updateOptions.giverTypeOption.ifPresent(s -> giverType = s);
        updateOptions.recipientTypeOption.ifPresent(s -> recipientType = s);
        updateOptions.numberOfEntitiesToGiveFeedbackToOption.ifPresent(s -> numberOfEntitiesToGiveFeedbackTo = s);
        updateOptions.showResponsesToOption.ifPresent(s -> showResponsesTo = s);
        updateOptions.showGiverNameToOption.ifPresent(s -> showGiverNameTo = s);
        updateOptions.showRecipientNameToOption.ifPresent(s -> showRecipientNameTo = s);

        removeIrrelevantVisibilityOptions();
    }

    /**
     * Returns a {@link UpdateOptions.Builder}
     * to build {@link UpdateOptions} for a question.
     */
    public static FeedbackQuestionAttributes.UpdateOptions.Builder updateOptionsBuilder(String feedbackQuestionId) {
        return new FeedbackQuestionAttributes.UpdateOptions.Builder(feedbackQuestionId);
    }

    /**
     * A Builder class for {@link FeedbackQuestionAttributes}.
     */
    public static class Builder extends BasicBuilder<FeedbackQuestionAttributes, Builder> {
        private final FeedbackQuestionAttributes feedbackQuestionAttributes;

        private Builder() {
            super(new UpdateOptions(""));
            thisBuilder = this;

            feedbackQuestionAttributes = new FeedbackQuestionAttributes();
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            Assumption.assertNotNull(feedbackSessionName);

            feedbackQuestionAttributes.feedbackSessionName = feedbackSessionName;
            return this;
        }

        public Builder withCourseId(String courseId) {
            Assumption.assertNotNull(courseId);

            feedbackQuestionAttributes.courseId = courseId;
            return this;
        }

        @Override
        public FeedbackQuestionAttributes build() {
            feedbackQuestionAttributes.update(updateOptions);
            feedbackQuestionAttributes.removeIrrelevantVisibilityOptions();

            return feedbackQuestionAttributes;
        }
    }

    /**
     * Helper class to specific the fields to update in {@link FeedbackQuestionAttributes}.
     */
    public static class UpdateOptions {
        private String feedbackQuestionId;

        private UpdateOption<FeedbackQuestionDetails> questionDetailsOption = UpdateOption.empty();
        private UpdateOption<String> questionDescriptionOption = UpdateOption.empty();
        private UpdateOption<Integer> questionNumberOption = UpdateOption.empty();
        private UpdateOption<FeedbackParticipantType> giverTypeOption = UpdateOption.empty();
        private UpdateOption<FeedbackParticipantType> recipientTypeOption = UpdateOption.empty();
        private UpdateOption<Integer> numberOfEntitiesToGiveFeedbackToOption = UpdateOption.empty();
        private UpdateOption<List<FeedbackParticipantType>> showResponsesToOption = UpdateOption.empty();
        private UpdateOption<List<FeedbackParticipantType>> showGiverNameToOption = UpdateOption.empty();
        private UpdateOption<List<FeedbackParticipantType>> showRecipientNameToOption = UpdateOption.empty();

        private UpdateOptions(String feedbackQuestionId) {
            Assumption.assertNotNull(feedbackQuestionId);

            this.feedbackQuestionId = feedbackQuestionId;
        }

        public String getFeedbackQuestionId() {
            return feedbackQuestionId;
        }

        @Override
        public String toString() {
            return "FeedbackQuestionAttributes.UpdateOptions ["
                    + "feedbackQuestionId = " + feedbackQuestionId
                    + ", questionDetails = " + JsonUtils.toJson(questionDetailsOption)
                    + ", questionDescription = " + questionDescriptionOption
                    + ", questionNumber = " + questionNumberOption
                    + ", giverType = " + giverTypeOption
                    + ", recipientType = " + recipientTypeOption
                    + ", numberOfEntitiesToGiveFeedbackTo = " + numberOfEntitiesToGiveFeedbackToOption
                    + ", showResponsesTo = " + showResponsesToOption
                    + ", showGiverNameTo = " + showGiverNameToOption
                    + ", showRecipientNameTo = " + showRecipientNameToOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder> {

            private Builder(String feedbackQuestionId) {
                super(new UpdateOptions(feedbackQuestionId));
                thisBuilder = this;
            }

            @Override
            public UpdateOptions build() {
                return updateOptions;
            }

        }

    }

    /**
     * Basic builder to build {@link FeedbackQuestionAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>> {

        FeedbackQuestionAttributes.UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withQuestionDetails(FeedbackQuestionDetails questionDetails) {
            Assumption.assertNotNull(questionDetails);

            updateOptions.questionDetailsOption = UpdateOption.of(questionDetails.getDeepCopy());
            return thisBuilder;
        }

        public B withQuestionDescription(String questionDescription) {
            // questionDescription can be null

            updateOptions.questionDescriptionOption = UpdateOption.of(questionDescription);
            return thisBuilder;
        }

        public B withQuestionNumber(int questionNumber) {
            updateOptions.questionNumberOption = UpdateOption.of(questionNumber);
            return thisBuilder;
        }

        public B withGiverType(FeedbackParticipantType giverType) {
            Assumption.assertNotNull(giverType);

            updateOptions.giverTypeOption = UpdateOption.of(giverType);
            return thisBuilder;
        }

        public B withRecipientType(FeedbackParticipantType recipientType) {
            Assumption.assertNotNull(recipientType);

            updateOptions.recipientTypeOption = UpdateOption.of(recipientType);
            return thisBuilder;
        }

        public B withNumberOfEntitiesToGiveFeedbackTo(int numberOfEntitiesToGiveFeedbackTo) {
            updateOptions.numberOfEntitiesToGiveFeedbackToOption = UpdateOption.of(numberOfEntitiesToGiveFeedbackTo);
            return thisBuilder;
        }

        public B withShowResponsesTo(List<FeedbackParticipantType> showResponsesTo) {
            Assumption.assertNotNull(showResponsesTo);
            Assumption.assertNotNull((Object[]) showResponsesTo.toArray());

            updateOptions.showResponsesToOption = UpdateOption.of(new ArrayList<>(showResponsesTo));
            return thisBuilder;
        }

        public B withShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
            Assumption.assertNotNull(showGiverNameTo);
            Assumption.assertNotNull((Object[]) showGiverNameTo.toArray());

            updateOptions.showGiverNameToOption = UpdateOption.of(new ArrayList<>(showGiverNameTo));
            return thisBuilder;
        }

        public B withShowRecipientNameTo(List<FeedbackParticipantType> showRecipientNameTo) {
            Assumption.assertNotNull(showRecipientNameTo);
            Assumption.assertNotNull((Object[]) showRecipientNameTo.toArray());

            updateOptions.showRecipientNameToOption = UpdateOption.of(new ArrayList<>(showRecipientNameTo));
            return thisBuilder;
        }

        public abstract T build();

    }
}
