package teammates.client.scripts;

import java.io.IOException;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.CourseStudent;

/**
 * Script to remove google ID of {@link CourseStudent} if it is fake google ID.
 */
public class DataMigrationForSampleGoogleIdInStudentAttributes
        extends DataMigrationEntitiesBaseScript<CourseStudent> {

    public static void main(String[] args) throws IOException {
        DataMigrationForSampleGoogleIdInStudentAttributes migrator =
                new DataMigrationForSampleGoogleIdInStudentAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected Query<CourseStudent> getFilterQuery() {
        String sampleGoogleId = "alice.b.tmms.sampleData";
        // Uncomment the google ID to be removed as necessary
        // sampleGoogleId = "benny.c.tmms.sampleData";
        // sampleGoogleId = "charlie.d.tmms.sampleData";
        // sampleGoogleId = "danny.e.tmms.sampleData";
        // sampleGoogleId = "emma.f.tmms.sampleData";
        // sampleGoogleId = "francis.g.tmms.sampleData";
        // sampleGoogleId = "gene.h.tmms.sampleData";
        // sampleGoogleId = "teammates.demo.instructor";

        return ofy().load().type(CourseStudent.class)
                .filter("googleId =", sampleGoogleId);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(CourseStudent student) throws Exception {
        return true;
    }

    @Override
    protected void migrateEntity(CourseStudent student) throws Exception {
        student.setGoogleId("");

        saveEntityDeferred(student);
    }
}
