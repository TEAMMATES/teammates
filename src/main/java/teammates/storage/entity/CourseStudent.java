package teammates.storage.entity;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.util.Assumption;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

/**
 * An association class that represents the association Account -->
 * [enrolled in] --> Course.
 */
@Entity
@Index
public class CourseStudent extends BaseEntity {

    /**
     * Setting this to true prevents changes to the lastUpdate time stamp.
     * Set to true when using scripts to update entities when you want to
     * preserve the lastUpdate time stamp.
     **/
    @Ignore
    public transient boolean keepUpdateTimestamp;

    /**
     * ID of the student.
     *
     * @see #makeId()
     */
    @Id
    private String id;

    private Date createdAt;

    private Date updatedAt;

    private transient String registrationKey;

    /**
     * The student's Google ID. Links to the Account object.
     * This can be null if the student hasn't joined the course yet.
     */
    @SerializedName("google_id")
    private String googleId;

    @SerializedName("email")
    private String email;

    /**
     * The student's Course ID. References the primary key of the course.
     */
    @SerializedName("coursename")
    private String courseId;

    @Unindex
    @SerializedName("name")
    private String name;

    @Unindex
    @SerializedName("lastName")
    private String lastName;

    @Unindex
    private String comments;

    @SerializedName("teamname")
    private String teamName;

    @SerializedName("sectionname")
    private String sectionName;

    @SuppressWarnings("unused")
    private CourseStudent() {
        // required by Objectify
    }

    public CourseStudent(String email, String name, String googleId, String comments, String courseId,
                         String teamName, String sectionName) {
        setEmail(email);
        setName(name);
        setGoogleId(googleId);
        setComments(comments);
        setCourseId(courseId);
        setTeamName(teamName);
        setSectionName(sectionName);

        setCreatedAt(Instant.now());

        this.id = makeId();
        registrationKey = generateRegistrationKey();
    }

    private String makeId() {
        return getEmail() + '%' + getCourseId();
    }

    public Instant getCreatedAt() {
        return TimeHelper.convertDateToInstant(createdAt);
    }

    public void setCreatedAt(Instant created) {
        this.createdAt = TimeHelper.convertInstantToDate(created);
        setLastUpdate(created);
    }

    public Instant getUpdatedAt() {
        return TimeHelper.convertDateToInstant(updatedAt);
    }

    public void setLastUpdate(Instant updatedAt) {
        if (!keepUpdateTimestamp) {
            this.updatedAt = TimeHelper.convertInstantToDate(updatedAt);
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

    @OnSave
    public void updateLastUpdateTimestamp() {
        this.setLastUpdate(Instant.now());
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
