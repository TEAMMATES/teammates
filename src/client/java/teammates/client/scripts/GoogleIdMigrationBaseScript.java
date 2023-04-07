package teammates.client.scripts;

import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.cmd.Query;

import teammates.client.util.BackDoor;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.entity.Account;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.Instructor;

/**
 * Base script to migrate a googleId to a new googleId.
 *
 * <p>The change of googleId is not atomic due to the constrains imposed by the Datastore.
 *
 * <p>Instructions to use the script:
 *
 * <ul>
 * <li>The script scans all {@code Account} entities and checks each accordingly.
 * To limit the query range, override {@link GoogleIdMigrationBaseScript#getFilterQuery()}.</li>
 * <li>Implement {@link GoogleIdMigrationBaseScript#isMigrationOfGoogleIdNeeded(Account)}
 * to check whether the googleId migration is needed for the account.</li>
 * <li>Implement {@link GoogleIdMigrationBaseScript#generateNewGoogleId(Account)}
 * to generate the new googleId for the account.</li>
 * </ul>
 *
 * @see GoogleIdMigrationBaseScript#shouldUseTransaction()
 */
public abstract class GoogleIdMigrationBaseScript extends DataMigrationEntitiesBaseScript<Account> {

    @Override
    protected Query<Account> getFilterQuery() {
        return ofy().load().type(Account.class);
    }

    @Override
    protected boolean isMigrationNeeded(Account account) {
        if (!isMigrationOfGoogleIdNeeded(account)) {
            return false;
        }

        String newGoogleId = generateNewGoogleId(account);
        log(String.format("Going to migrate account with googleId %s to new googleId %s",
                account.getGoogleId(), newGoogleId));

        return true;
    }

    @Override
    protected void migrateEntity(Account oldAccount) throws Exception {
        String oldGoogleId = oldAccount.getGoogleId();
        String newGoogleId = generateNewGoogleId(oldAccount);

        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class)
                .filter("googleId =", oldGoogleId).list();

        List<Instructor> oldInstructors = ofy().load().type(Instructor.class)
                .filter("googleId =", oldGoogleId).list();

        // update students and instructors

        if (!oldStudents.isEmpty()) {
            oldStudents.forEach(student -> student.setGoogleId(newGoogleId));
            ofy().save().entities(oldStudents).now();
        }

        if (!oldInstructors.isEmpty()) {
            oldInstructors.forEach(instructor -> instructor.setGoogleId(newGoogleId));
            ofy().save().entities(oldInstructors).now();

            DataBundle bundle = new DataBundle();
            oldInstructors.stream()
                    .map(InstructorAttributes::valueOf)
                    .collect(Collectors.toList())
                    .forEach(instructor -> bundle.instructors.put(instructor.getEmail(), instructor));
            BackDoor.getInstance().putDocuments(bundle);
        }

        // recreate account

        oldAccount.setGoogleId(newGoogleId);
        if (ofy().load().type(Account.class).id(newGoogleId).now() == null) {
            ofy().save().entity(oldAccount).now();
        } else {
            log(String.format("Skip creation of new account as account (%s) already exists", newGoogleId));
        }
        ofy().delete().type(Account.class).id(oldGoogleId).now();

        log(String.format("Complete migration for account with googleId %s. The new googleId is %s",
                oldGoogleId, newGoogleId));
    }

    /**
     * Checks whether the googleId of the {@code account} is needed to be migrated or not.
     */
    protected abstract boolean isMigrationOfGoogleIdNeeded(Account account);

    /**
     * Generates a new googleId based on the {@code oldAccount}.
     */
    protected abstract String generateNewGoogleId(Account oldAccount);
}
