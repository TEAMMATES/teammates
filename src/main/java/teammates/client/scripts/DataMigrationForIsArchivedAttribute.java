package teammates.client.scripts;

import java.io.IOException;
import java.util.List;
import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.api.CoursesDb;
import teammates.storage.datastore.Datastore;

/**
 * Script to set the isArchived attribute of instructors if the course's isArchived 
 * attribute is set.
 * 
 */
public class DataMigrationForIsArchivedAttribute extends RemoteApiClient {

    private Logic logic = new Logic();
    private CoursesDb coursesDb = new CoursesDb();
    
    public static void main(String[] args) throws IOException {
        DataMigrationForIsArchivedAttribute migrator = new DataMigrationForIsArchivedAttribute();
        migrator.doOperationRemotely();
    }
    
    @Override
    protected void doOperation() {
        Datastore.initialize();
        
        List<CourseAttributes> allCourses = getAllCourses();
        
        try {
            for (CourseAttributes course : allCourses) {
                if (course.isArchived) {
                    setInstructorsIsArchivedInCourse(course);
                }
            }
        } catch (EntityDoesNotExistException | InvalidParametersException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("deprecation")
    private List<CourseAttributes> getAllCourses() {
        return coursesDb.getAllCourses();
    }

    /**
     * For the given course, set the isArchived attribute of the instructors in the course 
     * @throws EntityDoesNotExistException 
     * @throws InvalidParametersException
     */
    private void setInstructorsIsArchivedInCourse(CourseAttributes course) throws InvalidParametersException, EntityDoesNotExistException {
        System.out.println("Updating instructors of course: " + course.id);
        
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(course.id);
        for (InstructorAttributes instructor: instructorList) {
            instructor.isArchived = true;
            logic.updateInstructorByEmail(instructor.email, instructor);    
            
            System.out.println("Successfully updated instructor: [" + instructor.email + "] " + instructor.name);
            
        }
        
    }

}
