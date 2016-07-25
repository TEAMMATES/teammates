package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentWithOldRegistrationKeyAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.storage.api.StudentsDb;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Student;

public class DataMigrationForStudentToCourseStudent extends RemoteApiClient {
    
    private static final boolean isPreview = true;
    private static final int numDays = 100;
    private static final String courseId = "";
    
    private enum ScriptTarget {
        BY_TIME, BY_COURSE, ALL;
    }
    
    ScriptTarget target = ScriptTarget.BY_TIME;
    
    private StudentsDb studentsDb = new StudentsDb();
    
    public static void main(String[] args) throws IOException {
        new DataMigrationForStudentToCourseStudent().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();

        List<StudentAttributes> students;
        if (target == ScriptTarget.BY_TIME) {
            Calendar startCal = Calendar.getInstance();
            startCal.add(Calendar.DAY_OF_YEAR, -1 * numDays);
            
            students = getOldStudentsSince(startCal.getTime());
            
        } else if (target == ScriptTarget.BY_COURSE) {
            students = getOldStudentsForCourse(courseId);
            
        } else if (target == ScriptTarget.ALL) {
            students = getOldStudents();
            
        } else {
            students = null;
            Assumption.fail("no target selected");
        }
        
        if (isPreview) {
            System.out.println("Creating a CourseStudent copy of students ...");
        }
        
        for (StudentAttributes student : students) {
            StudentWithOldRegistrationKeyAttributes studentToSave =
                    studentsDb.getStudentForCopyingToCourseStudent(student.course, student.email);
            
            if (isPreview) {
                System.out.println("Preview: copying " + studentToSave.getIdentificationString());
            } else {
                try {
                    studentsDb.createEntityWithoutExistenceCheck(studentToSave);
                    System.out.println("Created CourseStudent for " + studentToSave.getIdentificationString());
                } catch (InvalidParametersException e) {
                    System.out.println("Failed to create CourseStudent " + studentToSave.getIdentificationString());
                    e.printStackTrace();
                    break;
                }
            }
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
    private List<StudentAttributes> getOldStudents() {
        return studentsDb.getAllOldStudents();
    }

    protected PersistenceManager getPm() {
        return Datastore.getPersistenceManager();
    }

}
