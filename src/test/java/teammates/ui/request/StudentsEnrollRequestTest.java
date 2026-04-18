package teammates.ui.request;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StudentsEnrollRequest}.
 */
public class StudentsEnrollRequestTest extends BaseTestCase {

    @Test
    public void testValidate_withValidRequest_shouldPass() throws Exception {
        StudentsEnrollRequest request = new StudentsEnrollRequest(Arrays.asList(getTypicalStudentEnrollRequest(0)));
        request.validate();
    }

    @Test
    public void testValidate_withNullValueInRequest_shouldFail() {
        StudentsEnrollRequest.StudentEnrollRequest request =
                new StudentsEnrollRequest.StudentEnrollRequest("typical name", null, "typical team",
                        "typical section", "typical comment");
        StudentsEnrollRequest enrollRequest = new StudentsEnrollRequest(Arrays.asList(request));
        assertThrows(InvalidHttpRequestBodyException.class, enrollRequest::validate);
    }

    @Test
    public void testValidate_withEmptyEnrollList_shouldFail() {
        StudentsEnrollRequest request = new StudentsEnrollRequest(new ArrayList<>());
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withDuplicateEmail_shouldFail() {
        StudentsEnrollRequest.StudentEnrollRequest requestOne = getTypicalStudentEnrollRequest(0);
        StudentsEnrollRequest.StudentEnrollRequest requestTwo = getTypicalStudentEnrollRequest(0);
        String duplicatedEmail = requestOne.getEmail();
        StudentsEnrollRequest enrollRequest = new StudentsEnrollRequest(Arrays.asList(requestOne, requestTwo));
        InvalidHttpRequestBodyException actualException =
                assertThrows(InvalidHttpRequestBodyException.class, enrollRequest::validate);
        assertEquals(actualException.getMessage(),
                "Error, duplicated email addresses detected in the input: " + duplicatedEmail);
    }

    @Test
    public void testValidate_reservedTeam_shouldFail() {
        StudentsEnrollRequest.StudentEnrollRequest request =
                new StudentsEnrollRequest.StudentEnrollRequest("typical name", "typical0@email.com",
                        Const.USER_TEAM_FOR_INSTRUCTOR, "typical section", "");
        StudentsEnrollRequest enrollRequest = new StudentsEnrollRequest(Arrays.asList(request));
        InvalidHttpRequestBodyException ex = assertThrows(InvalidHttpRequestBodyException.class,
                enrollRequest::validate);
        assertEquals(ex.getMessage(), EnrollmentReservedInputValidator.ERROR_RESERVED_TEAM_NAME);
    }

    @Test
    public void testValidate_explicitReservedSection_shouldFail() {
        StudentsEnrollRequest.StudentEnrollRequest request =
                new StudentsEnrollRequest.StudentEnrollRequest("typical name", "typical0@email.com",
                        "typical team", Const.DEFAULT_SECTION, "");
        StudentsEnrollRequest enrollRequest = new StudentsEnrollRequest(Arrays.asList(request));
        InvalidHttpRequestBodyException ex = assertThrows(InvalidHttpRequestBodyException.class,
                enrollRequest::validate);
        assertEquals(ex.getMessage(), EnrollmentReservedInputValidator.ERROR_RESERVED_SECTION_NAME);
    }

    @Test
    public void testValidate_withDuplicateEmailDifferingOnlyInCase_shouldFail() {
        StudentsEnrollRequest.StudentEnrollRequest requestOne = getTypicalStudentEnrollRequest(0);
        StudentsEnrollRequest.StudentEnrollRequest requestTwo =
                new StudentsEnrollRequest.StudentEnrollRequest("typical name", "Typical0@Email.com",
                        "typical team", "typical section", "typical comment");
        StudentsEnrollRequest enrollRequest = new StudentsEnrollRequest(Arrays.asList(requestOne, requestTwo));

        InvalidHttpRequestBodyException actualException =
                assertThrows(InvalidHttpRequestBodyException.class, enrollRequest::validate);

        assertEquals(actualException.getMessage(),
                "Error, duplicated email addresses detected in the input: Typical0@Email.com");
    }

    private StudentsEnrollRequest.StudentEnrollRequest getTypicalStudentEnrollRequest(int index) {
        return new StudentsEnrollRequest.StudentEnrollRequest("typical name",
                String.format("typical%d@email.com", index), "typical team",
                "typical section", "typical comment");
    }
}
