package teammates.main.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import teammates.common.exception.PendingDatabaseMigrationsException;
import teammates.common.util.Config;

import liquibase.command.CommandScope;

/**
 * Checks whether the liquibase status is successful.
 */
public final class LiquibaseStatusChecker {

    private static final String CHANGELOG_FILE = "db/changelog/db.changelog-root.xml";
    private static final String LIQUIBASE_PENDING_CHANGESETS_PHRASE = "not been applied";

    private LiquibaseStatusChecker() {
        // utility class
    }

    /**
     * Asserts that liquibase reports successful status.
     */
    public static void assertSuccessStatus() throws Exception {
        assertDatabaseUpToDate();
    }

    /**
     * Fails fast when Liquibase reports unapplied changesets.
     */
    private static void assertDatabaseUpToDate() throws Exception {
        Optional<String> pendingMigrationStatus = getPendingMigrationStatus();
        if (pendingMigrationStatus.isEmpty()) {
            return;
        }

        throw new PendingDatabaseMigrationsException(
                "Database schema is out of date. Apply pending Liquibase migrations before starting the server.",
                pendingMigrationStatus.get());
    }

    private static Optional<String> getPendingMigrationStatus() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new CommandScope("status")
                .addArgumentValue("changelogFile", CHANGELOG_FILE)
                .addArgumentValue("url", Config.getDbConnectionUrl())
                .addArgumentValue("username", Config.POSTGRES_USERNAME)
                .addArgumentValue("password", Config.POSTGRES_PASSWORD)
                .addArgumentValue("verbose", false)
                .setOutput(output)
                .execute();

        String status = output.toString(StandardCharsets.UTF_8).trim();
        return status.contains(LIQUIBASE_PENDING_CHANGESETS_PHRASE)
                ? Optional.of(status)
                : Optional.empty();
    }

}
