package teammates.ui.template;

public class AdminDraftEmailRow {
    private String emailId;
    private AdminEmailActions actions;
    private String addressReceiver;
    private String groupReceiver;
    private String subject;
    private String date;
    
    public AdminDraftEmailRow(final String emailId, final AdminEmailActions actions, final String addressReceiver,
                                    final String groupReceiver, final String subject, final String date) {
        this.emailId = emailId;
        this.actions = actions;
        this.addressReceiver = addressReceiver;
        this.groupReceiver = groupReceiver;
        this.subject = subject;
        this.date = date;
    }
    
    public String getEmailId() {
        return emailId;
    }
    
    public AdminEmailActions getActions() {
        return actions;
    }
    
    public String getAddressReceiver() {
        return addressReceiver;
    }
    
    public String getGroupReceiver() {
        return groupReceiver;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getDate() {
        return date;
    }
}
