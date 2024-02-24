package teammates.storage.sqlsearch;

import java.util.Map;

import teammates.storage.sqlentity.BaseEntity;

/**
 * Defines how we store document for indexing/searching.
 *
 * @param <T> Type of entity to be converted into document
 */
abstract class SearchDocument<T extends BaseEntity> {

    final T entity;

    SearchDocument(T entity) {
        this.entity = entity;
    }

    abstract Map<String, Object> getSearchableFields();

}
