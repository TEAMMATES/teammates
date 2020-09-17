package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of a list of instructors.
 */
public class InstructorsData extends ApiOutput {

    private List<InstructorData> instructors;

    public InstructorsData() {
        this.instructors = new ArrayList<>();
    }

    public InstructorsData(List<InstructorAttributes> instructorAttributesList) {
        this.instructors = instructorAttributesList.stream().map(InstructorData::new).collect(Collectors.toList());
    }

    public List<InstructorData> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<InstructorData> instructors) {
        this.instructors = instructors;
    }
}
