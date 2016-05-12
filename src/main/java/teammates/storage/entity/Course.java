package teammates.storage.entity;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a course entity. 
 */
@PersistenceCapable
public class Course {
    @PrimaryKey
    @Persistent
    @SerializedName("id")
    private String ID;

    @Persistent
    private String name;

    @Persistent
    private Date createdAt;
    
    @Persistent
    private Boolean archiveStatus;


    public Course(final String courseId, final String courseName, final Boolean archiveStatus, final Date createdAt) {
        this.setUniqueId(courseId);
        this.setName(courseName);
        if (createdAt == null) {
            this.setCreatedAt(new Date());
        } else {
            this.setCreatedAt(createdAt);            
        }
        this.setArchiveStatus(archiveStatus);
    }

    public String getUniqueId() {
        return ID;
    }

    public void setUniqueId(final String uniqueId) {
        this.ID = uniqueId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name.trim();
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getArchiveStatus() {
        return archiveStatus;
    }
    
    public void setArchiveStatus(final Boolean status) {
        this.archiveStatus = status;
    }
}
