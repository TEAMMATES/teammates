package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

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
    public static List<FeedbackResponseAttributes.UpdateOptions> makeConsistencyUpdateForOneQuestionFromOneGiver(
            List<FeedbackResponseAttributes> responses, int maxRank) {
        List<FeedbackResponseAttributes.UpdateOptions> updateOptions = new ArrayList<>();

        if (maxRank <= 0) {
            return updateOptions;
        }

        FeedbackResponseDetails details;
        boolean[] isRankUsed = new boolean[maxRank];
        boolean isUpdateNeeded = false;

        int answer;
        for (FeedbackResponseAttributes response : responses) {
            details = response.getResponseDetails();
            if (details instanceof FeedbackRankRecipientsResponseDetails) {
                FeedbackRankRecipientsResponseDetails responseDetails = (FeedbackRankRecipientsResponseDetails) details;
                answer = responseDetails.getAnswer();
                if (answer > maxRank) {
                    isUpdateNeeded = true;
                } else {
                    isRankUsed[answer - 1] = true;
                }
            }
        }

        if (!isUpdateNeeded) {
            return updateOptions;
        }

        int maxUnusedRank = 0;
        FeedbackResponseAttributes.UpdateOptions updateOption;

        for (int i = maxRank - 1; i >= 0; i--) {
            if (!isRankUsed[i]) {
                maxUnusedRank = i + 1;
                break;
            }
        }

        assert maxUnusedRank > 0;

        for (FeedbackResponseAttributes response : responses) {
            details = response.getResponseDetails();
            if (details instanceof FeedbackRankRecipientsResponseDetails) {
                FeedbackRankRecipientsResponseDetails responseDetails = (FeedbackRankRecipientsResponseDetails) details;
                answer = responseDetails.getAnswer();
                if (answer > maxUnusedRank) {
                    responseDetails.setAnswer(Math.min(answer - 1, maxRank));
                    updateOption = FeedbackResponseAttributes.updateOptionsBuilder(response.getId())
                            .withFeedbackResponseDetail(responseDetails)
                            .build();
                    updateOptions.add(updateOption);
                }
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
