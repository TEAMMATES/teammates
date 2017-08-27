package teammates.common.datatransfer;

/**
 * Enum that defines supported displaying modes for section.
 */
public enum SectionDisplayMode {
    GIVER_IN_SECTION, RECIPIENT_IN_SECTION, BOTH_IN_SECTION, GIVER_OR_RECIPIENT_IN_SECTION;

    public static SectionDisplayMode valueOfOrDefault(String displayMode) {
        return displayMode == null ? GIVER_OR_RECIPIENT_IN_SECTION : SectionDisplayMode.valueOf(displayMode);
    }
}
