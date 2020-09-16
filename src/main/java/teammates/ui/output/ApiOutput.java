package teammates.ui.output;

import javax.annotation.Nullable;

/**
 * Generic output format for all API requests.
 */
public class ApiOutput {

    @Nullable
    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
