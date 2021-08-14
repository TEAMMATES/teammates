package teammates.client.scripts;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import com.googlecode.objectify.cmd.Query;

import teammates.client.util.BackDoor;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.storage.entity.Course;

/**
 * Script to populate search documents into the system back-end.
 */
public class PopulateCourseSearchDocuments extends DataMigrationEntitiesBaseScript<Course> {

    private static final int STUDENT_SIZE_LIMIT = 300;
    private final Logic logic = Logic.inst();

    public PopulateCourseSearchDocuments() {
        numberOfScannedKey.set(0L);
        numberOfAffectedEntities.set(0L);
        numberOfUpdatedEntities.set(0L);
    }

    public static void main(String[] args) {
        PopulateCourseSearchDocuments populator = new PopulateCourseSearchDocuments();
        populator.doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        Instant createdAtUpperBound = Instant.now();
        Instant createdAtLowerBound = TimeHelper.parseInstant("2020-12-31T16:00:00.00Z");
        // To change the boundary of the createdAt timestamp, uncomment the next line and insert the appropriate timestamp.
        // createdAtUpperBound = TimeHelper.parseInstant("2021-06-30T16:00:00.00Z");
        // createdAtLowerBound = TimeHelper.parseInstant("2020-12-31T16:00:00.00Z");
        Query<Course> query = ofy().load().type(Course.class)
                .filter("createdAt <=", createdAtUpperBound);
        if (createdAtLowerBound != null) {
            query = query.filter("createdAt >=", createdAtLowerBound);
        }
        return query.order("-createdAt");
    }

    @Override
    protected boolean isPreview() {
        return false;
    }

    @Override
    protected boolean isMigrationNeeded(Course course) {
        return true;
    }

    @Override
    protected void migrateEntity(Course course) throws Exception {
        List<StudentAttributes> students = logic.getStudentsForCourse(course.getUniqueId());
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(course.getUniqueId());

        int nLoop = students.size() / STUDENT_SIZE_LIMIT;

        System.out.println("---------");
        System.out.println("Going to populate search documents for students and instructors from course "
                + course.getUniqueId());
        System.out.println("Course is created at epoch " + course.getCreatedAt().toEpochMilli());
        System.out.println();

        for (int i = 0; i <= nLoop + 1; i++) {
            DataBundle bundle = new DataBundle();
            bundle.students = new HashMap<>();
            bundle.instructors = new HashMap<>();
            if (i == nLoop + 1) {
                // For final loop, migrate instructors
                if (instructors.isEmpty()) {
                    System.out.println("No instructors to migrate");
                    System.out.println();
                    continue;
                }
                instructors.forEach(instructor -> bundle.instructors.put(instructor.getEmail(), instructor));
            } else {
                List<StudentAttributes> studentsSubList =
                        students.subList(i * STUDENT_SIZE_LIMIT,
                                Math.min(students.size(), (i + 1) * STUDENT_SIZE_LIMIT));
                if (studentsSubList.isEmpty()) {
                    System.out.println("No students to migrate");
                    System.out.println();
                    continue;
                }
                studentsSubList.forEach(student -> bundle.students.put(student.getEmail(), student));
            }

            long time = System.currentTimeMillis();
            System.out.println("Total load: " + bundle.students.size() + " students, "
                    + bundle.instructors.size() + " instructors");
            String result = BackDoor.getInstance().putDocuments(bundle);
            System.out.println("Operation result: " + result);
            System.out.println("Time elapsed: " + (System.currentTimeMillis() - time) + "ms");
            System.out.println();
        }

        System.out.println("Search document insertion completed");
        System.out.println("---------");
    }

}
