package teammates.e2e.cases.sql;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseEditPageSql;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_EDIT_PAGE}.
 */
public class InstructorCourseEditPageE2ETest extends BaseE2ETestCase {
    Course course;
    Instructor[] instructors = new Instructor[5];

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/InstructorCourseEditPageE2ETestSql.json"));

        course = testData.courses.get("ICEdit.CS2104");
        instructors[0] = testData.instructors.get("ICEdit.helper.CS2104");
        instructors[1] = testData.instructors.get("ICEdit.manager.CS2104");
        instructors[2] = testData.instructors.get("ICEdit.observer.CS2104");
        instructors[3] = testData.instructors.get("ICEdit.coowner.CS2104");
        instructors[4] = testData.instructors.get("ICEdit.tutor.CS2104");
    }

    @Test
    @Override
    public void testAll() {
        ______TS("verify cannot edit without privilege");
        // log in as instructor with no edit privilege
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withCourseId(course.getId());
        InstructorCourseEditPageSql editPage =
                loginToPage(url, InstructorCourseEditPageSql.class, instructors[2].getGoogleId());

        editPage.verifyCourseNotEditable();
        editPage.verifyInstructorsNotEditable();
        editPage.verifyAddInstructorNotAllowed();
        editPage.verifyCopyInstructorsNotAllowed();

        ______TS("verify loaded data");
        // re-log in as instructor with edit privilege
        logout();
        url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withCourseId(course.getId());
        editPage = loginToPage(url, InstructorCourseEditPageSql.class, instructors[3].getGoogleId());

        editPage.verifyCourseDetails(course);
        editPage.verifyInstructorDetails(instructors[0]);
        editPage.verifyInstructorDetails(instructors[1]);
        editPage.verifyInstructorDetails(instructors[2]);
        editPage.verifyInstructorDetails(instructors[3]);
        editPage.verifyInstructorDetails(instructors[4]);

        ______TS("add instructor");
        Instructor newInstructor = getTypicalInstructor();
        newInstructor.setCourse(course);
        newInstructor.setEmail("ICEdit.test@gmail.tmt");
        newInstructor.setName("Teammates Test");
        newInstructor.setDisplayedToStudents(true);
        newInstructor.setDisplayName("Instructor");
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        newInstructor.setRole(role);

        editPage.addInstructor(newInstructor);
        editPage.verifyStatusMessage("The instructor " + newInstructor.getName() + " has been added successfully. "
                + "An email containing how to 'join' this course will be sent to " + newInstructor.getEmail()
                + " in a few minutes.");
        editPage.verifyInstructorDetails(newInstructor);
        verifyPresentInDatabase(newInstructor);

        ______TS("copy instructors from other courses");
        Instructor instructorToCopy1 = testData.instructors.get("ICEdit.coowner.CS2103T");
        Instructor instructorToCopy2 = testData.instructors.get("ICEdit.observer.CS2103T");
        Instructor instructorToCopy3 = testData.instructors.get("ICEdit.manager.CS2105");
        List<Instructor> instructorsToCopy = List.of(instructorToCopy1, instructorToCopy2, instructorToCopy3);

        editPage.copyInstructors(instructorsToCopy);

        editPage.verifyStatusMessage("The selected instructor(s) have been added successfully. "
                + "An email containing how to 'join' this course will be sent to them in a few minutes.");
        for (Instructor i : instructorsToCopy) {
            newInstructor.setCourse(course);
            newInstructor.setEmail(i.getEmail());
            newInstructor.setName(i.getName());
            newInstructor.setDisplayedToStudents(i.isDisplayedToStudents());
            newInstructor.setDisplayName(i.getDisplayName());
            newInstructor.setRole(i.getRole());

            editPage.verifyInstructorDetails(newInstructor);
            verifyPresentInDatabase(newInstructor);
        }

        ______TS("cannot copy instructors whose email already exists");
        instructorToCopy1 = testData.instructors.get("ICEdit.tutor.CS2106");

        editPage.verifyCopyInstructorWithExistingEmailNotAllowed(instructorToCopy1);

        ______TS("resend invite");
        editPage.resendInstructorInvite(newInstructor);
        editPage.verifyStatusMessage("An email has been sent to " + newInstructor.getEmail());

        ______TS("edit instructor");
        instructors[0].setName("Edited Name");
        instructors[0].setEmail("ICEdit.edited@gmail.tmt");
        instructors[0].getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, true);
        instructors[0].getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, false);
        instructors[0].getPrivileges().updatePrivilege("Section 2",
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        instructors[0].getPrivileges().updatePrivilege("Section 1", "First feedback session",
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);

        editPage.editInstructor(2, instructors[0]);
        editPage.toggleCustomCourseLevelPrivilege(2, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        editPage.toggleCustomCourseLevelPrivilege(2, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        editPage.toggleCustomSectionLevelPrivilege(2, 1, "Section 2",
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
        editPage.toggleCustomSessionLevelPrivilege(2, 2, "Section 1", "First feedback session",
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);
        editPage.verifyStatusMessage("The instructor " + instructors[0].getName() + " has been updated.");
        editPage.verifyInstructorDetails(instructors[0]);

        // verify in database by reloading
        editPage.reloadPage();
        editPage.verifyInstructorDetails(instructors[0]);

        ______TS("delete instructor");
        editPage.deleteInstructor(newInstructor);
        editPage.verifyStatusMessage("Instructor is successfully deleted.");
        editPage.verifyNumInstructorsEquals(8);
        verifyAbsentInDatabase(newInstructor);

        ______TS("edit course");
        String newName = "New Course Name";
        String newTimeZone = "Asia/Singapore";
        course.setName(newName);
        course.setTimeZone(newTimeZone);

        editPage.editCourse(course);
        editPage.verifyStatusMessage("The course has been edited.");
        editPage.verifyCourseDetails(course);
        verifyPresentInDatabase(course);

        ______TS("delete course");
        editPage.deleteCourse();
        editPage.verifyStatusMessage("The course " + course.getId() + " has been deleted. "
                + "You can restore it from the Recycle Bin manually.");
        assertTrue(BACKDOOR.isCourseInRecycleBin(course.getId()));
    }
}
