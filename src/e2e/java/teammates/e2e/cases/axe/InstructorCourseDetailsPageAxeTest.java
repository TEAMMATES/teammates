package teammates.e2e.cases.axe;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorCourseDetailsPage;
import teammates.e2e.util.AxeUtil;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_DETAILS_PAGE}.
 */
public class InstructorCourseDetailsPageAxeTest extends BaseE2ETestCase {
    private StudentAttributes student;
    private CourseAttributes course;

    private String fileName;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseDetailsPageE2ETest.json");
        student = testData.students.get("charlie.tmms@ICDet.CS2104");
        student.setEmail(TestProperties.TEST_EMAIL);

        removeAndRestoreDataBundle(testData);
        course = testData.courses.get("ICDet.CS2104");
        fileName = "/" + course.getId() + "_studentList.csv";
    }

    @BeforeClass
    public void classSetup() {
        deleteDownloadsFile(fileName);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl detailsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                .withCourseId(course.getId());
        InstructorCourseDetailsPage detailsPage = loginToPage(detailsPageUrl, InstructorCourseDetailsPage.class,
                testData.instructors.get("ICDet.instr").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(detailsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }
}
