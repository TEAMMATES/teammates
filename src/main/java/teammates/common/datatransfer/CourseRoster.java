package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains a list of students and instructors in a course. Useful for caching
 * a copy of student and instructor details of a course instead of reading 
 * them from the database multiple times.
 */
public class CourseRoster {
    
    Map<String, StudentAttributes> studentListByEmail = new HashMap<String, StudentAttributes>();
    Map<String, InstructorAttributes> instructorListByEmail = new HashMap<String, InstructorAttributes>();
    
    public CourseRoster(List<StudentAttributes> students, List<InstructorAttributes> instructors){
        populateStuentListByEmail(students);
        populateInstructorListByEmail(instructors);
    }
    
    public List<StudentAttributes> getStudents(){
        return new ArrayList<StudentAttributes>(studentListByEmail.values());
    }
    
    public List<InstructorAttributes> getInstructors(){
        return new ArrayList<InstructorAttributes>(instructorListByEmail.values());
    }
    
    public boolean isStudentInCourse(String studentEmail){
        return studentListByEmail.containsKey(studentEmail);
    }
    
    public boolean isStudentInTeam(String studentEmail, String targetTeamName){
        StudentAttributes student = studentListByEmail.get(studentEmail);
        return (student != null) && (student.team.equals(targetTeamName));
    }
    
    public boolean isStudentsInSameTeam(String studentEmail1, String studentEmail2){
        StudentAttributes student1 = studentListByEmail.get(studentEmail1);
        StudentAttributes student2 = studentListByEmail.get(studentEmail2);
        return (student1 != null) && (student2 != null) 
                && (student1.team != null) && (student1.team.equals(student2.team));
    }
    
    public StudentAttributes getStudentForEmail(String email) {
        return studentListByEmail.get(email);
    }

    public InstructorAttributes getInstructorForEmail(String email) {
        return instructorListByEmail.get(email);
    }
    
    private void populateStuentListByEmail(List<StudentAttributes> students) {
        
        if (students == null){
            return;
        }
        
        for (StudentAttributes s: students) {
            studentListByEmail.put(s.email, s);
        }
    }
    
    private void populateInstructorListByEmail(List<InstructorAttributes> instructors) {
        
        if (instructors == null){
            return;
        }
        
        for (InstructorAttributes i: instructors) {
            instructorListByEmail.put(i.email, i);
        }
    }

}
