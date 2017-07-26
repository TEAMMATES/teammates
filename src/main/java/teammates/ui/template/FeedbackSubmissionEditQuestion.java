package teammates.ui.template;

import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;

public class FeedbackSubmissionEditQuestion {
    private String courseId;
    private int questionNumber;
    private int qnIndx; // If not showing real question number
    private String questionId;
    private String questionText;
    private String questionDescription;
    private String messageToDisplayIfNoRecipientAvailable;
    private List<String> visibilityMessages;
    private FeedbackQuestionType questionType;
    private int numberOfEntitiesToGiveFeedbackTo;
    private boolean isModeratedQuestion;
    private boolean isRecipientNameHidden;
    private boolean isGiverTeam;
    private boolean isRecipientTeam;

    public FeedbackSubmissionEditQuestion(FeedbackQuestionAttributes questionAttributes, int qnIndx,
                                    boolean isModeratedQuestion) {

        courseId = questionAttributes.courseId;
        questionNumber = questionAttributes.questionNumber;
        this.qnIndx = qnIndx;
        questionId = questionAttributes.getId();
        questionText = questionAttributes.getQuestionDetails().getQuestionText();
        Text description = questionAttributes.getQuestionDescription();
        questionDescription = description == null ? null : description.getValue();
        visibilityMessages = questionAttributes.getVisibilityMessage();
        questionType = questionAttributes.questionType;
        numberOfEntitiesToGiveFeedbackTo = questionAttributes.numberOfEntitiesToGiveFeedbackTo;
        this.isModeratedQuestion = isModeratedQuestion;
        isRecipientNameHidden = questionAttributes.isRecipientNameHidden();
        isGiverTeam = questionAttributes.giverType.equals(FeedbackParticipantType.TEAMS);
        isRecipientTeam = questionAttributes.recipientType.isTeam();

        setMessageToDisplayIfNoRecipientAvailable(questionAttributes);

    }

    private void setMessageToDisplayIfNoRecipientAvailable(FeedbackQuestionAttributes questionAttributes) {
        messageToDisplayIfNoRecipientAvailable = "";
        if (questionAttributes.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
            messageToDisplayIfNoRecipientAvailable = "This question is for team members and you don't have any team members."
                                                     + " Therefore, you will not be able to answer this question.";
        } else if (questionAttributes.recipientType == FeedbackParticipantType.TEAMS) {
            messageToDisplayIfNoRecipientAvailable = "This question is for other teams in this course and this course "
                                                     + "doesn't have any other team. Therefore, you will not be able to "
                                                     + "answer this question.";
        } else if (questionAttributes.recipientType == FeedbackParticipantType.STUDENTS) {
            messageToDisplayIfNoRecipientAvailable = "This question is for other students in this course and this course "
                                                     + "doesn't have any other student. Therefore, you will not be able to "
                                                     + "answer this question.";
        }
    }

    public String getCourseId() {
        return courseId;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getMessageToDisplayIfNoRecipientAvailable() {
        return messageToDisplayIfNoRecipientAvailable;
    }

    public int getQnIndx() {
        return qnIndx;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public List<String> getVisibilityMessages() {
        return visibilityMessages;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public boolean isQuestionTypeConstsum() {
        return questionType == FeedbackQuestionType.CONSTSUM;
    }

    public int getNumberOfEntitiesToGiveFeedbackTo() {
        return numberOfEntitiesToGiveFeedbackTo;
    }

    public boolean isModeratedQuestion() {
        return isModeratedQuestion;
    }

    public boolean isRecipientNameHidden() {
        return isRecipientNameHidden;
    }

    public boolean isGiverTeam() {
        return isGiverTeam;
    }

    public boolean isRecipientTeam() {
        return isRecipientTeam;
    }
}
