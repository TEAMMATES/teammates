package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.storage.api.CoursesDb;

/**
 * Defines how we store {@link SolrInputDocument} for indexing/searching.
 */
public abstract class SearchDocument {

    static final CoursesDb coursesDb = new CoursesDb();

    /**
     * Builds the search document.
     */
    public SolrInputDocument build() {
        prepareData();
        return toDocument();
    }

    abstract void prepareData();

    abstract SolrInputDocument toDocument();
}
