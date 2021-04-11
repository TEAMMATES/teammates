package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.storage.api.CoursesDb;

/**
 * Defines how we store {@link SolrInputDocument} for indexing/searching.
 */
abstract class SearchDocument {

    static final CoursesDb coursesDb = new CoursesDb();

    abstract SolrInputDocument toDocument();
}
