package teammates.ui.request;

import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.InstructorPermissionRole;

/**
 * The update request for an instructor.
 */
public class InstructorUpdateRequest extends BasicRequest {
    private UUID id;

    private String name;
    private String email;
    private InstructorPermissionRole role;

    @Nullable
    private String displayName;
    private Boolean isDisplayedToStudent;

    @SuppressWarnings("unused")
    private InstructorUpdateRequest() {
        // for Jackson deserialization
    }

    public InstructorUpdateRequest(UUID id, String name, String email, String roleName,
                                   String displayName, Boolean isDisplayedToStudent) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = InstructorPermissionRole.getEnum(roleName);
        this.displayName = displayName;
        this.isDisplayedToStudent = isDisplayedToStudent;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(id != null, "id cannot be null");
        validateTrue(name != null, "name cannot be null");
        validateTrue(email != null, "email cannot be null");
        validateTrue(role != null, "role name cannot be null");
        validateTrue(isDisplayedToStudent != null, "displayed to student boolean cannot be null");
    }

    public UUID getId() {
        return id;
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
}