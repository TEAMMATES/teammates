package teammates.client.scripts;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import teammates.storage.entity.CourseStudent;

import java.io.IOException;

public class ValidateAllTeamNameScript extends DataMigrationEntitiesBaseScript<CourseStudent>{

    public static void main(String[] args) throws IOException {
        new ValidateAllTeamNameScript().doOperationRemotely();
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
    protected boolean isMigrationNeeded(Key<CourseStudent> key) throws Exception {
        CourseStudent student = ofy().load().key(key).now();

        return student.getTeamName().equals(student.getEmail());
    }

    @Override
    protected void migrateEntity(Key<CourseStudent> entity) throws Exception {
        // no actual data migrations needed.
    }
}
