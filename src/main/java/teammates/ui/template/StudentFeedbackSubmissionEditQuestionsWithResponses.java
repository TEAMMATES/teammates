package teammates.ui.template;

import java.util.List;

public class StudentFeedbackSubmissionEditQuestionsWithResponses {
    private FeedbackSubmissionEditQuestion question;
    private List<FeedbackSubmissionEditResponse> responses;
    private int numOfResponseBoxes;
    private int maxResponsesPossible;

    public StudentFeedbackSubmissionEditQuestionsWithResponses(FeedbackSubmissionEditQuestion question,
                                    List<FeedbackSubmissionEditResponse> responses, int numOfResponseBoxes,
                                    int maxResponsesPossible) {
        this.question = question;
        this.responses = responses;
        this.numOfResponseBoxes = numOfResponseBoxes;
        this.maxResponsesPossible = maxResponsesPossible;
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
}
