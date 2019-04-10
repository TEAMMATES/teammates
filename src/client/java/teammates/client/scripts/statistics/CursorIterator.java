package teammates.client.scripts.statistics;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

/**
 * An iterator that iterates all the matched entities for a Datastore query with batch fetching enabled.
 *
 * @param <T> the type of entity to iterate
 */
public class CursorIterator<T> implements Iterator<T> {

    // cannot set number greater than 300
    // see https://stackoverflow.com/questions/41499505/objectify-queries-setting-limit-above-300-does-not-work
    private static final int BUFFER_SIZE = 300;

    private Cursor cursor;

    private Query<T> query;

    private Queue<T> buffer;

    CursorIterator(Query<T> query) {
        this.query = query;
        this.buffer = new LinkedList<>();
    }

    /**
     * Returns an {@link Iterable} which iterates all the matched entities of the {@code query}.
     */
    public static <T> Iterable<T> iterate(Query<T> query) {
        return () -> new CursorIterator<>(query);
    }

    /**
     * Fetches entities in batches and puts them into the buffer.
     */
    private void batchFetching() {
        Query<T> newQuery = this.query.limit(BUFFER_SIZE);
        if (this.cursor != null) {
            newQuery = newQuery.startAt(this.cursor);
        }
        QueryResultIterator<T> iterator = newQuery.iterator();

        boolean shouldContinue = false;
        while (iterator.hasNext()) {
            shouldContinue = true;
            this.buffer.offer(iterator.next());
        }

        if (shouldContinue) {
            this.cursor = iterator.getCursor();
        }
    }

    @Override
    public boolean hasNext() {
        if (this.buffer.isEmpty()) {
            batchFetching();
        }
        return !this.buffer.isEmpty();
    }

    @Override
    public T next() {
        return buffer.poll();
    }
}
