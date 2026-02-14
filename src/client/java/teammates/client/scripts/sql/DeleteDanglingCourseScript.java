package teammates.client.scripts.sql;

import java.io.IOException;

import java.util.Optional;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.test.FileHelper;


import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.QueryResults;
import com.googlecode.objectify.cmd.Query;

/**
 * Deletes the "dangling" course from SQL: the course at the cursor position left by
 * {@link DataMigrationForCourseEntitySql} when migration failed mid-course. Run this after
 * a failed run to remove the partially migrated course from SQL before re-running migration.
 */
public class DeleteDanglingCourseScript extends DatastoreClient {
    private static final String BASE_LOG_URI = "src/client/java/teammates/client/scripts/log/";
    private static final String COURSE_MIGRATION_SCRIPT_NAME = "DataMigrationForCourseEntitySql";
    public static void main(String[] args) {
        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
        
        DeleteDanglingCourseScript script = new DeleteDanglingCourseScript();
        script.doOperationRemotely();
    }

    protected void doOperation() {
        deleteDanglingCourse();
    }


    private void deleteDanglingCourse() {
        Cursor cursor = readPositionOfCursorFromFile().orElse(null);
        if (cursor == null) {
            throw new IllegalStateException("No cursor file found (run course migration first or create cursor file)");
        }

        Course danglingCourse = getDanglingCourse(cursor);
        log("Deleting partially migrated course: " + danglingCourse.getUniqueId());
        deleteCourseCascade(danglingCourse);
    }

    private Course getDanglingCourse(Cursor cursor) {
        Query<Course> filterQueryKeys = getFilterQuery().limit(1);
        filterQueryKeys = filterQueryKeys.startAt(cursor);
        QueryResults<?> iterator = filterQueryKeys.iterator();

        Course course = null;
        // Cascade delete the course if it is not fully migrated.
        if (iterator.hasNext()) {
            course = (Course) iterator.next();
            if (course.isMigrated()) {
                throw new IllegalStateException("Course at cursor position is already migrated; no dangling cleanup needed");
            }
            return course;
        } else {
            throw new IllegalStateException("No course remaining at cursor position");
        }
    }

    protected Query<Course> getFilterQuery() {
        return ofy().load().type(Course.class);
    }

    /**
     * Deletes the course and its related entities from sql database.
     */
    private void deleteCourseCascade(Course oldCourse) {
        String courseId = oldCourse.getUniqueId();
        
        HibernateUtil.beginTransaction();
        teammates.storage.sqlentity.Course newCourse = HibernateUtil.get(teammates.storage.sqlentity.Course.class, courseId);
        if (newCourse == null) {
            HibernateUtil.commitTransaction();
            throw new IllegalStateException("Course not found in SQL; nothing to delete");
        }

        log("delete dangling course with id: " + courseId);
        HibernateUtil.remove(newCourse);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
        HibernateUtil.commitTransaction();
    }

    /**
     * Reads the cursor position from the saved file.
     *
     * @return cursor if the file can be properly decoded.
     */
    private Optional<Cursor> readPositionOfCursorFromFile() {
        try {
            String cursorPosition =
                    FileHelper.readFile(BASE_LOG_URI + COURSE_MIGRATION_SCRIPT_NAME + ".cursor");
            return Optional.of(Cursor.fromUrlSafe(cursorPosition));
        } catch (IOException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    protected void log(String logLine) {
        System.out.println(String.format("%s %s", getLogPrefix(), logLine));
    }

    protected String getLogPrefix() {
        return String.format("Cleaning up dangling course: ");
    }
}
