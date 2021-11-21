package teammates.ui.output;

import teammates.common.datatransfer.InstructorPrivileges;

/**
 * The output format for privilege of an instructor.
 */
public class InstructorPrivilegeData extends ApiOutput {

    private final InstructorPrivileges privileges;

    public InstructorPrivilegeData(InstructorPrivileges privileges) {
        this.privileges = privileges;
    }

    public InstructorPrivileges getPrivileges() {
        return privileges;
    }

}
