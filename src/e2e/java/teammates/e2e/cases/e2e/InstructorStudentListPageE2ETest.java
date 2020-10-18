package teammates.e2e.cases.e2e;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseEnrollPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.e2e.pageobjects.InstructorStudentListPage;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_LIST_PAGE}.
 */
public class InstructorStudentListPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorStudentListPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {

        ______TS("verify loaded data");

        InstructorAttributes instructor = testData.instructors.get("instructorOfCourse1");
        String instructorId = instructor.googleId;

        AppUrl listPageUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);
        InstructorStudentListPage listPage = loginAdminToPage(listPageUrl, InstructorStudentListPage.class);

        CourseAttributes course1 = testData.courses.get("course1");
        CourseAttributes course2 = testData.courses.get("course2");
        CourseAttributes course3 = testData.courses.get("course3");
        String course1Header = createHeaderText(course1);
        String course2Header = createHeaderText(course2);
        String course3Header = createHeaderText(course3);

        StudentAttributes[] studentsInCourse1 = {};

        // Note: by right, there should not be any student shown here as the instructor does not have sufficient privilege
        // However, due to issue #8000, the students will be listed anyway
        StudentAttributes[] studentsInCourse2 = {
                testData.students.get("Student1Course2"),
                testData.students.get("Student2Course2"),
                testData.students.get("Student3Course2"),
        };

        StudentAttributes[] studentsInCourse3 = {
                testData.students.get("Student1Course3"),
                testData.students.get("Student2Course3"),
                testData.students.get("Student3Course3"),
                testData.students.get("Student4Course3"),
        };

        Map<String, StudentAttributes[]> courseHeaderToStudents = new HashMap<>();
        courseHeaderToStudents.put(course1Header, studentsInCourse1);
        courseHeaderToStudents.put(course2Header, studentsInCourse2);
        courseHeaderToStudents.put(course3Header, studentsInCourse3);

        // Expand all headers first

        for (String header : courseHeaderToStudents.keySet()) {
            listPage.clickCourseTabHeader(header);
        }

        listPage.verifyStudentDetails(courseHeaderToStudents);

        ______TS("link: enroll page");

        InstructorCourseEnrollPage enrollPage = listPage.clickEnrollStudents(course3Header);
        enrollPage.verifyIsCorrectPage(course3.getId());

        listPage = loginAdminToPage(listPageUrl, InstructorStudentListPage.class);
        listPage.clickCourseTabHeader(course3Header);

        ______TS("link: view student details page");

        StudentAttributes studentToView = testData.students.get("Student1Course3");
        String studentEmail = studentToView.getEmail();

        InstructorCourseStudentDetailsViewPage studentDetailsViewPage =
                listPage.clickViewStudent(course3Header, studentEmail);
        studentDetailsViewPage.verifyIsCorrectPage(course3.getId(), studentEmail);
        studentDetailsViewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: edit student details page");

        InstructorCourseStudentDetailsEditPage studentDetailsEditPage =
                listPage.clickEditStudent(course3Header, studentEmail);
        studentDetailsEditPage.verifyIsCorrectPage(course3.getId(), studentEmail);
        studentDetailsEditPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: view all records page");

        InstructorStudentRecordsPage studentRecordsPage =
                listPage.clickViewAllRecords(course3Header, studentEmail);
        studentRecordsPage.verifyIsCorrectPage(course3.getId(), studentToView.getName());
        studentRecordsPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("action: delete student");

        StudentAttributes studentToDelete = testData.students.get("Student3Course3");

        listPage.deleteStudent(course3Header, studentToDelete.getEmail());

        StudentAttributes[] studentsAfterDelete = {
                testData.students.get("Student1Course3"),
                testData.students.get("Student2Course3"),
                testData.students.get("Student4Course3"),
        };

        listPage.verifyStudentDetails(course3Header, studentsAfterDelete);
        verifyAbsentInDatastore(studentToDelete);

    }

    private String createHeaderText(CourseAttributes course) {
        return String.format("[%s]: %s", course.getId(), course.getName());
    }

}
