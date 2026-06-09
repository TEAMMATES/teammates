package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.util.Set;

import org.mockito.MockedStatic;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.ui.output.LoginMethod;
import teammates.ui.output.LoginMethodsData;

/**
 * SUT: {@link GetLoginMethodsAction}.
 */
public class GetLoginMethodsActionTest extends BaseActionTest<GetLoginMethodsAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.LOGIN_METHODS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_withSupportedLoginMethods_shouldReturnCorrectMethods() {
        try (MockedStatic<Config> mockConfig = mockStatic(Config.class)) {
            mockConfig.when(() -> Config.getSupportedLoginMethods()).thenReturn(Set.of("google", "devserver"));

            GetLoginMethodsAction action = getAction();
            JsonResult result = action.execute();
            LoginMethodsData data = (LoginMethodsData) result.getOutput();

            Set<LoginMethod> loginMethods = data.getLoginMethods();
            assertEquals(2, loginMethods.size());
            assertTrue(loginMethods.contains(LoginMethod.GOOGLE));
            assertTrue(loginMethods.contains(LoginMethod.DEV_SERVER));
        }
    }

    @Test
    void testExecute_withUnsupportedLoginMethod_shouldThrowError() {
        try (MockedStatic<Config> mockConfig = mockStatic(Config.class)) {
            mockConfig.when(() -> Config.getSupportedLoginMethods()).thenReturn(Set.of("unsupported_method"));

            GetLoginMethodsAction action = getAction();
            assertThrows(IllegalArgumentException.class, action::execute);
        }
    }
}
