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


    public Course(String courseId, String courseName, Boolean archiveStatus, Date createdAt) {
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
    
    public Boolean getArchiveStatus() {
        return archiveStatus;
    }
    
    public void setArchiveStatus(Boolean status) {
        this.archiveStatus = status;
    }
}
