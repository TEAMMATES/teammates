package teammates.storage.entity;

import java.security.SecureRandom;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * An association class that represents the association Account
 * --> [is an instructor for] --> Course.
 */
@PersistenceCapable
public class Instructor extends Entity {

    /**
     * The name of the primary key of this entity type.
     */
    @NotPersistent
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(Instructor.class);

    /**
     * The primary key. Format: email%courseId e.g., adam@gmail.com%cs1101
     */
    @PrimaryKey
    @Persistent
    private String id;

    /**
     * The Google id of the instructor, used as the foreign key to locate the Account object.
     */
    @Persistent
    private String googleId;

    /** The foreign key to locate the Course object. */
    @Persistent
    private String courseId;

    /** new attribute. Default value: Old Entity--null  New Entity--false*/
    @Persistent
    private Boolean isArchived;

    /** The instructor's name used for this course. */
    @Persistent
    private String name;

    /** The instructor's email used for this course. */
    @Persistent
    private String email;

    /** The instructor's registration key used for joining. */
    @Persistent
    private String registrationKey;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String role;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Boolean isDisplayedToStudents;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String displayedName;

    @Persistent
    private Text instructorPrivilegesAsText;

    public Instructor(String instructorGoogleId, String courseId, Boolean isArchived, String instructorName,
                      String instructorEmail, String role, boolean isDisplayedToStudents, String displayedName,
                      String instructorPrivilegesAsText) {
        this.setGoogleId(instructorGoogleId);
        this.setCourseId(courseId);
        this.setIsArchived(isArchived);
        this.setName(instructorName);
        this.setEmail(instructorEmail);
        this.setRole(role);
        this.setIsDisplayedToStudents(isDisplayedToStudents);
        this.setDisplayedName(displayedName);
        this.setInstructorPrivilegeAsText(instructorPrivilegesAsText);
        // setId should be called after setting email and courseId
        this.setUniqueId(this.getEmail() + '%' + this.getCourseId());
        this.setRegistrationKey(generateRegistrationKey());
    }

    /**
     * Constructor used for testing purpose only.
     */
    public Instructor(String instructorGoogleId, String courseId, Boolean isArchived, String instructorName,
                      String instructorEmail, String key, String role, boolean isDisplayedToStudents,
                      String displayedName, String instructorPrivilegesAsText) {
        this.setGoogleId(instructorGoogleId);
        this.setCourseId(courseId);
        this.setIsArchived(isArchived);
        this.setName(instructorName);
        this.setEmail(instructorEmail);
        this.setRole(role);
        this.setIsDisplayedToStudents(isDisplayedToStudents);
        this.setDisplayedName(displayedName);
        this.setInstructorPrivilegeAsText(instructorPrivilegesAsText);
        // setId should be called after setting email and courseId
        this.setUniqueId(this.getEmail() + '%' + this.getCourseId());
        this.setRegistrationKey(key);
    }

    /**
     * Returns the unique ID of the entity (format: googleId%courseId).
     */
    public String getUniqueId() {
        return id;
    }

    /**
     * Sets the unique ID for the instructor entity.
     *
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

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
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

    public void setGeneratedKeyIfNull() {
        if (this.registrationKey == null) {
            setRegistrationKey(generateRegistrationKey());
        }
    }

    /**
     * Generate unique registration key for the instructor.
     * The key contains random elements to avoid being guessed.
     */
    private String generateRegistrationKey() {
        String uniqueId = getUniqueId();
        SecureRandom prng = new SecureRandom();

        return uniqueId + prng.nextInt();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isDisplayedToStudents() {
        if (this.isDisplayedToStudents == null) {
            return true;
        }
        return isDisplayedToStudents.booleanValue();
    }

    public void setIsDisplayedToStudents(boolean shouldDisplayToStudents) {
        this.isDisplayedToStudents = Boolean.valueOf(shouldDisplayToStudents);
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getInstructorPrivilegesAsText() {
        if (instructorPrivilegesAsText == null) {
            return null;
        }
        return instructorPrivilegesAsText.getValue();
    }

    public void setInstructorPrivilegeAsText(String instructorPrivilegesAsText) {
        this.instructorPrivilegesAsText = new Text(instructorPrivilegesAsText);
    }
}
