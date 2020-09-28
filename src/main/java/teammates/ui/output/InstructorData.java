package teammates.ui.output;

import javax.annotation.Nullable;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The API output format of an instructor.
 */
public class InstructorData extends ApiOutput {
    @Nullable
    private String googleId;
    private final String courseId;
    private final String email;
    @Nullable
    private Boolean isDisplayedToStudents;
    @Nullable
    private String displayedToStudentsAs;
    private final String name;
    @Nullable
    private InstructorPermissionRole role;
    private JoinState joinState;
    @Nullable
    private String key;
    @Nullable
    private String institute;

    public InstructorData(InstructorAttributes instructorAttributes) {
        this.courseId = instructorAttributes.getCourseId();
        this.email = instructorAttributes.getEmail();
        this.role = instructorAttributes.getRole() == null ? null
                : InstructorPermissionRole.getEnum(instructorAttributes.getRole());
        this.isDisplayedToStudents = instructorAttributes.isDisplayedToStudents();
        this.displayedToStudentsAs = instructorAttributes.getDisplayedName();
        this.name = instructorAttributes.getName();

        this.joinState = instructorAttributes.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEmail() {
        return email;
    }

    public InstructorPermissionRole getRole() {
        return role;
    }

    public void setRole(InstructorPermissionRole role) {
        this.role = role;
    }

    public Boolean getIsDisplayedToStudents() {
        return isDisplayedToStudents;
    }

    public void setIsDisplayedToStudents(Boolean displayedToStudents) {
        isDisplayedToStudents = displayedToStudents;
    }

    public String getDisplayedToStudentsAs() {
        return displayedToStudentsAs;
    }

    public void setDisplayedToStudentsAs(String displayedToStudentsAs) {
        this.displayedToStudentsAs = displayedToStudentsAs;
    }

    public String getName() {
        return name;
    }

    public JoinState getJoinState() {
        return joinState;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    /**
     * Adds additional attributes only for search result for admin.
     *
     * @param key Encrypted registration key
     * @param institute Institute of the instructor
     * @param googleId Google ID of the instructor
     */
    public void addAdditionalInformationForAdminSearch(String key, String institute, String googleId) {
        setKey(key);
        setInstitute(institute);
        setGoogleId(googleId);
    }
}
