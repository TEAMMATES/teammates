package teammates.storage.search;

import java.util.Map;

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.storage.api.CoursesDb;

/**
 * Defines how we store document for indexing/searching.
 *
 * @param <T> type of entity to be converted into document
 */
abstract class SearchDocument<T extends EntityAttributes<?>> {

    static final CoursesDb coursesDb = new CoursesDb();
    final T attribute;

    SearchDocument(T attribute) {
        this.attribute = attribute;
    }

    abstract Map<String, Object> getSearchableFields();

}
