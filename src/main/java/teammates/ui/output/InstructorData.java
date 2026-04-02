package teammates.ui.output;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.storage.sqlentity.Instructor;

/**
 * The API output format of an instructor.
 */
public class InstructorData extends ApiOutput {
    @Nullable
    private String accountId;
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
    @Nullable
    private String loginIdentifier;
    @Nullable
    private String loginProvider;

    public InstructorData(Instructor instructor) {
        this.courseId = instructor.getCourseId();
        this.email = instructor.getEmail();
        this.role = instructor.getRole();
        this.isDisplayedToStudents = instructor.isDisplayedToStudents();
        this.displayedToStudentsAs = instructor.getDisplayName();
        this.name = instructor.getName();
        this.joinState = instructor.getAccount() == null ? JoinState.NOT_JOINED : JoinState.JOINED;
        this.institute = instructor.getCourse().getInstitute();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

    public String getLoginProvider() {
        return loginProvider;
    }

    public void setLoginProvider(String loginProvider) {
        this.loginProvider = loginProvider;
    }

    /**
     * Adds additional attributes only for search result for admin.
     *
     * @param key Registration key
     * @param institute Institute of the instructor
     * @param accountId Account id of the instructor
     * @param loginIdentifier Login identifier of the instructor
     * @param loginProvider Login provider of the instructor
     */
    public void addAdditionalInformationForAdminSearch(
            String key, String institute, String accountId, String loginIdentifier, String loginProvider) {
        setKey(key);
        setInstitute(institute);
        setAccountId(accountId);
        setLoginIdentifier(loginIdentifier);
        setLoginProvider(loginProvider);
    }
}
