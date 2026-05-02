package teammates.common.datatransfer.logs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Contains specific structure and processing logic for instance log.
 */
@JsonPropertyOrder({ "event", "instanceId", "instanceEvent" })
public class InstanceLogDetails extends LogDetails {

    private String instanceId;
    private String instanceEvent;

    public InstanceLogDetails() {
        super(LogEvent.INSTANCE_LOG);
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceEvent() {
        return instanceEvent;
    }

    public void setInstanceEvent(String instanceEvent) {
        this.instanceEvent = instanceEvent;
    }

    @Override
    public void hideSensitiveInformation() {
        // no fields need to be hidden
    }

}
