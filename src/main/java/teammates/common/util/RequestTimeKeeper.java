package teammates.common.util;

import java.util.Date;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DeadlineExceededException;

/**
 * This class gives methods time to react to GAE's DeadlineExceededException by throwing it
 * a specified amount of time before the Google App Engine default.
 */
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

    /**
     * * Checks if enough time is remaining and throws a DeadlineExceededException if there is not.
     * @throws DeadlineExceededException
     */
    public void confirmEnoughTimeLeft() throws DeadlineExceededException {
        if (!hasEnoughTime()) {
            throw new DeadlineExceededException();
        }
    }
    
    public boolean hasEnoughTime() {
        return deadlineExpiryTime - getCurrentTimeInMillis() > remainingMillisThreshold;
    }
}
