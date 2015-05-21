package teammates.storage.entity;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import teammates.common.util.StringHelper;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.annotations.SerializedName;

/**
 * An association class that represents the association Account -->
 * [enrolled in] --> Course.
 */
@PersistenceCapable
public class Student {
    // TODO: some of the serialized names are not correct.

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private transient Long registrationKey = null;

    /**
     * The student's Google ID. Used as the foreign key for the Account object.
     * This can be null/empty if the student's hasn't joined the course yet.
     */
    @Persistent
    @SerializedName("google_id")
    private String ID = null;

    /**
     * The email used to contact the student regarding this course.
     */
    @Persistent
    @SerializedName("email")
    private String email;

    /**
     * The student's Course ID. References the primary key of the course.
     * This shows the course the student is taking.
     */
    @Persistent
    @SerializedName("coursename")
    private String courseID;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("name")
    private String name = null;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    @SerializedName("lastName")
    private String lastName = null;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String comments = null;

    @Persistent
    @SerializedName("teamname")
    private String teamName = null;

    @Persistent
    @SerializedName("sectionname")
    private String sectionName = null;

    /**
     * 
     * @param email
     *            Student's email used for this course.
     * @param name
     *            Student name.
     * @param lastName
     *            Student last name
     * @param googleId
     *            Student's Google Id. Can be null/empty if the student hasn't
     *            registered yet.
     * @param comments
     *            Comments about the student.
     * @param courseId
     * @param teamName
     */
    public Student(String email, String name, String googleId, String comments, String courseId,
                   String teamName, String sectionName) {
        this.setEmail(email);
        this.setName(name);
        this.setGoogleId(googleId);
        this.setComments(comments);
        this.setCourseId(courseId);
        this.setTeamName(teamName);
        this.setSectionName(sectionName);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getGoogleId() {
        return ID;
    }

    public void setGoogleId(String googleId) {
        this.ID = (googleId == null ? null : googleId.trim());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.trim();
        String processedFullName = StringHelper.splitName(name)[2];
        this.name = processedFullName.trim();
        this.setLastName(StringHelper.splitName(name)[1]);
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

    // null comment setting are not tested
    public void setComments(String comments) {
        this.comments = (comments == null ? null : comments.trim());
    }

    public Long getRegistrationKey() {
        return registrationKey;
    }

    public String getCourseId() {
        return courseID;
    }

    public void setCourseId(String courseId) {
        this.courseID = courseId.trim();
    }

    public String getTeamName() {
        return teamName;
    }

    // null team name setting are not tested
    public void setTeamName(String teamName) {
        this.teamName = (teamName == null ? null : teamName.trim());
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = (sectionName == null ? null : sectionName.trim());
    }

    // not tested as this is part of client script
    public boolean isRegistered() {
        // Null or "" => unregistered
        return ID != null && !ID.isEmpty();
    }

    public static String getStringKeyForLongKey(long longKey) {
        return KeyFactory.createKeyString(Student.class.getSimpleName(), longKey);
    }
}
