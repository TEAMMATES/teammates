package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;
import teammates.common.util.FieldValidator.FieldType;
import teammates.storage.entity.Course;

/**
 * The data transfer object for Course entities.
 */
public class CourseAttributes extends EntityAttributes implements Comparable<CourseAttributes> {
    
    //Note: be careful when changing these variables as their names are used in *.json files.
    public String id;
    public String name;
    public Date createdAt;
    public boolean isArchived;
    
    public CourseAttributes() {
    }

    public CourseAttributes(String courseId, String name) {
        this.id = Sanitizer.sanitizeTitle(courseId);
        this.name = Sanitizer.sanitizeTitle(name);
        this.isArchived = false;
    }
    
    public CourseAttributes(String courseId, String name, boolean archiveStatus) {
        this.id = Sanitizer.sanitizeTitle(courseId);
        this.name = Sanitizer.sanitizeTitle(name);
        this.isArchived = archiveStatus;
    }

    public CourseAttributes(Course course) {
        this.id = course.getUniqueId();
        this.name = course.getName();
        this.createdAt = course.getCreatedAt();
        
        Boolean status = course.getArchiveStatus();
        if (status == null) {
            this.isArchived = false;
        } else {
            this.isArchived = status.booleanValue(); 
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public List<String> getInvalidityInfo() {
        
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        error= validator.getInvalidityInfo(FieldType.COURSE_ID, id);
        if (!error.isEmpty()) { 
            errors.add(error); 
        }
        
        error= validator.getInvalidityInfo(FieldType.COURSE_NAME, name);
        if (!error.isEmpty()) { 
            errors.add(error); 
        }
        
        return errors;
    }

    public Course toEntity() {
        return new Course(id, name, Boolean.valueOf(isArchived), createdAt);
    }

    public String toString() {
        return "[" + CourseAttributes.class.getSimpleName() + "] id: " + id + " name: " + name + " isArchived: " 
               + isArchived;
    }

    @Override
    public String getIdentificationString() {
        return id;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Course";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + id;
    }
    
    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, CourseAttributes.class);
    }
    
    @Override
    public void sanitizeForSaving() {
        id = Sanitizer.sanitizeForHtml(id);
        name = Sanitizer.sanitizeForHtml(name);
    }

    @Override
    public int compareTo(CourseAttributes o) {
        if(o == null){
            return 0;
        }
        return o.createdAt.compareTo(createdAt);
    }
    
    public static void sortByCreatedDate(List<CourseAttributes> courses) {
        Collections.sort(courses, createdDateComparator);
    }
    
    private static Comparator<CourseAttributes> createdDateComparator = new Comparator<CourseAttributes>() {
        public int compare(CourseAttributes course1, CourseAttributes course2) {
            if (course1.createdAt.compareTo(course2.createdAt) == 0) {
                return course1.id.compareTo(course2.id);
            }
            
            // sort by newest course first
            return -1 * course1.createdAt.compareTo(course2.createdAt);
        }
    };
}
