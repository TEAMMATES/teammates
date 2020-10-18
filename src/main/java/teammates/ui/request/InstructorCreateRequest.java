package teammates.ui.request;

import javax.annotation.Nullable;

import teammates.ui.output.InstructorPermissionRole;

/**
 * The create request for an instructor to be created.
 */
public class InstructorCreateRequest extends BasicRequest {
    @Nullable
    private String id;

    private String name;
    private String email;
    private InstructorPermissionRole role;

    @Nullable
    private String displayName;
    private Boolean isDisplayedToStudent;

    public InstructorCreateRequest(String id, String name, String email, String roleName, String displayName,
                                   Boolean isDisplayedToStudent) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = InstructorPermissionRole.getEnum(roleName);
        this.displayName = displayName;
        this.isDisplayedToStudent = isDisplayedToStudent;
    }

    @Override
    public void validate() {
        assertTrue(name != null, "name cannot be null");
        assertTrue(email != null, "email cannot be null");
        assertTrue(role != null, "role name cannot be null");
        assertTrue(isDisplayedToStudent != null, "displayed to student boolean cannot be null");
    }

    public String getId() {
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
