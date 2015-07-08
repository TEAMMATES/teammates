package teammates.ui.template;

import java.util.List;

public class InstructorStudentListFilterBox {

    private List<InstructorStudentListFilterCourse> courses;
    private boolean displayArchive;

    public InstructorStudentListFilterBox(List<InstructorStudentListFilterCourse> courses,
                                          boolean displayArchive) {
        this.courses = courses;
        this.displayArchive = displayArchive;
    }

    public List<InstructorStudentListFilterCourse> getCourses() {
        return courses;
    }

    public boolean isDisplayArchive() {
        return displayArchive;
    }

}
