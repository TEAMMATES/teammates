package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.ui.template.CourseInstructorCopyTable;
import teammates.ui.template.CourseInstructorCopyTableRow;

/**
 * The data model for the table of copiable instructor loaded in the copy instructor modal.
 */
public class InstructorCourseInstructorCopyPageData extends PageData {

    private final List<InstructorAttributes> instructors;

    public InstructorCourseInstructorCopyPageData(
            AccountAttributes account, String sessionToken, List<InstructorAttributes> copiableInstructors) {
        super(account, sessionToken);
        instructors = copiableInstructors;
    }

    public CourseInstructorCopyTable getCopyInstructorForm() {
        List<CourseInstructorCopyTableRow> copyInstructorModalRows = buildCopyInstructorModalRows(instructors);
        return new CourseInstructorCopyTable(copyInstructorModalRows);
    }

    private List<CourseInstructorCopyTableRow> buildCopyInstructorModalRows(
            List<InstructorAttributes> copiableInstructors) {
        List<CourseInstructorCopyTableRow> copyInstructorRows = new ArrayList<>();

        for (InstructorAttributes instructor : copiableInstructors) {
            String courseId = instructor.getCourseId();
            String name = instructor.getName();
            String accessLevel = instructor.getRole();
            String displayedName = instructor.getDisplayedName();
            String email = instructor.getEmail();

            CourseInstructorCopyTableRow row =
                    new CourseInstructorCopyTableRow(courseId, name, accessLevel, displayedName, email);
            copyInstructorRows.add(row);
        }

        return copyInstructorRows;
    }
}
