package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;

public class InstructorCourseJoinConfirmationPageData extends PageData {

    private String regkey;
    private String institute;
    
    public InstructorCourseJoinConfirmationPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(String regkey, String institute) {
        this.regkey = regkey;
        this.institute = institute;
    }
    
    public String getRegkey() {
        return regkey;
    }
    
    public String getInstitue() {
        return institute;
    }
    
    public String getConfirmationLink() {
        String ref = "";
        if (institute == null) {

            ref = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED
                    + "?key=" + regkey;
        } else {
            ref = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED
                    + "?key=" + regkey + "&"
                    + Const.ParamsNames.INSTRUCTOR_INSTITUTION + "="
                    + Sanitizer.sanitizeForUri(institute);
        }
        return ref;
    }
}
