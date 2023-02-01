package teammates.common.util;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link LoginLinkOptions}.
 */
public class LoginLinkOptionsTest extends BaseTestCase {

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            LoginLinkOptions.builder()
                    .withUserEmail(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            LoginLinkOptions.builder()
                    .withContinueUrl(null)
                    .build();
        });
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        String userEmail = "test@example.com";
        String continueUrl = "http://localhost:4200/continue/url";
        LoginLinkOptions loginLinkOptions = LoginLinkOptions.builder()
                .withUserEmail(userEmail)
                .withContinueUrl(continueUrl)
                .build();

        assertEquals(userEmail, loginLinkOptions.getUserEmail());
        assertEquals(continueUrl, loginLinkOptions.getContinueUrl());

    }
}
