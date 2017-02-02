package teammates.common.util;

import java.util.Date;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DeadlineExceededException;

public class RequestTimeKeeper {
    private static final long REMAINING_TIME_THRESHOLD = 5000;
    private static final long TIME_BETWEEN_CHECKS = 5000;
    private Date lastCalledDateForEnoughTimeMethod = new Date();
    
    public void hasEnoughTimeThrowException() throws DeadlineExceededException {
        Date now = new Date();
        if (lastCalledDateForEnoughTimeMethod != null
                && now.getTime() - lastCalledDateForEnoughTimeMethod.getTime() <= TIME_BETWEEN_CHECKS) {
            return;
        }
        if (!hasEnoughTime()) {
            throw new DeadlineExceededException();
        }
    }
    
    public boolean hasEnoughTime() {
        return ApiProxy.getCurrentEnvironment().getRemainingMillis() > REMAINING_TIME_THRESHOLD;
    }
}
