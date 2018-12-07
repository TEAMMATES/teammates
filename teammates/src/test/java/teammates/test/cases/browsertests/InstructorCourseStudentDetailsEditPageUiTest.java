package teammates.test.cases.browsertests;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.driver.BackDoor;
import teammates.test.driver.StringHelperExtension;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsEditPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT}.
 */
public class InstructorCourseStudentDetailsEditPageUiTest extends BaseUiTestCase {
    private InstructorCourseStudentDetailsEditPage editPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseStudentDetailsEditPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testInputValidation();
        // no links to check
        testEditAction();
    }

    private void testContent() throws Exception {

        String instructorId = testData.instructors.get("CCSDEditUiT.instr").googleId;
        String courseId = testData.courses.get("CCSDEditUiT.CS2104").getId();

        ______TS("content: unregistered student");

        AppUrl editPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT)
                                        .withUserId(instructorId)
                                        .withCourseId(courseId)
                                        .withStudentEmail(testData.students.get("unregisteredStudent").email);

        editPage = loginAdminToPage(editPageUrl, InstructorCourseStudentDetailsEditPage.class);
        editPage.verifyHtmlMainContent("/instructorCourseStudentEditUnregisteredPage.html");

        ______TS("content: registered student");

        editPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT)
            .withUserId(instructorId)
            .withCourseId(courseId)
            .withStudentEmail(testData.students.get("registeredStudent").email);

        editPage = loginAdminToPage(editPageUrl, InstructorCourseStudentDetailsEditPage.class);

        // This is the full HTML verification for Instructor Course Student Edit Page, the rest can all be verifyMainHtml
        editPage.verifyHtml("/instructorCourseStudentEditPage.html");
    }

    private void testInputValidation() throws Exception {

        ______TS("input validation");

        editPage.submitUnsuccessfully(null, "", null, null)
                .waitForTextsForAllStatusMessagesToUserEquals(
                        getPopulatedEmptyStringErrorMessage(
                                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                                FieldValidator.TEAM_NAME_FIELD_NAME, FieldValidator.TEAM_NAME_MAX_LENGTH));

        ______TS("empty student name and the team field is edited");
        String newTeamName = "New teamname";
        editPage.submitUnsuccessfully("", newTeamName, null, null)
                .waitForTextsForAllStatusMessagesToUserEquals(
                        getPopulatedEmptyStringErrorMessage(
                                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH));

        ______TS("long student name and the team field is not edited");
        String invalidStudentName = StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        editPage.submitUnsuccessfully(invalidStudentName, null, null, null)
                .waitForTextsForAllStatusMessagesToUserEquals(
                        getPopulatedErrorMessage(
                                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidStudentName,
                                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                                FieldValidator.PERSON_NAME_MAX_LENGTH));

        String newStudentName = "New guy";
        String invalidTeamName = StringHelperExtension.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
        editPage.submitUnsuccessfully(newStudentName, invalidTeamName, null, null)
                .waitForTextsForAllStatusMessagesToUserEquals(
                        getPopulatedErrorMessage(
                                FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidTeamName,
                                FieldValidator.TEAM_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                                FieldValidator.TEAM_NAME_MAX_LENGTH));

        String invalidEmail = "invalidemail";
        editPage.submitUnsuccessfully(newStudentName, newTeamName, invalidEmail, null)
                .waitForTextsForAllStatusMessagesToUserEquals(
                        getPopulatedErrorMessage(
                                FieldValidator.EMAIL_ERROR_MESSAGE, invalidEmail,
                                FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                                FieldValidator.EMAIL_MAX_LENGTH));
    }

    private void testEditAction() {

        ______TS("Error case, invalid email parameter (email already taken by others)");

        StudentAttributes anotherStudent = testData.students.get("unregisteredStudent");

        editPage = editPage.submitUnsuccessfully("New name2", "New team2", anotherStudent.email, "New comments2");
        editPage.waitForTextsForAllStatusMessagesToUserEquals(
                String.format(Const.StatusMessages.STUDENT_EMAIL_TAKEN_MESSAGE, anotherStudent.name, anotherStudent.email));
        editPage.verifyIsCorrectPage("CCSDEditUiT.jose.tmms@gmail.tmt");

        // Verify data
        StudentAttributes student = BackDoor.getStudent(testData.courses.get("CCSDEditUiT.CS2104").getId(),
                                                                             "CCSDEditUiT.jose.tmms@gmail.tmt");
        assertEquals("José Gómez</option></td></div>'\"", student.name);
        assertEquals("Team 1</td></div>'\"", student.team);
        assertEquals(testData.students.get("registeredStudent").googleId, student.googleId);
        assertEquals("CCSDEditUiT.jose.tmms@gmail.tmt", student.email);
        assertEquals("This student's name is José Gómez</option></td></div>'\"", student.comments);

        ______TS("edit action");

        InstructorCourseDetailsPage detailsPage =
                editPage.submitSuccessfully("New name", "New team", "newemail@gmail.tmt", "New comments");
        detailsPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_EDITED);
        detailsPage.verifyIsCorrectPage(testData.courses.get("CCSDEditUiT.CS2104").getId());

        // Verify data
        student = BackDoor.getStudent(testData.courses.get("CCSDEditUiT.CS2104").getId(),
                                      "newemail@gmail.tmt");
        assertEquals("New name", student.name);
        assertEquals("New team", student.team);
        assertEquals("newemail@gmail.tmt", student.email);
        assertEquals("New comments", student.comments);
        assertTrue(student.googleId.isEmpty()); // Due to Google ID reset

        // Verify adding the original student again does not overwrite the edited entity
        BackDoor.createStudent(testData.students.get("registeredStudent"));
        student = BackDoor.getStudent(testData.courses.get("CCSDEditUiT.CS2104").getId(),
                                      "newemail@gmail.tmt");
        assertNotNull(student);
    }

}
