package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.jdo.PersistenceManager;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Student;

/**
 * Adds sections to large courses without sections. For use after migrating evaluations
 * to feedback sessions. Handles updating sections in responses, but not comments. 
 * 
 */
public class AddSectionsToLargeCourses extends RemoteApiClient {
    
    private Logic logic = new Logic();
    
    // modify this to modify only a specific course or all courses
    private final boolean isForAllCourses = false;
    
    // modify this to modify the max size of a course without a section
    // if numStudents in a course > maxCourseSizeWithoutSections,
    // then sections will be added to the course
    private final int maxCourseSizeWithoutSections = 100;
    
    // if not modifying all courses, specify which course to modify here
    private final String courseToAddSectionsTo = "demo-course";
    
    // when adding teams to a section, when this value is reached or exceeded,  
    // change the section for the next team
    private final int numOfStudentsInSection = 100;
    
    // modify for preview
    boolean isPreview = true;
    
    /*
     * IMPORTANT: *******************************
     * This script does not update FeedbackResponseComments because it was created
     * originally to deal with feedback responses migrated from legacy data (those
     * responses did not have comments)
     * ******************************************
     */
    
    
    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();
        
        AddSectionsToLargeCourses migrator = new AddSectionsToLargeCourses();
        migrator.doOperationRemotely();
        
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
    }
    
    @Override
    protected void doOperation() {
        if (isPreview) {
            System.out.println("In Preview Mode");
        }
        
        Datastore.initialize();
        
        Set<String> courses;
        if (isForAllCourses) {
            courses = getCourses();
        } else {
            courses = new HashSet<String>();
            courses.add(courseToAddSectionsTo);
        }
        
        courses = filterLargeCoursesWithoutSections(courses);
        
        for (String course : courses) {
            addSectionsToCourse(course);
        }
    }
    
    private Set<String> getCourses() {
        String q = "SELECT FROM " + Course.class.getName();
        @SuppressWarnings("unchecked")
        List<Course> courses = (List<Course>) Datastore.getPersistenceManager().newQuery(q).execute();
        
        Set<String> allCourses = new HashSet<String>();
        
        for(Course course : courses) {
            allCourses.add(course.getUniqueId());
        }
        return allCourses;
   }

    public Set<String> filterLargeCoursesWithoutSections(Set<String> courses) {
        Set<String> largeCoursesWithoutSections = new HashSet<String>();
        
        CourseDetailsBundle courseDetails = null;
        for (String courseId : courses) {

            try {
                courseDetails = logic.getCourseDetails(courseId);
            } catch (EntityDoesNotExistException e1) {
                System.out.println("Course not found" + courseId);
                e1.printStackTrace();
                continue;
            }
            
            boolean hasSection = courseDetails.stats.sectionsTotal != 0;
            boolean isCourseSizeSmall = courseDetails.stats.studentsTotal <= maxCourseSizeWithoutSections;
            if (!hasSection && !isCourseSizeSmall) {
                largeCoursesWithoutSections.add(courseId);
            }
        }
        
        return largeCoursesWithoutSections;
    }

    
    public void addSectionsToCourse(String courseId) {
        System.out.println("Course: [" + courseId + "] ");
        
        
        List<TeamDetailsBundle> teams;
        try {
            teams = logic.getTeamsForCourse(courseId);
        } catch (EntityDoesNotExistException e1) {
            System.out.println("ERROR Course not found" + courseId);
            e1.printStackTrace();
            return;
        }

        int numSections = 1;
        String sectionPrefix = "Section "; 
        String currentSection = sectionPrefix + numSections;
        
        int currentSectionSize = 0;
        
        for (TeamDetailsBundle team : teams) {
            String teamName = team.name;
            
            List<StudentAttributes> students = team.students;
            System.out.println("Students in team " + teamName + " : "+students.size());
            
            for (StudentAttributes student : students) {
                updateStudentSection(currentSection, student);
                
                currentSectionSize = currentSectionSize + 1;
            }
            
            if (currentSectionSize >= numOfStudentsInSection) {
                // increment section
                numSections++;
                currentSection = sectionPrefix + numSections; 
                currentSectionSize = 0;
            }
        }
        
        
        System.out.println();
    }

    private void updateStudentSection(String currentSection, StudentAttributes student) {
         
        try {
            System.out.println("Update " + student.email + " to section " + currentSection);
            if (isPreview) {
                return;
            } 
            
            PersistenceManager pm = Datastore.getPersistenceManager();
            
            Student studentEntity = getStudent(student.email, student.course, pm);
            updateStudentToBeInSection(studentEntity, currentSection);
            
            List<FeedbackResponse> responsesForStudent = getResponsesForStudent(student, pm);
            updateFeedbackResponsesToBeInSection(responsesForStudent, student, currentSection);
            
            pm.close();
            
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            System.out.println("ERROR failed to update student " + student.email);
            e.printStackTrace();
            
            confirmToContinue();
        }        
    }
    
    private Student getStudent(String email, String courseId, PersistenceManager pm) {
        
        String q = "SELECT FROM " + Student.class.getName() + " " +
                "WHERE email == emailParam && courseID == courseIdParam" + " " +
                "PARAMETERS String emailParam, String courseIdParam";
        @SuppressWarnings("unchecked")
        List<Student> studentList = (List<Student>) pm.newQuery(q).execute(email, courseId);
        
        return studentList.get(0);
    }
    
    private void updateStudentToBeInSection(Student student, String sectionToChangeTo) throws InvalidParametersException, EntityDoesNotExistException {
        if (isPreview) {
            return;
        }
        
        student.setSectionName(sectionToChangeTo);
    }
    

    private void updateFeedbackResponsesToBeInSection(List<FeedbackResponse> responses, StudentAttributes student, String sectionName) {
        if (isPreview) {
            return;
        }
        
        String studentEmail = student.email;
        String studentTeam = student.team;
        
        for (FeedbackResponse response : responses) {
            if (response.getRecipientEmail().equals(studentEmail) ||
                response.getRecipientEmail().equals(studentTeam)) {
                
                response.setRecipientSection(sectionName);
            } 
            
            if (response.getGiverEmail().equals(studentEmail)) {
                response.setGiverSection(sectionName);
            }
        }
        
        // note that comments are not updated
    }
    
    private List<FeedbackResponse> getResponsesForStudent(StudentAttributes student, PersistenceManager pm) {
        String studentEmail = student.email;
        String studentTeam = student.team;
        String course = student.course;
        
        String q = "SELECT FROM " + FeedbackResponse.class.getName() + " " +
                   "WHERE giverEmail == emailParam" + " " +
                   "&& courseId == courseParam" + " " +
                   "PARAMETERS String emailParam, String courseParam";
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> responsesAsGiver = (List<FeedbackResponse>) pm.newQuery(q).execute(studentEmail, course);
        
        q = "SELECT FROM " + FeedbackResponse.class.getName() + " " +
            "WHERE (receiver == emailParam" + " " +
            "|| receiver == teamParam)" + " " +
            "&& courseId == courseParam" + " " +
            "PARAMETERS String emailParam, String teamParam, String courseParam";
     
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> responsesAsReceiver = (List<FeedbackResponse>) Datastore.getPersistenceManager().newQuery(q).execute(studentEmail, studentTeam, course);
        
        List<FeedbackResponse> responses = new ArrayList<FeedbackResponse>(); 
        responses.addAll(responsesAsGiver);
        responses.addAll(responsesAsReceiver);
        
        return responses;
    }
    
    private void confirmToContinue() {
        System.out.println("An error occurred, continue?");
        Scanner s = new Scanner(System.in);
        s.next();
        s.close();
    }
    
}
