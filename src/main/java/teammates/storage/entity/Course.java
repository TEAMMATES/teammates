package teammates.storage.entity;

import java.util.Date;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import teammates.common.util.Const;

/**
 * Represents a course entity.
 */
@PersistenceCapable
public class Course extends Entity {

    /**
     * The name of the primary key of this entity type.
     */
    @NotPersistent
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(Course.class);

    @PrimaryKey
    @Persistent
    @SerializedName("id")
    // CHECKSTYLE.OFF:AbbreviationAsWordInName|MemberName the database uses ID
    private String ID;
    // CHECKSTYLE.ON:AbbreviationAsWordInName|MemberName

    @Persistent
    private String name;

    @Persistent
    private Date createdAt;

    @Persistent
    private String timeZone;

    public Course(String courseId, String courseName, String courseTimeZone, Date createdAt) {
        this.setUniqueId(courseId);
        this.setName(courseName);
        if (courseTimeZone == null) {
            this.setTimeZone(Const.DEFAULT_TIMEZONE);
        } else {
            this.setTimeZone(courseTimeZone);
        }
        if (createdAt == null) {
            this.setCreatedAt(new Date());
        } else {
            this.setCreatedAt(createdAt);
        }
    }

    public String getUniqueId() {
        return ID;
    }

    public void setUniqueId(String uniqueId) {
        this.ID = uniqueId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
