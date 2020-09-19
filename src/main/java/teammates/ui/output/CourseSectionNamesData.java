package teammates.ui.output;

import java.util.List;

/**
 * The API output format of a list of section names (as strings).
 */
public class CourseSectionNamesData extends ApiOutput {
    private List<String> sectionNames;

    public CourseSectionNamesData(List<String> sectionNames) {
        this.sectionNames = sectionNames;
    }

    public List<String> getSectionNames() {
        return this.sectionNames;
    }
}
