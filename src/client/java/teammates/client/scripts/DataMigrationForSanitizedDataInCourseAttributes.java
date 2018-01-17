package teammates.client.scripts;

import static teammates.common.util.SanitizationHelper.desanitizeFromHtml;
import static teammates.common.util.SanitizationHelper.isSanitizedHtml;

import java.io.IOException;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.client.scripts.util.LoopHelper;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.CoursesLogic;
import teammates.storage.entity.Course;

/**
 * Script to desanitize the name field of {@link CourseAttributes} if it is sanitized.
 *
 * <p>Field {@link CourseAttributes#name} is no longer sanitized before saving
 * and the field is expected to be in its unsanitized form.</p>
 *
 * <p>This script desanitizes the field of existing CourseAttributes if it is sanitized so that
 * all courses will have unsanitized values in these fields.</p>
 */
public class DataMigrationForSanitizedDataInCourseAttributes extends RemoteApiClient {
    /**
     * Will not perform updates on the datastore if true.
     */
    private static final boolean isPreview = true;

    private CoursesLogic coursesLogic = CoursesLogic.inst();

    public static void main(String[] args) throws IOException {
        new DataMigrationForSanitizedDataInCourseAttributes().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<Course> allCourses = ofy().load().type(Course.class).list();
        int numberOfAffectedCourses = 0;
        int numberOfUpdatedCourses = 0;
        LoopHelper loopHelper = new LoopHelper(100, "courses processed.");
        println("Running data migration for sanitization on courses...");
        println("Preview: " + isPreview);
        for (Course course : allCourses) {
            loopHelper.recordLoop();
            if (!isSanitizedHtml(course.getName())) {
                // skip the update if the name field is already unsanitized
                continue;
            }
            numberOfAffectedCourses++;
            try {
                desanitizeAndUpdateCourse(course);
                numberOfUpdatedCourses++;
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                println("Problem sanitizing course with id " + course.getUniqueId());
                println(e.getMessage());
            }
        }
        println("Total number of courses: " + loopHelper.getCount());
        println("Number of affected courses: " + numberOfAffectedCourses);
        println("Number of updated courses: " + numberOfUpdatedCourses);
    }

    private void desanitizeAndUpdateCourse(Course originalCourse)
            throws InvalidParametersException, EntityDoesNotExistException {
        CourseAttributes courseToUpdate = CourseAttributes.builder(
                originalCourse.getUniqueId(),
                desanitizeFromHtml(originalCourse.getName()),
                originalCourse.getTimeZone())
                .build();

        if (!courseToUpdate.isValid()) {
            throw new InvalidParametersException(courseToUpdate.getInvalidityInfo());
        }

        if (isPreview) {
            return;
        }

        coursesLogic.updateCourse(courseToUpdate);
    }
}
