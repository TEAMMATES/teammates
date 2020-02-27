package teammates.ui.webapi.output;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * Output format for instructor search result.
 */
public class SearchInstructorsData extends ApiOutput {
    private final List<InstructorAttributes> instructors;

    public SearchInstructorsData(List<InstructorAttributes> instructors) {
        this.instructors = instructors;
    }

    public List<InstructorAttributes> getInstructors() {
        return instructors;
    }
}
