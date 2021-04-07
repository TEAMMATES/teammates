package teammates.e2e.cases;

import java.time.ZoneId;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_EDIT_PAGE}.
 */
public class InstructorCourseEditPageE2ETest extends BaseE2ETestCase {
    CourseAttributes course;
    InstructorAttributes[] instructors = new InstructorAttributes[5];

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEditPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        course = testData.courses.get("ICEdit.CS2104");
        instructors[0] = testData.instructors.get("ICEdit.helper");
        instructors[1] = testData.instructors.get("ICEdit.manager");
        instructors[2] = testData.instructors.get("ICEdit.observer");
        instructors[3] = testData.instructors.get("ICEdit.coowner");
        instructors[4] = testData.instructors.get("ICEdit.tutor");
    }

    @Test
    @Override
    public void testAll() {
        ______TS("verify cannot edit without privilege");
        // log in as instructor with no edit privilege
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withUserId(instructors[2].googleId)
                .withCourseId(course.getId());
        InstructorCourseEditPage editPage = loginAdminToPage(url, InstructorCourseEditPage.class);

        editPage.verifyCourseNotEditable();
        editPage.verifyInstructorsNotEditable();
        editPage.verifyAddInstructorNotAllowed();

        ______TS("verify loaded data");
        // re-log in as instructor with edit privilege
        url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withUserId(instructors[3].googleId)
                .withCourseId(course.getId());
        editPage = getNewPageInstance(url, InstructorCourseEditPage.class);

        editPage.verifyCourseDetails(course);
        editPage.verifyInstructorDetails(instructors[0]);
        editPage.verifyInstructorDetails(instructors[1]);
        editPage.verifyInstructorDetails(instructors[2]);
        editPage.verifyInstructorDetails(instructors[3]);
        editPage.verifyInstructorDetails(instructors[4]);

        ______TS("add instructor");
        InstructorAttributes newInstructor = InstructorAttributes
                .builder(course.getId(), "ICEdit.test@gmail.tmt")
                .withName("Teammates Test")
                .withIsDisplayedToStudents(true)
                .withDisplayedName("Instructor")
                .withRole("Tutor")
                .build();

        editPage.addInstructor(newInstructor);
        editPage.verifyStatusMessage("\"The instructor " + newInstructor.name + " has been added successfully. "
                + "An email containing how to 'join' this course will be sent to " + newInstructor.email
                + " in a few minutes.\"");
        editPage.verifyInstructorDetails(newInstructor);
        verifyPresentInDatastore(newInstructor);

        ______TS("resend invite");
        editPage.resendInstructorInvite(newInstructor);
        editPage.verifyStatusMessage("An email has been sent to " + newInstructor.email);

        ______TS("edit instructor");
        instructors[0].name = "Edited Name";
        instructors[0].email = "ICEdit.edited@gmail.tmt";
        instructors[0].privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, true);
        instructors[0].privileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, false);
        instructors[0].privileges.updatePrivilege("Section 2",
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        instructors[0].privileges.updatePrivilege("Section 1", "First feedback session",
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);

        editPage.editInstructor(1, instructors[0]);
        editPage.toggleCustomCourseLevelPrivilege(1, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        editPage.toggleCustomCourseLevelPrivilege(1, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        editPage.toggleCustomSectionLevelPrivilege(1, 1, "Section 2",
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
        editPage.toggleCustomSessionLevelPrivilege(1, 2, "Section 1", "First feedback session",
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        editPage.verifyStatusMessage("The instructor " + instructors[0].name + " has been updated.");
        editPage.verifyInstructorDetails(instructors[0]);

        // verify in datastore by reloading
        editPage.reloadPage();
        editPage.verifyInstructorDetails(instructors[0]);

        ______TS("delete instructor");
        editPage.deleteInstructor(newInstructor);
        editPage.verifyStatusMessage("Instructor is successfully deleted.");
        editPage.verifyNumInstructorsEquals(5);
        verifyAbsentInDatastore(newInstructor);

        ______TS("edit course");
        String newName = "New Course Name";
        ZoneId newTimeZone = ZoneId.of("Asia/Singapore");
        course.setName(newName);
        course.setTimeZone(newTimeZone);

        editPage.editCourse(course);
        editPage.verifyStatusMessage("The course has been edited.");
        editPage.verifyCourseDetails(course);
        verifyPresentInDatastore(course);

        ______TS("delete course");
        editPage.deleteCourse();
        editPage.verifyStatusMessage("The course " + course.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        assertTrue(BACKDOOR.isCourseInRecycleBin(course.getId()));
    }
}
