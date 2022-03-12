package teammates.common.datatransfer;

/**
 * Represents the type of target user groups for notifications.
 */
public enum NotificationTargetUser {

    /**
     * Target users are students only.
     */
    STUDENT,

    /**
     * Target users are instructors only.
     */
    INSTRUCTOR,

    /**
     * Target users are both instructors and students.
     */
    GENERAL
}
