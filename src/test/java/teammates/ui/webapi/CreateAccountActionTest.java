package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.StringHelperExtension;
import teammates.ui.output.JoinLinkData;
import teammates.ui.request.AccountCreateRequest;

/**
 * SUT: {@link CreateAccountAction}.
 */
public class CreateAccountActionTest extends BaseActionTest<CreateAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() {
        loginAsAdmin();
        String name = "JamesBond";
        String email = "jamesbond89@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";

        ______TS("Not enough parameters");
        AccountCreateRequest badRequest = buildCreateRequest(null, institute, email);

        try {
            getAction(badRequest).execute();
        } catch (InvalidHttpRequestBodyException e) {
            assertEquals("name cannot be null", e.getMessage());
        }

        badRequest = buildCreateRequest(name, null, email);
        try {
            getAction(badRequest).execute();
        } catch (InvalidHttpRequestBodyException e) {
            assertEquals("institute cannot be null", e.getMessage());
        }

        badRequest = buildCreateRequest(name, institute, null);
        try {
            getAction(badRequest).execute();
        } catch (InvalidHttpRequestBodyException e) {
            assertEquals("email cannot be null", e.getMessage());
        }

        ______TS("Normal case");

        String nameWithSpaces = "   " + name + "   ";
        String emailWithSpaces = "   " + email + "   ";
        String instituteWithSpaces = "   " + institute + "   ";

        AccountCreateRequest req = buildCreateRequest(nameWithSpaces, instituteWithSpaces, emailWithSpaces);
        CreateAccountAction a = getAction(req);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        String courseId = generateNextDemoCourseId(email, FieldValidator.COURSE_ID_MAX_LENGTH);
        InstructorAttributes instructor = logic.getInstructorForEmail(courseId, email);

        String joinLink = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(StringHelper.encrypt(instructor.key))
                .withInstructorInstitution(institute)
                .withInstitutionMac(StringHelper.generateSignature(institute))
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toAbsoluteString();
        JoinLinkData output = (JoinLinkData) r.getOutput();
        assertEquals(joinLink, output.getJoinLink());

        verifyNumberOfEmailsSent(a, 1);

        EmailWrapper emailSent = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(String.format(EmailType.NEW_INSTRUCTOR_ACCOUNT.getSubject(), name),
                emailSent.getSubject());
        assertEquals(email, emailSent.getRecipient());

        ______TS("Error: invalid parameter");

        String invalidName = "James%20Bond99";

        req = buildCreateRequest(invalidName, institute, emailWithSpaces);

        CreateAccountAction finalA = getAction(req);
        try {
            finalA.execute();
        } catch (InvalidHttpRequestBodyException e) {
            String expectedError =
                    "\"" + invalidName + "\" is not acceptable to TEAMMATES as a/an person name because "
                            + "it contains invalid characters. A/An person name must start with an "
                            + "alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%).";
            assertEquals(expectedError, e.getMessage());
        }

        verifyNoEmailsSent(finalA);
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Test
    public void testGenerateNextDemoCourseId() {
        testGenerateNextDemoCourseIdForLengthLimit(40);
        testGenerateNextDemoCourseIdForLengthLimit(20);
    }

    private void testGenerateNextDemoCourseIdForLengthLimit(int maximumIdLength) {
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

    private String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength) {
        CreateAccountAction a = new CreateAccountAction();
        return a.generateNextDemoCourseId(instructorEmailOrProposedCourseId, maximumIdLength);
    }

    private AccountCreateRequest buildCreateRequest(String name, String institution, String email) {
        AccountCreateRequest req = new AccountCreateRequest();

        req.setInstructorName(name);
        req.setInstructorInstitution(institution);
        req.setInstructorEmail(email);

        return req;
    }
}
