package teammates.e2e.cases.sql;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseEnrollPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.e2e.pageobjects.InstructorStudentListPageSql;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_LIST_PAGE}.
 */
public class InstructorStudentListPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                        loadSqlDataBundle("/InstructorStudentListPageE2ETestSql.json"));
    }

    @Test
    @Override
    public void testAll() {

        ______TS("verify loaded data");

        Instructor instructor = testData.instructors.get("instructorOfCourse1");
        String instructorId = instructor.getGoogleId();

        AppUrl listPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_LIST_PAGE);
        InstructorStudentListPageSql listPage = loginToPage(listPageUrl, InstructorStudentListPageSql.class, instructorId);

        listPage.verifyAllCoursesHaveTabs(testData.courses.values());

        Course course1 = testData.courses.get("course1");
        Course course2 = testData.courses.get("course2");
        Course course3 = testData.courses.get("course3");

        // Expand all headers first

        listPage.clickCourseTabHeader(course1);
        listPage.clickCourseTabHeader(course2);
        listPage.clickCourseTabHeader(course3);

        Student[] studentsInCourse1 = {};

        Student[] studentsInCourse3 = {
                testData.students.get("Student1Course3"),
                testData.students.get("Student2Course3"),
                testData.students.get("Student3Course3"),
                testData.students.get("Student4Course3"),
        };

        Map<String, Student[]> courseIdToStudents = new HashMap<>();
        courseIdToStudents.put(course1.getId(), studentsInCourse1);
        courseIdToStudents.put(course3.getId(), studentsInCourse3);

        Map<String, Course> courseIdToCourse = new HashMap<>();
        courseIdToCourse.put(course1.getId(), course1);
        courseIdToCourse.put(course3.getId(), course3);

        listPage.verifyStudentDetails(courseIdToCourse, courseIdToStudents);
        listPage.verifyStudentDetailsNotViewable(course2);

        ______TS("link: enroll page");

        InstructorCourseEnrollPage enrollPage = listPage.clickEnrollStudents(course3);
        enrollPage.verifyIsCorrectPage(course3.getId());

        listPage = getNewPageInstance(listPageUrl, InstructorStudentListPageSql.class);
        listPage.clickCourseTabHeader(course3);

        ______TS("link: view student details page");

        Student studentToView = testData.students.get("Student1Course3");
        String studentEmail = studentToView.getEmail();

        InstructorCourseStudentDetailsViewPage studentDetailsViewPage =
                listPage.clickViewStudent(course3, studentEmail);
        studentDetailsViewPage.verifyIsCorrectPage(course3.getId(), studentEmail);
        studentDetailsViewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: edit student details page");

        InstructorCourseStudentDetailsEditPage studentDetailsEditPage =
                listPage.clickEditStudent(course3, studentEmail);
        studentDetailsEditPage.verifyIsCorrectPage(course3.getId(), studentEmail);
        studentDetailsEditPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: view all records page");

        InstructorStudentRecordsPage studentRecordsPage =
                listPage.clickViewAllRecords(course3, studentEmail);
        studentRecordsPage.verifyIsCorrectPage(course3.getId(), studentToView.getName());
        studentRecordsPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("action: delete student");

        Student studentToDelete = testData.students.get("Student3Course3");

        listPage.deleteStudent(course3, studentToDelete.getEmail());

        Student[] studentsAfterDelete = {
                testData.students.get("Student1Course3"),
                testData.students.get("Student2Course3"),
                testData.students.get("Student4Course3"),
        };

        listPage.verifyStudentDetails(course3, studentsAfterDelete);
        verifyAbsentInDatabase(studentToDelete);

    }

}
