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
    private StudentProfile localStudentProfile = null;

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
        if (localStudentProfile != null && localStudentProfile.getGoogleId().isEmpty()) { // only in local attribute tests
            return localStudentProfile;
        }
        if (studentProfile == null) {
            return null;
        }
        return studentProfile.get();
    }

    public void setStudentProfile(StudentProfile studentProfile) {
        if (studentProfile == null) {
            this.studentProfile = null;
            this.localStudentProfile = null;
            return;
        }
        if (studentProfile.getGoogleId().isEmpty()) { // only in local attribute tests
            this.studentProfile = null;
            this.localStudentProfile = studentProfile;
            return;
        }
        this.studentProfile = Ref.create(studentProfile);
    }
}
