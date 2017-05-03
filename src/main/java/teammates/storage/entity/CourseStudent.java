package teammates.storage.entity;

import java.security.SecureRandom;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;

import com.google.gson.annotations.SerializedName;

import teammates.common.util.Assumption;
import teammates.common.util.StringHelper;

/**
 * An association class that represents the association Account -->
 * [enrolled in] --> Course.
 */
@PersistenceCapable
public class CourseStudent extends Entity implements StoreCallback {

    /**
     * The name of the primary key of this entity type.
     */
    @NotPersistent
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(CourseStudent.class);

    /**
     * Setting this to true prevents changes to the lastUpdate time stamp.
     * Set to true when using scripts to update entities when you want to
     * preserve the lastUpdate time stamp.
     **/
    @NotPersistent
    public transient boolean keepUpdateTimestamp;

    /**
     * ID of the student.
     *
     * @see #makeId()
     */
    @PrimaryKey
    @Persistent
    private String id;

    @Persistent
    private Date createdAt;

    @Persistent
    private Date updatedAt;

    @Persistent
    private transient String registrationKey;

    /**
     * The student's Google ID. Links to the Account object.
     * This can be null if the student hasn't joined the course yet.
     */
    @Persistent
    @SerializedName("google_id")
    private String googleId;

    @Persistent
    @SerializedName("email")
    private String email;

    /**
     * The student's Course ID. References the primary key of the course.
     */
    @Persistent
    @SerializedName("coursename")
    private String courseId;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("name")
    private String name;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("lastName")
    private String lastName;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String comments;

    @Persistent
    @SerializedName("teamname")
    private String teamName;

    @Persistent
    @SerializedName("sectionname")
    private String sectionName;

    public CourseStudent(String email, String name, String googleId, String comments, String courseId,
                         String teamName, String sectionName) {
        setEmail(email);
        setName(name);
        setGoogleId(googleId);
        setComments(comments);
        setCourseId(courseId);
        setTeamName(teamName);
        setSectionName(sectionName);

        setCreatedAt(new Date());

        this.id = makeId();
        registrationKey = generateRegistrationKey();
    }

    private String makeId() {
        return getEmail() + '%' + getCourseId();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date created) {
        this.createdAt = created;
        setLastUpdate(created);
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setLastUpdate(Date updatedAt) {
        if (!keepUpdateTimestamp) {
            this.updatedAt = updatedAt;
        }
    }

    public String getUniqueId() {
        return this.id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId == null ? null : googleId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String trimmedName = name.trim();
        String processedFullName = StringHelper.splitName(trimmedName)[2];
        this.name = processedFullName.trim();
        this.setLastName(StringHelper.splitName(trimmedName)[1]);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    public String getLastName() {
        // for legacy data. do not remove even if not covered in test.
        if (this.lastName == null) {
            this.lastName = StringHelper.splitName(this.name)[1];
        }
        return lastName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId.trim();
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName == null ? null : teamName.trim();
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName == null ? null : sectionName.trim();
    }

    /**
     * Called by jdo before storing takes place.
     */
    @Override
    public void jdoPreStore() {
        this.setLastUpdate(new Date());
    }

    /**
     * Returns unique registration key for the student.
     */
    private String generateRegistrationKey() {
        String uniqueId = getUniqueId();
        Assumption.assertNotNull(uniqueId);

        SecureRandom prng = new SecureRandom();
        return uniqueId + "%" + prng.nextInt();
    }
}
