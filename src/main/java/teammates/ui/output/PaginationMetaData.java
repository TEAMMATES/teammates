package teammates.ui.output;

/**
 * Pagination metadata.
 */
public class PaginationMetaData extends ApiOutput {
    private final int noResponses;
    private final String questionId;

    public PaginationMetaData(int noResponses, String questionId) {
        this.noResponses = noResponses;
        this.questionId = questionId;
    }

    public int getNoResponses() {
        return noResponses;
    }

    public String getQuestionId() {
        return questionId;
    }
}
