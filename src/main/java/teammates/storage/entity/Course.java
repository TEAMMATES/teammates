package teammates.storage.entity;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import com.google.gson.annotations.SerializedName;

import teammates.common.util.Const;

/**
 * Represents a course entity.
 */
@Entity
@Index
public class Course extends BaseEntity {

    /**
     * The name of the primary key of this entity type.
     */
    @Ignore
    public static final String PRIMARY_KEY_NAME = getFieldWithPrimaryKeyAnnotation(Course.class);

    @Id
    @SerializedName("id")
    // CHECKSTYLE.OFF:AbbreviationAsWordInName|MemberName the database uses ID
    private String ID;
    // CHECKSTYLE.ON:AbbreviationAsWordInName|MemberName

    private String name;

    private Date createdAt;

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
