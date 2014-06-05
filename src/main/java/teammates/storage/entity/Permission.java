package teammates.storage.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * A class that represents permission for an instructor
 *
 */
public class Permission {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private String id;
    
    /** The email of instructor */
    @Persistent
    private String instructorEmail;
    
    /** The Id of course */
    @Persistent
    private String courseId;
    
    /** The role of this instructor */
    @Persistent
    private String role;
    
    /** The text representing of exceptions */
    @Persistent
    private Text contradiction;
    
    /**
     * @param instrEmail
     * @param courseId
     * @param instrRole
     * @param contradiction
     */
    public Permission(String instrEmail, String courseId, String instrRole, Text contradiction) {
        this.instructorEmail = instrEmail;
        this.courseId = courseId;
        this.role = instrRole;
        this.contradiction = contradiction;
    }

    /**
     * @return The unique ID of the entity (format: instructorId%permission)
     */
    public String getUniqueId() {
        return id;
    }

    /**
     * @param uniqueId
     *          The unique ID of the entity (format: instructorId%permission)
     */
    public void setUniqueId(String uniqueId) {
        this.id = uniqueId;
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
     * @return the exceptionField
     */
    public Text getContradiction() {
        return contradiction;
    }

    /**
     * @param exception the exception to set
     */
    public void setContradiction(Text contradiction) {
        this.contradiction = contradiction;
    }

}
