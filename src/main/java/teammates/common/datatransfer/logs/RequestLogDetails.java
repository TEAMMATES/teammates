package teammates.common.datatransfer.logs;

/**
 * Contains specific structure and processing logic for HTTP request log.
 */
public class RequestLogDetails extends LogDetails {

    public RequestLogDetails() {
        super(LogEvent.REQUEST_LOG);
    }

    @Override
    public void hideSensitiveInformation() {
        // TODO
    }

}
