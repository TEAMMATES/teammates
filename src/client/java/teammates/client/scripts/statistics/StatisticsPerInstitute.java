package teammates.client.scripts.statistics;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.LoopHelper;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.Instructor;

/**
 * Script that calculates the number of unique students and instructors per institute.
 */
public class StatisticsPerInstitute extends DatastoreClient {

    private final StatisticsBundle bundle;
    private final Map<String, String> courseToInstituteCache = new HashMap<>();

    StatisticsPerInstitute() throws Exception {
        bundle = FileStore.getStatisticsBundleFromFileIfPossible();
    }

    public static void main(String[] args) throws Exception {
        StatisticsPerInstitute statistics = new StatisticsPerInstitute();
        statistics.doOperationRemotely();
    }

    private String getCourseInstitute(String courseId) {
        Course course = ofy().load().type(Course.class).id(courseId).now();
        return course == null || course.getInstitute() == null ? Const.UNKNOWN_INSTITUTION : course.getInstitute();
    }

    @Override
    protected void doOperation() {
        Instant now = Instant.now();
        Instant queryEntitiesFrom = bundle.getStatsSince();

        // generate stats incrementally
        while (queryEntitiesFrom.isBefore(now)) {
            Instant queryEntitiesTo = queryEntitiesFrom.plus(Duration.ofDays(30));
            if (queryEntitiesTo.isAfter(now)) {
                queryEntitiesTo = now;
            }
            printQueryRangeMessage(queryEntitiesFrom, queryEntitiesTo);

            // construct queries
            Query<CourseStudent> studentQuery =
                    ofy().load().type(CourseStudent.class)
                            .filter("createdAt >", queryEntitiesFrom)
                            .filter("createdAt <=", queryEntitiesTo);
            Query<Instructor> instructorQuery =
                    ofy().load().type(Instructor.class)
                            .filter("createdAt >", queryEntitiesFrom)
                            .filter("createdAt <=", queryEntitiesTo);

            // generate institute stats by scanning student entities
            LoopHelper loopHelper = new LoopHelper(100,
                    "Counting institutions stats by scanning student entities...");
            Iterable<CourseStudent> students = CursorIterator.iterate(studentQuery);
            for (CourseStudent student : students) {
                String instituteOfTheStudent = courseToInstituteCache.computeIfAbsent(student.getCourseId(),
                        k -> getCourseInstitute(student.getCourseId()));
                bundle.addStudentEmailToInstitute(instituteOfTheStudent, student.getEmail());
                loopHelper.recordLoop();
            }

            // generate institute stats by scanning account (instructor) entities
            loopHelper = new LoopHelper(100,
                    "Counting institutions stats by scanning instructor entities...");
            Iterable<Instructor> instructors = CursorIterator.iterate(instructorQuery);
            for (Instructor instructor : instructors) {
                String instituteOfTheInstructor = courseToInstituteCache.computeIfAbsent(instructor.getCourseId(),
                        k -> getCourseInstitute(instructor.getCourseId()));
                bundle.addInstructorEmailToInstitute(instituteOfTheInstructor, instructor.getEmail());
                loopHelper.recordLoop();
            }

            saveCheckpointOfData(queryEntitiesFrom, queryEntitiesTo);
            queryEntitiesFrom = queryEntitiesTo;
        }

        printStatsPerInstitute();
        printUniqueStudentEmailStatsInWholeSystem();
        printUniqueInstructorEmailStatsInWholeSystem();
    }

    private void printStatsPerInstitute() {
        System.out.println("===============Stats Per Institute=================");
        System.out.println("Format=> Instructors + Students = Total [Institute]");
        System.out.println("===================================================");
        int i = 0;
        int runningTotal = 0;
        for (StatisticsBundle.InstituteStats stats : bundle.getInstituteStatsSortByTotalStudentsDescending()) {
            i++;
            int numInstructors = stats.getInstructorTotal();
            int numStudents = stats.getStudentTotal();
            int total = numInstructors + numStudents;
            runningTotal += total;
            System.out.println(
                    "[" + i + "]" + numInstructors + " + " + numStudents + "="
                            + total + "{" + runningTotal + "}\t[" + stats.getName() + "]");
        }
    }

    private void printUniqueStudentEmailStatsInWholeSystem() {
        System.out.println("===============Unique Student Emails===============\n"
                + "Format=> Total Unique Emails [Total Emails]\n"
                + "===================================================\n"
                + bundle.getNumOfUniqueStudentEmails() + "\n");
    }

    private void printUniqueInstructorEmailStatsInWholeSystem() {
        System.out.println("===============Unique Instructor Emails===============\n"
                + "Format=> Total Unique Emails [Total Emails]\n"
                + "===================================================\n"
                + bundle.getNumOfUniqueInstructorEmails() + "\n");
    }

    private void printQueryRangeMessage(Instant queryEntitiesFrom, Instant queryEntitiesTo) {
        System.out.println(String.format("===== Counting stats from %s to %s =====%n", queryEntitiesFrom, queryEntitiesTo));
    }

    private void saveCheckpointOfData(Instant queryEntitiesFrom, Instant queryEntitiesTo) {
        try {
            bundle.setStatsSince(queryEntitiesTo);
            FileStore.saveStatisticsBundleToFile(bundle);
        } catch (Exception e) {
            System.out.println("===== Error saving checkpoint when counting stats from %s to %s =====%n");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println(String.format("===== Checkpoint stats saved (%s to %s) =====%n",
                queryEntitiesFrom, queryEntitiesTo));
    }
}
