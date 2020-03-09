package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * Contains Instructor search results.
 */
public class SearchInstructorsResult extends ApiOutput {
    private final List<SearchInstructorData> instructors;

    public SearchInstructorsResult(List<InstructorAttributes> instructors) {
        this.instructors = instructors.stream().map(SearchInstructorData::new).collect(Collectors.toList());
    }

    public List<SearchInstructorData> getInstructors() {
        return instructors;
    }
}
