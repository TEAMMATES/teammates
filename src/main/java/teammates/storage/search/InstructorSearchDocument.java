package teammates.storage.search;

import java.util.Comparator;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

/**
 * The {@link SearchDocument} object that defines how we store {@link SolrInputDocument} for instructors.
 */
class InstructorSearchDocument extends SearchDocument {

    private final InstructorAttributes instructor;

    InstructorSearchDocument(InstructorAttributes instructor) {
        this.instructor = instructor;
    }

    @Override
    SolrInputDocument toDocument() {
        SolrInputDocument document = new SolrInputDocument();

        CourseAttributes course = coursesDb.getCourse(instructor.courseId);

        document.addField("id", StringHelper.encrypt(instructor.key));
        document.addField("name", instructor.getName());
        document.addField("email", instructor.getEmail());
        document.addField("courseId", instructor.getCourseId());
        document.addField("courseName", course == null ? "" : course.getName());
        document.addField("googleId", instructor.getGoogleId());
        document.addField("role", instructor.getRole());
        document.addField("displayedName", instructor.getDisplayedName());

        return document;
    }

    /**
     * Produces an {@link InstructorSearchResultBundle} from the {@code QueryResponse} collection.
     *
     * <p>This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID.</p>
     */
    static InstructorSearchResultBundle fromResponse(QueryResponse response) {
        if (response == null) {
            return new InstructorSearchResultBundle();
        }

        InstructorSearchResultBundle bundle = constructBaseBundle(response.getResults());
        sortInstructorResultList(bundle.instructorList);

        return bundle;
    }

    private static InstructorSearchResultBundle constructBaseBundle(List<SolrDocument> results) {
        InstructorSearchResultBundle bundle = new InstructorSearchResultBundle();

        for (SolrDocument document : results) {
            String instructorId = (String) document.getFirstValue("id");
            InstructorAttributes instructor = instructorsDb.getInstructorForRegistrationKey(instructorId);
            if (instructor == null) {
                // search engine out of sync as SearchManager may fail to delete documents
                // the chance is low and it is generally not a big problem
                instructorsDb.deleteDocumentByEncryptedInstructorKey(instructorId);
                continue;
            }

            bundle.instructorList.add(instructor);
        }

        bundle.numberOfResults = bundle.instructorList.size();
        return bundle;
    }

    private static void sortInstructorResultList(List<InstructorAttributes> instructorList) {
        instructorList.sort(Comparator.comparing((InstructorAttributes instructor) -> instructor.courseId)
                //TODO TO REMOVE AFTER DATA MIGRATION - needed to work with code before the sanitizing was removed
                .thenComparing(instructor -> SanitizationHelper.desanitizeIfHtmlSanitized(instructor.role))
                .thenComparing(instructor -> SanitizationHelper.desanitizeIfHtmlSanitized(instructor.name))
                .thenComparing(instructor -> instructor.email));
    }
}
