package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;

public class InstructorStudentRecordsPageData extends PageData {

    public String courseId;
    public InstructorAttributes currentInstructor;
    public StudentProfileAttributes studentProfile;
    public List<CommentAttributes> comments;
    public List<SessionAttributes> sessions;
    public String showCommentBox;

    public InstructorStudentRecordsPageData(AccountAttributes account) {
        super(account);
    }

}
