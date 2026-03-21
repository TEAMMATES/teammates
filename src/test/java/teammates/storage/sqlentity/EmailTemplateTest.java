package teammates.storage.sqlentity;

import java.util.List;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link EmailTemplate}.
 */
public class EmailTemplateTest extends BaseTestCase {

    @Test
    public void testGetInvalidityInfo_validTemplate_returnsEmptyList() {
        EmailTemplate template = new EmailTemplate("KEY", "Subject", "Body");

        List<String> errors = template.getInvalidityInfo();

        assertTrue(errors.isEmpty());
    }

    @Test
    public void testGetInvalidityInfo_blankTemplateKey_returnsError() {
        EmailTemplate template = new EmailTemplate("", "Subject", "Body");

        List<String> errors = template.getInvalidityInfo();

        assertFalse(errors.isEmpty());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Template key cannot be empty."));
    }

    @Test
    public void testGetInvalidityInfo_blankSubject_returnsError() {
        EmailTemplate template = new EmailTemplate("KEY", "", "Body");

        List<String> errors = template.getInvalidityInfo();

        assertFalse(errors.isEmpty());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Email template subject cannot be empty."));
    }

    @Test
    public void testGetInvalidityInfo_blankBody_returnsError() {
        EmailTemplate template = new EmailTemplate("KEY", "Subject", "");

        List<String> errors = template.getInvalidityInfo();

        assertFalse(errors.isEmpty());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Email template body cannot be empty."));
    }

    @Test
    public void testGetInvalidityInfo_allFieldsBlank_returnsMultipleErrors() {
        EmailTemplate template = new EmailTemplate("", "", "");

        List<String> errors = template.getInvalidityInfo();

        assertEquals(3, errors.size());
    }

    @Test
    public void testGetInvalidityInfo_whitespaceOnlyFields_returnsErrors() {
        EmailTemplate template = new EmailTemplate("   ", "   ", "   ");

        List<String> errors = template.getInvalidityInfo();

        assertEquals(3, errors.size());
    }
}
