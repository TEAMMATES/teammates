package teammates.common.datatransfer;

/**
 * Represents the type of target user groups for notifications.
 */
public enum NotificationTargetUser {

    // booleans represent: hasStudent?, hasInstructor?

    /**
     * Target users are students only.
     */
    STUDENT(true, false),

    /**
     * Target users are instructors only.
     */
    INSTRUCTOR(false, true),

    /**
     * Target users are both instructors and students.
     */
    GENERAL(true, true);

    private final boolean hasStudent;
    private final boolean hasInstructor;

    NotificationTargetUser(boolean hasStudent, boolean hasInstructor) {
        this.hasStudent = hasStudent;
        this.hasInstructor = hasInstructor;
    }

    /**
     * Checks if a target group includes students.
     */
    public boolean hasStudent() {
        return hasStudent;
    }

    /**
     * Checks if a target group includes instructors.
     */
    public boolean hasInstructor() {
        return hasInstructor;
    }

    /**
     * Formats the participant type as a singular noun.
     *
     * @return A user-friendly {@code String} representing this participant in singular form.
     */
    public String toSingularFormString() {
        switch (this) {
        case STUDENT:
            return "student";
        case INSTRUCTOR:
            return "instructor";
        case GENERAL:
            return "general";
        default:
            return super.toString();
        }
    }

    /**
     * Finds the matching type of given string.
     *
     * @return An enum that matches the given {@code String s}.
     */
    public static NotificationTargetUser find(String s) {
        switch (s) {
        case "student":
            return STUDENT;
        case "instructor":
            return INSTRUCTOR;
        case "general":
            return GENERAL;
        default:
            return null;
        }
    }
}
