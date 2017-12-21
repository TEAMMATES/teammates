package teammates.storage.search;

import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

/**
 * The {@link SearchDocument} object that defines how we store {@link Document} for instructors.
 */
public class InstructorSearchDocument extends SearchDocument {

    private InstructorAttributes instructor;
    private CourseAttributes course;

    public InstructorSearchDocument(InstructorAttributes instructor) {
        this.instructor = instructor;
    }

    @Override
    protected void prepareData() {
        if (instructor == null) {
            return;
        }

        course = coursesDb.getCourse(instructor.courseId);
    }

    @Override
    protected Document toDocument() {

        String delim = ",";

        // produce searchableText for this instructor document:
        // contains courseId, courseName, instructorName, instructorEmail, instructorGoogleId, instructorRole, displayedName
        String searchableText = instructor.courseId + delim
                                + (course == null ? "" : course.getName()) + delim
                                + instructor.name + delim
                                + instructor.email + delim
                                + (instructor.googleId == null ? "" : instructor.googleId) + delim
                                + instructor.role + delim
                                + instructor.displayedName;

        return Document.newBuilder()
                // searchableText is used to match the query string
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT)
                                            .setText(searchableText))
                // attribute field is used to convert a doc back to attribute
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.INSTRUCTOR_ATTRIBUTE)
                                            .setText(JsonUtils.toJson(instructor)))
                .setId(StringHelper.encrypt(instructor.key))
                .build();
    }

    /**
     * Produces an {@link InstructorSearchResultBundle} from the {@code Results<ScoredDocument>} collection.
     *
     * <p>This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID.
     */
    public static InstructorSearchResultBundle fromResults(Results<ScoredDocument> results) {
        InstructorSearchResultBundle bundle = new InstructorSearchResultBundle();
        if (results == null) {
            return bundle;
        }

        for (ScoredDocument doc : results) {
            InstructorAttributes instructor = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.INSTRUCTOR_ATTRIBUTE).getText(),
                    InstructorAttributes.class);

            if (instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(instructor.key)) == null) {
                instructorsDb.deleteDocument(instructor);
                continue;
            }

            bundle.instructorList.add(instructor);
            bundle.numberOfResults++;
        }

        sortInstructorResultList(bundle.instructorList);

        return bundle;
    }

    private static void sortInstructorResultList(List<InstructorAttributes> instructorList) {

        instructorList.sort(Comparator.comparing((InstructorAttributes instructor) -> instructor.courseId)
                .thenComparing(instructor -> instructor.role)
                .thenComparing(instructor -> instructor.name)
                .thenComparing(instructor -> instructor.email));

    }

}
