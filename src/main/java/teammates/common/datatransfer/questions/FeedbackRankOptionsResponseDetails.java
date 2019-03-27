package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

public class FeedbackRankOptionsResponseDetails extends FeedbackRankResponseDetails {
    private List<Integer> answers;

    public FeedbackRankOptionsResponseDetails() {
        super(FeedbackQuestionType.RANK_OPTIONS);
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
                                       FeedbackQuestionDetails questionDetails,
                                       String[] answer) {
        List<Integer> rankAnswer = new ArrayList<>();
        for (String answerPart : answer) {
            try {
                rankAnswer.add(Integer.parseInt(answerPart));
            } catch (NumberFormatException e) {
                rankAnswer.add(Const.POINTS_NOT_SUBMITTED);
            }
        }
        FeedbackRankOptionsQuestionDetails rankQuestion = (FeedbackRankOptionsQuestionDetails) questionDetails;
        this.setRankResponseDetails(rankAnswer, rankQuestion.options);
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

    public List<Integer> getAnswerList() {
        return new ArrayList<>(answers);
    }

    @Override
    public String getAnswerString() {
        String listString = getFilteredSortedAnswerList().toString(); //[1, 2, 3] format
        return listString.substring(1, listString.length() - 1); //remove []
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        FeedbackRankOptionsQuestionDetails rankQuestion = (FeedbackRankOptionsQuestionDetails) questionDetails;

        SortedMap<Integer, List<String>> orderedOptions = generateMapOfRanksToOptions(rankQuestion);

        StringBuilder csvBuilder = new StringBuilder();

        for (int rank = 1; rank <= rankQuestion.options.size(); rank++) {
            if (!orderedOptions.containsKey(rank)) {
                csvBuilder.append(',');
                continue;
            }
            List<String> optionsWithGivenRank = orderedOptions.get(rank);

            String optionsInCsv = SanitizationHelper.sanitizeForCsv(StringHelper.toString(optionsWithGivenRank, ", "));

            csvBuilder.append(optionsInCsv).append(',');
        }

        csvBuilder.deleteCharAt(csvBuilder.length() - 1); // remove last comma
        return csvBuilder.toString();
    }

    @Override
    public List<String> validateResponseDetails(FeedbackQuestionAttributes correspondingQuestion) {
        return new ArrayList<>();
    }

    private SortedMap<Integer, List<String>> generateMapOfRanksToOptions(
                                    FeedbackRankOptionsQuestionDetails rankQuestion) {
        SortedMap<Integer, List<String>> orderedOptions = new TreeMap<>();
        for (int i = 0; i < answers.size(); i++) {
            String option = rankQuestion.options.get(i);
            Integer answer = answers.get(i);
            orderedOptions.computeIfAbsent(answer, key -> new ArrayList<>())
                          .add(option);
        }
        return orderedOptions;
    }

    private void setRankResponseDetails(List<Integer> answers, List<String> options) {
        this.answers = answers;

        Assumption.assertEquals("Rank question: number of responses does not match number of options. "
                                        + answers.size() + "/" + options.size(),
                                answers.size(), options.size());

    }

}
