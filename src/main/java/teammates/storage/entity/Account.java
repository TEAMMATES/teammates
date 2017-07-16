package teammates.storage.entity;

import java.util.Date;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

/**
 * Represents a unique user in the system.
 */
@Entity
@Index
public class Account extends BaseEntity {

    @Id
    private String googleId;

    private String name;

    private boolean isInstructor;

    private String email;

    private String institute;

    private Date createdAt;

    private Ref<StudentProfile> studentProfile;

    @Ignore // used in local attribute tests that give a shell student profile (empty googleId)
    private StudentProfile localStudentProfile;

    @Ignore // session-specific based on whether profile retrieval is enabled
    private boolean isStudentProfileEnabled = true;

    @SuppressWarnings("unused") // required by Objectify
    private Account() {
    }

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

    /**
     * Fetches the student profile from the datastore the first time this is called. Returns null if student profile was
     * explicitly set to null (e.g. when the student profile is intentionally not retrieved). If a shell student profile
     * with an empty Google ID was set, simply returns this shell student profile without interacting with the datastore.
     */
    public StudentProfile getStudentProfile() {
        if (!isStudentProfileEnabled) {
            return null;
        }
        if (localStudentProfile != null && localStudentProfile.getGoogleId().isEmpty()) { // only in local attribute tests
            return localStudentProfile;
        }
        if (studentProfile == null) {
            return null;
        }
        return studentProfile.get();
    }

    /**
     * Sets a reference to {@code studentProfile} which subsequent calls to {@code getStudentProfile()} will use to fetch
     * from. To disable this behaviour (e.g. when the student profile is intentionally not retrieved), set to null. If a
     * shell student profile with an empty Google ID is set, subsequent calls to {@code getStudentProfile()} will simply
     * return the shell student profile without interacting with the datastore.
     */
    public void setStudentProfile(StudentProfile studentProfile) {
        if (studentProfile == null) {
            setIsStudentProfileEnabled(false);
            return;
        }
        setIsStudentProfileEnabled(true);
        if (studentProfile.getGoogleId().isEmpty()) { // only in local attribute tests
            this.localStudentProfile = studentProfile;
            return;
        }
        this.studentProfile = Ref.create(studentProfile);
    }

    /**
     * Sets whether or not the student profile fetch should be enabled. When the entity is fetched from the local cache,
     * this value might be outdated as it is preserved from the previous session. Hence, this property should be set on
     * every new session (every call that gets the entity).
     */
    public void setIsStudentProfileEnabled(boolean isStudentProfileEnabled) {
        this.isStudentProfileEnabled = isStudentProfileEnabled;
    }
}
