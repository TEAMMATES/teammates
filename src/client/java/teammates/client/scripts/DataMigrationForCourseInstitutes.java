package teammates.client.scripts;

import java.lang.reflect.Field;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.storage.entity.Course;

/**
 * Script to set institute in course objects.
 *
 * <p>See issue #11362
 */
public class DataMigrationForCourseInstitutes extends DataMigrationEntitiesBaseScript<Course> {

    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    public static void main(String[] args) {
        new DataMigrationForCourseInstitutes().doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        return ofy().load().type(Course.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(Course course) {
        try {
            Field institute = course.getClass().getDeclaredField("institute");
            institute.setAccessible(true);
            return institute.get(course) == null;
        } catch (ReflectiveOperationException e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(Course course) {
        String institute = Const.UNKNOWN_INSTITUTION;
        try {
            String retrievedInstitute = accountsLogic.getCourseInstitute(course.getUniqueId());
            if (!StringHelper.isEmpty(retrievedInstitute) && !"undefined".equals(retrievedInstitute)
                    && !"null".equals(retrievedInstitute)) {
                institute = retrievedInstitute;
            }
        } catch (AssertionError e) {
            // institute cannot be found; use default "Unknown Institution" instead
        }

        course.setInstitute(institute);

        saveEntityDeferred(course);
    }

}
