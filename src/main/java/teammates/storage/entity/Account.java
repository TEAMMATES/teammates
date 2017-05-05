package teammates.storage.entity;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Represents a unique user in the system.
 */
@PersistenceCapable
public class Account extends Entity {

    /**
     * The name of the primary key of this entity type.
     */
    @NotPersistent
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(Account.class);

    @PrimaryKey
    @Persistent
    private String googleId;

    @Persistent
    private String name;

    @Persistent
    private boolean isInstructor;

    @Persistent
    private String email;

    @Persistent
    private String institute;

    @Persistent
    private Date createdAt;

    @Persistent(dependent = "true", defaultFetchGroup = "false")
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private StudentProfile studentProfile;

    /**
     * Instantiates a new account.
     *
     * @param googleId
     *            the Google ID of the user.
     * @param name
     *            The name of the user.
     * @param isInstructor
     *            Does this account has instructor privileges?
     * @param email
     *            The official email of the user.
     * @param institute
     *            The university/school/institute e.g., "Abrons State University, Alaska"
     * @param studentProfile
     *            It is a StudentProfile object that contains all the attributes
     *            of a student profile
     */
    public Account(String googleId, String name, boolean isInstructor,
            String email, String institute, StudentProfile studentProfile) {
        this.setGoogleId(googleId);
        this.setName(name);
        this.setIsInstructor(isInstructor);
        this.setEmail(email);
        this.setInstitute(institute);
        this.setCreatedAt(new Date());
        this.setStudentProfile(studentProfile);
    }

    public Account(String googleId, String name, boolean isInstructor,
            String email, String institute) {
        this(googleId, name, isInstructor, email, institute, new StudentProfile(googleId));
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInstructor() {
        return isInstructor;
    }

    public void setIsInstructor(boolean accountIsInstructor) {
        this.isInstructor = accountIsInstructor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public StudentProfile getStudentProfile() {
        return this.studentProfile;
    }

    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;

    }
}
