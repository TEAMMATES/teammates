package teammates.ui.webapi.action;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Data transfer objects for {@link FeedbackQuestionAttributes} between controller and HTTP.
 */
public class FeedbackQuestionInfo {

    /**
     * The setting of number of entities to giver feedback to.
     */
    public enum NumberOfEntitiesToGiveFeedbackToSetting {
        /**
         * Custom number of entities to give feedback to.
         */
        CUSTOM,

        /**
         * Unlimited number of entities to give feedback to.
         */
        UNLIMITED,
    }

    /**
     * The feedback visibility type.
     */
    public enum FeedbackVisibilityType {
        /**
         * General recipient.
         */
        RECIPIENT,

        /**
         * Giver's team member.
         */
        GIVER_TEAM_MEMBERS,

        /**
         * Recipient's team members.
         */
        RECIPIENT_TEAM_MEMBERS,

        /**
         * Students in the course.
         */
        STUDENTS,

        /**
         * Instructors in the course.
         */
        INSTRUCTORS,
    }

    /**
     * The feedback question response.
     */
    public static class FeedbackQuestionResponse extends ApiOutput {
        private final String feedbackQuestionId;
        private int questionNumber;
        private final String questionBrief;
        private final String questionDescription;

        private final FeedbackQuestionDetails questionDetails;

        private final FeedbackQuestionType questionType;
        private final FeedbackParticipantType giverType;
        private final FeedbackParticipantType recipientType;

        private final NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting;
        private final Integer customNumberOfEntitiesToGiveFeedbackTo;

        private List<FeedbackVisibilityType> showResponsesTo;
        private List<FeedbackVisibilityType> showGiverNameTo;
        private List<FeedbackVisibilityType> showRecipientNameTo;

