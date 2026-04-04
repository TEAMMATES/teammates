package teammates.ui.output;

import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.storage.sqlentity.Student;

/**
 * The API output format of {@link Student}.
 */
public class StudentData extends ApiOutput {

    @Nullable
    private final UUID studentId;

    private final String email;
    private final String courseId;

    private final String name;
    @Nullable
    private String accountId;
    @Nullable
    private String comments;
    @Nullable
    private String key;
    @Nullable
    private String institute;
    @Nullable
    private JoinState joinState;
    @Nullable
    private String loginIdentifier;
    @Nullable
    private String loginProvider;

    private final String teamName;
    private final String sectionName;

    public StudentData(Student student) {
        this.studentId = student.getId();
        this.email = student.getEmail();
        this.courseId = student.getCourseId();
        this.name = student.getName();
        this.accountId = student.getAccountId();
        this.joinState = student.isRegistered() ? JoinState.JOINED : JoinState.NOT_JOINED;
        this.comments = student.getComments();
        this.teamName = student.getTeamName();
        this.sectionName = student.getSectionName();
    }

    public UUID getStudentId() {
        return studentId;
    }

    public String getEmail() {
        return email;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getComments() {
        return comments;
    }

    public JoinState getJoinState() {
        return joinState;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getKey() {
        return key;
    }

    public String getInstitute() {
        return institute;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setJoinState(JoinState joinState) {
        this.joinState = joinState;
    }

    public void setKey(String key) {
        this.key = key;
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
     * Hides some attributes to student.
     */
    public void hideInformationForStudent() {
        setComments(null);
        setJoinState(null);
    }

    /**
     * Adds additional information only for search result for admin.
     * @param key The registration key
     * @param institute The institute of the student
     * @param accountId The account id of the student
     * @param loginIdentifier Login identifier of the student
     * @param loginProvider Login provider of the student
     */
    public void addAdditionalInformationForAdminSearch(
            String key, String institute, String accountId, String loginIdentifier, String loginProvider) {
        this.setKey(key);
        this.setInstitute(institute);
        this.setAccountId(accountId);
        this.setLoginIdentifier(loginIdentifier);
        this.setLoginProvider(loginProvider);
    }
}
