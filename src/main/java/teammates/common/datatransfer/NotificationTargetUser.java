package teammates.common.datatransfer;

import teammates.common.util.Const;

/**
 * Represents the type of target user groups for notifications.
 */
public enum NotificationTargetUser {

    /**
     * Target users are students only.
     */
    STUDENT(Const.NotificationTargetUser.STUDENT),

    /**
     * Target users are instructors only.
     */
    INSTRUCTOR(Const.NotificationTargetUser.INSTRUCTOR),

    /**
     * Target users are both instructors and students.
     */
    GENERAL(Const.NotificationTargetUser.GENERAL);

    private final String targetUser;

    NotificationTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getTargetUser() {
        return targetUser;
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
     * Get enum from string.
     */
    public static NotificationTargetUser getEnum(String targetUser) {
        switch (targetUser) {
        case Const.NotificationTargetUser.STUDENT:
            return STUDENT;
        case Const.NotificationTargetUser.INSTRUCTOR:
            return INSTRUCTOR;
        case Const.NotificationTargetUser.GENERAL:
            return GENERAL;
        default:
            return null;
        }
    }
}
