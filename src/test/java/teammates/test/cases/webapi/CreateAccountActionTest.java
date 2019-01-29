package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.driver.StringHelperExtension;
import teammates.ui.webapi.action.CreateAccountAction;
import teammates.ui.webapi.action.CreateAccountAction.JoinLink;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link CreateAccountAction}.
 */
public class CreateAccountActionTest extends BaseActionTest<CreateAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        String name = "JamesBond";
        String email = "jamesbond89@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";

        ______TS("Not enough parameters");

        loginAsAdmin();

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.INSTRUCTOR_NAME, name);
        verifyHttpParameterFailure(Const.ParamsNames.INSTRUCTOR_EMAIL, email);
        verifyHttpParameterFailure(Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        verifyHttpParameterFailure(
                Const.ParamsNames.INSTRUCTOR_NAME, name,
                Const.ParamsNames.INSTRUCTOR_EMAIL, email
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.INSTRUCTOR_NAME, name,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.INSTRUCTOR_EMAIL, email,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute
        );

        ______TS("Normal case");

        String nameWithSpaces = "   " + name + "   ";
        String emailWithSpaces = "   " + email + "   ";
        String instituteWithSpaces = "   " + institute + "   ";

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_NAME, nameWithSpaces,
                Const.ParamsNames.INSTRUCTOR_EMAIL, emailWithSpaces,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, instituteWithSpaces,
        };
        CreateAccountAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        String courseId = generateNextDemoCourseId(email, FieldValidator.COURSE_ID_MAX_LENGTH);
        InstructorAttributes instructor = logic.getInstructorForEmail(courseId, email);

        String joinLink = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(StringHelper.encrypt(instructor.key))
                .withInstructorInstitution(institute)
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();
        JoinLink output = (JoinLink) r.getOutput();
        assertEquals(joinLink, output.getJoinLink());

        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), name),
                emailSent.getSubject());
        assertEquals(email, emailSent.getRecipient());

        ______TS("Error: invalid parameter");

        String invalidName = "James%20Bond99";
        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_NAME, invalidName,
                Const.ParamsNames.INSTRUCTOR_EMAIL, email,
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute,
        };
        a = getAction(params);
        r = getJsonResult(a);

        String expectedError =
                "\"" + invalidName + "\" is not acceptable to TEAMMATES as a/an person name because "
                        + "it contains invalid characters. A/An person name must start with an "
                        + "alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).";

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        MessageOutput msgOutput = (MessageOutput) r.getOutput();
        assertEquals(expectedError, msgOutput.getMessage());

        verifyNoEmailsSent(a);
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Test
    public void testGenerateNextDemoCourseId() throws Exception {
        testGenerateNextDemoCourseIdForLengthLimit(40);
        testGenerateNextDemoCourseIdForLengthLimit(20);
    }

    private void testGenerateNextDemoCourseIdForLengthLimit(int maximumIdLength) throws Exception {
        String normalIdSuffix = ".gma-demo";
        String atEmail = "@gmail.tmt";
        int normalIdSuffixLength = normalIdSuffix.length(); // 9
        String strShortWithWordDemo =
                StringHelperExtension.generateStringOfLength((maximumIdLength - normalIdSuffixLength) / 2) + "-demo";
        String strWayShorterThanMaximum =
                StringHelperExtension.generateStringOfLength((maximumIdLength - normalIdSuffixLength) / 2);
        String strOneCharShorterThanMaximum =
                StringHelperExtension.generateStringOfLength(maximumIdLength - normalIdSuffixLength);
        String strOneCharLongerThanMaximum =
                StringHelperExtension.generateStringOfLength(maximumIdLength - normalIdSuffixLength + 1);
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
        CreateAccountAction a = new CreateAccountAction();
        return (String) invokeMethod(a.getClass(), "generateNextDemoCourseId",
                new Class<?>[] { String.class, int.class },
                a, new Object[] { instructorEmailOrProposedCourseId, maximumIdLength });
    }

}
