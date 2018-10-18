package teammates.ui.automated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Logger;

/**
 * Task queue worker action: prepares admin email to be sent via task queue in address mode,
 * i.e. using the address list given directly.
 */
public class AdminPrepareEmailAddressModeWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        log.info("Preparing admin email task queue in address mode...");

        String emailId = getRequestParamValue(ParamsNames.ADMIN_EMAIL_ID);
        Assumption.assertPostParamNotNull(ParamsNames.ADMIN_EMAIL_ID, emailId);

        String addressReceiverListString = getRequestParamValue(ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS);
        Assumption.assertPostParamNotNull(ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, addressReceiverListString);

        AdminEmailAttributes adminEmail = logic.getAdminEmailById(emailId);
        Assumption.assertNotNull(adminEmail);
        List<String> addressList = new ArrayList<>();

        if (addressReceiverListString.contains(",")) {
            addressList.addAll(Arrays.asList(addressReceiverListString.split(",")));
        } else {
            addressList.add(addressReceiverListString);
        }

        for (String emailAddress : addressList) {
            taskQueuer.scheduleAdminEmailForSending(emailId, emailAddress, adminEmail.getSubject(),
                                                    adminEmail.getContentValue());
        }
    }

}
