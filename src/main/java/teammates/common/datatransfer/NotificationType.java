package teammates.common.datatransfer;

/**
 * Represents the type of target user groups for notifications.
 */
public enum NotificationType {

    // booleans represent: isMaintenance?, isVersionNote?, isDeprecation?, isTips?
    /**
     * Notes for maintenance.
     */
    MAINTENANCE(true, false, false, false),

    /**
     * Notes for version release.
     */
    VERSION_NOTE(false, true, false, false),

    /**
     * Notes for deprecation.
     */
    DEPRECATION(false, false, true, false),

    /**
     * Usage tips.
     */
    TIPS(false, false, false, true);

    private final boolean isMaintenance;
    private final boolean isVersionNote;
    private final boolean isDeprecation;
    private final boolean isTips;

    NotificationType(boolean isMaintenance, boolean isVersionNote, boolean isDeprecation, boolean isTips) {
        this.isMaintenance = isMaintenance;
        this.isVersionNote = isVersionNote;
        this.isDeprecation = isDeprecation;
        this.isTips = isTips;
    }

    public boolean isMaintenance() {
        return isMaintenance;
    }

    public boolean isVersionNote() {
        return isVersionNote;
    }

    public boolean isDeprecation() {
        return isDeprecation;
    }

    public boolean isTips() {
        return isTips;
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
     * Finds the matching type of given string.
     *
     * @return An enum that matches the given {@code String s}.
     */
    public static NotificationType find(String s) {
        switch (s) {
        case "maintenance":
            return MAINTENANCE;
        case "versionnote":
            return VERSION_NOTE;
        case "deprecation":
            return DEPRECATION;
        case "tips":
            return TIPS;
        default:
            return null;
        }
    }
}
