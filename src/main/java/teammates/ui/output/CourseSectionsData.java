package teammates.ui.output;

import java.util.List;
import java.util.Set;

import teammates.storage.entity.Section;

/**
 * The API output format of a list of sections.
 */
public class CourseSectionsData implements ApiOutput {
    private List<CourseSectionData> sections;

    public CourseSectionsData(Set<Section> sections) {
        this.sections = sections.stream().map(CourseSectionData::new).toList();
    }

    public List<CourseSectionData> getSections() {
        return this.sections;
    }
}
