package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.SessionAttributes;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.StudentAttributes;

public class InstructorStudentRecordsPageData extends PageData {
	
	public String courseId;
	public StudentAttributes student;
	public List<CommentAttributes> comments;
	public List<SessionAttributes> sessions;
	public List<SessionResultsBundle> results;
	
	public InstructorStudentRecordsPageData(AccountAttributes account) {
		super(account);
	}
}
