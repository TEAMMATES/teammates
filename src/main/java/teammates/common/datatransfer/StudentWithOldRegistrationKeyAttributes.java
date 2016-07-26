package teammates.common.datatransfer;

import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.Student;

public class StudentWithOldRegistrationKeyAttributes extends StudentAttributes {
    
    public StudentWithOldRegistrationKeyAttributes(Student old) {
        googleId = old.getGoogleId();
        name = old.getName();
        lastName = old.getLastName();
        email = old.getEmail();
        course = old.getCourseId();
        comments = old.getComments();
        team = old.getTeamName();
        section = old.getSectionName();
        key = old.getRegistrationKey();

        // copies the createdAt of the existing Student
        // updatedAt is set to the time when CourseStudent is written to the database
        createdAt = old.getCreatedAt();
    }
    
    public CourseStudent toEntity() {
        CourseStudent entity =
                new CourseStudent(email, name, googleId, comments, course, team, section);
        
        entity.setOldRegistrationKey(this.key);
        return entity;
    }
}
