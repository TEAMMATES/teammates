package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * Represents an Instructor search result.
 */
public class SearchInstructorData extends CommonSearchUserData {
    public SearchInstructorData(InstructorAttributes instructorAttributes) {
        super(instructorAttributes.getName(), instructorAttributes.getEmail(), instructorAttributes.getCourseId(),
                instructorAttributes.getGoogleId(), instructorAttributes.isRegistered());
    }
}
