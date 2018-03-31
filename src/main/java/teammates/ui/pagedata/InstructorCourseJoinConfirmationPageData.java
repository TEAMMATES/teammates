package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;

public class InstructorCourseJoinConfirmationPageData extends PageData {

    private String regkey;
    private String institute;

    public InstructorCourseJoinConfirmationPageData(AccountAttributes account, String sessionToken, String regkey,
            String institute) {
        super(account, sessionToken);
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

        return Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED + "?key=" + regkey
             + (institute == null
                ? ""
                : '&' + Const.ParamsNames.INSTRUCTOR_INSTITUTION + '=' + SanitizationHelper.sanitizeForUri(institute));
    }
}
