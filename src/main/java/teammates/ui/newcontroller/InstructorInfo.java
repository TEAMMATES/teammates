package teammates.ui.newcontroller;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * Data transfer objects for {@link InstructorAttributes} between controller and HTTP.
 */
public class InstructorInfo {

    /**
     * The response for an instructor attribute.
     */
    public static class InstructorResponse extends ActionResult.ActionOutput {

        private final String name;

        public InstructorResponse(InstructorAttributes instructorAttributes) {
            this.name = instructorAttributes.getName();
        }

        public String getName() {
            return name;
        }
    }

}
