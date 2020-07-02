package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;

public class FeedbackRankOptionsResponseDetails extends FeedbackRankResponseDetails {
    private List<Integer> answers;

    public FeedbackRankOptionsResponseDetails() {
        super(FeedbackQuestionType.RANK_OPTIONS);
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

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        List<String> errors = new ArrayList<>();
        FeedbackRankQuestionDetails rankQuestionDetails = (FeedbackRankQuestionDetails) correspondingQuestion
                .getQuestionDetails();
        boolean areDuplicatesAllowed = rankQuestionDetails.areDuplicatesAllowed();
        int minOptionsToBeRanked = rankQuestionDetails.minOptionsToBeRanked;
        int maxOptionsToBeRanked = rankQuestionDetails.maxOptionsToBeRanked;
        List<String> options = ((FeedbackRankOptionsQuestionDetails) correspondingQuestion
                .getQuestionDetails()).getOptions();

        boolean isMinOptionsEnabled = minOptionsToBeRanked != Integer.MIN_VALUE;
        boolean isMaxOptionsEnabled = maxOptionsToBeRanked != Integer.MIN_VALUE;

        List<Integer> filteredAnswers = getFilteredSortedAnswerList();
        Set<Integer> set = new HashSet<>(filteredAnswers);
        boolean isAnswerContainsDuplicates = set.size() < filteredAnswers.size();

        // if duplicate ranks are not allowed but have been assigned trigger this error
        if (isAnswerContainsDuplicates && !areDuplicatesAllowed) {
            errors.add("Duplicate Ranks are not allowed.");
        }
        // if number of options ranked is less than the minimum required trigger this error
        if (isMinOptionsEnabled && filteredAnswers.size() < minOptionsToBeRanked) {
            errors.add("You must rank at least " + minOptionsToBeRanked + " options.");
        }
        // if number of options ranked is more than the maximum possible trigger this error
        if (isMaxOptionsEnabled && filteredAnswers.size() > maxOptionsToBeRanked) {
            errors.add("You can rank at most " + maxOptionsToBeRanked + " options.");
        }
        // if rank assigned is invalid trigger this error
        boolean isRankInvalid = filteredAnswers.stream().anyMatch(answer -> answer < 1 || answer > options.size());
        if (isRankInvalid) {
            errors.add("Invalid rank assigned.");
        }
        return errors;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }
}
