package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackContributionResponseDetails extends FeedbackResponseDetails {

    /**
     * This is the claimed points from giver to recipient.
     */
    private int answer;

    public FeedbackContributionResponseDetails() {
        super(FeedbackQuestionType.CONTRIB);
        answer = Const.POINTS_NOT_SUBMITTED;
    }

    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        List<String> errors = new ArrayList<>();
        boolean validAnswer = false;

        // Valid answers: 0, 10, 20, .... 190, 200
        boolean isValidRange = answer >= 0 && answer <= 200;
        boolean isMultipleOf10 = answer % 10 == 0;
        boolean isNotSureAllowed = ((FeedbackContributionQuestionDetails) correspondingQuestion
                .getQuestionDetails()).isNotSureAllowed();

        if (isValidRange && isMultipleOf10) {
            validAnswer = true;
        }
        if (answer == Const.POINTS_NOT_SURE && isNotSureAllowed || answer == Const.POINTS_NOT_SUBMITTED) {
            validAnswer = true;
        }
        if (!validAnswer) {
            errors.add(Const.FeedbackQuestion.CONTRIB_ERROR_INVALID_OPTION);
        }
        return errors;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
