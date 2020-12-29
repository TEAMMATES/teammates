package teammates.e2e.cases;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.e2e.pageobjects.InstructorSearchPage;
import teammates.e2e.pageobjects.InstructorSearchPage.CommentSearchResponseResult;
import teammates.e2e.pageobjects.InstructorSearchPage.CommentSearchSessionResult;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SEARCH_PAGE}.
 */
public class InstructorSearchPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorSearchPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {

        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        AppUrl searchPageUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_SEARCH_PAGE).withUserId(instructorId);

        InstructorSearchPage searchPage = loginAdminToPage(searchPageUrl, InstructorSearchPage.class);

        ______TS("cannot click search button if no checkbox is selected");

        searchPage.search(false, false, "anykeyword");

        ______TS("search with no result");

        searchPage.search(true, true, "thiswillnothitanything");
        searchPage.verifyStatusMessage("No results found.");

        ______TS("search for students");

        searchPage.search(true, false, "student2");

        CourseAttributes course1 = testData.courses.get("typicalCourse1");
        CourseAttributes course2 = testData.courses.get("typicalCourse2");

        StudentAttributes[] studentsInCourse1 = {
                testData.students.get("student2.2InCourse1"),
                testData.students.get("student2InCourse1"),
        };
        StudentAttributes[] studentsInCourse2 = {
                testData.students.get("student2.2InCourse2"),
                testData.students.get("student2InCourse2"),
        };

        Map<String, StudentAttributes[]> courseIdToStudents = new HashMap<>();
        courseIdToStudents.put(course1.getId(), studentsInCourse1);
        courseIdToStudents.put(course2.getId(), studentsInCourse2);

        Map<String, CourseAttributes> courseIdToCourse = new HashMap<>();
        courseIdToCourse.put(course1.getId(), course1);
        courseIdToCourse.put(course2.getId(), course2);

        searchPage.verifyStudentDetails(courseIdToCourse, courseIdToStudents);

        ______TS("link: view student details page");

        StudentAttributes studentToView = testData.students.get("student2.2InCourse1");
        String studentEmail = studentToView.getEmail();

        InstructorCourseStudentDetailsViewPage studentDetailsViewPage =
                searchPage.clickViewStudent(course1, studentEmail);
        studentDetailsViewPage.verifyIsCorrectPage(course1.getId(), studentEmail);
        studentDetailsViewPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: edit student details page");

        InstructorCourseStudentDetailsEditPage studentDetailsEditPage =
                searchPage.clickEditStudent(course1, studentEmail);
        studentDetailsEditPage.verifyIsCorrectPage(course1.getId(), studentEmail);
        studentDetailsEditPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("link: view all records page");

        InstructorStudentRecordsPage studentRecordsPage =
                searchPage.clickViewAllRecords(course1, studentEmail);
        studentRecordsPage.verifyIsCorrectPage(course1.getId(), studentToView.getName());
        studentRecordsPage.closeCurrentWindowAndSwitchToParentWindow();

        ______TS("action: delete student");

        StudentAttributes studentToDelete = testData.students.get("student2InCourse2");

        searchPage.deleteStudent(course2, studentToDelete.getEmail());

        StudentAttributes[] studentsAfterDelete = {
                testData.students.get("student2.2InCourse2"),
        };

        searchPage.verifyStudentDetails(course2, studentsAfterDelete);
        verifyAbsentInDatastore(studentToDelete);

        ______TS("search for response comments");

        searchPage.search(false, true, "comment");

        CommentSearchSessionResult firstResult = new CommentSearchSessionResult();
        firstResult.session = testData.feedbackSessions.get("First Session");

        CommentSearchResponseResult firstResponse = new CommentSearchResponseResult();
        firstResponse.question = testData.feedbackQuestions.get("qn1");
        firstResponse.response = testData.feedbackResponses.get("qn1response1");
        firstResponse.comments = new FeedbackResponseCommentAttributes[] {
                testData.feedbackResponseComments.get("qn1Comment1"),
                testData.feedbackResponseComments.get("qn1Comment2"),
        };

        CommentSearchResponseResult secondResponse = new CommentSearchResponseResult();
        secondResponse.question = testData.feedbackQuestions.get("qn1");
        secondResponse.response = testData.feedbackResponses.get("qn1response3");
        secondResponse.comments = new FeedbackResponseCommentAttributes[] {
                testData.feedbackResponseComments.get("qn1Comment3"),
        };

        firstResult.responses = new CommentSearchResponseResult[] { firstResponse, secondResponse };

        CommentSearchSessionResult[] commentSearchSessionResults = { firstResult };
        searchPage.verifyCommentSearchResults(commentSearchSessionResults, testData.students.values(),
                testData.instructors.values());

    }

}
