package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Logger;
import teammates.ui.template.EnrollResultPanel;

/**
 * PageData: page data for the 'Result' page after the "request account" of an instructor.
 */
public class InstructorAccountRequestResultPageData extends PageData {
	
    private static final Logger log = Logger.getLogger();

    private String name;
    private String university;
    private String url;
    private String email;
    private String comments;

    public InstructorAccountRequestResultPageData(String name, String university,
                                                String country, String email,
                                                String url, String comments) {
    }
}

