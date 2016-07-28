package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.storage.api.StudentsDb;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Student;

public class DataMigrationForStudentToCourseStudent extends RemoteApiClient {
    
    private static final boolean isPreview = true;
    
    // When using ScriptTarget.BY_TIME, numDays can be changed to target
    // students created in the past number of days
    private static final int numDays = 100;
    
    // When using ScriptTarget.BY_COURSE, specify the course to target with courseId
    private static final String courseId = "";
    
    /**
     * BY_TIME: migration will affect students created in the past {@code numDays} days
     * BY_COURSE: migration will affects students in the specified {@code courseId}
     * ALL: all students will be migrated
     */
    private enum ScriptTarget {
        BY_TIME, BY_COURSE, ALL;
    }
    
    private ScriptTarget target = ScriptTarget.BY_TIME;
    
    private StudentsDb studentsDb = new StudentsDb();
    
    public static void main(String[] args) throws IOException {
        new DataMigrationForStudentToCourseStudent().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();

        List<StudentAttributes> students = getOldStudentsForMigration(target);
        
        System.out.println("Creating CourseStudent copies of students ...");
        
        System.out.println("Total size is " + students.size());
        for (StudentAttributes student : students) {
            if (isPreview) {
                System.out.println("Preview: will copy " + student.course + "/" + student.email);
            } else {
                // if an existing CourseStudent already exists, this overwrites it
                studentsDb.copyStudentToCourseStudent(student.course, student.email);
            }
        }
    }
    
    private List<StudentAttributes> getOldStudentsForMigration(ScriptTarget target) {
        switch (target) {
        case BY_TIME:
            Calendar startCal = Calendar.getInstance();
            startCal.add(Calendar.DAY_OF_YEAR, -1 * numDays);
            
            return getOldStudentsSince(startCal.getTime());
        case BY_COURSE:
            return getOldStudentsForCourse(courseId);
        case ALL:
            return getAllOldStudents();
        default:
            Assumption.fail("no target selected");
            return null;
        }
    }

    private List<StudentAttributes> getOldStudentsSince(Date date) {
        String query = "SELECT FROM " + Student.class.getName()
                + " WHERE createdAt >= startDate"
                + " PARAMETERS java.util.Date startDate";
        @SuppressWarnings("unchecked")
        List<Student> oldStudents =
                (List<Student>) Datastore.getPersistenceManager().newQuery(query).execute(date);
        return getListOfStudentAttributes(oldStudents);
    }

    private List<StudentAttributes> getOldStudentsForCourse(String courseId) {
        Query q = Datastore.getPersistenceManager().newQuery(Student.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseID == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Student> oldStudents = (List<Student>) q.execute(courseId);
        
        return getListOfStudentAttributes(oldStudents);
    }
    
    private List<StudentAttributes> getListOfStudentAttributes(List<Student> oldStudents) {
        List<StudentAttributes> students = new ArrayList<>();
        for (Student oldStudent : oldStudents) {
            students.add(new StudentAttributes(oldStudent));
        }
        return students;
    }

    @SuppressWarnings("deprecation")
    private List<StudentAttributes> getAllOldStudents() {
        return studentsDb.getAllOldStudents();
    }

}
