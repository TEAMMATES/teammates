package teammates.ui.webapi.action;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Data transfer objects for {@link InstructorAttributes} between controller and HTTP.
 */
public class InstructorInfo {

    /**
     * The response for an instructor attribute.
     */
    public static class InstructorResponse extends ApiOutput {

        private final String name;

        public InstructorResponse(InstructorAttributes instructorAttributes) {
            this.name = instructorAttributes.getName();
        }

        public String getName() {
            return name;
        }
    }

}
