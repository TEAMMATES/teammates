package teammates.ui.webapi.output;

import java.util.Optional;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of {@link InstructorAttributes}.
 */
public class InstructorData extends ApiOutput {
    private final String name;

    public InstructorData(InstructorAttributes instructorAttributes) {
        this.name = Optional.ofNullable(instructorAttributes.getName()).orElse("DEFAULT_NAME");
    }

    public String getName() {
        return name;
    }
}
