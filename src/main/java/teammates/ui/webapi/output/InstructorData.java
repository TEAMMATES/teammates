package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of {@link FeedbackResponseAttributes}.
 */
public class InstructorData extends ApiOutput {

    private final String name;

    public InstructorData(InstructorAttributes instructorAttributes) {
        this.name = instructorAttributes.getName();
    }

    public String getName() {
        return name;
    }
}
