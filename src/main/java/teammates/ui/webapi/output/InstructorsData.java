package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of a list of instructors.
 */
public class InstructorsData extends ApiOutput {

    private final List<InstructorData> instructors;

    public InstructorsData(List<InstructorAttributes> instructorAttributesList) {
        this.instructors = instructorAttributesList.stream().map(InstructorData::new).collect(Collectors.toList());
    }

    public List<InstructorData> getInstructors() {
        return instructors;
    }
}
