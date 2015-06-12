package teammates.ui.template;

import java.util.List;

public class InstructorStudentsListFilterBox {
    
    private List<InstructorStudentsListFilterCourses> courses;
    private boolean displayArchive;
    
    public InstructorStudentsListFilterBox(List<InstructorStudentsListFilterCourses> courses,
                                           boolean displayArchive) {
        this.courses = courses;
        this.displayArchive = displayArchive;
    }
    
    public List<InstructorStudentsListFilterCourses> getCourses() {
        return courses;
    }

    public boolean isDisplayArchive() {
        return displayArchive;
    }

}
