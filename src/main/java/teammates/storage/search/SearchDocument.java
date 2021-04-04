package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

/**
 * Defines how we store {@link SolrInputDocument} for indexing/searching.
 */
abstract class SearchDocument {

    static final CoursesDb coursesDb = new CoursesDb();
    static final InstructorsDb instructorsDb = new InstructorsDb();
    static final StudentsDb studentsDb = new StudentsDb();

    abstract SolrInputDocument toDocument();
}
