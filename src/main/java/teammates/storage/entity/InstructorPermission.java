package teammates.storage.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Represents permission for an instructor
 *
 */
@PersistenceCapable
public class InstructorPermission {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    /** The email of instructor */
    @Persistent
    private String instructorEmail;
    
    /** The Id of course */
    @Persistent
    private String courseId;
    
    /** The role of this instructor */
    @Persistent
    private String role;
    
    /** The text representing of privileges */
    @Persistent
    private Text access;
    
    /**
     * @param instrEmail
     * @param courseId
     * @param instrRole
     * @param contradiction
     */
    public InstructorPermission(String instrEmail, String courseId, String instrRole, Text access) {
        this.instructorEmail = instrEmail;
        this.courseId = courseId;
        this.role = instrRole;
        this.access = access;
    }

    /**
     * @return The ID of the entity
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the instructorEmail
     */
    public String getInstructorEmail() {
        return instructorEmail;
    }

    /**
     * @param instructorEmail the instructorEmail to set
     */
    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    /**
     * @return the courseId
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * @param courseId the courseId to set
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the access
     */
    public Text getAccess() {
        return access;
    }

    /**
     * @param access the access to set
     */
    public void setAccess(Text access) {
        this.access = access;
    }

}
