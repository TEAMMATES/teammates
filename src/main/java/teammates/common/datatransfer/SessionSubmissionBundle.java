package teammates.common.datatransfer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseRecipient;

/**
 * Contains all submission data for a feedback session.
 */
public class SessionSubmissionBundle {

    private final List<QuestionSubmissionBundle> questionSubmissionBundles;

    public SessionSubmissionBundle(List<QuestionSubmissionBundle> questionSubmissionBundles) {
        this.questionSubmissionBundles = questionSubmissionBundles;
    }

    public List<QuestionSubmissionBundle> getQuestionSubmissionBundles() {
        return questionSubmissionBundles;
    }

    /**
     * Contains submission data for a feedback question.
     */
    public static class QuestionSubmissionBundle {

        private final FeedbackQuestion question;
        private final Optional<List<String>> dynamicallyGeneratedOptions;
        private final Set<ResponseRecipient> recipients;
        private final List<FeedbackResponse> responses;

        public QuestionSubmissionBundle(FeedbackQuestion question, Optional<List<String>> dynamicallyGeneratedOptions,
                Set<ResponseRecipient> recipients, List<FeedbackResponse> responses) {
            this.question = question;
            this.dynamicallyGeneratedOptions = dynamicallyGeneratedOptions;
            this.recipients = recipients;
            this.responses = responses;
        }

        public FeedbackQuestion getQuestion() {
            return question;
        }

        public Optional<List<String>> getDynamicallyGeneratedOptions() {
            return dynamicallyGeneratedOptions;
        }

        public Set<ResponseRecipient> getRecipients() {
            return recipients;
        }

        public List<FeedbackResponse> getResponses() {
            return responses;
        }
    }
}
