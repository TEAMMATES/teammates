package teammates.common.datatransfer.statistics;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.questions.FeedbackQuestionType;

/**
 * Recipient-specific contribution statistics for session results.
 */
public class FeedbackContributionRecipientStatistics extends FeedbackQuestionRecipientResultsStatistics {
    private RecipientView myView = new RecipientView();
    private RecipientView teamView = new RecipientView();

    public FeedbackContributionRecipientStatistics() {
        super(FeedbackQuestionType.CONTRIB);
    }

    public RecipientView getMyView() {
        return myView;
    }

    public void setMyView(RecipientView myView) {
        this.myView = myView;
    }

    public RecipientView getTeamView() {
        return teamView;
    }

    public void setTeamView(RecipientView teamView) {
        this.teamView = teamView;
    }

    /**
     * A view of contribution statistics for a single recipient,
     * either the recipient themselves or the team as a whole.
     */
    public static class RecipientView {
        private int ofMe;
        private List<Integer> ofOthers = new ArrayList<>();

        public int getOfMe() {
            return ofMe;
        }

        public void setOfMe(int ofMe) {
            this.ofMe = ofMe;
        }

        public List<Integer> getOfOthers() {
            return ofOthers;
        }

        public void setOfOthers(List<Integer> ofOthers) {
            this.ofOthers = ofOthers;
        }
    }
}
