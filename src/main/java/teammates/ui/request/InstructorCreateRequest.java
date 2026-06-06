package teammates.ui.request;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.ui.exception.InvalidHttpRequestBodyException;

/**
 * The create request for an instructor to be created.
 */
public class InstructorCreateRequest extends BasicRequest {
    private String name;
    private String email;
    private InstructorPermissionRole role;

    @Nullable
    private String displayName;
    private Boolean isDisplayedToStudent;

    @Nullable
    private InstructorPrivileges privileges;

    @SuppressWarnings("unused")
    private InstructorCreateRequest() {
        // for Jackson deserialization
    }

    public InstructorCreateRequest(String name, String email, String roleName,
                                   String displayName, Boolean isDisplayedToStudent,
                                   @Nullable InstructorPrivileges privileges) {
        this.name = name;
        this.email = email;
        this.role = InstructorPermissionRole.getEnum(roleName);
        this.displayName = displayName;
        this.isDisplayedToStudent = isDisplayedToStudent;
        this.privileges = privileges;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(name != null, "name cannot be null");
        validateTrue(email != null, "email cannot be null");
        validateTrue(role != null, "role name cannot be null");
        validateTrue(isDisplayedToStudent != null, "displayed to student boolean cannot be null");
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleName() {
        return role.getRoleName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public Boolean getIsDisplayedToStudent() {
        return isDisplayedToStudent;
    }

    public InstructorPrivileges getPrivileges() {
        return privileges;
    }
}
