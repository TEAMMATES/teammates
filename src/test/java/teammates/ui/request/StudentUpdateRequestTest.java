package teammates.ui.request;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StudentUpdateRequest}.
 */
public class StudentUpdateRequestTest extends BaseTestCase {

    @Test
    public void testValidate_reservedTeam_shouldFail() {
        StudentUpdateRequest request = new StudentUpdateRequest("name", "a@b.com",
                Const.USER_TEAM_FOR_INSTRUCTOR, "Section A", "comments", false);
        InvalidHttpRequestBodyException ex = assertThrows(InvalidHttpRequestBodyException.class,
                request::validate);
        assertEquals(ex.getMessage(), EnrollmentReservedInputValidator.ERROR_RESERVED_TEAM_NAME);
    }

    @Test
    public void testValidate_explicitReservedSection_shouldFail() {
        StudentUpdateRequest request = new StudentUpdateRequest("name", "a@b.com",
                "Team A", Const.DEFAULT_SECTION, "comments", false);
        InvalidHttpRequestBodyException ex = assertThrows(InvalidHttpRequestBodyException.class,
                request::validate);
        assertEquals(ex.getMessage(), EnrollmentReservedInputValidator.ERROR_RESERVED_SECTION_NAME);
    }
}
