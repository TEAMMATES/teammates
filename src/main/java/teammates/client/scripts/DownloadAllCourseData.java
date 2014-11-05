package teammates.client.scripts;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course;

public class DownloadAllCourseData extends OfflineBackup {

   
    
    public static void main(String[] args) throws IOException {
        DownloadAllCourseData downloadAllCourseData = new DownloadAllCourseData();
        downloadAllCourseData.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize();
        
        backupFileDirectory = "Backup/" + getCurrentDateAndTime();
        createBackupDirectory(backupFileDirectory);
        retrieveEntitiesByCourse(getCoursesToBackup());
    }
    
    @SuppressWarnings("unchecked")
    private Set<String> getCoursesToBackup() {
        String q = "SELECT FROM " + Course.class.getName();
        List<Course> courses = (List<Course>) Datastore.getPersistenceManager().newQuery(q).execute();
        
        Set<String> allCourses = new HashSet<String>();
        
        for(Course course : courses) {
            allCourses.add(course.getUniqueId());
        }
        return allCourses;
    }
}
