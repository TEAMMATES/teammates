package teammates.test.cases.browsertests;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.StudentCourseDetailsPage;

/**
 * SUT: {@link Const.ActionURIs#STUDENT_COURSE_DETAILS_PAGE}.
 */
public class StudentCourseDetailsPageUiTest extends BaseUiTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentCourseDetailsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() throws Exception {

        ______TS("content");

        //with teammates"
        // This is the full HTML verification for Student Course Details Page, the rest can all be verifyMainHtml
        verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.alice", "/studentCourseDetailsWithTeammatesHTML.html", true);

        //without teammates
        verifyContent("SCDetailsUiT.CS2104", "SCDetailsUiT.charlie",
                      "/studentCourseDetailsWithoutTeammatesHTML.html", false);

        ______TS("content: data requiring sanitization");

        verifyContent("SCDetailsUiT.TSCourse",
                "SCDetailsUiT.student1InTSCourse",
                "/studentCourseDetailsTestingSanitization.html",
                false);

        ______TS("links, inputValidation, actions");

        //nothing to test here.

    }

    private void verifyContent(String courseObjectId, String studentObjectId, String filePath,
                               boolean isFullPageChecked) throws Exception {
        AppUrl detailsPageUrl = createUrl(Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE)
                                .withUserId(testData.students.get(studentObjectId).googleId)
                                .withCourseId(testData.courses.get(courseObjectId).getId());
        StudentCourseDetailsPage detailsPage = loginAdminToPage(detailsPageUrl, StudentCourseDetailsPage.class);
        if (isFullPageChecked) {
            detailsPage.verifyHtml(filePath);
        } else {
            detailsPage.verifyHtmlMainContent(filePath);
        }
    }

}
