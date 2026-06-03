package teammates.ui.output;

import java.util.UUID;

import teammates.storage.entity.Section;

/**
 * The API output format of a section.
 */
public class CourseSectionData implements ApiOutput {
    private UUID sectionId;
    private String sectionName;

    public CourseSectionData(Section section) {
        this.sectionId = section.getId();
        this.sectionName = section.getName();
    }

    public UUID getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }
}
