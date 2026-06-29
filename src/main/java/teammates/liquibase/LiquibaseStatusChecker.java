package teammates.liquibase;

import java.util.List;
import java.util.Optional;

import teammates.common.exception.PendingDatabaseMigrationsException;
import teammates.common.util.Config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

/**
 * Checks whether the liquibase status is successful.
 */
public final class LiquibaseStatusChecker {

    private static final String CHANGELOG_FILE = "db/changelog/db.changelog-root.xml";

    private LiquibaseStatusChecker() {
        // utility class
    }

    /**
     * Asserts that liquibase reports successful status.
     */
    public static void assertSuccessStatus() throws LiquibaseException, PendingDatabaseMigrationsException {
        assertDatabaseUpToDate();
    }

    /**
     * Fails fast when Liquibase reports unapplied changesets.
     */
    private static void assertDatabaseUpToDate() throws LiquibaseException, PendingDatabaseMigrationsException {
        Optional<String> pendingMigrationStatus = getPendingMigrationStatus();
        if (pendingMigrationStatus.isEmpty()) {
            return;
        }

        throw new PendingDatabaseMigrationsException(
                "Database schema is out of date. Apply pending Liquibase migrations before starting the server.",
                pendingMigrationStatus.get());
    }

    private static Optional<String> getPendingMigrationStatus() throws LiquibaseException {
        List<ChangeSet> unrunChangeSets = listUnrunChangeSets();
        return unrunChangeSets.isEmpty()
                ? Optional.empty()
                : Optional.of(formatPendingMigrationStatus(unrunChangeSets));
    }

    private static List<ChangeSet> listUnrunChangeSets() throws LiquibaseException {
        try (ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
                Liquibase liquibase = createLiquibase(resourceAccessor)) {
            return liquibase.listUnrunChangeSets(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            throw new LiquibaseException("Failed to check Liquibase status", e);
        }
    }

    private static Liquibase createLiquibase(ResourceAccessor resourceAccessor) throws DatabaseException {
        Database database = DatabaseFactory.getInstance().openDatabase(
                Config.getDbConnectionUrl(),
                Config.POSTGRES_USERNAME,
                Config.POSTGRES_PASSWORD,
                null,
                resourceAccessor);
        return new Liquibase(CHANGELOG_FILE, resourceAccessor, database);
    }

    private static String formatPendingMigrationStatus(List<ChangeSet> unrunChangeSets) {
        return unrunChangeSets.size() + " pending Liquibase changeset(s).";
    }

}
