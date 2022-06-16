package teammates.storage.entity;

import java.security.SecureRandom;
import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Translate;
import com.googlecode.objectify.annotation.Unindex;

import teammates.common.util.StringHelper;

/**
 * An association class that represents the association Account -->
 * [enrolled in] --> Course.
 */
@Entity
@Index
public class CourseStudent extends BaseEntity {
    /**
     * ID of the student.
     *
     * @see #generateId(String, String)
     */
    @Id
    private String id;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant updatedAt;

    private transient String registrationKey;

    /**
     * The student's Google ID. Links to the Account object.
     * This can be null if the student hasn't joined the course yet.
     */
    private String googleId;

    private String email;

    private String courseId;

    @Unindex
    private String name;

    @Unindex
    private String comments;

    private String teamName;

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

        this.id = generateId(getEmail(), getCourseId());
        setRegistrationKey(generateRegistrationKey());
    }

    /**
     * Generates an unique ID for the student.
     */
    public static String generateId(String email, String courseId) {
        return email + '%' + courseId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the createdAt timestamp.
     */
    public void setCreatedAt(Instant created) {
        this.createdAt = created;
        setLastUpdate(created);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setLastUpdate(Instant updatedAt) {
        this.updatedAt = updatedAt;
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

    /**
     * Sets the full name of the student.
     */
    public void setName(String name) {
        this.name = name.trim();
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

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
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
     * Updates the updatedAt timestamp when saving.
     */
    @OnSave
    public void updateLastUpdateTimestamp() {
        this.setLastUpdate(Instant.now());
    }

    /**
     * Returns unique registration key for the student.
     */
    private String generateRegistrationKey() {
        String uniqueId = getUniqueId();
        assert uniqueId != null;

        SecureRandom prng = new SecureRandom();

        return StringHelper.encrypt(uniqueId + "%" + prng.nextInt());
    }
}
