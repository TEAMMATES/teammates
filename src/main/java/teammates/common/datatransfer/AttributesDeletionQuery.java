package teammates.common.datatransfer;

import teammates.common.util.Assumption;

/**
 * The query for attributes deletion.
 */
public class AttributesDeletionQuery {

    private String courseId;
    private String feedbackSessionName;
    private String questionId;
    private String responseId;

    private AttributesDeletionQuery() {
        // use builder to construct query
    }

    public boolean isCourseIdPresent() {
        return courseId != null;
    }

    public boolean isFeedbackSessionNamePresent() {
        return feedbackSessionName != null;
    }

    public boolean isQuestionIdPresent() {
        return questionId != null;
    }

    public boolean isResponseIdPresent() {
        return responseId != null;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getResponseId() {
        return responseId;
    }

    /**
     * Returns a builder for {@link AttributesDeletionQuery}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link AttributesDeletionQuery}.
     */
    public static class Builder {

        private static final String INVALID_COMBINATION = "Invalid combination";

        private AttributesDeletionQuery attributesDeletionQuery;

        private Builder() {
            attributesDeletionQuery = new AttributesDeletionQuery();
        }

        public Builder withCourseId(String courseId) {
            Assumption.assertNotNull(courseId);
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isQuestionIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isResponseIdPresent());

            attributesDeletionQuery.courseId = courseId;
            return this;
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            Assumption.assertNotNull(feedbackSessionName);
            Assumption.assertTrue("session name must come together with course ID",
                    attributesDeletionQuery.isCourseIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isQuestionIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isResponseIdPresent());

            attributesDeletionQuery.feedbackSessionName = feedbackSessionName;
            return this;
        }

        public Builder withQuestionId(String questionId) {
            Assumption.assertNotNull(questionId);
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isCourseIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isFeedbackSessionNamePresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isResponseIdPresent());

            attributesDeletionQuery.questionId = questionId;
            return this;
        }

        public Builder withResponseId(String responseId) {
            Assumption.assertNotNull(responseId);
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isCourseIdPresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isFeedbackSessionNamePresent());
            Assumption.assertFalse(INVALID_COMBINATION, attributesDeletionQuery.isQuestionIdPresent());

            attributesDeletionQuery.responseId = responseId;
            return this;
        }

        public AttributesDeletionQuery build() {
            Assumption.assertTrue(INVALID_COMBINATION,
                    attributesDeletionQuery.isCourseIdPresent()
                            || attributesDeletionQuery.isFeedbackSessionNamePresent()
                            || attributesDeletionQuery.isQuestionIdPresent()
                            || attributesDeletionQuery.isResponseIdPresent());

            return attributesDeletionQuery;
        }
    }
}
