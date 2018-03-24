package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.ui.template.CourseInstructorCopyTable;
import teammates.ui.template.CourseInstructorCopyTableRow;

public class InstructorCourseInstructorCopyPageData extends PageData{

    private final List<InstructorAttributes> instructors;

    public InstructorCourseInstructorCopyPageData(
            AccountAttributes account, String sessionToken, List<InstructorAttributes> copiableInstructors) {
        super(account, sessionToken);
        instructors = copiableInstructors;
    }

    public CourseInstructorCopyTable getCopyQnForm() {
        List<CourseInstructorCopyTableRow> copyQuestionRows = buildCopyInstructorModalRows(instructors);
        return new CourseInstructorCopyTable(copyQuestionRows);
    }

    private List<CourseInstructorCopyTableRow> buildCopyInstructorModalRows(
            List<InstructorAttributes> copiableInstructors) {
        List<CourseInstructorCopyTableRow> copyInstructorRows = new ArrayList<>();

        for (InstructorAttributes instructor : copiableInstructors) {
            String courseId = instructor.getCourseId();
            String name = instructor.getName();
            String displayedName = instructor.getDisplayedName();
            String email = instructor.getEmail();

            CourseInstructorCopyTableRow row =
                    new CourseInstructorCopyTableRow(courseId, name, displayedName, email);
            copyInstructorRows.add(row);
        }

        return copyInstructorRows;
    }
}
