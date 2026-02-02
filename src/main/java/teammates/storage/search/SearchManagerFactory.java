package teammates.storage.search;

/**
 * Factory that returns search manager implementation.
 */
public final class SearchManagerFactory {

    private static InstructorSearchManager instructorInstance;
    private static StudentSearchManager studentInstance;
    private static AccountRequestSearchManager accountRequestInstance;

    private SearchManagerFactory() {
        // prevents initialization
    }

    public static InstructorSearchManager getInstructorSearchManager() {
        return instructorInstance;
    }

    /**
     * Registers the instructor search service into the factory.
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton") // ok to ignore as method is only invoked at application startup
    public static void registerInstructorSearchManager(InstructorSearchManager instructorSearchManager) {
        if (instructorInstance == null) {
            instructorInstance = instructorSearchManager;
        }
    }

    public static StudentSearchManager getStudentSearchManager() {
        return studentInstance;
    }

    /**
     * Registers the student search service into the factory.
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton") // ok to ignore as method is only invoked at application startup
    public static void registerStudentSearchManager(StudentSearchManager studentSearchManager) {
        if (studentInstance == null) {
            studentInstance = studentSearchManager;
        }
    }

    public static AccountRequestSearchManager getAccountRequestSearchManager() {
        return accountRequestInstance;
    }

    /**
     * Registers the account request search service into the factory.
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton") // ok to ignore as method is only invoked at application startup
    public static void registerAccountRequestSearchManager(AccountRequestSearchManager accountRequestSearchManager) {
        if (accountRequestInstance == null) {
            accountRequestInstance = accountRequestSearchManager;
        }
    }
}
