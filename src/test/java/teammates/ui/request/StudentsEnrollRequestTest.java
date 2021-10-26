package teammates.ui.request;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

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

    private StudentsEnrollRequest.StudentEnrollRequest getTypicalStudentEnrollRequest(int index) {
        return new StudentsEnrollRequest.StudentEnrollRequest("typical name",
                String.format("typical%d@email.com", index), "typical team",
                "typical section", "typical comment");
    }
}
