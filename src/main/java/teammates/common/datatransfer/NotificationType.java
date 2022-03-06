package teammates.common.datatransfer;

import teammates.common.util.Const;

/**
 * Represents the type of target user groups for notifications.
 */
public enum NotificationType {

    /**
     * Notes for maintenance.
     */
    MAINTENANCE(Const.NotificationType.MAINTENANCE),

    /**
     * Notes for version release.
     */
    VERSION_NOTE(Const.NotificationType.VERSION_NOTE),

    /**
     * Notes for deprecation.
     */
    DEPRECATION(Const.NotificationType.DEPRECATION),

    /**
     * Usage tips.
     */
    TIPS(Const.NotificationType.TIPS);

    private final String notificationType;

    NotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationType() {
        return notificationType;
    }

    /**
     * Formats the participant type as a singular noun.
     *
     * @return A user-friendly {@code String} representing this participant in singular form.
     */
    public String toSingularFormString() {
        switch (this) {
        case MAINTENANCE:
            return "maintenance";
        case VERSION_NOTE:
            return "versionnote";
        case DEPRECATION:
            return "deprecation";
        case TIPS:
            return "tips";
        default:
            return super.toString();
        }
    }

    /**
     * Get enum from string.
     */
    public static NotificationType getEnum(String notificationType) {
        switch (notificationType) {
        case Const.NotificationType.MAINTENANCE:
            return MAINTENANCE;
        case Const.NotificationType.VERSION_NOTE:
            return VERSION_NOTE;
        case Const.NotificationType.DEPRECATION:
            return DEPRECATION;
        case Const.NotificationType.TIPS:
            return TIPS;
        default:
            return null;
        }
    }
}
