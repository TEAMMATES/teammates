package teammates.test.cases.pagedata;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.InstructorCourseStudentDetailsEditPageData;
import teammates.ui.template.StudentInfoTable;

/**
 * SUT: {@link InstructorCourseStudentDetailsEditPageData}.
 */
public class InstructorCourseStudentDetailsEditPageDataTest extends BaseTestCase {

    private StudentAttributes inputStudent;
    private boolean hasSection = true;
    private boolean isOpenOrPublishedEmailSentForTheCourse;

    @Test
    public void allTests() {
        InstructorCourseStudentDetailsEditPageData data = createData();

        ______TS("With no student profile (Details edit shows only the info table)");
        assertNull(data.getStudentProfile());

        StudentInfoTable studentInfoTable = data.getStudentInfoTable();
        assertNotNull(studentInfoTable);

        assertEquals(inputStudent.name, studentInfoTable.getName());
        assertEquals(inputStudent.email, studentInfoTable.getEmail());
        assertEquals(inputStudent.section, studentInfoTable.getSection());
        assertEquals(inputStudent.team, studentInfoTable.getTeam());
        assertEquals(inputStudent.comments, studentInfoTable.getComments());
        assertEquals(inputStudent.course, studentInfoTable.getCourse());
        assertEquals(hasSection, studentInfoTable.getHasSection());
    }

    private InstructorCourseStudentDetailsEditPageData createData() {
        String name = "John Doe";
        String email = "john@doe.com";

        createStudent(name, email);

        return new InstructorCourseStudentDetailsEditPageData(AccountAttributes.builder().build(),
                dummySessionToken, inputStudent, email, hasSection, isOpenOrPublishedEmailSentForTheCourse);
    }

    private void createStudent(String name, String email) {
        String comments = "This is a comment for John Doe.";
        String courseId = "CourseForJohnDoe";
        String team = "TeamForJohnDoe";
        String section = "SectionForJohnDoe";

        inputStudent = StudentAttributes
                .builder(courseId, name, email)
                .withSection(section)
                .withTeam(team)
                .withComments(comments)
                .build();
    }

}
