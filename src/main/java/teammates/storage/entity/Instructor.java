package teammates.storage.entity;

import java.security.SecureRandom;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;


/**
 * An association class that represents the association Account
 * --> [is an instructor for] --> Course.
 */
@PersistenceCapable
public class Instructor {
    /**
     * The primary key. Format: email%courseId e.g., adam@gmail.com%cs1101
     */
    @PrimaryKey
    @Persistent
    private String id;

    /**
     * The Google id of the instructor, used as the foreign key to locate the
     * Account object.
     */
    @Persistent
    private String googleId;

    /** The foreign key to locate the Course object. */
    @Persistent
    private String courseId;

    /** The instructor's name used for this course. */
    @Persistent
    private String name;

    /** The instructor's email used for this course. */
    @Persistent
    private String email;
    
    /** The instructor's registration key used for joining */
    @Persistent
    private String registrationKey;
    
    @Persistent
    private String role;
    
    @Persistent
    private String displayedName;
    
    @Persistent
    private Text instructorPrivilegeAsText;
    
    public Instructor(String instructorGoogleId, String courseId, String instructorName, String instructorEmail,
            String role, String displayedName, Text instructorPrivilegeAsText) {
        this.setGoogleId(instructorGoogleId);
        this.setCourseId(courseId);
        this.setName(instructorName);
        this.setEmail(instructorEmail);
        this.setRole(role);
        this.setDisplayedName(displayedName);
        this.setInstructorPrivilegeAsText(instructorPrivilegeAsText);
        // setId should be called after setting email and courseId
        this.setUniqueId(this.getEmail() + '%' + this.getCourseId());
        this.setRegistrationKey(generateRegistrationKey());
    }

    /**
     * Constructor used for testing purpose only.
     */
    public Instructor(String instructorGoogleId, String courseId, String instructorName, String instructorEmail, 
            String role, String displayedName, Text instructorPrivilegeAsText, String key) {
        this.setGoogleId(instructorGoogleId);
        this.setCourseId(courseId);
        this.setName(instructorName);
        this.setEmail(instructorEmail);
        this.setRole(role);
        this.setDisplayedName(displayedName);
        this.setInstructorPrivilegeAsText(instructorPrivilegeAsText);
        // setId should be called after setting email and courseId
        this.setUniqueId(this.getEmail() + '%' + this.getCourseId());
        this.setRegistrationKey(key);
    }

    /**
     * @return The unique ID of the entity (format: googleId%courseId).
     */
    public String getUniqueId() {
        return id;
    }

    /**
     * @param uniqueId
     *          The unique ID of the entity (format: googleId%courseId).
     */
    public void setUniqueId(String uniqueId) {
        this.id = uniqueId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String instructorGoogleId) {
        this.googleId = instructorGoogleId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String instructorName) {
        this.name = instructorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String instructorEmail) {
        this.email = instructorEmail;
    }
    
    public String getRegistrationKey() {
        return registrationKey;
    }
    
    public void setRegistrationKey(String key) {
        this.registrationKey = key;
    }
    
    /**
     * Generate unique registration key for the instructor. 
     * The key contains random elements to avoid being guessed.
     * @return
     */
    private String generateRegistrationKey() {
        String uniqueId = getUniqueId();
        SecureRandom prng = new SecureRandom();
        
        String key = uniqueId + prng.nextInt();
        
        return key;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    public Text getInstructorPrivilegeAsText() {
        return instructorPrivilegeAsText;
    }

    public void setInstructorPrivilegeAsText(Text instructorPrivilegeAsText) {
        this.instructorPrivilegeAsText = instructorPrivilegeAsText;
    }
}
