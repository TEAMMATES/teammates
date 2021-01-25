package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.FeedbackResponseRecordAttributes;
import teammates.common.util.Assumption;
import teammates.storage.entity.FeedbackResponseRecord;

/**
 * Handles CRUD operations for feedback response records.
 *
 * @see FeedbackResponseRecord
 * @see FeedbackResponseRecordAttributes
 */
public class FeedbackResponseMonitorDb extends EntitiesDb<FeedbackResponseRecord, FeedbackResponseRecordAttributes> {

    @Override
    boolean hasExistingEntities(FeedbackResponseRecordAttributes entityToCreate) {
        return !load()
                .filterKey(Key.create(FeedbackResponseRecord.class,
                        entityToCreate.generateEntityKey()))
                .list()
                .isEmpty();
    }

    @Override
    LoadType<FeedbackResponseRecord> load() {
        return ofy().load().type(FeedbackResponseRecord.class);
    }

    @Override
    FeedbackResponseRecordAttributes makeAttributes(FeedbackResponseRecord entity) {
        Assumption.assertNotNull(entity);

        return FeedbackResponseRecordAttributes.valueOf(entity);
    }

    /**
     * Gets a set of response records with chosen duration and interval.
     */
    public List<FeedbackResponseRecordAttributes> getResponseRecords(long duration, long interval) {
        long currentTimeInSec = System.currentTimeMillis() / 1000;
        long startTimeInSec = currentTimeInSec - duration;

        List<Key<FeedbackResponseRecord>> keysOfRecords = load().keys().list();
        List<FeedbackResponseRecord> records = new ArrayList<>();

        long lastTimestamp = -1;
        for (Key<FeedbackResponseRecord> key : keysOfRecords) {
            String[] tokens = key.getName().split("-");
            long timestamp = Long.parseLong(tokens[0]);
            if (timestamp < startTimeInSec) {
                continue;
            }

            if (lastTimestamp == -1 || timestamp - lastTimestamp >= interval) {
                records.add(new FeedbackResponseRecord(key.getName()));
                lastTimestamp = timestamp;
            }
        }

        return makeAttributes(records);
    }

    /**
     * Purse old entries, for local development use.
     */
    public void purgeResponseRecords() {
        ofy().delete().entities(load().list()).now();
    }

}
