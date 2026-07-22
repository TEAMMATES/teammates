package teammates.common.exception;

/**
 * Exception thrown when there are unapplied database migrations.
 */
public class PendingDatabaseMigrationsException extends Exception {

    private final String migrationStatus;

    public PendingDatabaseMigrationsException(String message, String migrationStatus) {
        super(message);
        this.migrationStatus = migrationStatus;
    }

    public String getMigrationStatus() {
        return migrationStatus;
    }

}
