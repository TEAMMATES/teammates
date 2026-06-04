package teammates.ui.output;

import java.util.List;

/**
 * API output format for feedback session submission data.
 */
public class SessionSubmissionData implements ApiOutput {

    private final List<SessionSubmissionQuestionData> questions;

    public SessionSubmissionData(List<SessionSubmissionQuestionData> questions) {
        this.questions = questions;
    }

    public List<SessionSubmissionQuestionData> getQuestions() {
        return questions;
    }

    /**
     * API output format for a feedback question with submission recipients and responses.
     */
    public static class SessionSubmissionQuestionData implements ApiOutput {

        private final FeedbackQuestionData question;
        private final List<FeedbackQuestionRecipientData> recipients;
        private final List<FeedbackResponseData> responses;

        public SessionSubmissionQuestionData(FeedbackQuestionData question,
                List<FeedbackQuestionRecipientData> recipients, List<FeedbackResponseData> responses) {
            this.question = question;
            this.recipients = recipients;
            this.responses = responses;
        }

        public FeedbackQuestionData getQuestion() {
            return question;
        }

        public List<FeedbackQuestionRecipientData> getRecipients() {
            return recipients;
        }

        public List<FeedbackResponseData> getResponses() {
            return responses;
        }
    }
}
