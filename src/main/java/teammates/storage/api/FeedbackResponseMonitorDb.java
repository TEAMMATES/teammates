package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

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
}
