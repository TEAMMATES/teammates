package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import com.googlecode.objectify.cmd.Query;
import teammates.common.datatransfer.attributes.FeedbackSessionLogEntryAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.FeedbackSessionLogEntry;

/**
 * Handles CRUD operations for feedback session logs.
 *
 * @see FeedbackSessionLogEntry
 * @see FeedbackSessionLogEntryAttributes
 */
public final class FeedbackSessionLogEntriesDb extends EntitiesDb<FeedbackSessionLogEntry,
        FeedbackSessionLogEntryAttributes> {

    private static final FeedbackSessionLogEntriesDb instance = new FeedbackSessionLogEntriesDb();

    private FeedbackSessionLogEntriesDb() {
        // prevent initialization
    }

    public static FeedbackSessionLogEntriesDb inst() {
        return instance;
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters.
     */
    public List<FeedbackSessionLogEntryAttributes> getFeedbackSessionLogs(String courseId, String email,
                                                                          long startTime, long endTime, String fsName) {
        Query<FeedbackSessionLogEntry> query = load()
                .filter("courseId =", courseId)
                .filter("timestamp >=", startTime)
                .filter("timestamp <=", endTime);

        if (email != null) {
            query = query.filter("studentEmail =", email);
        }

        if (fsName != null) {
            query = query.filter("feedbackSessionName =", fsName);
        }

        List<FeedbackSessionLogEntry> entries = query.list();

        return makeAttributes(entries);
    }

    /**
     * Gets all feedback session logs.
     */
    public List<FeedbackSessionLogEntryAttributes> getAllFeedbackSessionLogs() {
        List<FeedbackSessionLogEntry> entries = load().list();

        return makeAttributes(entries);
    }

    /**
     * Gets latest timestamp for feedback session logs.
     */
    public long getLatestLogTimestamp() {
        List<FeedbackSessionLogEntry> entries =
                load().order("-timestamp").limit(1).list();

        if (entries.isEmpty()) {
            return 0;
        }

        return entries.get(0).getTimestamp();
    }

    /**
     * Creates feedback session logs.
     */
    public List<FeedbackSessionLogEntryAttributes> createFeedbackSessionLogs(
            List<FeedbackSessionLogEntryAttributes> entries) throws InvalidParametersException {
        assert entries != null;

        return putEntities(entries);
    }

    @Override
    boolean hasExistingEntities(FeedbackSessionLogEntryAttributes entityToCreate) {
        return !load()
                .filterKey(Key.create(FeedbackSessionLogEntry.class, entityToCreate.getFeedbackSessionLogEntryId()))
                .keys()
                .list()
                .isEmpty();
    }

    @Override
    LoadType<FeedbackSessionLogEntry> load() {
        return ofy().load().type(FeedbackSessionLogEntry.class);
    }

    @Override
    FeedbackSessionLogEntryAttributes makeAttributes(FeedbackSessionLogEntry entity) {
        assert entity != null;

        return FeedbackSessionLogEntryAttributes.valueOf(entity);
    }
}
