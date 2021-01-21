package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Set<FeedbackResponseRecord> getResponseRecords(long duration, long interval) {
        long currentTimeInSec = System.currentTimeMillis() / 1000;
        long startTimeInSec = currentTimeInSec - duration;
        List<Key<FeedbackResponseRecord>> keysOfRecords = load().keys().list();
        Set<FeedbackResponseRecord> records = new HashSet<>();
        long currentTimestamp = -1;
        for (Key<FeedbackResponseRecord> key : keysOfRecords) {
            String[] tokens = key.getName().split("-");
            long timestamp = Long.parseLong(tokens[0]);
            if (timestamp >= startTimeInSec) {
                if (currentTimestamp == -1 || timestamp - currentTimestamp == interval) {
                    records.add(new FeedbackResponseRecord(key.getName()));
                }
                currentTimestamp = timestamp;
            }
        }
        return records;
    }
}
