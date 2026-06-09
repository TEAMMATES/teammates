package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.util.Set;

import org.mockito.MockedStatic;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.ui.output.ConfigData;
import teammates.ui.output.LoginMethod;

/**
 * SUT: {@link GetConfigAction}.
 */
public class GetConfigActionTest extends BaseActionTest<GetConfigAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.CONFIG;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_withSupportedLoginMethods_shouldReturnCorrectMethods() {
        try (MockedStatic<Config> mockConfig = mockStatic(Config.class)) {
            mockConfig.when(() -> Config.getSupportedLoginMethods())
                    .thenReturn(Set.of(LoginMethod.GOOGLE, LoginMethod.DEV_SERVER));

            GetConfigAction action = getAction();
            JsonResult result = action.execute();
            ConfigData data = (ConfigData) result.getOutput();

            Set<LoginMethod> loginMethods = data.getLoginMethods();
            assertEquals(2, loginMethods.size());
            assertTrue(loginMethods.contains(LoginMethod.GOOGLE));
            assertTrue(loginMethods.contains(LoginMethod.DEV_SERVER));
        }
    }
}
