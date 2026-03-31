package teammates.sqlui.webapi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.EmailTemplatesData;
import teammates.ui.webapi.ConfigurableEmailTemplate;
import teammates.ui.webapi.GetEmailTemplatesAction;

/**
 * SUT: {@link GetEmailTemplatesAction}.
 */
public class GetEmailTemplatesActionTest extends BaseActionTest<GetEmailTemplatesAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.EMAIL_TEMPLATES;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        loginAsAdmin();
    }

    @Test
    void testAccessControl_onlyAdminsCanAccess() {
        verifyOnlyAdminsCanAccess();
    }

    @Test
    void testExecute_returnsConfigurableTemplateKeys() {
        GetEmailTemplatesAction action = getAction();
        EmailTemplatesData output = (EmailTemplatesData) getJsonResult(action).getOutput();

        List<String> expectedKeys = Arrays.stream(ConfigurableEmailTemplate.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        assertEquals(expectedKeys, output.getTemplateKeys());
    }
}
