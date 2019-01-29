package teammates.ui.webapi.action;

import java.util.Map;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.JsonUtils;
import teammates.ui.webapi.request.BasicRequest;

/**
 * Data transfer objects for {@link teammates.common.datatransfer.attributes.FeedbackResponseAttributes}
 * between controller and HTTP.
 */
public class FeedbackResponseInfo {

    /**
     * The basic HTTP body request of a feedback response.
     */
    private static class FeedbackResponseBasicRequest extends BasicRequest {

        private String recipientIdentifier;

        private FeedbackQuestionType questionType;

        private Map<String, Object> responseDetails;

        public String getRecipientIdentifier() {
            return recipientIdentifier;
        }

        public FeedbackQuestionType getQuestionType() {
            return questionType;
        }

        public FeedbackResponseDetails getResponseDetails() {
            FeedbackResponseDetails details =
                    JsonUtils.fromJson(JsonUtils.toJson(responseDetails), questionType.getResponseDetailsClass());
            details.setQuestionType(questionType);
            return details;
        }

        @Override
        public void validate() {
            assertTrue(recipientIdentifier != null, "recipientIdentifier cannot be null");
            assertTrue(questionType != null, "questionType cannot be null");
            assertTrue(responseDetails != null, "responseDetails cannot be null");
        }
    }

    /**
     * The create request of a feedback response.
     */
    public static class FeedbackResponseCreateRequest extends FeedbackResponseBasicRequest {

    }

    /**
     * The save request of a feedback response.
     */
    public static class FeedbackResponseSaveRequest extends FeedbackResponseBasicRequest {

    }
}
