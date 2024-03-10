package teammates.common.datatransfer;

/**
 * The query for attributes deletion.
 */
public final class AttributesDeletionQuery {

    private String courseId;
    private String feedbackSessionName;
    private String questionId;
    private String responseId;
    private String userEmail;
    private Boolean isInstructor;

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

    public boolean isUserEmailPresent() {
        return userEmail != null;
    }

    public boolean isIsInstructorPresent() {
        return isInstructor != null;
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

    public String getUserEmail() {
        return userEmail;
    }

    public Boolean getIsInstructor() {
        return isInstructor;
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
    public static final class Builder {

        private static final String INVALID_COMBINATION = "Invalid combination";

        private AttributesDeletionQuery attributesDeletionQuery;

        private Builder() {
            attributesDeletionQuery = new AttributesDeletionQuery();
        }

        public Builder withCourseId(String courseId) {
            assert courseId != null;
            assert !attributesDeletionQuery.isQuestionIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isResponseIdPresent() : INVALID_COMBINATION;

            attributesDeletionQuery.courseId = courseId;
            return this;
        }

        public Builder withFeedbackSessionName(String feedbackSessionName) {
            assert feedbackSessionName != null;
            assert attributesDeletionQuery.isCourseIdPresent() : "Session name must come together with course ID";
            assert !attributesDeletionQuery.isQuestionIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isResponseIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isUserEmailPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isIsInstructorPresent() : INVALID_COMBINATION;

            attributesDeletionQuery.feedbackSessionName = feedbackSessionName;
            return this;
        }

        public Builder withQuestionId(String questionId) {
            assert questionId != null;
            assert !attributesDeletionQuery.isCourseIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isFeedbackSessionNamePresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isResponseIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isUserEmailPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isIsInstructorPresent() : INVALID_COMBINATION;

            attributesDeletionQuery.questionId = questionId;
            return this;
        }

        public Builder withResponseId(String responseId) {
            assert responseId != null;
            assert !attributesDeletionQuery.isCourseIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isFeedbackSessionNamePresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isQuestionIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isUserEmailPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isIsInstructorPresent() : INVALID_COMBINATION;

            attributesDeletionQuery.responseId = responseId;
            return this;
        }

        public Builder withUserEmail(String userEmail) {
            assert userEmail != null;
            assert !attributesDeletionQuery.isFeedbackSessionNamePresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isResponseIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isQuestionIdPresent() : INVALID_COMBINATION;

            attributesDeletionQuery.userEmail = userEmail;
            return this;
        }

        public Builder withIsInstructor(boolean isInstructor) {
            assert !attributesDeletionQuery.isFeedbackSessionNamePresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isResponseIdPresent() : INVALID_COMBINATION;
            assert !attributesDeletionQuery.isQuestionIdPresent() : INVALID_COMBINATION;

            attributesDeletionQuery.isInstructor = isInstructor;
            return this;
        }

        public AttributesDeletionQuery build() {
            assert attributesDeletionQuery.isCourseIdPresent()
                    || attributesDeletionQuery.isFeedbackSessionNamePresent()
                    || attributesDeletionQuery.isQuestionIdPresent()
                    || attributesDeletionQuery.isResponseIdPresent()
                    || attributesDeletionQuery.isUserEmailPresent()
                    || attributesDeletionQuery.isIsInstructorPresent()
                    : INVALID_COMBINATION;

            return attributesDeletionQuery;
        }
    }
}
