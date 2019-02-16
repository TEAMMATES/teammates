package teammates.ui.webapi.request;

/**
 * The create request for an instructor to be created.
 */
public class InstructorCreateRequest extends BasicRequest {
    private String instructorId;
    private String name;
    private String email;
    private String roleName;
    private String displayName;
    private Boolean isDisplayedToStudent;

    public InstructorCreateRequest(String instructorId, String name, String email, String roleName, String displayName,
                                   Boolean isDisplayedToStudent) {
        this.instructorId = instructorId;
        this.name = name;
        this.email = email;
        this.roleName = roleName;
        this.displayName = displayName;
        this.isDisplayedToStudent = isDisplayedToStudent;
    }

    @Override
    public void validate() {
        assertTrue(name != null, "name cannot be null");
        assertTrue(email != null, "email cannot be null");
        assertTrue(roleName != null, "role name cannot be null");
    }

    public String getId() {
        return this.instructorId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Boolean getIsDisplayedToStudent() {
        return isDisplayedToStudent;
    }
}
