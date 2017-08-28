package teammates.common.datatransfer;

/**
 * Enum that defines supported displaying modes for section.
 */
public enum SectionDisplayMode {
    GIVER_IN_SECTION("As long as giver is in section"),
    RECIPIENT_IN_SECTION("As long as recipient is in section"),
    BOTH_IN_SECTION("Both giver AND recipient are in section"),
    GIVER_OR_RECIPIENT_IN_SECTION("Either giver OR recipient is in section");

    private String displayedName;

    SectionDisplayMode(String displayedName) {
        this.displayedName = displayedName;
    }

    public static SectionDisplayMode valueOfOrDefault(String displayMode) {
        return displayMode == null ? BOTH_IN_SECTION : SectionDisplayMode.valueOf(displayMode);
    }

    public String getDisplayedName() {
        return displayedName;
    }
}
