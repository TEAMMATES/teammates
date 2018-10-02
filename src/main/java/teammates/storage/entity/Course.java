package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;

import teammates.common.util.Const;

/**
 * Represents a course entity.
 */
@Entity
@Index
public class Course extends BaseEntity {

    @Id
    private String id;

    private String name;

    @Translate(InstantTranslatorFactory.class)
    private Instant createdAt;

    @Translate(InstantTranslatorFactory.class)
    private Instant deletedAt;

    private String timeZone;

    @SuppressWarnings("unused")
    private Course() {
        // required by Objectify
    }

    public Course(String courseId, String courseName, String courseTimeZone, Instant createdAt, Instant deletedAt) {
        this.setUniqueId(courseId);
        this.setName(courseName);
        if (courseTimeZone == null) {
            this.setTimeZone(Const.DEFAULT_TIME_ZONE.getId());
        } else {
            this.setTimeZone(courseTimeZone);
        }
        if (createdAt == null) {
            this.setCreatedAt(Instant.now());
        } else {
            this.setCreatedAt(createdAt);
        }
        this.setDeletedAt(deletedAt);
    }

    public String getUniqueId() {
        return id;
    }

    public void setUniqueId(String uniqueId) {
        this.id = uniqueId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
