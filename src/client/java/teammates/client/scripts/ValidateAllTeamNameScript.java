package teammates.client.scripts;

import static teammates.common.util.FieldValidator.REGEX_EMAIL;

import java.io.IOException;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.common.util.StringHelper;
import teammates.storage.entity.CourseStudent;



/**
 * Script to check if any team name is a email.
 *
 * <p>See issue #7285</p>
 */
public class ValidateAllTeamNameScript extends DataMigrationEntitiesBaseScript<CourseStudent> {

    public static void main(String[] args) throws IOException {
        new ValidateAllTeamNameScript().doOperationRemotely();
    }

    @Override
    protected boolean shouldUseTransaction() {
        return false;
    }

    @Override
    protected Query<CourseStudent> getFilterQuery() {
        return ofy().load().type(CourseStudent.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected String getLastPositionOfCursor() {
        return null;
    }

    @Override
    protected int getCursorInformationPrintCycle() {
        return 100;
    }

    @Override
    protected boolean isMigrationNeeded(Key<CourseStudent> key) {
        CourseStudent student = ofy().load().key(key).now();
        String teamName = student.getTeamName();
        return StringHelper.isMatching(teamName, REGEX_EMAIL);
    }

    @Override
    protected void migrateEntity(Key<CourseStudent> entity) {
        // no actual data migrations needed.
    }
}
