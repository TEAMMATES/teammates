package teammates.ui.template;

import java.util.List;

public class InstructorStudentListFilterBox {

    private List<InstructorStudentListFilterCourse> courses;
    private boolean shouldDisplayArchive;

    public InstructorStudentListFilterBox(List<InstructorStudentListFilterCourse> courses,
                                          boolean displayArchive) {
        this.courses = courses;
        this.shouldDisplayArchive = displayArchive;
    }

    public List<InstructorStudentListFilterCourse> getCourses() {
        return courses;
    }

    public boolean isDisplayArchive() {
        return shouldDisplayArchive;
    }

}
