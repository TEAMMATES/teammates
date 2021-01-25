package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackResponseRecordAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackResponseMonitorDb;

/**
 * Handles operations related to feedback response recording.
 *
 * @see FeedbackResponseRecordAttributes
 * @see FeedbackResponseMonitorDb
 */
public class FeedbackResponseRecordLogic {

    private static FeedbackResponseRecordLogic instance = new FeedbackResponseRecordLogic();

    private static final FeedbackResponseMonitorDb frmDb = new FeedbackResponseMonitorDb();

    public static FeedbackResponseRecordLogic inst() {
        return instance;
    }

    public void createFeedbackResponseRecord(int count, int timestamp)
            throws InvalidParametersException, EntityAlreadyExistsException {
        frmDb.createEntity(new FeedbackResponseRecordAttributes(count, timestamp));
    }

    public List<FeedbackResponseRecordAttributes> getFeedbackResponseRecords(long duration, long interval) {
        return frmDb.getResponseRecords(duration, interval);
    }

    public void purgeFeedbackResponseRecord() {
        frmDb.purgeResponseRecords();
    }

}
