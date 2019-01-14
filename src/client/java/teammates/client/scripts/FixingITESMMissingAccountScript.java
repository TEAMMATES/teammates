package teammates.client.scripts;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;

/**
 * Script to fix the missing account in ITESM.
 */
public class FixingITESMMissingAccountScript extends DataMigrationEntitiesBaseScript<Instructor> {

    public static void main(String[] args) throws Exception {
        new FixingITESMMissingAccountScript().doOperationRemotely();
    }

    @Override
    protected Query<Instructor> getFilterQuery() {
        return ofy().load().type(Instructor.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean shouldUseTransaction() {
        return false;
    }

    @Override
    protected String getLastPositionOfCursor() {
        return "";
    }

    @Override
    protected int getCursorInformationPrintCycle() {
        return 100;
    }

    @Override
    protected boolean isMigrationNeeded(Key<Instructor> instructorKey) throws Exception {
        Instructor instructor = ofy().load().key(instructorKey).now();

        String googleId = instructor.getGoogleId();
        if (googleId == null || googleId.isEmpty()) {
            return false;
        }

        if (!googleId.endsWith("@tec.mx")) {
            return false;
        }

        Account correspondingAccount = ofy().load().type(Account.class).id(googleId).now();
        return correspondingAccount == null;
    }

    @Override
    protected void migrateEntity(Key<Instructor> instructorKey) throws Exception {
        Instructor instructor = ofy().load().key(instructorKey).now();

        Account newAccount = new Account(instructor.getGoogleId(), instructor.getName(),
                true, instructor.getGoogleId(), "ITESM, Mexico");
        ofy().save().entity(newAccount).now();
    }
}
