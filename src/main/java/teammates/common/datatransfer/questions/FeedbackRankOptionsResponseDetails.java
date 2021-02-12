package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;

public class FeedbackRankOptionsResponseDetails extends FeedbackResponseDetails {
    private List<Integer> answers;

    public FeedbackRankOptionsResponseDetails() {
        super(FeedbackQuestionType.RANK_OPTIONS);
        answers = new ArrayList<>();
    }

    /**
     * Returns List of sorted answers, with uninitialised values filtered out.
     */
    public List<Integer> getFilteredSortedAnswerList() {
        List<Integer> filteredAnswers = new ArrayList<>();

        for (int answer : answers) {
            if (answer != Const.POINTS_NOT_SUBMITTED) {
                filteredAnswers.add(answer);
            }
        }

        filteredAnswers.sort(null);
        return filteredAnswers;
    }

    @Override
    public String getAnswerString() {
        String listString = getFilteredSortedAnswerList().toString(); //[1, 2, 3] format
        return listString.substring(1, listString.length() - 1); //remove []
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }
}
