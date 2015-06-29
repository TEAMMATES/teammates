package teammates.ui.controller;

import java.util.List;
import java.util.ArrayList;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.ui.template.RemindParticularStudentsCheckboxEmailNamePair;

public class InstructorFeedbackRemindParticularStudentsPageData extends PageData {
    public FeedbackSessionResponseStatus responseStatus;
    public String courseId;
    public String fsName;
    private List<RemindParticularStudentsCheckboxEmailNamePair> emailNamePairs;
    
    public InstructorFeedbackRemindParticularStudentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init() {
        emailNamePairs = new ArrayList<RemindParticularStudentsCheckboxEmailNamePair>();

        for (String studentEmail : responseStatus.noResponse) {
            String studentName = responseStatus.emailNameTable.get(studentEmail);
            
            RemindParticularStudentsCheckboxEmailNamePair emailNamePair = 
                new RemindParticularStudentsCheckboxEmailNamePair(studentEmail, studentName);
            
            emailNamePairs.add(emailNamePair);
        }
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public String getFsName() {
        return fsName;
    }
    
    public List<RemindParticularStudentsCheckboxEmailNamePair> getEmailNamePairs() {
        return emailNamePairs;
    }
}
