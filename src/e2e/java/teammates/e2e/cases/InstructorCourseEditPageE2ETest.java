package teammates.e2e.cases;

import java.util.List;

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

        sqlTestData = removeAndRestoreSqlDataBundle(loadSqlDataBundle("/InstructorCourseEditPageE2ETest_SqlEntities.json"));

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
        InstructorCourseEditPage editPage = loginToPage(url, InstructorCourseEditPage.class, instructors[2].getGoogleId());

        editPage.verifyCourseNotEditable();
        editPage.verifyInstructorsNotEditable();
        editPage.verifyAddInstructorNotAllowed();
        editPage.verifyCopyInstructorsNotAllowed();

        ______TS("verify loaded data");
        // re-log in as instructor with edit privilege
        logout();
        url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withCourseId(course.getId());
        editPage = loginToPage(url, InstructorCourseEditPage.class, instructors[3].getGoogleId());

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
        editPage.verifyStatusMessage("The instructor " + newInstructor.getName() + " has been added successfully. "
                + "An email containing how to 'join' this course will be sent to " + newInstructor.getEmail()
                + " in a few minutes.");
        editPage.verifyInstructorDetails(newInstructor);
        verifyPresentInDatabase(newInstructor);

        ______TS("copy instructors from other courses");
        InstructorAttributes instructorToCopy1 = testData.instructors.get("ICEdit.coowner.CS2103T");
        InstructorAttributes instructorToCopy2 = testData.instructors.get("ICEdit.observer.CS2103T");
        InstructorAttributes instructorToCopy3 = testData.instructors.get("ICEdit.manager.CS2105");
        List<InstructorAttributes> instructorsToCopy = List.of(instructorToCopy1, instructorToCopy2, instructorToCopy3);

        editPage.copyInstructors(instructorsToCopy);

        editPage.verifyStatusMessage("The selected instructor(s) have been added successfully. "
                + "An email containing how to 'join' this course will be sent to them in a few minutes.");
        for (InstructorAttributes i : instructorsToCopy) {
            newInstructor = InstructorAttributes
                    .builder(course.getId(), i.getEmail())
                    .withName(i.getName())
                    .withIsDisplayedToStudents(i.isDisplayedToStudents())
                    .withDisplayedName(i.getDisplayedName())
                    .withRole(i.getRole())
                    .build();

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
