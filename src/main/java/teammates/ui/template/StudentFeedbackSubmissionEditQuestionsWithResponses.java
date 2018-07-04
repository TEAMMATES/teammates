package teammates.ui.template;

import java.util.List;

public class StudentFeedbackSubmissionEditQuestionsWithResponses {
    private FeedbackSubmissionEditQuestion question;
    private List<FeedbackSubmissionEditResponse> responses;
    private int numOfResponseBoxes;
    private int maxResponsesPossible;
    private boolean feedbackParticipantCommentsOnResponsesAllowed;

    public StudentFeedbackSubmissionEditQuestionsWithResponses(FeedbackSubmissionEditQuestion question,
            List<FeedbackSubmissionEditResponse> responses, int numOfResponseBoxes, int maxResponsesPossible,
            boolean feedbackParticipantCommentsOnResponsesAllowed) {
        this.question = question;
        this.responses = responses;
        this.numOfResponseBoxes = numOfResponseBoxes;
        this.maxResponsesPossible = maxResponsesPossible;
        this.feedbackParticipantCommentsOnResponsesAllowed = feedbackParticipantCommentsOnResponsesAllowed;
    }

    public FeedbackSubmissionEditQuestion getQuestion() {
        return question;
    }

    public List<FeedbackSubmissionEditResponse> getResponses() {
        return responses;
    }

    public int getNumOfResponseBoxes() {
        return numOfResponseBoxes;
    }

    public int getMaxResponsesPossible() {
        return maxResponsesPossible;
    }

    public boolean getFeedbackParticipantCommentsOnResponsesAllowed() {
        return feedbackParticipantCommentsOnResponsesAllowed;
    }
}
