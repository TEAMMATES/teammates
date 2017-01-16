package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.CommentsLogic;
import teammates.ui.controller.AdminHomePageData;
import teammates.ui.controller.AdminInstructorAccountAddAction;
import teammates.ui.controller.AjaxResult;

public class AdminInstructorAccountAddActionTest extends BaseActionTest {

    // private final DataBundle dataBundle = getTypicalDataBundle();
    //TODO: move all the input validation/sanitization js code to server side
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD;
        // removeAndRestoreTypicalDataInDatastore();
    }

    @Test
    public void testGenerateNextDemoCourseId() throws Exception {
        testGenerateNextDemoCourseIdForLengthLimit(40);
        testGenerateNextDemoCourseIdForLengthLimit(20);
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        final String newInstructorShortName = "James";
        final String name = "JamesBond";
        final String email = "jamesbond89@gmail.tmt";
        final String institute = "TEAMMATES Test Institute 1";
        final String adminUserId = "admin.user";
        
        ______TS("Not enough parameters");
        
        gaeSimulation.loginAsAdmin(adminUserId);
        verifyAssumptionFailure();
        verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_SHORT_NAME, newInstructorShortName);
        verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_SHORT_NAME, newInstructorShortName,
                Const.ParamsNames.INSTRUCTOR_NAME, name);
        verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_SHORT_NAME, newInstructorShortName,
                Const.ParamsNames.INSTRUCTOR_NAME, name,
                Const.ParamsNames.INSTRUCTOR_EMAIL, email);
        verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_SHORT_NAME, newInstructorShortName,
                Const.ParamsNames.INSTRUCTOR_NAME, name,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_SHORT_NAME, newInstructorShortName,
                Const.ParamsNames.INSTRUCTOR_EMAIL, email,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_NAME, name,
                Const.ParamsNames.INSTRUCTOR_EMAIL, email,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);

        ______TS("Normal case: not importing demo couse, extra spaces around values");
        final String newInstructorShortNameWithSpaces = "   " + newInstructorShortName + "   ";
        final String nameWithSpaces = "   " + name + "   ";
        final String emailWithSpaces = "   " + email + "   ";
        final String instituteWithSpaces = "   " + institute + "   ";

        AdminInstructorAccountAddAction a = getAction(
                Const.ParamsNames.INSTRUCTOR_SHORT_NAME, newInstructorShortNameWithSpaces,
                Const.ParamsNames.INSTRUCTOR_NAME, nameWithSpaces,
                Const.ParamsNames.INSTRUCTOR_EMAIL, emailWithSpaces,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, instituteWithSpaces);
        
        AjaxResult r = (AjaxResult) a.executeAndPostProcess();
        assertTrue(r.getStatusMessage().contains("Instructor " + name + " has been successfully created"));
        
        verifyNumberOfEmailsSent(a, 1);
        
        EmailWrapper emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), newInstructorShortName),
                     emailSent.getSubject());
        assertEquals(email, emailSent.getRecipient());
        
        ______TS("Error: invalid parameter");
        
        final String anotherNewInstructorShortName = "Bond";
        final String invalidName = "James%20Bond99";
        a = getAction(
                Const.ParamsNames.INSTRUCTOR_SHORT_NAME, anotherNewInstructorShortName,
                Const.ParamsNames.INSTRUCTOR_NAME, invalidName,
                Const.ParamsNames.INSTRUCTOR_EMAIL, email,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);

        String expectedError =
                "\"" + invalidName + "\" is not acceptable to TEAMMATES as a/an person name because "
                + "it contains invalid characters. All person name must start with an "
                + "alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).";
        
        AjaxResult rInvalidParam = (AjaxResult) a.executeAndPostProcess();
        assertEquals(expectedError, rInvalidParam.getStatusMessage());
        
        AdminHomePageData pageData = (AdminHomePageData) rInvalidParam.data;
        assertEquals(email, pageData.instructorEmail);
        assertEquals(anotherNewInstructorShortName, pageData.instructorShortName);
        assertEquals(institute, pageData.instructorInstitution);
        assertEquals(invalidName, pageData.instructorName);
        
        verifyNoEmailsSent(a);
        
        ______TS("Normal case: importing demo couse");
        
        a = getAction(
                Const.ParamsNames.INSTRUCTOR_SHORT_NAME, anotherNewInstructorShortName,
                Const.ParamsNames.INSTRUCTOR_NAME, name,
                Const.ParamsNames.INSTRUCTOR_EMAIL, email,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        
        r = (AjaxResult) a.executeAndPostProcess();
        assertTrue(r.getStatusMessage().contains("Instructor " + name + " has been successfully created"));
        
        verifyNumberOfEmailsSent(a, 1);
        
        emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), anotherNewInstructorShortName),
                     emailSent.getSubject());
        assertEquals(email, emailSent.getRecipient());
        
        // delete the comment that was created
        CommentAttributes comment =
                CommentsLogic.inst().getCommentsForReceiver(getDemoCourseIdRoot(email),
                                                            CommentParticipantType.PERSON, "alice.b.tmms@gmail.tmt").get(0);
        CommentsLogic.inst().deleteComment(comment);
        new Logic().deleteCourse(getDemoCourseIdRoot(email));
    }

    private void testGenerateNextDemoCourseIdForLengthLimit(int maximumIdLength) throws Exception {
        final String normalIdSuffix = ".gma-demo";
        final String atEmail = "@gmail.tmt";
        final int normalIdSuffixLength = normalIdSuffix.length(); // 9
        final String strShortWithWordDemo =
                StringHelper.generateStringOfLength((maximumIdLength - normalIdSuffixLength) / 2) + "-demo";
        final String strWayShorterThanMaximum =
                StringHelper.generateStringOfLength((maximumIdLength - normalIdSuffixLength) / 2);
        final String strOneCharShorterThanMaximum =
                StringHelper.generateStringOfLength(maximumIdLength - normalIdSuffixLength);
        final String strOneCharLongerThanMaximum =
                StringHelper.generateStringOfLength(maximumIdLength - normalIdSuffixLength + 1);
        assertEquals(strShortWithWordDemo + normalIdSuffix,
                     generateNextDemoCourseId(strShortWithWordDemo + atEmail, maximumIdLength));
        assertEquals(strShortWithWordDemo + normalIdSuffix + "0",
                     generateNextDemoCourseId(strShortWithWordDemo + normalIdSuffix, maximumIdLength));
        assertEquals(strShortWithWordDemo + normalIdSuffix + "1",
                     generateNextDemoCourseId(strShortWithWordDemo + normalIdSuffix + "0", maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix,
                     generateNextDemoCourseId(strWayShorterThanMaximum + atEmail, maximumIdLength));
        assertEquals(strOneCharShorterThanMaximum + normalIdSuffix,
                     generateNextDemoCourseId(strOneCharShorterThanMaximum + atEmail, maximumIdLength));
        assertEquals(strOneCharLongerThanMaximum.substring(1) + normalIdSuffix,
                     generateNextDemoCourseId(strOneCharLongerThanMaximum + atEmail, maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "0",
                     generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix, maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "1",
                     generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix + "0", maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "10",
                     generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix + "9", maximumIdLength));
        assertEquals(strOneCharShorterThanMaximum.substring(2) + normalIdSuffix + "10",
                     generateNextDemoCourseId(strOneCharShorterThanMaximum.substring(1) + normalIdSuffix + "9",
                                              maximumIdLength));
    }
    
    private String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength)
            throws Exception {
        AdminInstructorAccountAddAction a = new AdminInstructorAccountAddAction();
        return (String) invokeMethod(a.getClass(), "generateNextDemoCourseId",
                                     new Class<?>[] { String.class, int.class },
                                     a, new Object[] { instructorEmailOrProposedCourseId, maximumIdLength });
    }

    private AdminInstructorAccountAddAction getAction(String... parameters) {
        return (AdminInstructorAccountAddAction) gaeSimulation.getActionObject(uri, parameters);
    }

    private String getDemoCourseIdRoot(String instructorEmail) {
        final String[] splitedEmail = instructorEmail.split("@");
        final String head = splitedEmail[0];
        final String emailAbbreviation = splitedEmail[1].substring(0, 3);
        return head + "." + emailAbbreviation
                + "-demo";
    }

}