        public FeedbackQuestionResponse(FeedbackQuestionAttributes feedbackQuestionAttributes) {
            FeedbackQuestionDetails feedbackQuestionDetails = feedbackQuestionAttributes.getQuestionDetails();

            this.feedbackQuestionId = feedbackQuestionAttributes.getFeedbackQuestionId();
            this.questionNumber = feedbackQuestionAttributes.getQuestionNumber();
            this.questionBrief = feedbackQuestionDetails.getQuestionText();
            this.questionDescription = feedbackQuestionAttributes.getQuestionDescription();

            this.questionDetails = feedbackQuestionDetails;

            this.questionType = feedbackQuestionAttributes.getQuestionType();
            this.giverType = feedbackQuestionAttributes.getGiverType();
            this.recipientType = feedbackQuestionAttributes.getRecipientType();

            if (feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo() == Const.MAX_POSSIBLE_RECIPIENTS) {
                this.numberOfEntitiesToGiveFeedbackToSetting = NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED;
                this.customNumberOfEntitiesToGiveFeedbackTo = null;
            } else {
                this.numberOfEntitiesToGiveFeedbackToSetting = NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM;
                this.customNumberOfEntitiesToGiveFeedbackTo =
                        feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo();
            }

            // the visibility types are mixed in feedback participant type
            // therefore, we convert them to visibility types
            this.showResponsesTo = convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowResponsesTo());
            this.showGiverNameTo = convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowGiverNameTo());
            this.showRecipientNameTo =
                    convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowRecipientNameTo());

            // specially handling for contribution questions
            // TODO: remove the hack
            if (this.questionType == FeedbackQuestionType.CONTRIB
                    && this.giverType == FeedbackParticipantType.STUDENTS
                    && this.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF
                    && this.showResponsesTo.contains(FeedbackVisibilityType.GIVER_TEAM_MEMBERS)) {
                // remove the redundant visibility type as GIVER_TEAM_MEMBERS is just RECIPIENT_TEAM_MEMBERS
                // contribution question keep the redundancy for legacy reason
                this.showResponsesTo.remove(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
            }
        }

        /**
         * Converts a list of feedback participant type to a list of visibility type.
         */
        private List<FeedbackVisibilityType> convertToFeedbackVisibilityType(
                List<FeedbackParticipantType> feedbackParticipantTypeList) {
            return feedbackParticipantTypeList.stream().map(feedbackParticipantType -> {
                switch (feedbackParticipantType) {
                case STUDENTS:
                    return FeedbackVisibilityType.STUDENTS;
                case INSTRUCTORS:
                    return FeedbackVisibilityType.INSTRUCTORS;
                case RECEIVER:
                    return FeedbackVisibilityType.RECIPIENT;
                case OWN_TEAM_MEMBERS:
                    return FeedbackVisibilityType.GIVER_TEAM_MEMBERS;
                case RECEIVER_TEAM_MEMBERS:
                    return FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS;
                default:
                    Assumption.fail("Unknown feedbackParticipantType" + feedbackParticipantType);
                    break;
                }
                return null;
            }).collect(Collectors.toList());
        }

        public String getFeedbackQuestionId() {
            return feedbackQuestionId;
        }

        public int getQuestionNumber() {
            return questionNumber;
        }

        public void setQuestionNumber(int questionNumber) {
            this.questionNumber = questionNumber;
        }

        public String getQuestionBrief() {
            return questionBrief;
        }

        public String getQuestionDescription() {
            return questionDescription;
        }

        public FeedbackQuestionDetails getQuestionDetails() {
            return questionDetails;
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

        public NumberOfEntitiesToGiveFeedbackToSetting getNumberOfEntitiesToGiveFeedbackToSetting() {
            return numberOfEntitiesToGiveFeedbackToSetting;
        }

        public Integer getCustomNumberOfEntitiesToGiveFeedbackTo() {
            return customNumberOfEntitiesToGiveFeedbackTo;
        }

        public List<FeedbackVisibilityType> getShowResponsesTo() {
            return showResponsesTo;
        }

        public List<FeedbackVisibilityType> getShowGiverNameTo() {
            return showGiverNameTo;
        }

        public List<FeedbackVisibilityType> getShowRecipientNameTo() {
            return showRecipientNameTo;
        }
    }

    /**
     * The feedback questions response.
     */
    public static class FeedbackQuestionsResponse extends ApiOutput {
        private List<FeedbackQuestionResponse> questions;

        public FeedbackQuestionsResponse(List<FeedbackQuestionAttributes> questionAttributesList) {
            questions = questionAttributesList.stream().map(FeedbackQuestionResponse::new).collect(Collectors.toList());
        }

        public List<FeedbackQuestionResponse> getQuestions() {
            return questions;
        }

        /**
         * Normalizes question number in questions by setting question number in sequence (i.e. 1, 2, 3, 4 ...).
         */
        public void normalizeQuestionNumber() {
            for (int i = 1; i <= questions.size(); i++) {
                questions.get(i - 1).setQuestionNumber(i);
            }
        }
    }

    /**
     * The basic request body format for creating/saving of feedback question.
     */
    private static class FeedbackQuestionBasicRequest extends Action.RequestBody {
        private int questionNumber;
        private String questionBrief;
        private String questionDescription;

        private Map<String, Object> questionDetails;

        private FeedbackQuestionType questionType;
        private FeedbackParticipantType giverType;
        private FeedbackParticipantType recipientType;

        private NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting;
        private Integer customNumberOfEntitiesToGiveFeedbackTo;

        private List<FeedbackVisibilityType> showResponsesTo;
        private List<FeedbackVisibilityType> showGiverNameTo;
        private List<FeedbackVisibilityType> showRecipientNameTo;

        @Override
        public void validate() {
            assertTrue(questionNumber >= 1, "Invalid question number");
            assertTrue(questionBrief != null, "Question brief cannot be null");
            assertTrue(!questionBrief.isEmpty(), "Question brief cannot be empty");
            assertTrue(questionDetails != null, "Question details cannot be null");

            assertTrue(questionType != null, "Question type cannot be null");
            assertTrue(giverType != null, "Giver type cannot be null");
            assertTrue(recipientType != null, "Recipient type cannot be null");

            assertTrue(numberOfEntitiesToGiveFeedbackToSetting != null,
                    "numberOfEntitiesToGiveFeedbackToSetting cannot be null");
            if (numberOfEntitiesToGiveFeedbackToSetting == NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM) {
                assertTrue(customNumberOfEntitiesToGiveFeedbackTo != null,
                        "customNumberOfEntitiesToGiveFeedbackTo must be set");
            }

            assertTrue(showResponsesTo != null, "showResponsesTo cannot be null");
            assertTrue(showGiverNameTo != null, "showGiverNameTo cannot be null");
            assertTrue(showRecipientNameTo != null, "showRecipientNameTo cannot be null");
        }

        public int getQuestionNumber() {
            return questionNumber;
        }

        public String getQuestionDescription() {
            return questionDescription;
        }

        public FeedbackQuestionDetails getQuestionDetails() {
            FeedbackQuestionDetails details =
                    JsonUtils.fromJson(JsonUtils.toJson(questionDetails), questionType.getQuestionDetailsClass());
            details.setQuestionText(questionBrief);
            return details;
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
            switch (numberOfEntitiesToGiveFeedbackToSetting) {
            case CUSTOM:
                return customNumberOfEntitiesToGiveFeedbackTo;
            case UNLIMITED:
                return Const.MAX_POSSIBLE_RECIPIENTS;
            default:
                Assumption.fail("Unknown numberOfEntitiesToGiveFeedbackToSetting"
                        + numberOfEntitiesToGiveFeedbackToSetting);
                break;
            }
            return 0;
        }

        public List<FeedbackParticipantType> getShowResponsesTo() {
            List<FeedbackParticipantType> showResponsesTo =
                    this.convertToFeedbackParticipantType(this.showResponsesTo);

            // specially handling for contribution questions
            // TODO: remove the hack
            if (this.questionType == FeedbackQuestionType.CONTRIB
                    && this.giverType == FeedbackParticipantType.STUDENTS
                    && this.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF
                    && showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
                // add the redundant participant type OWN_TEAM_MEMBERS even if it is just RECIPIENT_TEAM_MEMBERS
                // contribution question keep the redundancy for legacy reason
                showResponsesTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
            }

            return showResponsesTo;
        }

        public List<FeedbackParticipantType> getShowGiverNameTo() {
            return this.convertToFeedbackParticipantType(showGiverNameTo);
        }

        public List<FeedbackParticipantType> getShowRecipientNameTo() {
            return this.convertToFeedbackParticipantType(showRecipientNameTo);
        }

        /**
         * Converts a list of feedback visibility type to a list of feedback participant type.
         */
        private List<FeedbackParticipantType> convertToFeedbackParticipantType(
                List<FeedbackVisibilityType> feedbackVisibilityTypes) {
            return feedbackVisibilityTypes.stream().map(feedbackVisibilityType -> {
                switch (feedbackVisibilityType) {
                case STUDENTS:
                    return FeedbackParticipantType.STUDENTS;
                case INSTRUCTORS:
                    return FeedbackParticipantType.INSTRUCTORS;
                case RECIPIENT:
                    return FeedbackParticipantType.RECEIVER;
                case GIVER_TEAM_MEMBERS:
                    return FeedbackParticipantType.OWN_TEAM_MEMBERS;
                case RECIPIENT_TEAM_MEMBERS:
                    return FeedbackParticipantType.RECEIVER_TEAM_MEMBERS;
                default:
                    Assumption.fail("Unknown feedbackVisibilityType" + feedbackVisibilityType);
                    break;
                }
                return null;
            }).collect(Collectors.toList());
        }

        public void setQuestionNumber(int questionNumber) {
            this.questionNumber = questionNumber;
        }

        public void setQuestionBrief(String questionBrief) {
            this.questionBrief = questionBrief;
        }

        public void setQuestionDescription(String questionDescription) {
            this.questionDescription = questionDescription;
        }

        public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
            this.questionDetails = JsonUtils.fromJson(JsonUtils.toJson(questionDetails), Map.class);
        }

        public void setQuestionType(FeedbackQuestionType questionType) {
            this.questionType = questionType;
        }

        public void setGiverType(FeedbackParticipantType giverType) {
            this.giverType = giverType;
        }

        public void setRecipientType(FeedbackParticipantType recipientType) {
            this.recipientType = recipientType;
        }

        public void setNumberOfEntitiesToGiveFeedbackToSetting(
                NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting) {
            this.numberOfEntitiesToGiveFeedbackToSetting = numberOfEntitiesToGiveFeedbackToSetting;
        }

        public void setCustomNumberOfEntitiesToGiveFeedbackTo(Integer customNumberOfEntitiesToGiveFeedbackTo) {
            this.customNumberOfEntitiesToGiveFeedbackTo = customNumberOfEntitiesToGiveFeedbackTo;
        }

        public void setShowResponsesTo(List<FeedbackVisibilityType> showResponsesTo) {
            this.showResponsesTo = showResponsesTo;
        }

        public void setShowGiverNameTo(List<FeedbackVisibilityType> showGiverNameTo) {
            this.showGiverNameTo = showGiverNameTo;
        }

        public void setShowRecipientNameTo(List<FeedbackVisibilityType> showRecipientNameTo) {
            this.showRecipientNameTo = showRecipientNameTo;
        }
    }

    /**
     * The request body format for saving of feedback question.
     */
    public static class FeedbackQuestionSaveRequest extends FeedbackQuestionBasicRequest {

    }

    /**
     * The request body format for creating of feedback question.
     */
    public static class FeedbackQuestionCreateRequest extends FeedbackQuestionBasicRequest {

    }
}
