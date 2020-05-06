package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.InstructorCourseEditPage;
import teammates.e2e.pageobjects.InstructorHomePage;

import java.time.ZoneId;

public class InstructorCourseEditPageE2ETest extends BaseE2ETestCase {
    CourseAttributes course;
    InstructorAttributes[] instructors = new InstructorAttributes[5];

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEditPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        course = testData.courses.get("InsCrsEdit.CS2104");
        instructors[0] = testData.instructors.get("InsCrsEdit.helper");
        instructors[1] = testData.instructors.get("InsCrsEdit.manager");
        instructors[2] = testData.instructors.get("InsCrsEdit.observer");
        instructors[3] = testData.instructors.get("InsCrsEdit.coowner");
        instructors[4] = testData.instructors.get("InsCrsEdit.tutor");
    }

    @Test
    public void testAll() {
        ______TS("verify cannot edit without privilege");
        // log in as instructor with no edit privilege
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withUserId(instructors[2].googleId)
                .withCourseId(course.getId());
        loginAdminToPage(url, InstructorHomePage.class);
        InstructorCourseEditPage editPage = AppPage.getNewPageInstance(browser, url, InstructorCourseEditPage.class);

        editPage.verifyCourseNotEditable();
        editPage.verifyInstructorsNotEditable();
        editPage.verifyAddInstructorNotAllowed();

        ______TS("verify loaded data");
        // re-log in as instructor with edit privilege
        url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withUserId(instructors[3].googleId)
                .withCourseId(course.getId());
        editPage = AppPage.getNewPageInstance(browser, url, InstructorCourseEditPage.class);

        editPage.verifyCourseDetails(course);

        editPage.verifyInstructorDetails(1, instructors[0]);
        editPage.verifyInstructorDetails(2, instructors[1]);
        editPage.verifyInstructorDetails(3, instructors[2]);
        editPage.verifyInstructorDetails(4, instructors[3]);
        editPage.verifyInstructorDetails(5, instructors[4]);

        ______TS("add instructor");
        InstructorAttributes newInstructor = InstructorAttributes
                .builder(course.getId(), "InsCrsEdit.test@gmail.tmt")
                .withName("Teammates Test")
                .withIsDisplayedToStudents(true)
                .withDisplayedName("Instructor")
                .withRole("Tutor")
                .build();

        editPage.addInstructor(newInstructor);
        editPage.verifyStatusMessage("\"The instructor Teammates Test has been added successfully. " +
                "An email containing how to 'join' this course will be sent to InsCrsEdit.test@gmail.tmt" +
                " in a few minutes.\"");
        editPage.verifyInstructorDetails(6, newInstructor);

        ______TS("resend invite");
        editPage.resendInstructorInvite(6);
        editPage.verifyStatusMessage("An email has been sent to InsCrsEdit.test@gmail.tmt");

        ______TS("edit instructor");
        instructors[0].name = "Edited Name";
        instructors[0].email = "InsCrsEdit.edited@gmail.tmt";
        instructors[0].privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
        instructors[0].privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        instructors[0].privileges.updatePrivilege("Section 2",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        instructors[0].privileges.updatePrivilege("Section 1", "First feedback session",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, true);

        editPage.editInstructor(1, instructors[0]);
        editPage.toggleCustomCourseLevelPrivilege(1, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        editPage.toggleCustomCourseLevelPrivilege(1, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        editPage.toggleCustomSectionLevelPrivilege(1, 1, "Section 2",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
        editPage.toggleCustomSessionLevelPrivilege(1, 2, "Section 1", "First feedback session",
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        editPage.verifyStatusMessage("The instructor Edited Name has been updated.");
        editPage.verifyInstructorDetails(1, instructors[0]);

        ______TS("delete instructor");
        editPage.deleteInstructor(6);
        editPage.verifyNumInstructorsEquals(5);

        ______TS("edit course");
        String newName = "New Course Name";
        ZoneId newTimeZone = ZoneId.of("Asia/Singapore");
        course.setName(newName);
        course.setTimeZone(newTimeZone);

        editPage.editCourse(course);
        editPage.verifyStatusMessage("The course has been edited.");
        editPage.verifyCourseDetails(course);

        ______TS("delete course");
        editPage.deleteCourse();
        editPage.verifyStatusMessage("The course InsCrsEdit.CS2104 has been deleted. " +
                "You can restore it from the Recycle Bin manually.");
    }
}