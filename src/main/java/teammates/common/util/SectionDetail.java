package teammates.common.util;

/**
 * Shows more information for different types of details for each section.
 */
public enum SectionDetail {
    EITHER("Show response if either the giver or evaluee is in the selected section"),
    GIVER("Show response if the giver is in the selected section"),
    EVALUEE("Show response if the evaluee is in the selected section"),
    BOTH("Show response only if both are in the selected section");

    private String sectionDetail;

    SectionDetail(String sectionDetail) {
        this.sectionDetail = sectionDetail;
    }

    public String getSectionDetail() {
        return sectionDetail;
    }
}
