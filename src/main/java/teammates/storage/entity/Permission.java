package teammates.storage.entity;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

public class Permission {
    /**
     * The primary key. Format: instructorId%permission e.g., adam%cs1101%permission
     */
    @PrimaryKey
    @Persistent
    private String id;
    
    /** The foreign key to locate the Instructor object. */
    @Persistent
    private String instructorId;
    
    /** The role of this instructor */
    @Persistent
    private String role;
    
    /** The text representing of exceptions */
    @Persistent
    private Text exceptionField;
    
    /**
     * @param instrId
     * @param instrRole
     * @param exception
     */
    public Permission(String instrId, String instrRole, Text exception) {
        this.instructorId = instrId + "%permission";
        this.role = instrRole;
        this.exceptionField = exception;
    }
    
    /**
     * @param instrGoogleId
     * @param courseId
     * @param instrRole
     * @param exception
     */
    public Permission(String instrGoogleId, String courseId, String instrRole, Text exception) {
        this.instructorId = instrGoogleId + courseId + "%permission";
        this.role = instrRole;
        this.exceptionField = exception;
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
     * @return the instructorId
     */
    public String getInstructorId() {
        return instructorId;
    }

    /**
     * @param instructorId the instructorId to set
     */
    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
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
    public Text getException() {
        return exceptionField;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Text exception) {
        this.exceptionField = exception;
    }

}
