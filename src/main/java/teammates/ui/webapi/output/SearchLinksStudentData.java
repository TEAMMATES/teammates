package teammates.ui.webapi.output;

/**
 * Represents a Links search result for students.
 */
public class SearchLinksStudentData extends CommonSearchLinksData {
    private String recordsPageLink;

    public SearchLinksStudentData() {
        super();
        this.recordsPageLink = null;
    }

    public String getRecordsPageLink() {
        return recordsPageLink;
    }

    public void setRecordsPageLink(String recordsPageLink) {
        this.recordsPageLink = recordsPageLink;
    }
}
