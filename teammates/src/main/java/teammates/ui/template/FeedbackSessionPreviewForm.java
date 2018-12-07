package teammates.ui.template;

import java.util.List;

/**
 * Data model for the form for previewing a feedback session as a student or instructor.
 */
public class FeedbackSessionPreviewForm {

    private String courseId;
    private String fsName;

    private List<ElementTag> studentToPreviewAsOptions;
    private List<ElementTag> instructorToPreviewAsOptions;

    public FeedbackSessionPreviewForm(String courseId, String fsName, List<ElementTag> studentList,
                                      List<ElementTag> instructorList) {
        this.courseId = courseId;
        this.fsName = fsName;
        this.studentToPreviewAsOptions = studentList;
        this.instructorToPreviewAsOptions = instructorList;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFsName() {
        return fsName;
    }

    public void setFsName(String fsName) {
        this.fsName = fsName;
    }

    public List<ElementTag> getStudentToPreviewAsOptions() {
        return studentToPreviewAsOptions;
    }

    public void setStudentToPreviewAsOptions(List<ElementTag> studentToPreviewAsOptions) {
        this.studentToPreviewAsOptions = studentToPreviewAsOptions;
    }

    public List<ElementTag> getInstructorToPreviewAsOptions() {
        return instructorToPreviewAsOptions;
    }

    public void setInstructorToPreviewAsOptions(List<ElementTag> instructorToPreviewAsOptions) {
        this.instructorToPreviewAsOptions = instructorToPreviewAsOptions;
    }

}
