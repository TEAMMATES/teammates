package teammates.storage.search;

import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * The {@link SearchDocument} object that defines how we store {@link Document} for students.
 */
public class StudentSearchDocument extends SearchDocument {

    private StudentAttributes student;
    private CourseAttributes course;

    public StudentSearchDocument(StudentAttributes student) {
        this.student = student;
    }

    @Override
    void prepareData() {
        if (student == null) {
            return;
        }

        course = coursesDb.getCourse(student.course);
    }

    @Override
    Document toDocument() {

        String delim = ",";

        // produce searchableText for this student document:
        // it contains courseId, courseName, studentEmail, studentName studentTeam and studentSection
        String searchableText = student.course + delim
                                + (course == null ? "" : course.getName()) + delim
                                + student.email + delim
                                + student.name + delim
                                + student.team + delim
                                + student.section;

        return Document.newBuilder()
                // this is used to filter documents visible to certain instructor
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.COURSE_ID)
                                            .setText(student.course))
                // searchableText and createdDate are used to match the query string
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT)
                                            .setText(searchableText))
                .setId(student.key)
                .build();
    }

    /**
     * Produces a {@link StudentSearchResultBundle} from the {@code Results<ScoredDocument>} collection.
     *
     * <p>The list of {@link InstructorAttributes} is used to filter out the search result.</p>
     *
     * <p>This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID.</p>
     */
    public static StudentSearchResultBundle fromResults(Results<ScoredDocument> results) {
        if (results == null) {
            return new StudentSearchResultBundle();
        }

        StudentSearchResultBundle bundle = constructBaseBundle(results);

        sortStudentResultList(bundle.studentList);

        return bundle;
    }

    /**
     * Produces a {@link StudentSearchResultBundle} from the {@code Results<ScoredDocument>} collection.
     *
     * <p>The list of {@link InstructorAttributes} is used to filter out the search result.</p>
     */
    public static StudentSearchResultBundle fromResults(Results<ScoredDocument> results,
                                                        List<InstructorAttributes> instructors) {
        if (results == null) {
            return new StudentSearchResultBundle();
        }

        List<ScoredDocument> filteredResults = filterOutCourseId(results, instructors);
        StudentSearchResultBundle bundle = constructBaseBundle(filteredResults);

        for (InstructorAttributes ins : instructors) {
            bundle.courseIdInstructorMap.put(ins.courseId, ins);
        }

        sortStudentResultList(bundle.studentList);

        return bundle;
    }

    private static StudentSearchResultBundle constructBaseBundle(Iterable<ScoredDocument> results) {
        StudentSearchResultBundle bundle = new StudentSearchResultBundle();

        for (ScoredDocument doc : results) {
            StudentAttributes student = studentsDb.getStudentForRegistrationKey(StringHelper.encrypt(doc.getId()));
            if (student == null) {
                // search engine out of sync as SearchManager may fail to delete documents due to GAE error
                // the chance is low and it is generally not a big problem
                studentsDb.deleteDocumentByStudentKey(doc.getId());
                continue;
            }

            bundle.studentList.add(student);
            bundle.numberOfResults++;
        }

        return bundle;
    }

    private static void sortStudentResultList(List<StudentAttributes> studentList) {

        studentList.sort(Comparator.comparing((StudentAttributes student) -> student.course)
                .thenComparing(student -> student.section)
                .thenComparing(student -> student.team)
                .thenComparing(student -> student.name)
                .thenComparing(student -> student.email));
    }

}
