package teammates.test.cases.browsertests;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE}.
 */
public class InstructorCourseStudentDetailsPageUiTest extends BaseUiTestCase {

    private String instructorId;
    private String courseId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseStudentDetailsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        instructorId = testData.instructors.get("CCSDetailsUiT.instr").googleId;
        courseId = testData.courses.get("CCSDetailsUiT.CS2104").getId();
    }

    @Test
    public void testAll() throws Exception {

        testContent();
    }

    private void testContent() throws Exception {

        ______TS("content: registered student");

        InstructorCourseStudentDetailsViewPage viewPage = getCourseStudentDetailsPage("registeredStudent");

        // This is the full HTML verification for Instructor Student Details Page, the rest can all be verifyMainHtml
        viewPage.verifyHtml("/instructorCourseStudentDetailsRegistered.html");

        ______TS("content: unregistered student");

        viewPage = getCourseStudentDetailsPage("unregisteredStudent");
        viewPage.verifyHtmlMainContent("/instructorCourseStudentDetailsUnregistered.html");

        ______TS("content: registered student with helper view");

        // the helper here is configured to be able to view studentDetailsPage
        instructorId = testData.instructors.get("CCSDetailsUiT.Helper").googleId;

        viewPage = getCourseStudentDetailsPage("registeredStudent");
        viewPage.verifyHtmlMainContent("/instructorCourseStudentDetailsRegisteredWithHelperView.html");

        ______TS("content: registered student with attempted script and html injection");

        instructorId = testData.instructors.get("instructor1OfTestingSanitizationCourse").googleId;
        courseId = testData.courses.get("testingSanitizationCourse").getId();

        viewPage = getCourseStudentDetailsPage("student1InTestingSanitizationCourse");
        viewPage.verifyHtmlMainContent("/instructorCourseStudentDetailsRegisteredWithAttemptedScriptInjection.html");

        // TODO: add test for the comment box in this page
    }

    private InstructorCourseStudentDetailsViewPage getCourseStudentDetailsPage(String studentStr) {
        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
                .withUserId(instructorId)
                .withCourseId(courseId)
                .withStudentEmail(testData.students.get(studentStr).email);

        return loginAdminToPage(viewPageUrl, InstructorCourseStudentDetailsViewPage.class);
    }

}
