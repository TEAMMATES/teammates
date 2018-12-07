package teammates.ui.template;

import java.util.List;

/**
 * A table contains details of students in a course whose student details contain the search
 * keyword entered by the instructor.
 */
public class SearchStudentsTable {
    private String courseId;
    private List<StudentListSectionData> sections;
    private boolean hasSection;

    public SearchStudentsTable(String courseId, List<StudentListSectionData> sections) {
        this.courseId = courseId;
        this.sections = sections;
        if (sections.size() == 1) {
            StudentListSectionData section = sections.get(0);
            this.hasSection = !"None".equals(section.getSectionName());
        } else {
            this.hasSection = true;
        }
    }

    public String getCourseId() {
        return courseId;
    }

    public List<StudentListSectionData> getSections() {
        return sections;
    }

    public boolean isHasSection() {
        return hasSection;
    }

}
