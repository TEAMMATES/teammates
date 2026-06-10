package teammates.ui.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The create request for an error report to be emailed to the admin.
 */
public class ErrorReportRequest extends BasicRequest {
    private String content;
    private String subject;
    private String requestId;

    @JsonCreator
    public ErrorReportRequest(String requestId, String subject, String content) {
        this.requestId = requestId;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(content != null, "content cannot be null");
        validateTrue(subject != null, "subject cannot be null");
        validateTrue(requestId != null, "requestId cannot be null");
    }

    public String getContent() {
        return content;
    }

    public String getSubject() {
        return subject;
    }

    public String getRequestId() {
        return requestId;
    }
}
