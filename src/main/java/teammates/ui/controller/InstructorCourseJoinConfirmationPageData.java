package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;

public class InstructorCourseJoinConfirmationPageData extends PageData {

    private String regkey;
    private String institute;
    
    public InstructorCourseJoinConfirmationPageData(AccountAttributes account, String regkey, String institute) {
        super(account);
        this.regkey = regkey;
        this.institute = institute;
    }
    
    public String getRegkey() {
        return regkey;
    }
    
    public String getInstitute() {
        return institute;
    }
    
    public String getConfirmationLink() {
        String ref = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED + "?key=" + regkey;
        if (institute != null) {
            ref +=  "&" + Const.ParamsNames.INSTRUCTOR_INSTITUTION + "=" + Sanitizer.sanitizeForUri(institute);
        }
        return ref;
    }
}
