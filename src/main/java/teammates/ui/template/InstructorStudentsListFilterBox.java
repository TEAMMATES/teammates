package teammates.ui.template;

import java.util.List;

public class InstructorStudentsListFilterBox {
    
    private List<InstructorStudentsListFilterCourses> courses;
    private int numOfCourses;
    private boolean displayArchive;
    
    public InstructorStudentsListFilterBox(List<InstructorStudentsListFilterCourses> courses,
                                           boolean displayArchive) {
        this.courses = courses;
        this.numOfCourses = courses.size();
        this.displayArchive = displayArchive;
    }
    
    public List<InstructorStudentsListFilterCourses> getCourses() {
        return courses;
    }

    public int getNumOfCourses() {
        return numOfCourses;
    }

    public boolean isDisplayArchive() {
        return displayArchive;
    }

}
