package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.ui.template.AdminDraftEmailRow;
import teammates.ui.template.AdminDraftEmailTable;
import teammates.ui.template.AdminEmailActions;
import teammates.ui.template.AdminSentEmailRow;
import teammates.ui.template.AdminSentEmailTable;
import teammates.ui.template.AdminTrashEmailActions;
import teammates.ui.template.AdminTrashEmailRow;
import teammates.ui.template.AdminTrashEmailTable;

public abstract class AdminEmailPageData extends PageData {
    protected AdminEmailPageState state;
    private AdminSentEmailTable sentEmailTable;
    private AdminDraftEmailTable draftEmailTable;
    private AdminTrashEmailTable trashEmailTable;

    protected enum AdminEmailPageState {
        COMPOSE, SENT, TRASH, DRAFT
    }

    protected AdminEmailPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init() {
        sentEmailTable = createAdminSentEmailTable();
        draftEmailTable = createAdminDraftEmailTable();
        trashEmailTable = createAdminTrashEmailTable();
    }

    public AdminSentEmailTable getSentEmailTable() {
        return sentEmailTable;
    }

    public AdminDraftEmailTable getDraftEmailTable() {
        return draftEmailTable;
    }

    public AdminTrashEmailTable getTrashEmailTable() {
        return trashEmailTable;
    }

    public AdminEmailPageState getPageState() {
        return this.state;
    }

    public boolean isAdminEmailCompose() {
        return getClass().toString().contains("AdminEmailCompose");
    }

    public boolean isAdminEmailSent() {
        return getClass().toString().contains("AdminEmailSent");
    }

    public boolean isAdminEmailDraft() {
        return getClass().toString().contains("AdminEmailDraft");
    }

    public boolean isAdminEmailTrash() {
        return getClass().toString().contains("AdminEmailTrash");
    }

    public AdminEmailComposePageData getAdminEmailComposePageData() {
        return state.equals(AdminEmailPageState.COMPOSE) ? (AdminEmailComposePageData) this : null;
    }

    public AdminEmailSentPageData getAdminEmailSentPageData() {
        return state.equals(AdminEmailPageState.SENT) ? (AdminEmailSentPageData) this : null;
    }

    public AdminEmailDraftPageData getAdminEmailDraftPageData() {
        return state.equals(AdminEmailPageState.DRAFT) ? (AdminEmailDraftPageData) this : null;
    }

    public AdminEmailTrashPageData getAdminEmailTrashPageData() {
        return state.equals(AdminEmailPageState.TRASH) ? (AdminEmailTrashPageData) this : null;
    }

    // Sent email table

    private AdminSentEmailTable createAdminSentEmailTable() {
        List<AdminSentEmailRow> rows = new ArrayList<>();

        if (state.equals(AdminEmailPageState.SENT)) {
            AdminEmailSentPageData sentPageData = (AdminEmailSentPageData) this;

            for (AdminEmailAttributes ae : sentPageData.adminSentEmailList) {
                rows.add(createAdminSentEmailRow(ae));
            }
        }

        return new AdminSentEmailTable(getNumEmailsSent(), rows);
    }

    private int getNumEmailsSent() {
        try {
            return getAdminEmailSentPageData().adminSentEmailList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private AdminSentEmailRow createAdminSentEmailRow(AdminEmailAttributes ae) {
        String emailId = ae.getEmailId();
        String addressReceiver = ae.getAddressReceiver().size() > 0 ? ae.getAddressReceiver().get(0) : "";
        String groupReceiver = ae.getGroupReceiver().size() > 0 ? ae.getGroupReceiver().get(0) : "";

        return new AdminSentEmailRow(emailId, new AdminEmailActions(emailId, "sentpage", getSessionToken()), addressReceiver,
                                        groupReceiver, ae.getSubject(), ae.getSendDateForDisplay());
    }

    // Draft email table

    private AdminDraftEmailTable createAdminDraftEmailTable() {
        List<AdminDraftEmailRow> rows = new ArrayList<>();

        if (state.equals(AdminEmailPageState.DRAFT)) {
            AdminEmailDraftPageData draftPageData = (AdminEmailDraftPageData) this;

            for (AdminEmailAttributes ae : draftPageData.draftEmailList) {
                rows.add(createAdminDraftEmailRow(ae));
            }
        }

        return new AdminDraftEmailTable(getNumEmailsDraft(), rows);
    }

    private int getNumEmailsDraft() {
        try {
            return getAdminEmailDraftPageData().draftEmailList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private AdminDraftEmailRow createAdminDraftEmailRow(AdminEmailAttributes ae) {
        String emailId = ae.getEmailId();
        String addressReceiver = ae.getAddressReceiver().size() > 0 ? ae.getAddressReceiver().get(0) : "";
        String groupReceiver = ae.getGroupReceiver().size() > 0 ? ae.getGroupReceiver().get(0) : "";

        return new AdminDraftEmailRow(emailId, new AdminEmailActions(emailId, "draftpage", getSessionToken()),
                                      addressReceiver, groupReceiver, ae.getSubject(), ae.getCreateDateForDisplay());
    }

    // Trash email table

    private AdminTrashEmailTable createAdminTrashEmailTable() {
        List<AdminTrashEmailRow> rows = new ArrayList<>();

        if (state.equals(AdminEmailPageState.TRASH)) {
            AdminEmailTrashPageData trashPageData = (AdminEmailTrashPageData) this;

            for (AdminEmailAttributes ae : trashPageData.adminTrashEmailList) {
                rows.add(createAdminTrashEmailRow(ae));
            }
        }

        return new AdminTrashEmailTable(getNumEmailsTrash(), rows, getEmptyTrashActionUrl());
    }

    private int getNumEmailsTrash() {
        try {
            return getAdminEmailTrashPageData().adminTrashEmailList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private String getEmptyTrashActionUrl() {
        try {
            return getAdminEmailTrashPageData().getEmptyTrashBinActionUrl();
        } catch (Exception e) {
            return "";
        }
    }

    private AdminTrashEmailRow createAdminTrashEmailRow(AdminEmailAttributes ae) {
        String emailId = ae.getEmailId();
        String addressReceiver = ae.getAddressReceiver().size() > 0 ? ae.getAddressReceiver().get(0) : "";
        String groupReceiver = ae.getGroupReceiver().size() > 0 ? ae.getGroupReceiver().get(0) : "";

        return new AdminTrashEmailRow(emailId, new AdminTrashEmailActions(emailId, getSessionToken()), addressReceiver,
                                        groupReceiver, ae.getSubject(), ae.getSendDateForDisplay());
    }
}
