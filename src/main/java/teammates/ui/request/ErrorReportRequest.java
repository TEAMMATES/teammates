package teammates.ui.request;

/**
 * The create request for an error report to be emailed to the admin.
 */
public class ErrorReportRequest extends BasicRequest {
    private String content;
    private String subject;
    private String requestId;

    public ErrorReportRequest(String requestId, String subject, String content) {
        this.requestId = requestId;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(content != null, "content cannot be null");
        assertTrue(subject != null, "subject cannot be null");
        assertTrue(requestId != null, "requestId cannot be null");
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
