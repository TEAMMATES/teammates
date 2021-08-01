package teammates.common.datatransfer.logs;

/**
 * Contains specific structure and processing logic for exception log.
 */
public class ExceptionLogDetails extends LogDetails {

    public ExceptionLogDetails() {
        super(LogEvent.EXCEPTION_LOG);
    }

    @Override
    public void hideSensitiveInformation() {
        // TODO
    }

}
