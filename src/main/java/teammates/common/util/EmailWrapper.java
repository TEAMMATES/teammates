package teammates.common.util;

import java.util.ArrayList;
import java.util.List;

public class EmailWrapper {
    
    private String senderName;
    private String senderEmail;
    private List<String> recipientsList = new ArrayList<String>();
    private String subject;
    private String content;
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderEmail() {
        return senderEmail;
    }
    
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
    
    public List<String> getRecipientsList() {
        return recipientsList;
    }
    
    public String getFirstRecipient() {
        return getRecipientsList().get(0);
    }
    
    public void addRecipient(String recipient) {
        if (!recipientsList.contains(recipient)) {
            recipientsList.add(recipient);
        }
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
}
