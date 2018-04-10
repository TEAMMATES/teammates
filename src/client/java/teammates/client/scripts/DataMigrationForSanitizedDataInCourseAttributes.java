package teammates.client.scripts;

import static teammates.common.util.SanitizationHelper.desanitizeFromHtml;
import static teammates.common.util.SanitizationHelper.isSanitizedHtml;

import java.io.IOException;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.CoursesDb;

/**
 * Script to desanitize the name field of {@link CourseAttributes} if it is sanitized.
 *
 * <p>Field {@link CourseAttributes#name} is no longer sanitized before saving
 * and the field is expected to be in its unsanitized form.</p>
 *
 * <p>This script desanitizes the field of existing CourseAttributes if it is sanitized so that
 * all courses will have unsanitized values in these fields.</p>
 */
public class DataMigrationForSanitizedDataInCourseAttributes extends DataMigrationForEntities<CourseAttributes> {

    private CoursesDb coursesDb = new CoursesDb();

    public static void main(String[] args) throws IOException {
        new DataMigrationForSanitizedDataInCourseAttributes().doOperationRemotely();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPreview() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    protected List<CourseAttributes> getEntities() {
        return coursesDb.getAllCourses();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMigrationNeeded(CourseAttributes course) {
        return isSanitizedHtml(course.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void printPreviewInformation(CourseAttributes course) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void migrate(CourseAttributes course) throws InvalidParametersException, EntityDoesNotExistException {
        course.setName(desanitizeFromHtml(course.getName()));

        if (!course.isValid()) {
            throw new InvalidParametersException(course.getInvalidityInfo());
        }

        coursesDb.updateCourse(course);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postAction() {
        // nothing to do
    }

}
