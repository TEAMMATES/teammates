package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Const;

/**
 * Contains specific structure and processing logic for rank recipients feedback responses.
 */
public class FeedbackRankRecipientsResponseDetails extends FeedbackResponseDetails {
    private int answer;

    public FeedbackRankRecipientsResponseDetails() {
        super(FeedbackQuestionType.RANK_RECIPIENTS);
        answer = Const.POINTS_NOT_SUBMITTED;
    }

    /**
     * Provides updates of responses for 'rank recipient question', such that the ranks in the responses are consistent.
     * @param responses responses to one feedback question, from one giver
     * @param maxRank the maximum rank in each response
     * @return a list of {@code UpdateOptions} that contains the updates for the responses, if any
     */
    public static List<FeedbackResponseAttributes.UpdateOptions> getUpdateOptionsForRankRecipientQuestions(
            List<FeedbackResponseAttributes> responses, int maxRank) {
        List<FeedbackResponseAttributes.UpdateOptions> updateOptions = new ArrayList<>();

        if (maxRank <= 0) {
            return updateOptions;
        }

        FeedbackResponseDetails details;
        FeedbackRankRecipientsResponseDetails responseDetails;
        boolean[] isRankUsed;
        Set<FeedbackResponseAttributes> updatedResponses = new HashSet<>();
        boolean isUpdateNeeded = false;
        int answer;
        int maxUnusedRank = 0;

        // Checks whether update is needed.
        for (FeedbackResponseAttributes response : responses) {
            details = response.getResponseDetails();
            if (!(details instanceof FeedbackRankRecipientsResponseDetails)) {
                continue;
            }
            responseDetails = (FeedbackRankRecipientsResponseDetails) details;
            answer = responseDetails.getAnswer();
            if (answer > maxRank) {
                isUpdateNeeded = true;
                break;
            }
        }

        // Updates repeatedly, until all responses are consistent.
        while (isUpdateNeeded) {
            isUpdateNeeded = false; // will be set to true again once invalid rank appears after update
            isRankUsed = new boolean[maxRank];

            // Obtains the largest unused rank.
            for (FeedbackResponseAttributes response : responses) {
                details = response.getResponseDetails();
                if (!(details instanceof FeedbackRankRecipientsResponseDetails)) {
                    continue;
                }
                responseDetails = (FeedbackRankRecipientsResponseDetails) details;
                answer = responseDetails.getAnswer();
                if (answer <= maxRank) {
                    isRankUsed[answer - 1] = true;
                }
            }
            for (int i = maxRank - 1; i >= 0; i--) {
                if (!isRankUsed[i]) {
                    maxUnusedRank = i + 1;
                    break;
                }
            }
            assert maxUnusedRank > 0; // if update is needed, there must be at least one unused rank

            for (FeedbackResponseAttributes response : responses) {
                details = response.getResponseDetails();
                if (details instanceof FeedbackRankRecipientsResponseDetails) {
                    responseDetails = (FeedbackRankRecipientsResponseDetails) details;
                    answer = responseDetails.getAnswer();
                    if (answer > maxUnusedRank) {
                        answer--;
                        responseDetails.setAnswer(answer);
                        updatedResponses.add(response);
                    }
                    if (answer > maxRank) {
                        isUpdateNeeded = true; // sets the flag to true if the updated rank is still invalid
                    }
                }
            }

            // Adds the updated responses to the result list.
            FeedbackResponseAttributes.UpdateOptions updateOption;
            for (FeedbackResponseAttributes response : updatedResponses) {
                updateOption = FeedbackResponseAttributes.updateOptionsBuilder(response.getId())
                        .withFeedbackResponseDetails(response.getResponseDetails())
                        .build();
                updateOptions.add(updateOption);
            }
        }
        return updateOptions;
    }

    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
