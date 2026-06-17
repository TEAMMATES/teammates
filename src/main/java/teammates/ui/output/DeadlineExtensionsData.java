package teammates.ui.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import teammates.common.datatransfer.ExtensionUpdateType;
import teammates.common.datatransfer.UpdateExtensionsResult;
import teammates.storage.entity.DeadlineExtension;

/**
 * The API output format for deadline extensions.
 */
public class DeadlineExtensionsData implements ApiOutput {
    private Map<UUID, Long> userDeadlines;

    private DeadlineExtensionsData() {
        // for Jackson deserialization
    }

    public DeadlineExtensionsData(Set<DeadlineExtension> deadlineExtensions) {
        this.userDeadlines = new HashMap<>();
        for (DeadlineExtension extension : deadlineExtensions) {
            userDeadlines.put(extension.getUserId(), extension.getEndTime().toEpochMilli());
        }
    }

    public DeadlineExtensionsData(List<UpdateExtensionsResult> updateResults) {
        this.userDeadlines = new HashMap<>();
        for (UpdateExtensionsResult updateResult : updateResults) {
            if (updateResult.updateType() == ExtensionUpdateType.DELETED) {
                continue;
            }
            userDeadlines.put(updateResult.userId(), updateResult.newEndTime().toEpochMilli());
        }
    }

    public Map<UUID, Long> getUserDeadlines() {
        return userDeadlines;
    }

    public void setUserDeadlines(Map<UUID, Long> userDeadlines) {
        this.userDeadlines = userDeadlines;
    }
}
