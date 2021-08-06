package teammates.common.datatransfer.logs;

/**
 * Contains structure and processing logic for other types of logs (e.g. unrecognized types).
 */
public class DefaultLogDetails extends LogDetails {

    public DefaultLogDetails() {
        super(LogEvent.DEFAULT_LOG);
    }

    @Override
    public void hideSensitiveInformation() {
        setMessage(null);
    }

}
