package teammates.ui.datatransfer;


public enum SectionDisplayMode {
    GIVER_IN_SECTION, RECIPIENT_IN_SECTION, BOTH_IN_SECTION;

    public static SectionDisplayMode valueOfOrDefault(String displayMode) {
        return displayMode == null ? BOTH_IN_SECTION : SectionDisplayMode.valueOf(displayMode);
    }
}
