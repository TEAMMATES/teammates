package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of {@link InstructorAttributes}.
 */
public class InstructorData extends ApiOutput {
    private final String name;

    public InstructorData(String instructorName) {
        this.name = instructorName;
    }

    public String getName() {
        return name;
    }
}
