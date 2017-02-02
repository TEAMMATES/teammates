package teammates.common.util;

import java.util.Date;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DeadlineExceededException;

public class RequestTimeKeeper {
    private final long remainingMillisThreshold;
    private long deadlineExpiryTime;

    public RequestTimeKeeper(long remainingMillisThreshold) {
        this.remainingMillisThreshold = remainingMillisThreshold;
        deadlineExpiryTime = getCurrentTimeInMillis() + ApiProxy.getCurrentEnvironment().getRemainingMillis();
    }

    private long getCurrentTimeInMillis() {
        return new Date().getTime();
    }

    public void confirmEnoughTimeLeft() throws DeadlineExceededException {
        if (!hasEnoughTime()) {
            throw new DeadlineExceededException();
        }
    }
    
    public boolean hasEnoughTime() {
        return deadlineExpiryTime - getCurrentTimeInMillis() > remainingMillisThreshold;
    }
}
