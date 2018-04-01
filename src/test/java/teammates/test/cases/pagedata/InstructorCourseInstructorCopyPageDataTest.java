package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.InstructorCourseInstructorCopyPageData;
import teammates.ui.template.CourseInstructorCopyTable;

/**
 * SUT: {@link teammates.ui.pagedata.InstructorCourseInstructorCopyPageData}.
 */
public class InstructorCourseInstructorCopyPageDataTest extends BaseTestCase {

    private static DataBundle dataBundle = getTypicalDataBundle();

    @Test
    public void allTests() {
        ______TS("Typical case");

        List<InstructorAttributes> copiableInstructors = new ArrayList<>();
        copiableInstructors.addAll(dataBundle.instructors.values());

        InstructorCourseInstructorCopyPageData data = new InstructorCourseInstructorCopyPageData(
                dataBundle.accounts.get("instructor1OfCourse1"), dummySessionToken, copiableInstructors);
        CourseInstructorCopyTable copyForm = data.getCopyInstructorForm();
        assertEquals(dataBundle.instructors.size(), copyForm.getInstructorRows().size());
    }
}
